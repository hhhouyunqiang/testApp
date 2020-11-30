package com.ringdata.base.util.encrypt;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author: bella_wang
 * @Description:
 * @Date: Create in 2020/4/26 11:22
 */
public class AESUtil {

    public static String encode(String data) {
        //AES加密只需要将DES改成AES即可
        //1，得到cipher 对象（可翻译为密码器或密码系统）
        //初始化cipher 对象时可以指定更详细的参数
        //格式：”algorithm/mode/padding” ，即”算法/工作模式/填充模式” 具体看http://blog.csdn.net/axi295309066/article/details/52491077的最后面
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            //2，创建秘钥
//            SecretKey key = KeyGenerator.getInstance("AES").generateKey();
            Key key = new SecretKeySpec("1234567812345678".getBytes(), "AES");

            //加密
            //3，设置操作模式（加密/解密）
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //4，执行加密
            byte[] relBytes = cipher.doFinal(data.getBytes());
            //注意：加密过后用Base64编码 缺少这步会导致解密失败
            byte[] relBase = Base64.encode(relBytes, Base64.DEFAULT);
            String encodeStr = new String(relBase);
            Log.d("xl", encodeStr);
            return encodeStr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return "";
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return "";
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return "";
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decode(String encodeStr) {
        //AES加密只需要将DES改成AES即可
        //1，得到cipher 对象（可翻译为密码器或密码系统）
        //初始化cipher 对象时可以指定更详细的参数
        //格式：”algorithm/mode/padding” ，即”算法/工作模式/填充模式” 具体看http://blog.csdn.net/axi295309066/article/details/52491077的最后面
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES");
            //2，创建秘钥
            SecretKey key = KeyGenerator.getInstance("DES").generateKey();

            //解密
            //3，设置操作模式（加密/解密）
            cipher.init(Cipher.DECRYPT_MODE, key);
            //4，执行解密
            //先用Base64解密 缺少Base64编码会导致解密失败
            byte[] decode = Base64.decode(encodeStr, Base64.DEFAULT);
            byte[] bytes = cipher.doFinal(decode);
            String decodeStr = new String(bytes);
            Log.d("xl", decodeStr);
            return decodeStr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
