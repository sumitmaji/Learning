package com.sum.udemy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Serialization {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    static {
//        JSON_MAPPER.registerModule(new JavaTimeModule());
    }
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final String DOCUMENT_DELIMITER = "---";

    public static ObjectMapper jsonMapper() {
        return JSON_MAPPER;
    }

    public static ObjectMapper yamlMapper() {
        return YAML_MAPPER;
    }

    public static <T> String asJson(T object) throws UdemyClientException {
        try {
            return JSON_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw UdemyClientException.launderThrowable(e);
        }
    }

    public static <T> String asYaml(T object) throws UdemyClientException {
        try {
            return YAML_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw UdemyClientException.launderThrowable(e);
        }
    }

    /**
     * Unmarshals a stream.
     *
     * @param is    The {@link InputStream}.
     * @param <T>   The target type.
     *
     * @return returns de-serialized object
     * @throws UdemyClientException UdemyClientException
     */
    public static <T> T unmarshal(InputStream is) throws UdemyClientException {
        return unmarshal(is, JSON_MAPPER);
    }

    /**
     * Unmarshals a stream optionally performing placeholder substitution to the stream.
     * @param is    The {@link InputStream}.
     * @param parameters  A {@link Map} with parameters for placeholder substitution.
     * @param <T>   The target type.
     * @return returns returns de-serialized object
     * @throws UdemyClientException UdemyClientException
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(InputStream is, Map<String, String> parameters) {
        String specFile = readSpecFileFromInputStream(is);
        if (containsMultipleDocuments(specFile)) {
            return (T) getUdemyResourceList(parameters, specFile);
        }
        return unmarshal(new ByteArrayInputStream(specFile.getBytes()), JSON_MAPPER, parameters);
    }

    /**
     * Unmarshals a stream.
     * @param is      The {@link InputStream}.
     * @param mapper  The {@link ObjectMapper} to use.
     * @param <T>     The target type.
     * @return returns de-serialized object
     */
    public static <T> T unmarshal(InputStream is, ObjectMapper mapper) {
        return unmarshal(is, mapper, Collections.emptyMap());
    }

    /**
     * Unmarshals a stream optionally performing placeholder substitution to the stream.
     * @param is          The {@link InputStream}.
     * @param mapper      The {@link ObjectMapper} to use.
     * @param parameters  A {@link Map} with parameters for placeholder substitution.
     * @param <T>         The target type.
     * @return returns de-serialized object
     */
    public static <T> T unmarshal(InputStream is, ObjectMapper mapper, Map<String, String> parameters) {
        try (
                InputStream wrapped = parameters != null && !parameters.isEmpty() ? ReplaceValueStream.replaceValues(is, parameters) : is;
                BufferedInputStream bis = new BufferedInputStream(wrapped)
        ) {
            bis.mark(-1);
            int intch;
            do {
                intch = bis.read();
            } while (intch > -1 && Character.isWhitespace(intch));
            bis.reset();

            if (intch != '{') {
                mapper = YAML_MAPPER;
            }
            return mapper.readerFor(UdemyResource.class).readValue(bis);
        } catch (IOException e) {
            throw UdemyClientException.launderThrowable(e);
        }
    }

    /**
     * Unmarshals a {@link String}
     * @param str   The {@link String}.
     * @param type  The target type.
     * @param <T>   template argument denoting type
     * @return returns de-serialized object
     */
    public static<T> T unmarshal(String str, final Class<T> type) {
        return unmarshal(str, type, Collections.emptyMap());
    }

    /**
     * Unmarshals a {@link String} optionally performing placeholder substitution to the String.
     * @param str         The {@link String}.
     * @param type        The target type.
     * @param <T>         Template argument denoting type
     * @param parameters  A hashmap containing parameters
     *
     * @return returns de-serialized object
     * @throws UdemyClientException UdemyClientException
     */
    public static <T> T unmarshal(String str, final Class<T> type, Map<String, String> parameters) throws UdemyClientException {
        try (InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))) {
            return unmarshal(is, new TypeReference<T>() {
                @Override
                public Type getType() {
                    return type;
                }
            }, parameters);
        } catch (IOException e) {
            throw UdemyClientException.launderThrowable(e);
        }
    }

    /**
     * Unmarshals an {@link InputStream}.
     * @param is              The {@link InputStream}.
     * @param type            The type.
     * @param <T>           Template argument denoting type
     * @return returns de-serialized object
     */
    public static <T> T unmarshal(InputStream is, final Class<T> type) {
        return unmarshal(is, type, Collections.emptyMap());
    }

    /**
     * Unmarshals an {@link InputStream} optionally performing placeholder substitution to the stream.
     * @param is              The {@link InputStream}.
     * @param type            The type.
     * @param parameters      A {@link Map} with parameters for placeholder substitution.
     * @param <T>             Template argument denoting type
     * @return returns de-serialized object
     * @throws UdemyClientException UdemyClientException
     */
    public static <T> T unmarshal(InputStream is, final Class<T> type, Map<String, String> parameters) throws UdemyClientException {
        return unmarshal(is, new TypeReference<T>() {
            @Override
            public Type getType() {
                return type;
            }
        }, parameters);
    }


    /**
     * Unmarshals an {@link InputStream}.
     * @param is            The {@link InputStream}.
     * @param type          The {@link TypeReference}.
     * @param <T>           Template argument denoting type
     * @return returns de-serialized object
     */
    public static <T> T unmarshal(InputStream is, TypeReference<T> type) {
        return unmarshal(is, type, Collections.emptyMap());
    }

    /**
     * Unmarshals an {@link InputStream} optionally performing placeholder substitution to the stream.
     *
     * @param is            The {@link InputStream}.
     * @param type          The {@link TypeReference}.
     * @param parameters    A {@link Map} with parameters for placeholder substitution.
     * @param <T>           Template argument denoting type
     *
     * @return returns de-serialized object
     * @throws UdemyClientException UdemyClientException
     */
    public static <T> T unmarshal(InputStream is, TypeReference<T> type, Map<String, String> parameters) {
        try (
                InputStream wrapped = parameters != null && !parameters.isEmpty() ? ReplaceValueStream.replaceValues(is, parameters) : is;
                BufferedInputStream bis = new BufferedInputStream(wrapped)
        ) {
            bis.mark(-1);
            int intch;
            do {
                intch = bis.read();
            } while (intch > -1 && Character.isWhitespace(intch));
            bis.reset();

            ObjectMapper mapper = JSON_MAPPER;
            if (intch != '{') {
                mapper = YAML_MAPPER;
            }
            return mapper.readValue(bis, type);
        } catch (IOException e) {
            throw UdemyClientException.launderThrowable(e);
        }
    }


    private static List<UdemyResource> getUdemyResourceList(Map<String, String> parameters, String specFile) {
        return splitSpecFile(specFile).stream().filter(Serialization::validate)
                .map(document ->
                        (UdemyResource) Serialization.unmarshal(new ByteArrayInputStream(document.getBytes()), parameters))
                .collect(Collectors.toList());
    }

    static boolean containsMultipleDocuments(String specFile) {
        final long validDocumentCount = splitSpecFile(specFile).stream().filter(Serialization::validate)
                .count();
        return validDocumentCount > 1;
    }

    private static List<String> splitSpecFile(String aSpecFile) {
        final List<String> documents = new ArrayList<>();
        final StringBuilder documentBuilder = new StringBuilder();
        for (String line : aSpecFile.split("\r?\n")) {
            if (line.startsWith(DOCUMENT_DELIMITER)) {
                documents.add(documentBuilder.toString());
                documentBuilder.setLength(0);
            } else {
                documentBuilder.append(line).append(System.lineSeparator());
            }
        }
        if (documentBuilder.length() > 0) {
            documents.add(documentBuilder.toString());
        }
        return documents;
    }

    private static boolean validate(String document) {
        Matcher keyValueMatcher = Pattern.compile("(\\S+):\\s(\\S*)(?:\\b(?!:)|$)").matcher(document);
        return !document.isEmpty() && keyValueMatcher.find();
    }

    private static String readSpecFileFromInputStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read InputStream." + e);
        }
    }

    public static String prettyPrint(Object obj) throws JsonProcessingException {
        return JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
