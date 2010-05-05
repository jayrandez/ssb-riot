package demerit;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * An immutable class which can perform various encryption techniques on a provided source string.
 * Supported encryption/decryption methods are SHA-1, MD5, and AES (Rjindael) with a String-based key.
 * NOTE: 	This class was written before S.E.T., for another project of mine.
 * 			If the code looks different, that is why.
 */
public final class SecurityString {
	private final String data;

	/**
	 * Class constructor
	 * @param data Immutable string to be hashed, encrypted, or decrypted.
	 */
	public SecurityString(String data) {
		this.data = data;
	}
	
	/**
	 * Creates a hashed string from the data using the MD5 algorithm.
	 * @return Hashed data.
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public String getMd5Hash() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		return toHexString(getRawHash(md5Digest, data.getBytes("UTF-8")));
	}
	
	/**
	 * Creates a hashed string from the data using the SHA-1 algorithm.
	 * @return Hashed data.
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String getShaHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest shaDigest = MessageDigest.getInstance("SHA");
		shaDigest.reset();
		shaDigest.update(data.getBytes("UTF-8"));
		return toHexString(shaDigest.digest());
	}
	
	/**
	 * Encrypts the data using a hashed key generated from the given key source string.
	 * @param keySource Key you will be using to encrypt and later decrypt the data.
	 * @return Encrypted data.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String encryptAes(String keySource) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES");
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		byte[] hashedKey = getRawHash(md5Digest, keySource.getBytes("UTF-8"));
		SecretKeySpec keySpec = new SecretKeySpec(hashedKey, "AES");
		return toHexString(getRawCrypt(cipher, keySpec, data.getBytes("UTF-8"), Cipher.ENCRYPT_MODE));
	}
	
	/**
	 * Decrypts the data using a hashed key generated from the given key source string.
	 * @param keySource Key you previously used to encrypt the data.
	 * @return Decrypted data.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decryptAes(String keySource) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES");
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		byte[] hashedKey = getRawHash(md5Digest, keySource.getBytes("UTF-8"));
		SecretKeySpec keySpec = new SecretKeySpec(hashedKey, "AES");
		return new String(getRawCrypt(cipher, keySpec, fromHexString(data), Cipher.DECRYPT_MODE));
	}
	
	/**
	 * Converts data in a byte array to a string containing the hexadecimal representation.
	 * @param input Raw byte array.
	 * @return Hexadecimal string.
	 */
	private String toHexString(byte[] input) {
		String assembled = new String();
		for(byte b : input) {
			String digits = Integer.toHexString(0xFF & b);
			if(digits.length() == 1) {
				digits = "0" + digits;
			}
			assembled += digits;
		}
		return assembled;
	}
	
	/**
	 * Converts a string containing hexadecimal numbers in sequence to a real byte array.
	 * @param input Hexadecimal string.
	 * @return Raw byte array.
	 */
	private byte[] fromHexString(String input) {
		byte[] bytes = new byte[input.length()/2];
		for(int i = 0; i < input.length(); i+=2) {
			String current = input.substring(i, i+2);
			Integer value = Integer.parseInt(current, 16);
			bytes[i/2] = value.byteValue();
		}
		return bytes;
	}
	
	/**
	 * Hashes encrypt raw data using an already chosen algorithm.
	 * @param md The message digest already assembled to the specified algorithm.
	 * @param input The raw data.
	 * @return Raw hashed data.
	 */
	private byte[] getRawHash(MessageDigest md, byte[] input) {
		md.update(input);
		return md.digest();
	}
	
	/**
	 * Encrypts or decrypts raw data using an already selected algorithm.
	 * @param cipher Cipher used for the encryption/decryption process.
	 * @param keySpec SecretKeySpec previously assembled from a hashed key.
	 * @param input Raw data to be encrypted/decrypted.
	 * @param mode Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
	 * @return Raw encrypted or decrypted data.
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] getRawCrypt(Cipher cipher, SecretKeySpec keySpec, byte[] input, Integer mode) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(mode, keySpec);
		return cipher.doFinal(input);
	}
}

