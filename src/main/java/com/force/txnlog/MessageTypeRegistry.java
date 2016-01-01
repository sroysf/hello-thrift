package com.force.txnlog;

import com.force.thrift.*;
import org.apache.thrift.TBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sroy on 12/31/15.
 */
public class MessageTypeRegistry {

    private static final Map<Class<? extends TBase>, Short> typeMap = new HashMap<>();
    private static final List<Class<? extends TBase>> typeList = new ArrayList<>();
    private static short nextTypeId = 0;

    private MessageTypeRegistry() {
    }

    static {
        // Never, ever change the order of these unless you want serialized data to become obsolete!!

        registerType(AccessTimeUpdatedEdgeMartEvent.class);
        registerType(CoreDeleteEdgeMartEvent.class);
        registerType(DownloadedEdgeMartEvent.class);
        registerType(FoundEdgeMartEvent.class);
        registerType(ProducedEdgeMartEvent.class);
    }

    private static final void registerType(Class<? extends TBase> clazz) {
        typeMap.put(clazz, nextTypeId);
        typeList.add(clazz);
        nextTypeId++;
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

    public static final Class<? extends TBase> getClassFromId(Short typeId) {
        if ((typeId > (typeList.size()-1)) || (typeId < 0)) {
            throw new RuntimeException("Invalid class id requested from registry: " + typeId);
        }

        return typeList.get(typeId);
    }
}
