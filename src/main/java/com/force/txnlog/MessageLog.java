package com.force.txnlog;

import com.force.thrift.MessageEnvelope;
import org.apache.thrift.TBase;

import java.util.List;

/**
 * Created by sroy on 12/31/15.
 */
public interface MessageLog {

    void append(TBase msg) throws Exception;

    List<MessageEnvelope> getMessages(int start, int maxMessages) throws Exception;
}
