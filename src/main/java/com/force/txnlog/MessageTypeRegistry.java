package com.force.txnlog;

import com.force.ser.*;
import org.apache.thrift.TBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sroy on 12/31/15.
 */
public class MessageTypeRegistry {

    private static final Map<Class<? extends TBase>, Short> typeMap = new HashMap<>();

    private MessageTypeRegistry() {
    }

    static {
        typeMap.put(AccessTimeUpdatedEdgeMartEvent.class, createTypeId(1));
        typeMap.put(CoreDeleteEdgeMartEvent.class, createTypeId(2));
        typeMap.put(DownloadedEdgeMartEvent.class, createTypeId(3));
        typeMap.put(FoundEdgeMartEvent.class, createTypeId(4));
        typeMap.put(ProducedEdgeMartEvent.class, createTypeId(5));
    }

    // Just to make the code slightly more readable
    private static Short createTypeId(int id){
        return (short)id;
    }

    public static final Short getTypeId(Class<? extends TBase> clazz) {
        Short typeId = typeMap.get(clazz);
        if (typeId == null) {
            throw new RuntimeException(String.format("Type [%s] has not been properly registered in MessageTypeRegistry!", clazz.toString()));
        }

        return typeId;
    }
}
