package com.dnp.util;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueType;
import org.msgpack.value.impl.ImmutableBooleanValueImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p>
 * Remark   : MsgPack工具集
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 6/22/2016
 */
public class MsgPackUtil {

    public static Map<String, Object> toMap(byte[] bytes) {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
        try {
            return toMap(unpacker.unpackValue().asMapValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Map<String, Object> toMap(MapValue mapValue) {
        Map<String, Object> result = null;

        try {
            Map<Value, Value> map = mapValue.map();
            result = new HashMap<>(map.size());
            for (Map.Entry<Value, Value> entry : map.entrySet()) {
                String key = entry.getKey().asStringValue().asString();
                Value value = entry.getValue();
                result.put(key, unpackObj(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Object> toList(byte[] bytes) {
        List<Object> result = null;

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
        try {
            List<Value> list = unpacker.unpackValue().asArrayValue().list();
            result = new ArrayList<>(list.size());
            for (Value value : list) {

                result.add(unpackObj(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Object> toList(ArrayValue arrayValue) {
        List<Object> result = null;

        try {
            List<Value> list = arrayValue.list();
            result = new ArrayList<>(list.size());
            for (Value value : list) {

                result.add(unpackObj(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Map<String, Object>> toListMap(ArrayValue arrayValue) {
        List<Value> values = arrayValue.list();
        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (Value value : values) {
            Map<String, Object> propsUseMap = MsgPackUtil.toMap(value.asMapValue());
            mapArrayList.add(propsUseMap);
        }
        return mapArrayList;
    }

    public static <K, V> byte[] mapToByte(Map<K, V> map) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packMapHeader(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            packer(packer, entry.getKey());
            packer(packer, entry.getValue());
        }
        packer.close();
        return out.toByteArray();
    }

    public static <T> byte[] listToByte(Collection<T> list) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packArrayHeader(list.size());
        for (T o : list) {
            packer(packer, o);
        }
        packer.close();
        return out.toByteArray();
    }

    public static <K, T> byte[] listMapToByte(Collection<Map<K, T>> list) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packArrayHeader(list.size());
        for (Map<K, T> o : list) {
            byte[] mapByte = mapToByte(o);
            packer.packBinaryHeader(mapByte.length);
            packer.writePayload(mapByte);
        }
        packer.close();
        return out.toByteArray();
    }

    private static void packer(MessagePacker packer, Object o) throws IOException {
        if (o instanceof Integer) {
            packer.packInt((Integer) o);
        } else if (o instanceof String) {
            packer.packString((String) o);
        } else if (o instanceof Boolean) {
            packer.packBoolean((Boolean) o);
        } else if (o instanceof Long) {
            packer.packLong((Long) o);
        } else if (o instanceof Float) {
            packer.packFloat((Float) o);
        } else if (o instanceof Date) {
            long time = ((Date) o).getTime();
            packer.packString(String.valueOf(time));
        } else if (o.getClass().isArray()) {
            packer.writePayload((byte[]) o);
        }
    }

    private static Object unpackObj(Value value) throws IOException {
        ValueType valueType = value.getValueType();
        Object obj;
        switch (valueType) {
            case NIL:
                obj = null;
                break;
            case BOOLEAN:
                obj = ((ImmutableBooleanValueImpl) value).getBoolean();
                break;
            case INTEGER:
                if (value.asIntegerValue().isIntegerValue()) {
                    obj = value.asIntegerValue().asInt();
                } else if (value.asIntegerValue().isInLongRange()) {
                    obj = value.asIntegerValue().asLong();
                } else {
                    throw new IOException();
                }
                break;
            case FLOAT:
                obj = value.asFloatValue().toFloat();
                break;
            case STRING: {
                obj = value.asStringValue().asString();
                break;
            }
            case BINARY: {
                obj = value.asBinaryValue().asByteArray();
                break;
            }
            case MAP: {
                obj = value.asMapValue().map();
                break;
            }
            default:
                throw new IOException("value type is not found : " + valueType);
        }
        return obj;
    }


}
