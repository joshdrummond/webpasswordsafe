package net.webpasswordsafe.server.plugin.authentication.duosecurity.client;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Util {
    public static byte[] hmacSha1(byte[] key_bytes, byte[] text_bytes)
        throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha1;

        try {
            hmacSha1 = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException nsae) {
            hmacSha1 = Mac.getInstance("HMAC-SHA-1");
        }

        SecretKeySpec macKey = new SecretKeySpec(key_bytes, "RAW");
        hmacSha1.init(macKey);
        return hmacSha1.doFinal(text_bytes);
    }

    public static String bytes_to_hex(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    // Taken from:
    // http://stackoverflow.com/questions/1515437/java-function-for-arrays-like-phps-join/1515548#1515548
    static String join(Object[] s, String glue) {
        int k = s.length;
        if (k == 0)
            return "";
        StringBuilder out = new StringBuilder();
        out.append(s[0]);
        for (int x = 1; x < k; ++x)
            out.append(glue).append(s[x]);
        return out.toString();
    }
}
