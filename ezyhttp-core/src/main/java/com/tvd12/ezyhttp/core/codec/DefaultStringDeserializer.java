package com.tvd12.ezyhttp.core.codec;

import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.io.EzyDataConverter;
import com.tvd12.ezyfox.io.EzyDates;
import com.tvd12.ezyfox.io.EzyStrings;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class DefaultStringDeserializer implements StringDeserializer {

    protected final Map<Class<?>, StringMapper> mappers;

    public DefaultStringDeserializer() {
        this.mappers = defaultMappers();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(
        String value,
        Class<T> outType,
        Class<?> genericType
    ) throws IOException {
        if (genericType != null) {
            if (Set.class.isAssignableFrom(outType)) {
                return (T) stringToSet(value, genericType);
            } else if (Collection.class.isAssignableFrom(outType)) {
                return (T) stringToList(value, genericType);
            }
        }
        return deserialize(value, outType);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T deserialize(
        String value,
        Class<T> outType
    ) throws IOException {
        StringMapper mapper = mappers.get(outType);
        if (mapper == null) {
            if (value == null) {
                return null;
            }
            if (outType.isEnum()) {
                return stringToEnum(value, outType);
            }
            throw new IOException("has no deserializer for: " + outType.getName());
        }
        try {
            return (T) mapper.apply(value);
        } catch (Exception e) {
            throw new IOException(
                "can't deserialize value: " + value + " to: " + outType.getName(),
                e
            );
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T deserializeOrNull(
        String value,
        Class<T> outType
    ) {
        StringMapper mapper = mappers.get(outType);
        if (mapper == null) {
            if (value == null) {
                return null;
            }
            if (outType.isEnum()) {
                return stringToEnum(value, outType);
            }
        } else {
            try {
                return (T) mapper.apply(value);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> T stringToEnum(String value, Class<T> outType) {
        try {
            return (T) Enum.valueOf((Class) outType, value);
        } catch (IllegalArgumentException e) {
            return (T) Enum.valueOf((Class) outType, value.toUpperCase());
        }
    }

    protected Map<Class<?>, StringMapper> defaultMappers() {
        Map<Class<?>, StringMapper> map = new HashMap<>();
        map.put(String.class, v -> v);
        map.put(boolean.class, Boolean::parseBoolean);
        map.put(byte.class, v -> v != null ? Byte.parseByte(v) : (byte) 0);
        map.put(char.class, EzyDataConverter::stringToChar);
        map.put(double.class, v -> v != null ? Double.parseDouble(v) : 0.0D);
        map.put(float.class, v -> v != null ? Float.parseFloat(v) : 0.0F);
        map.put(int.class, v -> v != null ? Integer.parseInt(v) : 0);
        map.put(long.class, v -> v != null ? Long.parseLong(v) : 0L);
        map.put(short.class, v -> v != null ? Short.parseShort(v) : (short) 0);

        map.put(Boolean.class, v -> v != null ? Boolean.valueOf(v) : null);
        map.put(Byte.class, v -> v != null ? Byte.valueOf(v) : null);
        map.put(Character.class, v -> v != null ? EzyDataConverter.stringToChar(v) : null);
        map.put(Double.class, v -> v != null ? Double.valueOf(v) : null);
        map.put(Float.class, v -> v != null ? Float.valueOf(v) : null);
        map.put(Integer.class, v -> v != null ? Integer.valueOf(v) : null);
        map.put(Long.class, v -> v != null ? Long.valueOf(v) : null);
        map.put(Short.class, v -> v != null ? Short.valueOf(v) : null);

        map.put(String[].class, this::stringToStringArray);
        map.put(List.class, this::stringToList);
        map.put(Set.class, this::stringToSet);

        map.put(boolean[].class, this::stringToPrimitiveBoolean);
        map.put(byte[].class, this::stringToPrimitiveByte);
        map.put(char[].class, this::stringToPrimitiveChar);
        map.put(double[].class, this::stringToPrimitiveDouble);
        map.put(float[].class, this::stringToPrimitiveFloat);
        map.put(int[].class, this::stringToPrimitiveInteger);
        map.put(long[].class, this::stringToPrimitiveLong);
        map.put(short[].class, this::stringToPrimitiveShort);

        map.put(Boolean[].class, this::stringToWrapperBoolean);
        map.put(Byte[].class, this::stringToWrapperByte);
        map.put(Character[].class, this::stringToWrapperChar);
        map.put(Double[].class, this::stringToWrapperDouble);
        map.put(Float[].class, this::stringToWrapperFloat);
        map.put(Integer[].class, this::stringToWrapperInteger);
        map.put(Long[].class, this::stringToWrapperLong);
        map.put(Short[].class, this::stringToWrapperShort);

        map.put(Date.class, v -> v != null ? new Date(Long.parseLong(v)) : null);
        map.put(Instant.class, v -> v != null ? Instant.ofEpochMilli(Long.parseLong(v)) : null);
        map.put(LocalDate.class, v -> v != null ? EzyDates.parseDate(v) : null);
        map.put(LocalTime.class, v -> v != null ? EzyDates.parseTime(v) : null);
        map.put(LocalDateTime.class, v -> v != null ? EzyDates.parseDateTime(v) : null);
        map.put(BigInteger.class, v -> v != null ? new BigInteger(v) : null);
        map.put(BigDecimal.class, v -> v != null ? new BigDecimal(v) : null);
        return map;
    }

    // =============== array, collection ===============
    protected String[] stringToStringArray(String value) {
        if (EzyStrings.isEmpty(value)) {
            return new String[0];
        }
        String[] array = value.split(",");
        for (int i = 0; i < array.length; ++i) {
            array[i] = array[i].trim();
        }
        return array;
    }

    protected List<String> stringToList(String value) {
        return Lists.newArrayList(stringToStringArray(value));
    }

    protected <T> List<T> stringToList(
        String value,
        Class<T> itemType
    ) throws IOException {
        if (value == null) {
            return Collections.emptyList();
        }
        String[] array = stringToStringArray(value);
        List<T> answer = new ArrayList<>();
        for (String s : array) {
            answer.add(deserialize(s, itemType));
        }
        return answer;
    }

    protected Set<String> stringToSet(String value) {
        return Sets.newHashSet(stringToStringArray(value));
    }

    protected <T> Set<T> stringToSet(
        String value,
        Class<T> itemType
    ) throws IOException {
        if (value == null) {
            return Collections.emptySet();
        }
        String[] array = stringToStringArray(value);
        Set<T> answer = new HashSet<>();
        for (String s : array) {
            answer.add(deserialize(s, itemType));
        }
        return answer;
    }

    // =============== primitive array ===============
    protected boolean[] stringToPrimitiveBoolean(String value) {
        String[] array = stringToStringArray(value);
        boolean[] answer = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Boolean.parseBoolean(array[i]);
        }
        return answer;
    }

    protected byte[] stringToPrimitiveByte(String value) {
        String[] array = stringToStringArray(value);
        byte[] answer = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Byte.parseByte(array[i]);
        }
        return answer;
    }

    protected char[] stringToPrimitiveChar(String value) {
        String[] array = stringToStringArray(value);
        char[] answer = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = array[i].charAt(0);
        }
        return answer;
    }

    protected double[] stringToPrimitiveDouble(String value) {
        String[] array = stringToStringArray(value);
        double[] answer = new double[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Double.parseDouble(array[i]);
        }
        return answer;
    }

    protected float[] stringToPrimitiveFloat(String value) {
        String[] array = stringToStringArray(value);
        float[] answer = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Float.parseFloat(array[i]);
        }
        return answer;
    }

    protected int[] stringToPrimitiveInteger(String value) {
        String[] array = stringToStringArray(value);
        int[] answer = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Integer.parseInt(array[i]);
        }
        return answer;
    }

    protected long[] stringToPrimitiveLong(String value) {
        String[] array = stringToStringArray(value);
        long[] answer = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Long.parseLong(array[i]);
        }
        return answer;
    }

    protected short[] stringToPrimitiveShort(String value) {
        String[] array = stringToStringArray(value);
        short[] answer = new short[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Short.parseShort(array[i]);
        }
        return answer;
    }

    // =============== wrapper array ===============
    protected Boolean[] stringToWrapperBoolean(String value) {
        String[] array = stringToStringArray(value);
        Boolean[] answer = new Boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Boolean.valueOf(array[i]);
        }
        return answer;
    }

    protected Byte[] stringToWrapperByte(String value) {
        String[] array = stringToStringArray(value);
        Byte[] answer = new Byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Byte.valueOf(array[i]);
        }
        return answer;
    }

    protected Character[] stringToWrapperChar(String value) {
        String[] array = stringToStringArray(value);
        Character[] answer = new Character[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = array[i].charAt(0);
        }
        return answer;
    }

    protected Double[] stringToWrapperDouble(String value) {
        String[] array = stringToStringArray(value);
        Double[] answer = new Double[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Double.valueOf(array[i]);
        }
        return answer;
    }

    protected Float[] stringToWrapperFloat(String value) {
        String[] array = stringToStringArray(value);
        Float[] answer = new Float[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Float.valueOf(array[i]);
        }
        return answer;
    }

    protected Integer[] stringToWrapperInteger(String value) {
        String[] array = stringToStringArray(value);
        Integer[] answer = new Integer[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Integer.valueOf(array[i]);
        }
        return answer;
    }

    protected Long[] stringToWrapperLong(String value) {
        String[] array = stringToStringArray(value);
        Long[] answer = new Long[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Long.valueOf(array[i]);
        }
        return answer;
    }

    protected Short[] stringToWrapperShort(String value) {
        String[] array = stringToStringArray(value);
        Short[] answer = new Short[array.length];
        for (int i = 0; i < array.length; ++i) {
            answer[i] = Short.valueOf(array[i]);
        }
        return answer;
    }
}
