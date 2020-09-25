package com.sum.udemy.util;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;

@JsonDeserialize(using = UdemyDeserializer.class)
public interface UdemyResource extends Serializable {
}
