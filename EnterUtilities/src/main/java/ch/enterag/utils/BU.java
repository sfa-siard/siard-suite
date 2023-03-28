/*== BU.java ======================================================
BU implements a number of often used byte-buffer utilities.
Version     : $Id: BU.java 394 2014-10-28 14:21:07Z hartwig $
Application : Utilities
Description : Implements a number of often used byte-buffer utilities.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 12.03.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils;

/*====================================================================*/

/**
 * Implements a number of often used byte-buffer utilities.
 *
 * @author Hartwig Thomas
 */
public class BU {
    /**
     * long bit mask for single byte
     */
    private static final long lBYTE_MASK = 0x00000000000000FFL;
    /**
     * int bit mask for single byte
     */
    private static final int iBYTE_MASK = 0x000000FF;
    /**
     * byte mask for extracting low order byte from 2-byte integer
     */
    private static final int iNYBBLE_MASK = 0x0000000F;
    /**
     * byte mask for high bit in byte
     */
    private static final int iHIGH_BIT_MASK = 0x00000080;

  /*====================================================================
  hex utilities
  ====================================================================*/
    /**
     * hex digits
     */
    private static String sHexDigits = "0123456789ABCDEF";

    /*------------------------------------------------------------------*/

    /**
     * converts int to byte (unsigned)
     *
     * @param i int to be converted.
     * @return (unsigned) byte.
     */
    private static byte lowByte(int i) {
        i = i & iBYTE_MASK;
        if (i >= 0x00000080)
            i = -0x00000100 + i;
        return (byte) i;
    } /* lowByte */

    /*------------------------------------------------------------------*/

    /**
     * extracts an short from 2 bytes of a byte buffer in little-endian
     * order.
     *
     * @param bufShort little-endian byte buffer of size =&gt; 2.
     * @param iPos     position of short
     * @return extracted short.
     */
    public static short toShort(byte[] bufShort, int iPos) {
        short wShort = 0;
        int iByte = 0;
        if (bufShort.length < iPos + 2)
            throw new IllegalArgumentException("byte buffer for short must have length >= 2!");
        for (int i = iPos + 1; i >= iPos; i--) {
            /* shift output */
            wShort <<= 8;
            iByte = bufShort[i];
            /* bytes are signed! */
            iByte = iByte & iBYTE_MASK;
            /* set lowest byte */
            wShort = (short) (wShort | iByte);
        }
        return wShort;
    } /* toShort */

    /*------------------------------------------------------------------*/

    /**
     * extracts an short from first 2 bytes of a byte buffer in little-endian
     * order.
     *
     * @param bufShort little-endian byte buffer of size =&gt; 2.
     * @return extracted short.
     */
    public static short toShort(byte[] bufShort) {
        return toShort(bufShort, 0);
    } /* toShort */

    /*------------------------------------------------------------------*/

    /**
     * converts a short into a 2-byte little-endian byte buffer.
     *
     * @param wShort short to be converted.
     * @return little-endian byte buffer.
     */
    public static byte[] fromShort(short wShort) {
        byte[] bufShort = new byte[2];
        for (int i = 1; i >= 0; i--) {
            bufShort[1 - i] = lowByte(wShort);
            /* shift input */
            wShort >>= 8;
        }
        return bufShort;
    } /* fromShort */

    /*------------------------------------------------------------------*/

    /**
     * extracts an int from 4 bytes of a byte buffer in little-endian
     * order.
     *
     * @param bufInt little-endian byte buffer of size =&gt; 4.
     * @param iPos   starting position.
     * @return extracted int.
     */
    public static int toInt(byte[] bufInt, int iPos) {
        int iInt = 0;
        int iByte = 0;
        if (bufInt.length < iPos + 4)
            throw new IllegalArgumentException("byte buffer for int must have length >= 4!");
        for (int i = iPos + 3; i >= iPos; i--) {
            /* shift output */
            iInt <<= 8;
            iByte = bufInt[i];
            /* bytes are signed! */
            iByte = iByte & iBYTE_MASK;
            /* set lowest byte */
            iInt |= iByte;
        }
        return iInt;
    } /* toInt */

    /*------------------------------------------------------------------*/

    /**
     * extracts an int from first 4 bytes of a byte buffer in little-endian
     * order.
     *
     * @param bufInt little-endian byte buffer of size =&gt; 4.
     * @return extracted int.
     */
    public static int toInt(byte[] bufInt) {
        return toInt(bufInt, 0);
    } /* toInt */

    /*------------------------------------------------------------------*/

