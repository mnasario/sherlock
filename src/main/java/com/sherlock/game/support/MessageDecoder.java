package com.sherlock.game.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.core.domain.message.Envelop;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

@Slf4j
public class MessageDecoder implements Decoder.Text<Envelop> {

    private static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Envelop decode(String message) throws DecodeException {
        log.debug("Message raw: " + message);
        try {
            return MAPPER.readValue(message, Envelop.class);
        } catch (JsonProcessingException e) {
            throw new DecodeException("UTF-8", message, e);
        }
    }

    @Override
    public boolean willDecode(String message) {
        return (message != null);
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