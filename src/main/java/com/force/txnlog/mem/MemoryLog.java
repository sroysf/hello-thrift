package com.force.txnlog.mem;

import com.force.ser.CoreDeleteEdgeMartEvent;
import com.force.ser.MessageEnvelope;
import com.force.ser.ProducedEdgeMartEvent;
import com.force.txnlog.MessageTypeRegistry;
import com.force.txnlog.MessageLog;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a purely in-memory implementation of the transaction log
 */
public class MemoryLog implements MessageLog {

    private TSerializer serializer;
    private TDeserializer deserializer;
    private List<MessageEnvelope> entryList;

        public MemoryLog() {
        TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
        serializer = new TSerializer(protocolFactory);
        deserializer = new TDeserializer(protocolFactory);
        entryList = new ArrayList<>();
    }

    @Override
    public synchronized void append(TBase msg) throws Exception {

        // This is an in-memory proof of concept for simplicity, but using this typeId and a byte array,
        // we can easily imagine the minor tweaks needed to do all of this in a file instead.

        Short typeId = MessageTypeRegistry.getTypeId(msg.getClass());
        MessageEnvelope msgEnvelope = new MessageEnvelope();
        msgEnvelope.setTypeId(typeId);
        msgEnvelope.setRawMsgBytes(serializer.serialize(msg));
        entryList.add(msgEnvelope);
    }

    public static void main(String[] args) throws Exception {

        MemoryLog mlog = new MemoryLog();

        CoreDeleteEdgeMartEvent cde = new CoreDeleteEdgeMartEvent();
        cde.setEmId("ABC");
        mlog.append(cde);

        ProducedEdgeMartEvent pde = new ProducedEdgeMartEvent();
        pde.setEmId("XYZ");
        mlog.append(pde);

        System.out.println(mlog);
    }
}