    /**
     * converts an int into a 4-byte little-endian byte buffer.
     *
     * @param iInt int to be converted.
     * @return little-endian byte buffer.
     */
    public static byte[] fromInt(int iInt) {
        byte[] bufInt = new byte[4];
        for (int i = 3; i >= 0; i--) {
            bufInt[3 - i] = lowByte(iInt);
            /* shift input */
            iInt >>= 8;
        }
        return bufInt;
    } /* fromInt */

    /*------------------------------------------------------------------*/

    /**
     * extracts a long from 8 bytes of a byte buffer in little-endian
     * order.
     *
     * @param bufLong little-endian byte buffer of size =&gt; 8.
     * @param iPos    starting position.
     * @return extracted long.
     */
    public static long toLong(byte[] bufLong, int iPos) {
        long lLong = 0;
        long lByte = 0;
        if (bufLong.length < iPos + 8)
            throw new IllegalArgumentException("byte buffer for long must have length >= 8!");
        for (int i = iPos + 7; i >= iPos; i--) {
            /* shift output */
            lLong <<= 8;
            lByte = bufLong[i];
            /* bytes are signed! */
            lByte = lByte & iBYTE_MASK;
            /* set lowest byte */
            lLong |= lByte;
        }
        return lLong;
    } /* toLong */

    /*------------------------------------------------------------------*/

    /**
     * extracts a long from first 8 bytes of a byte buffer in little-endian
     * order.
     *
     * @param bufLong little-endian byte buffer of size =&gt; 8.
     * @return extracted long.
     */
    public static long toLong(byte[] bufLong) {
        return toLong(bufLong, 0);
    } /* toLong */

    /*------------------------------------------------------------------*/

