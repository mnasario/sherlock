package com.sherlock.game.core.domain.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.core.exception.PayloadConvertException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Envelop {

    @JsonIgnore
    private ObjectMapper mapper;
    private Type type;
    private Subject subject;
    private String payload;

    @Builder
    public Envelop(ObjectMapper mapper, Type type, Subject subject) {
        this.mapper = mapper;
        this.type = type;
        this.subject = subject;
    }

    public <T> Envelop putPayload(T object) {
        try {
            payload = mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new PayloadConvertException(e.getMessage(), e);
        }
        return this;
    }
}