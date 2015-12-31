package com.force.txnlog;

import org.apache.thrift.TBase;

/**
 * Created by sroy on 12/31/15.
 */
public interface MessageLog {

    void append(TBase msg) throws Exception;


}
