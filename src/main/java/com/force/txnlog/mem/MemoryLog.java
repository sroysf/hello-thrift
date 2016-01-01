package com.force.txnlog.mem;

import com.force.thrift.CoreDeleteEdgeMartEvent;
import com.force.thrift.MessageEnvelope;
import com.force.thrift.ProducedEdgeMartEvent;
import com.force.txnlog.MessageTypeRegistry;
import com.force.txnlog.MessageLog;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Provides a purely in-memory implementation of the transaction log
 */
public class MemoryLog implements MessageLog {

    private TSerializer serializer;
    private TDeserializer deserializer;
    private List<MessageEnvelope> entryList;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public MemoryLog() {
        TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
        serializer = new TSerializer(protocolFactory);
        deserializer = new TDeserializer(protocolFactory);
        entryList = new ArrayList<>();
    }

    @Override
    public void append(TBase msg) throws Exception {

        try {

            this.lock.writeLock().lock();

            // This is an in-memory proof of concept for simplicity, but using this typeId and a byte array,
            // we can easily imagine the minor tweaks needed to do all of this in a file instead.

            Short typeId = MessageTypeRegistry.getTypeId(msg.getClass());
            MessageEnvelope msgEnvelope = new MessageEnvelope();
            msgEnvelope.setTypeId(typeId);
            msgEnvelope.setRawMsgBytes(serializer.serialize(msg));
            entryList.add(msgEnvelope);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public List<MessageEnvelope> getMessages(int start, int maxMessages) {

        List<MessageEnvelope> msgs = new ArrayList<>();

        try {
            this.lock.readLock().lock();

            if (start < 0) {
                throw new RuntimeException("Invalid start index: " + start);
            }

            int end = start + maxMessages;
            if (end > this.entryList.size()) {
                end = this.entryList.size();
            }

            for (int i = start; i < end; i++) {
                msgs.add(this.entryList.get(i));
            }
        } finally {
            this.lock.readLock().unlock();
        }

        return msgs;
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

        List<MessageEnvelope> messages = mlog.getMessages(1, 1);
        System.out.println(messages);
    }
}
