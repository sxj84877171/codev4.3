package com.pvi.ap.reader.data.common;

import it.sauronsoftware.base64.Base64;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

 /**
 * This Class is used for AES encode/decode,Base64 encode/decode,MD5 encrypt.
 * @author rd032
 * (C) Copyright 2010-2013, by www.pvi.com.tw.
 */


public class DencryptHelper
{
	/**
	 * 获取AES加密值
	 * @param sSrc
	 * @param sKey
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public static String aesencrypt(String sSrc, String sKey,String param) throws Exception 
    {
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
        
        IvParameterSpec iv = new IvParameterSpec(param.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());

        return new String(encrypted);
    }

    /**
     * 获取AES解密值
     * @param sSrc
     * @param sKey
     * @param param
     * @return
     * @throws Exception
     */
    public static String aesdecrypt(byte[] sSrc, byte[] sKey,byte[] param) throws Exception 
    {
        try 
        {
            //byte[] raw = sKey.getBytes();
        	
        	//System.out.println("KEY LENGTH======"+sKey.length);
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC"); 
           // System.out.println("IV length==="+param.length);
            
            
            IvParameterSpec iv = new IvParameterSpec(param);
            
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            try 
            {
            	byte[] original = cipher.doFinal(sSrc);
                String originalString = new String(original);
                //System.out.println(originalString);
                return originalString;
            } 
            catch (Exception e) 
            {
               // System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) 
        {
            //System.out.println(ex.toString());
            return null;
        }
    }
    
    
    /**
     * 获取AES解密值
     * @param sSrc
     * @param sKey
     * @param param
     * @return
     * @throws Exception
     */
    public static byte[] Decrypt(byte[] sSrc, byte[] sKey,byte[] param) throws Exception 
    {
        try 
        {
            //byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC"); 
            
            
            IvParameterSpec iv = new IvParameterSpec(param);
            
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            try 
            {
            	byte[] original = cipher.doFinal(sSrc);
                return original;
            } 
            catch (Exception e) 
            {
               // System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) 
        {
           // System.out.println(ex.toString());
            return null;
        }
    }

    /**
     * 获取AES解密值
     * @param content
     * @param password
     * @return
     */
    public static byte[] aesdecrypt(byte[] content, String password) 
    {   
        try 
        {   
             KeyGenerator kgen = KeyGenerator.getInstance("AES");   
             kgen.init(128, new SecureRandom(password.getBytes()));   
             SecretKey secretKey = kgen.generateKey();   
             byte[] enCodeFormat = secretKey.getEncoded();   
             SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");               
             Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化   
            byte[] result = cipher.doFinal(content);   
            return result; // 加密   
        } catch (NoSuchAlgorithmException e) {   
                e.printStackTrace();   
        } catch (NoSuchPaddingException e) {   
                e.printStackTrace();   
        } catch (InvalidKeyException e) {   
                e.printStackTrace();   
        } catch (IllegalBlockSizeException e) {   
                e.printStackTrace();   
        } catch (BadPaddingException e) {   
                e.printStackTrace();   
        }   
        return null;   
    }
    
    /**
     * 获取Base64加密值
     * @param str
     * @return
     */
    public static String base64encode(String str)
	{
		try
		{
			if((str==null)||("".equals(str)))
			{
				return null;
			}
			//MD5 encode
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");   
			messageDigest.update(str.getBytes());  
			//Base64 encode
			return new String(Base64.encode(messageDigest.digest()),"UTF-8"); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
    /**
     * 获取Base64解密值
     * @param str
     * @return
     */
	public static String base64decode(String str)
	{	
		try
		{
			byte[] temp = str.getBytes("UTF-8");
			return new String(Base64.decode(temp),"UTF-8");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * 获取Hex值
	 * @param byt
	 * @return
	 */
	public static String getHexString(byte[] byt)
	{
		String res = "";
		for (int j = byt.length-1; j >=0 ; j--) 
		{
			String hex = Integer.toHexString(byt[j] & 0xFF);
			if (hex.length() == 1) 
			{
				hex = '0' + hex;
			}
			res += hex.toUpperCase();
		}
		return res;
	}
	
	
	/**
	 * 获取MD5加密值
	 * @param str
	 * @return
	 */
	public static String md5encrypt(String str)
	{
		try
		{
			if((str==null)||("".equals(str)))
			{
				return null;
			}
			//MD5 encode
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");   
			messageDigest.update(str.getBytes());  
			//Base64 encode
			return new String(Base64.encode(messageDigest.digest()),"UTF-8"); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	
	
}
