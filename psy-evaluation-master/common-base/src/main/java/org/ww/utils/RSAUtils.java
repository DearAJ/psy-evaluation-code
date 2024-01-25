package org.ww.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author 13096
 * @Date 2022/2/6 9:19
 * @Version 1.0
 */
public class RSAUtils {
    private static final String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCKpuZTpf48LWw5VcaAiwscvKDorhj/4QZjaFs0Ab/maPji6MMIzjGumyzEypi9szXqWsjEaAT5U3empqaRvxtq0K8zuRSg8S5on+n9xvLOMUbzku6CMeUbZOWry4LMupdVCT02Z+4wpWZ2/VbWo9JsiQJYQm9UFaJ5OG64UV9dh6KBkBbZfpGP+vkBUqnnHo9vjEdY+OzhqhlnYPNui62Uc8sJx8Df2rn8gUxmtjT0A9oP7hh/GijXEIxxgPBBR5cwK6hHGulwSkruF4P+CQjDdEbRk164ojcMvO/lVPWQHf43Q4jYArFzfAcaYTFYp+NLmhG+2j4Hz4RpsKyJ8ZqHAgMBAAECggEAUkdmyV7o9vcRkRioumFIz93UMyAVb8suilFERiF/qpDCmYz9KD2MWAx3zbRYXcJ6jT8ggwUjZn20tG2SzUy2TXXXcn8zkapuGuJC7jlgnyG49BROe252ggInFQKo9SsJYQohjCayv6V3n8cJAEipN4U6ofuSqzhHeV4WvkRzrkFOEPqH0JqGbnEkkjAz0dGXomNiz0EUbgAnqoNWwK0WAb1x7LHH3q9kgUHvcsBbfwlKJTpJYhc5jOZZampKEqGIZFWrrpceM9+voNCeZoLgREHT5th89WFf9N144xcdS6rmoZKvgF0GsdgiXcD7F2T2vs+9Enx5cWdu4wMCvwNwyQKBgQDIhjUK5UYh6RwG3YUOtiLhwPvGpbKY84S0eqzGK+pF6mKvhjt1pKcqd6gF85MiQ7pkSeB8v9QuZpG41uP8APOoSoKCZjRVkHlpqDmDpHZ1IfV2dqK+IKSoQ7hfgRSesMtG7ljqCe14aMT+ETNMfujPfD3YXJRglF4lqVnQPe9aAwKBgQCxArDzHgluJrgQHUgIuVft3+MSgvkaorn9YlOTrWLzx9LF/jwOWm1Tz7cWbrb/+NPXvoG7mp40tPedBf7QG3ftGClIZTrhje4s5V26DfUjTmJUks9EvFz4B/8Ru+kURaWTEkXn7FAW+MChlgXSGeR6bRLh+S+wZq/Q/ogL68+YLQKBgFFVTjJrKHUcrgeC1jNPYPqcYgMCtlwfedYSY/3FVjKhjQGQupFPBZbODdLMJwOg4PtIk8tFq3RemgCU+MgAQtQh1Km7dsK15OkcGKfyjzGXYo54NC5V4zN0tFXYb1qmbu0IQ/w5pp5OMIi0xU/jsd5mkFcvxecehslT1WAHnwutAoGBAI3ri3YRUhxH3PHGmFeuSsqE5CmWayUo7Rgz+rB/psOwJdTrBrm3VMqK0s+giwbmOOtUae3BhTDbG2CBSxENxPSKl6bcyv3G7vx+FcyzniTllr8aSiZhI+UVfDN6xuCjCqLFVtkhRgF1BfVINHiEnARwaWBWix7S5goZ2Xg9Y98tAoGAd6b/lMwWep25LRO9D0kqNUasKWd1RZjXMoD7Ctu9MxRvgvzBO3SCEtrf969gJqExekSPQ1TmW3plh9a40Ui0Ck1NYXcVJdJaAaUswqxIkSqUMXW/UwhFm8jxnNGCBV4J5MQjv50yNu9EuowMtJBvHtWaMKSIatysfSUkO6y9OKY=";
    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiqbmU6X+PC1sOVXGgIsLHLyg6K4Y/+EGY2hbNAG/5mj44ujDCM4xrpssxMqYvbM16lrIxGgE+VN3pqamkb8batCvM7kUoPEuaJ/p/cbyzjFG85LugjHlG2Tlq8uCzLqXVQk9NmfuMKVmdv1W1qPSbIkCWEJvVBWieThuuFFfXYeigZAW2X6Rj/r5AVKp5x6Pb4xHWPjs4aoZZ2DzboutlHPLCcfA39q5/IFMZrY09APaD+4Yfxoo1xCMcYDwQUeXMCuoRxrpcEpK7heD/gkIw3RG0ZNeuKI3DLzv5VT1kB3+N0OI2AKxc3wHGmExWKfjS5oRvto+B8+EabCsifGahwIDAQAB";

    public static String decrypt(String str) {                           //rsa解密
        //64位解码加密后的字符串
        byte[] inputByte;
        String outStr = "";
        try {
            inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            outStr = new String(cipher.doFinal(inputByte));
        } catch (UnsupportedEncodingException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return outStr;
    }

    public static String encrypt(String str) {     // rsa加密
        String outStr = "";
        try {
            //base64编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outStr;
    }
}
