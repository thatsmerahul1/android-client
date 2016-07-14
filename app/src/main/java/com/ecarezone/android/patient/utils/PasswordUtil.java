package com.ecarezone.android.patient.utils;

import com.ecarezone.android.patient.service.AESUtil;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class PasswordUtil {

    private static boolean isTemporaryFix = false;

    public static String getHashedPassword(String password) {

        if(isTemporaryFix){
            return password;
        }
        else {
//            String temp = new String(Hex.encodeHex(DigestUtils.md5(password + Constants.salt)));
//            return new String(Hex.encodeHex(DigestUtils.md5(temp)));
            return AESUtil.encrypt(password);
        }
    }
}
