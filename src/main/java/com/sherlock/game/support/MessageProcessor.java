package com.sherlock.game.support;

import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;

public interface MessageProcessor<T> {

    Subject getSubject();

    Envelop process(T messageRequest);
}
