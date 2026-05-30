package org.immregistries.virs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON serialization using Jackson.
 */
public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {
        // prevent instantiation
    }

    /**
     * Serializes an object to a JSON string.
     *
     * @param value the object to serialize
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    public static String toJson(Object value) throws JsonProcessingException {
        return MAPPER.writeValueAsString(value);
    }

    /**
     * Returns the shared {@link ObjectMapper} instance.
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
