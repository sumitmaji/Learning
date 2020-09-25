package com.sum.udemy.util;

import java.util.Map;

public interface UdemyResourceMappingProvider {
    Map<String, Class<? extends UdemyResource>> getMappings();
}
