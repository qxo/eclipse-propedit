/*****************************************************
 *
 *   @author  Chomakichi
 *
 ****************************************************/
package jp.gr.java_conf.ussiy.app.propedit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * 
 * @author Chomakichi
 *  
 */
public class EncodeChanger {

	/**
	 * 
	 * @param UniStr
	 * @since 1.0.0
	 */
	public static String unicode2UnicodeEsc(String uniStr) {

		StringBuffer ret = new StringBuffer();
		if (uniStr == null) {
			return null;
		}
		int maxLoop = uniStr.length();
		for (int i = 0; i < maxLoop; i++) {
			char character = uniStr.charAt(i);
			if (character <= 127) {
				ret.append(character);
			} else {
				ret.append("\\u"); //$NON-NLS-1$
				String hexStr = Integer.toHexString(character);
				int zeroCount = 4 - hexStr.length();
				for (int j = 0; j < zeroCount; j++) {
					ret.append('0');
				}
				ret.append(hexStr);
			}
		}
		return ret.toString();
	}

	/**
	 * 
	 * @param EscStr
	 * @since 1.0.0
	 */
	public static String unicodeEsc2Unicode(String unicodeStr) {

		if (unicodeStr == null) {
			return null;
		}

		StringBuffer retBuf = new StringBuffer();
		int maxLoop = unicodeStr.length();
		for (int i = 0; i < maxLoop; i++) {
			if (unicodeStr.charAt(i) == '\\') {
				if (i < maxLoop - 5 && (unicodeStr.charAt(i + 1) == 'u' || unicodeStr.charAt(i + 1) == 'U')) {
					try {
						retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
						i += 5;
					} catch (NumberFormatException e) {
						retBuf.append(unicodeStr.charAt(i));
					}
				} else {
					retBuf.append(unicodeStr.charAt(i));
				}
			} else {
				retBuf.append(unicodeStr.charAt(i));
			}
		}

		return retBuf.toString();
	}

	public static String unicode2UnicodeEscWithoutComment(String uniStr) throws IOException {

		StringBuffer buf = new StringBuffer();
		BufferedReader reader = new BufferedReader(new StringReader(uniStr));
		boolean continueFlg = false;
		String line = null;
		while ((line = reader.readLine()) != null) {
			if ((line.trim().startsWith("#") || line.trim().startsWith("!")) && !continueFlg) { //$NON-NLS-1$ //$NON-NLS-2$
				buf.append(line);
			} else {
				if (line.endsWith("\\")) { //$NON-NLS-1$
					continueFlg = true;
				} else {
					continueFlg = false;
				}
				buf.append(EncodeChanger.unicode2UnicodeEsc(line));
			}
			buf.append("\n"); //$NON-NLS-1$
		}
		if (!uniStr.endsWith("\n")) { //$NON-NLS-1$
			buf.deleteCharAt(buf.length() - 1);
		}

		return buf.toString();
	}
}