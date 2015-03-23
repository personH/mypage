package com.txnetwork.mypage.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Static functions to simplifiy common {@link java.security.MessageDigest}
 * tasks. This class is thread safe.
 * 
 * @author 99bill
 * 
 */
public class MD5Util {

	private MD5Util() {
	}

	/**
	 * Returns a MessageDigest for the given <code>algorithm</code>.
	 * 
	 * @param algorithm
	 *            The MessageDigest algorithm name.
	 * @return An MD5 digest instance.
	 * @throws RuntimeException
	 *             when a {@link java.security.NoSuchAlgorithmException} is
	 *             caught
	 */

	static MessageDigest getDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element
	 * <code>byte[]</code>.
	 * 
	 * @param data
	 *            Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(byte[] data) {
		return getDigest().digest(data);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element
	 * <code>byte[]</code>.
	 * 
	 * @param data
	 *            Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(String data, String charset) {
	    if ( charset == null ) {
	        return md5(data.getBytes());
	    } else {
	        try {
                return md5(data.getBytes(charset));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	        
	        return null;
	    }
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex
	 * string.
	 * 
	 * @param data
	 *            Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(byte[] data) {
		return HexUtil.toHexString(md5(data));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex
	 * string.
	 * 
	 * @param data
	 *            Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(String data) {
		return HexUtil.toHexString(md5(data, null));
	}
	
	public static String md5Hex(String data, String charset) {
        return HexUtil.toHexString(md5(data, charset));
    }
}
