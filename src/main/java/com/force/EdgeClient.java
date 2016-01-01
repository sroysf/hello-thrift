package com.force;

import com.force.thrift.*;
import com.force.txnlog.MessageTypeRegistry;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sroy on 12/30/15.
 */
public class EdgeClient {

    private static Logger logger = LoggerFactory.getLogger(EdgeClient.class);

    private TSerializer serializer;
    private TDeserializer deserializer;

    public void start() {

        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            EdgeControlAPI.Client client = new EdgeControlAPI.Client(protocol);

            TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
            deserializer = new TDeserializer(protocolFactory);

            int checkPoint = 0;
            String knownUniqueId = null;

            while (true) {
                TimeUnit.SECONDS.sleep(5);

                TransactionList transactionList = client.playbackTransactions(checkPoint, 100);
                if (knownUniqueId == null) {
                    knownUniqueId = transactionList.getUniqueLogId();
                } else if (!transactionList.getUniqueLogId().equals(knownUniqueId)) {
                    logger.info("\nClient detected server restart, new transaction log!\n");
                    checkPoint = 0;
                    knownUniqueId = transactionList.getUniqueLogId();
                    continue;
                }


                for (MessageEnvelope messageEnvelope : transactionList.getEnvelopes()) {
                    short typeId = messageEnvelope.getTypeId();
                    Class<? extends TBase> msgClass = MessageTypeRegistry.getClassFromId(typeId);
                    TBase genericMessage = msgClass.newInstance();
                    deserializer.deserialize(genericMessage, messageEnvelope.getRawMsgBytes());

                    processMessage(checkPoint, genericMessage);
                    checkPoint++;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(int checkPoint, TBase genericMessage) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

        if (genericMessage instanceof AccessTimeUpdatedEdgeMartEvent) {
            AccessTimeUpdatedEdgeMartEvent atevent = (AccessTimeUpdatedEdgeMartEvent)genericMessage;
            logger.info(String.format("%d - AccessTimeUpdatedEdgeMartEvent - EmId:[%s] AccessTime:[%s]", checkPoint, atevent.getEmId(),
                    sdf.format(new Date(atevent.getLastAccessTime()))));
        } else if (genericMessage instanceof CoreDeleteEdgeMartEvent) {
            CoreDeleteEdgeMartEvent cdevent = (CoreDeleteEdgeMartEvent)genericMessage;
            logger.info(String.format("%d - CoreDeleteEdgeMartEvent - EmId:[%s]", checkPoint, cdevent.getEmId()));
        } else if (genericMessage instanceof DownloadedEdgeMartEvent) {
            DownloadedEdgeMartEvent dlevent = (DownloadedEdgeMartEvent)genericMessage;
            logger.info(String.format("%d - DownloadedEdgeMartEvent - EmId:[%s] AccessTime:[%s]", checkPoint, dlevent.getEmId(),
                    sdf.format(new Date(dlevent.getLastAccessTime()))));
        } else if (genericMessage instanceof FoundEdgeMartEvent) {
            FoundEdgeMartEvent femevent = (FoundEdgeMartEvent)genericMessage;
            logger.info(String.format("%d - FoundEdgeMartEvent - EmId:[%s] AccessTime:[%s]", checkPoint, femevent.getEmId(),
                    sdf.format(new Date(femevent.getLastAccessTime()))));
        } else if (genericMessage instanceof ProducedEdgeMartEvent) {
            ProducedEdgeMartEvent peevent = (ProducedEdgeMartEvent)genericMessage;
            logger.info(String.format("%d - ProducedEdgeMartEvent - EmId:[%s]", checkPoint, peevent.getEmId()));
        } else {
            logger.warn("{} - Unknown message type: " + genericMessage.getClass().getName());
        }
    }

    public static void main(String[] args) throws Exception {
        EdgeClient client = new EdgeClient();

        TimeUnit.SECONDS.sleep(2);

        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                client.start();
            }
        });

        logger.info("Starting client, following server transaction log...");
        clientThread.start();
        clientThread.join();
    }
}
