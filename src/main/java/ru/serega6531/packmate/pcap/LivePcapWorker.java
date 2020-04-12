package ru.serega6531.packmate.pcap;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import ru.serega6531.packmate.service.ServicesService;
import ru.serega6531.packmate.service.StreamService;

import java.net.UnknownHostException;
import java.util.concurrent.Executors;

@Slf4j
public class LivePcapWorker extends AbstractPcapWorker {

    private final PcapNetworkInterface device;

    public LivePcapWorker(ServicesService servicesService,
                          StreamService streamService,
                          String localIpString,
                          String interfaceName) throws PcapNativeException, UnknownHostException {
        super(servicesService, streamService, localIpString);
        device = Pcaps.getDevByName(interfaceName);

        if(device == null) {
            throw new IllegalArgumentException("Device " + interfaceName + " does not exist");
        }

        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("pcap-processor").build();
        processorExecutorService = Executors.newSingleThreadExecutor(factory);
    }

    public void start() throws PcapNativeException {
        log.info("Using interface " + device.getName());
        pcap = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 100);

        try {
            log.info("Intercept started");
            pcap.loop(-1, this, loopExecutorService);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // выходим
        } catch (Exception e) {
            log.error("Error while capturing packet", e);
            stop();
        }
    }

    @SneakyThrows
    public void stop() {
        if (pcap != null && pcap.isOpen()) {
            pcap.breakLoop();
            pcap.close();
        }

        log.info("Intercept stopped");
    }

}