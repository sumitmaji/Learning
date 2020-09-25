package com.sum.udemy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.utils.Serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 */
public class IOHelpers {

    public static String readFully(InputStream in) throws IOException {
        Reader r = new BufferedReader(new InputStreamReader(in));
        return readFully(r);
    }

    public static String readFully(Reader r) throws IOException {
        try (StringWriter w = new StringWriter()) {
            copy(r, w);
            return w.toString();
        }
    }


    private static void copy(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[8192];
        int len;
        for (; ; ) {
            len = reader.read(buffer);
            if (len > 0) {
                writer.write(buffer, 0, len);
            } else {
                writer.flush();
                break;
            }
        }
    }

    public static boolean isJSONValid(String json) {
        try{
            ObjectMapper objectMapper = io.fabric8.kubernetes.client.utils.Serialization.jsonMapper();
            objectMapper.readTree(json);
        } catch(JsonProcessingException e){
            return false;
        }
        return true;
    }

    public static String convertYamlToJson(String yaml) throws IOException {
        ObjectMapper yamlReader = io.fabric8.kubernetes.client.utils.Serialization.yamlMapper();
        Object obj = yamlReader.readValue(yaml, Object.class);

        ObjectMapper jsonWriter = Serialization.jsonMapper();
        return jsonWriter.writeValueAsString(obj);
    }

}
