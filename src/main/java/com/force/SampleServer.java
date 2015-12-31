package com.force;

import com.force.ser.simple.CrawlingService;
import com.force.ser.simple.Item;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by sroy on 12/30/15.
 */
public class SampleServer implements CrawlingService.Iface {

    private static Logger logger = LoggerFactory.getLogger(SampleServer.class);

    @Override
    public void write(List<Item> items) throws TException {
        logger.info("====== SERVER START ======");
        for (Item item : items) {
            logger.info(item.getId() + " -> " + item.getContent());
        }
        logger.info("====== SERVER END ======");
    }

    public void start() {

        try {
            CrawlingService.Processor<SampleServer> processor = new CrawlingService.Processor(this);

            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting the simple server...");
            server.serve();

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
