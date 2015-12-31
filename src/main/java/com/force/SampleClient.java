package com.force;

import com.force.ser.simple.CrawlingService;
import com.force.ser.simple.Item;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sroy on 12/30/15.
 */
public class SampleClient {

    private static Logger logger = LoggerFactory.getLogger(SampleClient.class);

    private TSerializer serializer;
    private TDeserializer deserializer;

    public void start() {

        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            CrawlingService.Client client = new CrawlingService.Client(protocol);


            TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
            serializer = new TSerializer(protocolFactory);
            deserializer = new TDeserializer(protocolFactory);


            long i=0;
            List<Item> items = new ArrayList<>();
            while (i < 50) {
                Item item1 = demoSerialization(i, "Msg:"+i);
                i++;
                Item item2 = demoSerialization(i, "Msg:"+i);
                i++;

                items.add(item1);
                items.add(item2);

                client.write(items);

                items.clear();
                TimeUnit.SECONDS.sleep(2);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Item demoSerialization(long id, String contents) throws TException {
        Item item = new Item();
        item.setId(id);
        item.setContent(contents);

        byte[] bytes = serializer.serialize(item);

        // Now deserialize it back from bytes
        Item deserItem = new Item();
        deserializer.deserialize(deserItem, bytes);

        logger.info("Client (de)serialized : [{} -> {}]", deserItem.getId(), deserItem.getContent());

        return deserItem;
    }
}
