package cn.dubby.nw.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author dubby
 * @date 2019/5/21 16:07
 */
public class RandomStringUtil {

    private static final char[] charSet = {
            'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y',
            'z',
            'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y',
            'Z',
            '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0',
    };

    private static final int charSetLength = charSet.length;

    public static String random(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            sb.append(charSet[ThreadLocalRandom.current().nextInt(charSetLength)]);
        }
        return sb.toString();
    }

}
