package ru.serega6531.packmate.pcap;

import com.google.common.collect.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import ru.serega6531.packmate.model.CtfService;
import ru.serega6531.packmate.model.enums.Protocol;
import ru.serega6531.packmate.model.pojo.UnfinishedStream;
import ru.serega6531.packmate.service.ServicesService;
import ru.serega6531.packmate.service.StreamService;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractPcapWorker implements PcapWorker, PacketListener {

    private final ServicesService servicesService;
    private final StreamService streamService;

    protected PcapHandle pcap = null;
    protected final ExecutorService loopExecutorService;

    // во время работы должен быть != null
    protected ExecutorService processorExecutorService;

    private final InetAddress localIp;

    private long packetIdCounter = 0;  // оно однопоточное, так что пусть будет без atomic

    private final ListMultimap<UnfinishedStream, ru.serega6531.packmate.model.Packet> unfinishedTcpStreams = ArrayListMultimap.create();
    private final ListMultimap<UnfinishedStream, ru.serega6531.packmate.model.Packet> unfinishedUdpStreams = ArrayListMultimap.create();

    // в следующих мапах в значениях находится srcIp соответствующего пакета
    private final SetMultimap<UnfinishedStream, ImmutablePair<Inet4Address, Integer>> fins = HashMultimap.create();
    private final SetMultimap<UnfinishedStream, ImmutablePair<Inet4Address, Integer>> acks = HashMultimap.create();

    public AbstractPcapWorker(ServicesService servicesService,
                              StreamService streamService,
                              String localIpString) throws UnknownHostException {
        this.servicesService = servicesService;
        this.streamService = streamService;

        this.localIp = InetAddress.getByName(localIpString);
        if (!(this.localIp instanceof Inet4Address)) {
            throw new IllegalArgumentException("Only ipv4 local ips are supported");
        }

        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("pcap-loop").build();
        loopExecutorService = Executors.newSingleThreadExecutor(factory);
    }

    public void gotPacket(Packet rawPacket) {
        if (!rawPacket.contains(IpV4Packet.class)) {
            return;
        }

        final long time = pcap.getTimestamp().getTime();

        if (rawPacket.contains(TcpPacket.class)) {
            gotTcpPacket(rawPacket, time);
        } else if (rawPacket.contains(UdpPacket.class)) {
            gotUdpPacket(rawPacket, time);
        }
    }

    private void gotTcpPacket(Packet rawPacket, long time) {
        final IpV4Packet.IpV4Header ipHeader = rawPacket.get(IpV4Packet.class).getHeader();
        Inet4Address sourceIp = ipHeader.getSrcAddr();
        Inet4Address destIp = ipHeader.getDstAddr();
        byte ttl = ipHeader.getTtl();

        final TcpPacket packet = rawPacket.get(TcpPacket.class);
        final TcpPacket.TcpHeader tcpHeader = packet.getHeader();
        int sourcePort = tcpHeader.getSrcPort().valueAsInt();
        int destPort = tcpHeader.getDstPort().valueAsInt();
        boolean ack = tcpHeader.getAck();
        boolean fin = tcpHeader.getFin();
        boolean rst = tcpHeader.getRst();
        byte[] content = packet.getPayload() != null ? packet.getPayload().getRawData() : new byte[0];

        String sourceIpString = sourceIp.getHostAddress();
        String destIpString = destIp.getHostAddress();

        final Optional<CtfService> serviceOptional =
                servicesService.findService(sourceIp, sourcePort, destIp, destPort);

        if (serviceOptional.isPresent()) {
            processorExecutorService.execute(() -> {
                UnfinishedStream stream = addNewPacket(sourceIp, destIp, time, sourcePort, destPort, ttl, content, Protocol.TCP);

                if (log.isDebugEnabled()) {
                    log.debug("tcp {} {}:{} -> {}:{}, packet number {}",
                            serviceOptional.get(), sourceIpString, sourcePort, destIpString, destPort,
                            unfinishedTcpStreams.get(stream).size());
                }

                checkTcpTermination(ack, fin, rst, new ImmutablePair<>(sourceIp, sourcePort), new ImmutablePair<>(destIp, destPort), stream);
            });
        } else { // сервис не найден
            if (log.isTraceEnabled()) {
                log.trace("tcp {}:{} -> {}:{}", sourceIpString, sourcePort, destIpString, destPort);
            }
        }
    }

    private void gotUdpPacket(Packet rawPacket, long time) {
        final IpV4Packet.IpV4Header ipHeader = rawPacket.get(IpV4Packet.class).getHeader();
        Inet4Address sourceIp = ipHeader.getSrcAddr();
        Inet4Address destIp = ipHeader.getDstAddr();
        byte ttl = ipHeader.getTtl();

        final UdpPacket packet = rawPacket.get(UdpPacket.class);
        final UdpPacket.UdpHeader udpHeader = packet.getHeader();
        int sourcePort = udpHeader.getSrcPort().valueAsInt();
        int destPort = udpHeader.getDstPort().valueAsInt();
        byte[] content = packet.getPayload() != null ? packet.getPayload().getRawData() : new byte[0];

        String sourceIpString = sourceIp.getHostAddress();
        String destIpString = destIp.getHostAddress();

        final Optional<CtfService> serviceOptional =
                servicesService.findService(sourceIp, sourcePort, destIp, destPort);

        if (serviceOptional.isPresent()) {
            processorExecutorService.execute(() -> {
                UnfinishedStream stream = addNewPacket(sourceIp, destIp, time, sourcePort, destPort, ttl, content, Protocol.UDP);

                if (log.isDebugEnabled()) {
                    log.debug("udp {} {}:{} -> {}:{}, packet number {}",
                            serviceOptional.get(), sourceIpString, sourcePort, destIpString, destPort,
                            unfinishedUdpStreams.get(stream).size());
                }
            });
        } else { // сервис не найден
            if (log.isTraceEnabled()) {
                log.trace("udp {}:{} -> {}:{}", sourceIpString, sourcePort, destIpString, destPort);
            }
        }
    }

    private UnfinishedStream addNewPacket(Inet4Address sourceIp, Inet4Address destIp, long time,
                                          int sourcePort, int destPort, byte ttl, byte[] content, Protocol protocol) {
        var incoming = destIp.equals(localIp);
        var stream = new UnfinishedStream(sourceIp, destIp, sourcePort, destPort, protocol);

        var packet = ru.serega6531.packmate.model.Packet.builder()
                .tempId(packetIdCounter++)
                .ttl(ttl)
                .timestamp(time)
                .incoming(incoming)
                .content(content)
                .build();

        final var streams = (protocol == Protocol.TCP) ? this.unfinishedTcpStreams : this.unfinishedUdpStreams;

        if (!streams.containsKey(stream)) {
            log.debug("New stream started");
        }

        streams.put(stream, packet);
        return stream;
    }

    /**
     * Udp не имеет фазы закрытия, поэтому закрывается только по таймауту
     */
    private void checkTcpTermination(boolean ack, boolean fin, boolean rst,
                                     ImmutablePair<Inet4Address, Integer> sourceIpAndPort,
                                     ImmutablePair<Inet4Address, Integer> destIpAndPort,
                                     UnfinishedStream stream) {

        if (fin) {
            fins.put(stream, sourceIpAndPort);
        }

        if (ack && fins.containsEntry(stream, destIpAndPort)) {  // проверяем destIp, потому что ищем ответ на его fin
            acks.put(stream, sourceIpAndPort);
        }

        // если соединение разорвано с помощью rst или закрыто с помощью fin-ack-fin-ack
        if (rst || (acks.containsEntry(stream, sourceIpAndPort) && acks.containsEntry(stream, destIpAndPort))) {
            streamService.saveNewStream(stream, unfinishedTcpStreams.get(stream));

            unfinishedTcpStreams.removeAll(stream);
            fins.removeAll(stream);
            acks.removeAll(stream);
        }
    }

    @Override
    @SneakyThrows
    public void closeAllStreams(Protocol protocol) {
        final var streams = (protocol == Protocol.TCP) ? this.unfinishedTcpStreams : this.unfinishedUdpStreams;

        Multimaps.asMap(streams).forEach((key, value) ->
                streamService.saveNewStream(key, new ArrayList<>(value)));

        streams.clear();
        if (protocol == Protocol.TCP) {
            fins.clear();
            acks.clear();
        }
    }

    @Override
    @SneakyThrows
    public int closeTimeoutStreams(Protocol protocol, long timeoutMillis) {
        return processorExecutorService.submit(() -> {
            int streamsClosed = 0;

            final long time = System.currentTimeMillis();
            final var streams = (protocol == Protocol.TCP) ? this.unfinishedTcpStreams : this.unfinishedUdpStreams;

            final var oldStreams = Multimaps.asMap(streams).entrySet().stream()
                    .filter(entry -> {
                        final var packets = entry.getValue();
                        return time - packets.get(packets.size() - 1).getTimestamp() > timeoutMillis;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            for (var entry : oldStreams.entrySet()) {
                final UnfinishedStream stream = entry.getKey();
                final var packets = entry.getValue();

                if (streamService.saveNewStream(stream, packets)) {
                    streamsClosed++;
                }

                if (protocol == Protocol.TCP) {
                    fins.removeAll(stream);
                    acks.removeAll(stream);
                }

                streams.removeAll(stream);
            }

            return streamsClosed;
        }).get();
    }

}
