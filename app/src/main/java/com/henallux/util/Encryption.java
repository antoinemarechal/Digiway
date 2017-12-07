package com.henallux.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption
{
    public static String encryptSHA512(String message) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] digest = messageDigest.digest(message.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte aDigest : digest)
        {
            sb.append(Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
