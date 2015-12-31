package com.force.txnlog;

/**
 * Created by sroy on 12/31/15.
 */
public class TxnId {
    String uniqueId;
    long id;

    public TxnId(String uniqueId, long id) {
        this.uniqueId = uniqueId;
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public long getId() {
        return id;
    }
}
