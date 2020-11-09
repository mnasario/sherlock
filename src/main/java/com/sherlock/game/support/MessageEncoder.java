package com.sherlock.game.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.core.domain.message.Envelop;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

@Slf4j
public class MessageEncoder implements Encoder.Text<Envelop> {

    private static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String encode(Envelop message) throws EncodeException {
        try {
            String messageAsString = MAPPER.writeValueAsString(message);
            log.debug("Enconding message: {}", messageAsString);
            return messageAsString;
        } catch (JsonProcessingException e) {
            throw new EncodeException(message, e.getMessage(), e);
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}