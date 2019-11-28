package com.lease.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author: lwh
 * Date: 3/16/16 17:57.
 */
public class AESUtil {

    /**
     * Here is Both function for encrypt and decrypt file in Sdcard folder. we
     * can not lock folder but we can encrypt file using AES in Android, it may
     * help you.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */

    public static File encrypt(File srcFile,File encodeFile,String decodeKey) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        if(srcFile==null || encodeFile==null || !srcFile.exists() || !encodeFile.exists() || decodeKey==null){
            return null;
        }

        FileInputStream fis = new FileInputStream(srcFile.getAbsolutePath());
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.
        FileOutputStream fos = new FileOutputStream(encodeFile.getAbsolutePath());

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec(decodeKey.getBytes(),
                "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
        return encodeFile;
    }

    public static File decrypt(File encodeFile,File decodeFile,String decodeKey) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        try{
            if(decodeFile==null || encodeFile==null || !decodeFile.exists() || !encodeFile.exists() || decodeKey==null){
                return null;
            }
            FileInputStream fis = new FileInputStream(encodeFile.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(decodeFile.getAbsolutePath());
            SecretKeySpec sks = new SecretKeySpec(decodeKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[8];
            while ((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return decodeFile;
    }

}
