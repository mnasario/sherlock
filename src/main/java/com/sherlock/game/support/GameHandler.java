package com.sherlock.game.support;

import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor
public final class GameHandler {

    public static Random RANDOM = new Random();

    public static String generateRandomCode(int targetStringLength) {
        targetStringLength = targetStringLength == 0 ? 10 : targetStringLength;

        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        return RANDOM.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
