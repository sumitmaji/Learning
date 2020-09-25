package com.sum.udemy.util;

import io.fabric8.kubernetes.client.utils.IOHelpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.sum.udemy.util.Utils.interpolateString;

/**
 * Replaces template parameter values in the stream to avoid
 * parsing issues of templates with numeric expressions
 */
public class ReplaceValueStream {
    private final Map<String, String> valuesMap;

    /**
     * Returns a stream with the template parameter expressions replaced
     *
     * @param is {@link InputStream} inputstream for
     * @param valuesMap a hashmap containing parameters
     * @return returns stream with template parameter expressions replaced
     */
    public static InputStream replaceValues(InputStream is, Map<String, String> valuesMap) throws IOException {
        return new io.fabric8.kubernetes.client.utils.ReplaceValueStream(valuesMap).createInputStream(is);
    }

    private ReplaceValueStream(Map<String, String> valuesMap) {
        this.valuesMap = valuesMap;
    }

    private InputStream createInputStream(InputStream is) throws IOException {
        return new ByteArrayInputStream(
                interpolateString(IOHelpers.readFully(is), valuesMap).getBytes(StandardCharsets.UTF_8));
    }
}
