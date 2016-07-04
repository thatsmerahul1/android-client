package com.ecarezone.android.patient.service;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Umesh on 01-07-2016.
 */
public class AESUtil {

    private static final String ALGO = "AES";

    private static String key = "3105199211071992";
//    private static byte[] key = {33,31,30,35,31,39,39,32,31,31,30,37,31,39,39, 32};

    static byte[] keyValue = key.getBytes();

    /**
     * The encrypt method encrypts the string using AES algorithm.
     *
     * @param data
     * @return encryptedValue
     * @throws Exception
     */
    public static String encrypt(String data) {

        String encryptedValue = data;
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(data.getBytes());
            encryptedValue = Base64.encodeToString(encVal, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedValue;
    }

    /**
     * The decrypt method decrypts the AES encrypted string.
     *
     * @param encryptedData
     * @return decryptedValue
     * @throws Exception
     */
    public static String decrypt(String encryptedData) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.decode(encryptedData, Base64.NO_WRAP);
            byte[] decValue = c.doFinal(decodedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
//        Key genkey = new SecretKeySpec(key, ALGO);
        return key;
    }

}
