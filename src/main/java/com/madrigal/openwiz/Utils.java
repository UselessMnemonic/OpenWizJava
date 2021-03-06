package com.madrigal.openwiz;

/**
 * Class for common operations.
 */
public class Utils {

    // Byte-to-hex string conversion,
    // found via this post:
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    public static final char[] HEX = "0123456789abcdef".toCharArray();

    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX[v >>> 4];
            hexChars[(i * 2) + 1] = HEX[v & 0x0F];
        }
        return new String(hexChars);
    }
}
