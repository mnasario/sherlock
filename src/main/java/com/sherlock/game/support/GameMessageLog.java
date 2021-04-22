package com.sherlock.game.support;

public class GameMessageLog {
    public static final String TRACE_PARAMS = "SID[{}]-GID[{}]=PN[{}] - %s";

    public static String messageTo(String logTitle) {
        return String.format(TRACE_PARAMS, logTitle);
    }
}
