package ru.serega6531.packmate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.serega6531.packmate.model.CtfService;
import ru.serega6531.packmate.model.UnfinishedStream;
import ru.serega6531.packmate.service.PacketService;
import ru.serega6531.packmate.service.PatternService;
import ru.serega6531.packmate.service.ServicesService;
import ru.serega6531.packmate.service.StreamService;

import javax.annotation.PreDestroy;
import java.net.Inet4Address;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class PcapWorker {

    private final ServicesService servicesService;
    private final StreamService streamService;
    private final PacketService packetService;
    private final PatternService patternService;

    private final PcapNetworkInterface device;
    private PcapHandle pcap = null;
    private final ExecutorService executorService;

    private final String localIp;

    private long packetIdCounter = 0;  // оно однопоточное, так что пусть будет без atomic

    private final Map<UnfinishedStream, List<ru.serega6531.packmate.model.Packet>> unfinishedStreams = new HashMap<>();

    // в следующих мапах в листах srcIp соответствующего пакета
    private final Map<UnfinishedStream, List<String>> fins = new HashMap<>();
    private final Map<UnfinishedStream, List<String>> acks = new HashMap<>();

    @Autowired
    public PcapWorker(ServicesService servicesService,
                      StreamService streamService,
                      PacketService packetService,
                      PatternService patternService,
                      @Value("${interface-name}") String interfaceName,
                      @Value("${local-ip}") String localIp) throws PcapNativeException {
        this.servicesService = servicesService;
        this.streamService = streamService;
        this.packetService = packetService;
        this.patternService = patternService;

        this.localIp = localIp;

        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("pcap-worker").build();
        executorService = Executors.newSingleThreadExecutor(factory);
        device = Pcaps.getDevByName(interfaceName);
    }

    public void start() throws PcapNativeException {
        System.out.println("Using interface " + device.getName());
        pcap = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 100);

        executorService.execute(() -> {
            try {
                log.info("Intercept started");
                while (true) {
                    if (pcap.isOpen()) {
                        try {
                            final Packet packet = pcap.getNextPacketEx();
                            processPacket(packet);
                        } catch (TimeoutException ignored) {
                        }
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("Error while capturing packet", e);
                stop();
            }
        });
    }

    @PreDestroy
    public void stop() {
        if (pcap != null && pcap.isOpen()) {
            pcap.close();
        }

        log.info("Intercept stopped");
    }

    private void processPacket(Packet rawPacket) {
        Inet4Address sourceIp = null;
        Inet4Address destIp = null;
        String sourceIpString = null;
        String destIpString = null;
        int sourcePort = -1;
        int destPort = -1;
        byte[] content = null;
        Protocol protocol = null;
        boolean ack = false;
        boolean fin = false;

        if(rawPacket.contains(IpV4Packet.class)){
            final IpV4Packet.IpV4Header header = rawPacket.get(IpV4Packet.class).getHeader();
            sourceIp = header.getSrcAddr();
            destIp = header.getDstAddr();
            sourceIpString = header.getSrcAddr().getHostAddress();
            destIpString = header.getDstAddr().getHostAddress();
        }

        if(rawPacket.contains(TcpPacket.class)) {
            final TcpPacket packet = rawPacket.get(TcpPacket.class);
            final TcpPacket.TcpHeader header = packet.getHeader();
            sourcePort = header.getSrcPort().valueAsInt();
            destPort = header.getDstPort().valueAsInt();
            ack = header.getAck();
            fin = header.getFin();
            content = packet.getRawData();
            protocol = Protocol.TCP;
        } else if(rawPacket.contains(UdpPacket.class)) {
            final UdpPacket packet = rawPacket.get(UdpPacket.class);
            final UdpPacket.UdpHeader header = packet.getHeader();
            sourcePort = header.getSrcPort().valueAsInt();
            destPort = header.getDstPort().valueAsInt();
            content = packet.getRawData();
            protocol = Protocol.UDP;
        }

        if(sourceIpString != null && sourcePort != -1) {
            Optional<CtfService> serviceOptional = Optional.empty();

            if(sourceIpString.equals(localIp)) {
                serviceOptional = servicesService.findByPort(sourcePort);
            } else if(destIpString.equals(localIp)) {
                serviceOptional = servicesService.findByPort(destPort);
            }

            if(serviceOptional.isPresent()) {
                UnfinishedStream stream = new UnfinishedStream(sourceIp, destIp, sourcePort, destPort, protocol);

                ru.serega6531.packmate.model.Packet packet = ru.serega6531.packmate.model.Packet.builder()
                        .tempId(packetIdCounter++)
                        .timestamp(System.currentTimeMillis())
                        .content(content)
                        .build();

                if(unfinishedStreams.containsKey(stream)) {
                    unfinishedStreams.get(stream).add(packet);
                } else {
                    List<ru.serega6531.packmate.model.Packet> packets = new ArrayList<>();
                    packets.add(packet);
                    unfinishedStreams.put(stream, packets);
                }

                if(protocol == Protocol.TCP) {
                    if(!fins.containsKey(stream)) {
                        fins.put(stream, new ArrayList<>());
                    }

                    if(!acks.containsKey(stream)) {
                        acks.put(stream, new ArrayList<>());
                    }

                    if(ack && fins.get(stream).contains(destIpString)) {  // проверяем destIp, потому что ищем ответ на его fin
                        //TODO
                    }

                    if(fin && acks.get(stream).contains(destIpString)) {
                        //TODO
                    }
                }

                log.info("{} {}:{} -> {}:{}, stream size {}, {} {}",
                        serviceOptional.get(), sourceIpString, sourcePort, destIpString, destPort,
                        unfinishedStreams.get(stream).size(), ack ? "ask" : "", fin ? "fin" : "");
            }
        }
    }
}
