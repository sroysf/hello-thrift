package com.force;

import com.force.thrift.*;
import com.force.txnlog.MessageLog;
import com.force.txnlog.MessageTypeRegistry;
import com.force.txnlog.mem.MemoryLog;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by sroy on 12/30/15.
 */
public class EdgeServer implements EdgeControlAPI.Iface {

    private static Logger logger = LoggerFactory.getLogger(EdgeServer.class);

    private MessageLog messageLog = new MemoryLog();
    private final String uniqueId = UUID.randomUUID().toString();

    @Override
    public TransactionList playbackTransactions(int start, int maxTransactions) throws TException {
        try {
            List<MessageEnvelope> msgEnvelopes = messageLog.getMessages(start, maxTransactions);
            TransactionList tlist = new TransactionList();
            tlist.setEnvelopes(msgEnvelopes);
            tlist.setUniqueLogId(uniqueId);
            return tlist;
        } catch (Exception e) {
            throw new TException("", e);
        }
    }

    public void start() {

        startDemoProducerThread();

        try {
            EdgeControlAPI.Processor<EdgeServer> processor = new EdgeControlAPI.Processor(this);

            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting edge server...");
            server.serve();

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    private void startDemoProducerThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeMessageLoop();
            }
        }).start();
    }

    private void writeMessageLoop() {
        TBase msg = null;

        while (true) {
            String emId = "EM:" + (int)(25000 * Math.random());
            int randomMessageType = (int)(5 * Math.random());
            switch (randomMessageType){
                case 0 :
                    AccessTimeUpdatedEdgeMartEvent atev = new AccessTimeUpdatedEdgeMartEvent();
                    atev.setEmId(emId);
                    atev.setLastAccessTime(new Date().getTime());
                    msg = atev;
                    break;
                case 1 :
                    CoreDeleteEdgeMartEvent cdev = new CoreDeleteEdgeMartEvent();
                    cdev.setEmId(emId);
                    msg = cdev;
                    break;
                case 2 :
                    DownloadedEdgeMartEvent dldev = new DownloadedEdgeMartEvent();
                    dldev.setEmId(emId);
                    dldev.setLastAccessTime(new Date().getTime());
                    msg = dldev;
                    break;
                case 3 :
                    FoundEdgeMartEvent fev = new FoundEdgeMartEvent();
                    fev.setEmId(emId);
                    fev.setLastAccessTime(new Date().getTime());
                    msg = fev;
                    break;
                case 4 :
                    ProducedEdgeMartEvent pev = new ProducedEdgeMartEvent();
                    pev.setEmId(emId);
                    msg = pev;
                    break;
            }

            try {
                TimeUnit.SECONDS.sleep(1);
                this.messageLog.append(msg);
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
    }
}
