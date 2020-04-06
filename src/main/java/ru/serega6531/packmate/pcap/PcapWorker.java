package ru.serega6531.packmate.pcap;

import org.pcap4j.core.PcapNativeException;
import ru.serega6531.packmate.model.enums.Protocol;

public interface PcapWorker {

    void start() throws PcapNativeException;
    void stop();

    /**
     * Выполняется в вызывающем потоке
     */
    void closeAllStreams(Protocol protocol);

    /**
     * Выполняется в потоке обработчика
     */
    int closeTimeoutStreams(Protocol protocol, long timeoutMillis);

}
