package com.infina.proxyClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;



public class HIBE {
	
	public static void main(String[] args) throws Exception {
		System.out.println(encryptShaPwd("1234"));
		System.out.println(decryptShaPwd("ec6665f6eab8d22dc99a176a89edbeb70baba048370f695fccb1a32e1280342b"));
		System.out.println(encrypt("1234", "1234567890123456"));
		System.out.print(decrypt(encrypt("1234", "1234567890123456"), "1234567890123456"));
	}

	public static byte[] encrypt(String password, String key) throws Exception {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);

		KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 65536, 256); // AES-256
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] keySalted = f.generateSecret(spec).getEncoded();
		SecretKeySpec keySpec = new SecretKeySpec(keySalted, "AES");

		byte[] ivBytes = new byte[16];
		random.nextBytes(ivBytes);

		Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
		c.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encValue = c.doFinal(password.getBytes());

		byte[] finalCiphertext = new byte[encValue.length+2*16];
		System.arraycopy(ivBytes, 0, finalCiphertext, 0, 16);
		System.arraycopy(salt, 0, finalCiphertext, 16, 16);
		System.arraycopy(encValue, 0, finalCiphertext, 32, encValue.length);

		return finalCiphertext;
	}

	public static String decrypt(byte[]  string, String key) throws Exception {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);

		KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 65536, 256); // AES-256
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] keySalted = f.generateSecret(spec).getEncoded();
		SecretKeySpec keySpec = new SecretKeySpec(keySalted, "AES");

	    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	    cipher.init(Cipher.DECRYPT_MODE, keySpec);
	    byte[] original = cipher.doFinal(string);

	    return new String(original, Charset.forName("UTF-8"));
	  
	}
	
	public static String encryptShaPwd(String sifre) {
		MessageDigest digest = null;
		byte[] encodedhash = new byte[100];
		String decodedSifre = null;
		try {					
			byte[] keyData = ("1234InfinaSifre1").getBytes();
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "AES");
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

			byte[] cleartext = sifre.getBytes();
			byte[] hasil = cipher.doFinal(cleartext);
			decodedSifre = new String(hasil);
			
			digest = MessageDigest.getInstance("SHA-256");
			encodedhash = digest.digest(decodedSifre.getBytes(StandardCharsets.UTF_8));

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < encodedhash.length; ++i) {
				sb.append(Integer.toHexString(encodedhash[i] & 0xFF | 0x100).substring(1, 3));
			}

			return sb.toString();
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		return decodedSifre;
	}
	
	public static String decryptShaPwd(String inputPassword) {

		MessageDigest digest = null;
		byte[] encodedhash = new byte[100];
		String decodedSifre = null;
		try {			
			byte[] keyData = ("1234InfinaSifre1").getBytes();
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "AES");
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

			byte[] cleartext = inputPassword.getBytes();
			byte[] hasil = cipher.doFinal(cleartext);
			decodedSifre = new String(hasil);
			
			digest = MessageDigest.getInstance("SHA-256");
			encodedhash = digest.digest(decodedSifre.getBytes(StandardCharsets.UTF_8));

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < encodedhash.length; ++i) {
				sb.append(Integer.toHexString(encodedhash[i] & 0xFF | 0x100).substring(1, 3));
			}

			return sb.toString();
		} catch (Exception e) {
			System.err.println("Error:"+e);
		}
		return decodedSifre;
	}
}