    /**
     * converts a long into a 8-byte little-endian byte buffer.
     *
     * @param lLong long to be converted.
     * @return little-endian byte buffer.
     */
    public static byte[] fromLong(long lLong) {
        byte[] bufLong = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bufLong[7 - i] = lowByte((int) (lLong & lBYTE_MASK));
            /* shift input */
            lLong >>= 8;
        }
        return bufLong;
    } /* fromLong */

    /*------------------------------------------------------------------*/

    /**
     * returns the eight binary digits representing the given byte.
     *
     * @param b byte to be converted.
     * @return binary string of length 8.
     */
    public static String toBinary(byte b) {
        StringBuffer sbBinary = new StringBuffer();
        int i = b & iBYTE_MASK;

        for (int iMask = iHIGH_BIT_MASK; iMask != 0; iMask >>>= 1) {
            if ((i & iMask) != 0)
                sbBinary.append("1");
            else
                sbBinary.append("0");
        }
        return sbBinary.toString();
    } /* toBinary */

    /*------------------------------------------------------------------*/

    /**
     * extracts a binary string from the byte buffer.
     *
     * @param buffer  buffer to be converted to binary string.
     * @param iOffset start offset (included).
     * @param iLength number of bytes to be converted.
     * @return binary string of length 8*iLength.
     */
    public static String toBinary(byte[] buffer, int iOffset, int iLength) {
        StringBuffer sbBinary = new StringBuffer();
        for (int i = iOffset; i < iOffset + iLength; i++)
            sbBinary.append(toBinary(buffer[i]));
        return sbBinary.toString();
    } /* toBinary */

    /*------------------------------------------------------------------*/

    /**
     * extracts a binary string from the byte buffer.
     *
     * @param buffer buffer to be converted to binary string.
     * @return binary string of length 8*iLength.
     */
    public static String toBinary(byte[] buffer) {
        return toBinary(buffer, 0, buffer.length);
    } /* toBinary */

    /*------------------------------------------------------------------*/

    /**
     * returns the two hex digits representing the given byte.
     *
     * @param b byte to be converted.
     * @return hex string of length 2.
     */
    public static String toHex(byte b) {
        StringBuffer sbHex = new StringBuffer();
        int i = b & iBYTE_MASK;
        int iLow = i & iNYBBLE_MASK;
        int iHigh = i >> 4;
        sbHex.append(sHexDigits.charAt(iHigh));
        sbHex.append(sHexDigits.charAt(iLow));
        return sbHex.toString();
    } /* toHex */

    /*------------------------------------------------------------------*/

    /**
     * extracts a hex string from the byte buffer.
     *
     * @param buffer  buffer to be converted to hex string.
     * @param iOffset start offset (included).
     * @param iLength number of bytes to be converted.
     * @return hex string of length 2*iLength.
     */
    public static String toHex(byte[] buffer, int iOffset, int iLength) {
        StringBuffer sbHex = new StringBuffer();
        for (int i = iOffset; i < iOffset + iLength; i++)
            sbHex.append(toHex(buffer[i]));
        return sbHex.toString();
    } /* toHex */

    /*------------------------------------------------------------------*/

    /**
     * extracts a hex string from the byte buffer.
     *
     * @param buffer buffer to be converted to hex string.
     * @return hex string of length 2*iLength.
     */
    public static String toHex(byte[] buffer) {
        return toHex(buffer, 0, buffer.length);
    } /* toHex */

    /*------------------------------------------------------------------*/

    /**
     * converts two hex characters into a byte.
     *
     * @param cHigh high hex digit.
     * @param cLow  high hex digit.
     * @return byte.
     */
    public static byte fromHex(char cHigh, char cLow) {
        byte b = 0;
        int iHigh = sHexDigits.indexOf(cHigh);
        int iLow = sHexDigits.indexOf(cLow);
        if ((iLow >= 0) && (iHigh >= 0)) {
            int i = iHigh << 4;
            i = i + iLow;
            b = lowByte(i);
        } else
            throw new IllegalArgumentException("Invalid hex data " + Character.toString(cHigh) + Character.toString(cLow) + "!");
        return b;
    } /* fromHex */

    /*------------------------------------------------------------------*/

    /**
     * converts four hex characters into an int.
     *
     * @param c3 most significant hex digit.
     * @param c2 second hex digit.
     * @param c1 third hex digit.
     * @param c0 least significant.
     * @return byte.
     */
    public static int fromHex(char c3, char c2, char c1, char c0) {
        int i = 0;
        int i3 = sHexDigits.indexOf(c3);
        int i2 = sHexDigits.indexOf(c2);
        int i1 = sHexDigits.indexOf(c1);
        int i0 = sHexDigits.indexOf(c0);
        if ((i3 >= 0) && (i2 >= 0) && (i1 >= 0) && (i0 >= 0))
            i = (i3 << 12) + (i2 << 8) + (i1 << 4) + i0;
        else
            throw new IllegalArgumentException("Invalid hex data " + Character.toString(c3) + Character.toString(c2) + Character.toString(
                    c1) + Character.toString(c0) + "!");
        return i;
    } /* fromHex */

    /*------------------------------------------------------------------*/

    /**
     * converts a hex string into a byte buffer.
     *
     * @param sHex hex string (even length).
     * @return byte buffer.
     */
    public static byte[] fromHex(String sHex) {
        if ((sHex.length() & 1) != 0)
            throw new IllegalArgumentException("Hex string must have even number of hex digits!");
        sHex = sHex.toUpperCase();
        byte[] buffer = new byte[sHex.length() / 2];
        for (int i = 0; i < buffer.length; i++)
            buffer[i] = fromHex(sHex.charAt(2 * i), sHex.charAt(2 * i + 1));
        return buffer;
    } /* fromHex */

    /*------------------------------------------------------------------*/

    /**
     * converts a byte to an int between 0 and 255.
     *
     * @param by byte to be converted.
     * @return int.
     */
    public static int fromUnsignedByte(byte by) {
        int iResult = by;
        if (iResult < 0)
            iResult = iResult + 256;
        return iResult;
    } /* fromUnsignedByte */

    /*------------------------------------------------------------------*/

    /**
     * converts an int between 0 and 255 to a byte.
     *
     * @param i int to be converted.
     * @return byte.
     */
    public static byte toUnsignedByte(int i) {
        if (i > 127)
            i = i - 256;
        byte by = (byte) i;
        return by;
    } /* toUnsignedByte */

    /*------------------------------------------------------------------*/

    /**
     * Search the data byte array for the first occurrence
     * of the byte array pattern using the Knuth-Morris-Pratt Pattern Matching Algorithm.
     * (from http://helpdesk.objects.com.au/java/search-a-byte-array-for-a-byte-sequence)
     *
     * @param data    data byte array.
     * @param pattern search pattern.
     * @return offset of first occurrence of search pattern or -1.
     */
    public static int indexOf(byte[] data, byte[] pattern) {
        int iIndex = -1;
        int[] failure = computeFailure(pattern);
        int j = 0;
        for (int i = 0; (iIndex < 0) && (i < data.length); i++) {
            while (j > 0 && pattern[j] != data[i])
                j = failure[j - 1];
            if (pattern[j] == data[i])
                j++;
            if (j == pattern.length)
                iIndex = i - pattern.length + 1;
        }
        return iIndex;
    } /* indexOf */

    /*------------------------------------------------------------------*/

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];
        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i])
                j = failure[j - 1];
            if (pattern[j] == pattern[i])
                j++;
            failure[i] = j;
        }
        return failure;
    } /* computeFailure */

} /* BU */
