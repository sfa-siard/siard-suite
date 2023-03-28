/*== SU.java ===========================================================
SU implements a number of often used string utilities.
Version     : $Id: SU.java 394 2014-10-28 14:21:07Z hartwig $
Application : Utilities
Description : SU implements a number of often used string utilities.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 15.02.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils;

import java.io.*;
import java.text.MessageFormat;


/*====================================================================*/

/**
 * SU implements a number of often used string utilities.
 *
 * @author Hartwig Thomas
 */
public abstract class SU {
    public static final String sUTF8_CHARSET_NAME = "UTF-8";
  
  /*====================================================================
  (static, public) Methods
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * returns true, if s neither has length 0 nor is null.
     *
     * @param s string to be tested.
     * @return true, if string is not null and has length greater than zero.
     */
    public static boolean isNotEmpty(String s) {
        return (s != null) && (s.length() > 0);
    } /* isNotEmpty */

    /*------------------------------------------------------------------*/

    /**
     * returns true, if s neither is white nor is null.
     *
     * @param s string to be tested.
     * @return true, if string is not null and has length greater than zero.
     */
    public static boolean isNotWhite(String s) {
        return (s != null) && (s.trim().length() > 0);
    } /* isNotWhite */

    /*------------------------------------------------------------------*/

    /**
     * replaces all occurrences of sFind by sReplace.
     *
     * @param s        string to be transformed.
     * @param sFind    substring to be replaced.
     * @param sReplace substring to insert instead of sFind.
     * @return transformed string.
     * @deprecated is available as a String method since JAVA 1.5.
     */
    @Deprecated
    public static String replace(String s, String sFind, String sReplace) {
        StringBuffer sb = new StringBuffer();
        if (SU.isNotEmpty(s)) {
            int iStart = 0;
            for (int iMatch = s.indexOf(sFind, iStart); iMatch >= 0; iMatch = s.indexOf(sFind, iStart)) {
                /* append next portion of s */
                sb.append(s.substring(iStart, iMatch));
                /* append sReplace */
                sb.append(sReplace);
                /* skip sFind */
                iStart = iMatch + sFind.length();
            }
            /* append last portion */
            sb.append(s.substring(iStart, s.length()));
        }
        return sb.toString();
    } /* replace */

    /*------------------------------------------------------------------*/

    /**
     * format works like MessageFormat.format but assumes, that all
     * single quotes not immediately preceding a { or } are meant to represent
     * a single quote.
     *
     * @param sPattern  string to be formatted.
     * @param aoReplace replacement arguments to be used.
     * @return formatted string.
     */
    public static String format(String sPattern, Object[] aoReplace) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < sPattern.length(); i++) {
            char c = sPattern.charAt(i);
            sb.append(c);
            if (sPattern.charAt(i) == '\'') {
                /* if it is not the last character and not followed by { or } then duplicate it */
                if (i < sPattern.length() - 1) {
                    c = sPattern.charAt(i + 1);
                    if ((c != '{') && (c != '}'))
                        sb.append('\'');
                }
            }
        }
        return MessageFormat.format(sb.toString(), aoReplace);
    } /* format */

    /*------------------------------------------------------------------*/

    /**
     * format replaces {0} in the pattern by the given string.
     *
     * @param sPattern string to be formatted.
     * @param sReplace replacement string to be used.
     * @return formatted string.
     */
    public static String format(String sPattern, String sReplace) {
        return format(sPattern, new Object[]{sReplace});
    } /* format */

    /*------------------------------------------------------------------*/

    /**
     * returns a string of the given length filled with a given character.
     *
     * @param iRepetitions length of returned String.
     * @param c            character to be used.
     * @return String of given length filled with desired character.
     */
    public static String repeat(int iRepetitions, char c) {
        StringBuffer sb = new StringBuffer(iRepetitions);
        for (int i = 0; i < iRepetitions; i++)
            sb.append(c);
        return sb.toString();
    } /* repeat */

    /*------------------------------------------------------------------*/

    /**
     * returns a string of the given number of repetitions of a base string.
     *
     * @param iRepetitions length of returned String.
     * @param s            character to be used.
     * @return String of given length filled with desired character.
     */
    public static String repeat(int iRepetitions, String s) {
        StringBuffer sb = new StringBuffer(iRepetitions);
        for (int i = 0; i < iRepetitions; i++)
            sb.append(s);
        return sb.toString();
    } /* repeat */

    /*------------------------------------------------------------------*/

    /**
     * returns the next possible line break point in the given range.
     *
     * @param s      String to be broken.
     * @param iStart beginning of the line.
     * @param iEnd   maximum value of breakpoint.
     * @return breakpoint.
     */
    public static int getBreakPoint(String s, int iStart, int iEnd) {
        if (iEnd > s.length())
            iEnd = s.length();
        /* if there are line breaks between iStart and iEnd then take the first */
        int iBreak = s.indexOf('\n', iStart) + 1;
        if ((iBreak <= iStart) || (iBreak > iEnd)) {
            iBreak = iEnd;
            if ((iEnd < s.length()) && (s.charAt(iEnd) != ' ')) {
                boolean bBlankFound = false;
                for (int i = iEnd - 1; !bBlankFound && (i > iStart); i--) {
                    char c = s.charAt(i + 1);
                    if ((c == ' ') || (c == '-') || (c == '\n') || (c == '\r') || (c == '\t')) {
                        iBreak = i + 1;
                        bBlankFound = true;
                    }
                }
            }
        }
        return iBreak;
    } /* getBreakPoint */
  
  /*====================================================================
  Utilities
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * converts a portion of a character array to an encoded byte buffer.
     *
     * @param ac        character array to be converted.
     * @param iOffset   offset of portion in character array.
     * @param iLength   length of portion in character array.
     * @param sEncoding encoding to be used.
     * @return encoded byte buffer.
     */
    private static byte[] putEncodedCharArray(char[] ac, int iOffset, int iLength, String sEncoding) {
        byte[] buf = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(iLength);
        try {
            OutputStreamWriter osw = new OutputStreamWriter(baos, sEncoding);
            for (int i = iOffset; i < iOffset + iLength; i++)
                osw.write(ac[i]);
            osw.close();
            buf = baos.toByteArray();
        } catch (UnsupportedEncodingException uee) {
            System.out.println(uee.getClass().getName() + ":" + uee.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.getClass().getName() + ":" + ie.getMessage());
        }
        return buf;
    } /* putEncodedCharArray */

    /*------------------------------------------------------------------*/

    /**
     * converts a character array to an UTF8-encoded byte buffer.
     *
     * @param ac      character array to be converted.
     * @param iOffset offset in character array where encoding is to start.
     * @param iLength length of character array to be converted.
     * @return encoded byte buffer.
     */
    public static byte[] putUtf8CharArray(char[] ac, int iOffset, int iLength) {
        return putEncodedCharArray(ac, iOffset, iLength, SU.sUTF8_CHARSET_NAME);
    } /* putUtf8CharacterArray */

    /*------------------------------------------------------------------*/

    /**
     * converts a string to an encoded byte buffer.
     *
     * @param s         string to be converted.
     * @param sEncoding encoding to be used.
     * @return encoded byte buffer.
     */
    public static byte[] putEncodedString(String s, String sEncoding) {
        byte[] buf = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(s.length());
        try {
            OutputStreamWriter osw = new OutputStreamWriter(baos, sEncoding);
            osw.write(s);
            osw.close();
            buf = baos.toByteArray();
        } catch (UnsupportedEncodingException uee) {
            System.out.println(uee.getClass().getName() + ":" + uee.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.getClass().getName() + ":" + ie.getMessage());
        }
        return buf;
    } /* putEncodedString */

    /*------------------------------------------------------------------*/

    /**
     * converts an string to an UTF8-encoded byte buffer.
     *
     * @param s string.
     * @return encoded byte buffer.
     */
    public static byte[] putUtf8String(String s) {
        return putEncodedString(s, SU.sUTF8_CHARSET_NAME);
    } /* putUtf8String */

    /*------------------------------------------------------------------*/

    /**
     * converts a string to an Cp437-encoded byte buffer.
     *
     * @param s string.
     * @return encoded byte buffer.
     */
    public static byte[] putCp437String(String s) {
        return putEncodedString(s, "Cp437");
    } /* putCp437String */

    /*------------------------------------------------------------------*/

    /**
     * converts a string to an ISO-8859-1-encoded byte buffer.
     *
     * @param s string.
     * @return encoded byte buffer.
     */
    public static byte[] putIsoLatin1String(String s) {
        return putEncodedString(s, "ISO-8859-1");
    } /* putIsoLatin1String */

    /*------------------------------------------------------------------*/

    /**
     * converts a string to a Windows-1252-encoded byte buffer.
     *
     * @param s string.
     * @return encoded byte buffer.
     */
    public static byte[] putWindows1252String(String s) {
        return putEncodedString(s, "Windows-1252");
    } /* putWindows1252String */

    /*------------------------------------------------------------------*/

    /**
     * converts an encoded byte buffer to a string.
     *
     * @param buf       byte buffer.
     * @param sEncoding encoding.
     * @return encoded string.
     */
    public static String getEncodedString(byte[] buf, String sEncoding) {
        StringBuffer sb = new StringBuffer();
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        try {
            InputStreamReader isr = new InputStreamReader(bais, sEncoding);
            for (int i = isr.read(); i != -1; i = isr.read())
                sb.append((char) i);
        } catch (UnsupportedEncodingException uee) {
            System.out.println(uee.getClass().getName() + ":" + uee.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.getClass().getName() + ":" + ie.getMessage());
        }
        return sb.toString();
    } /* getEncodedString */

    /*------------------------------------------------------------------*/

    /**
     * converts an UTF8-encoded byte buffer to a string.
     *
     * @param buf byte buffer.
     * @return UTF8-encoded string.
     */
    public static String getUtf8String(byte[] buf) {
        return getEncodedString(buf, SU.sUTF8_CHARSET_NAME);
    } /* getUtf8String */

    /*------------------------------------------------------------------*/

    /**
     * converts an Cp437-encoded byte buffer to a string.
     *
     * @param buf byte buffer.
     * @return Cp437-encoded string.
     */
    public static String getCp437String(byte[] buf) {
        return getEncodedString(buf, "Cp437");
    } /* getCp437String */

    /*------------------------------------------------------------------*/

    /**
     * converts an ISO-8859-1-encoded byte buffer to a string.
     *
     * @param buf byte buffer.
     * @return ISO-8859-1-encoded string.
     */
    public static String getIsoLatin1String(byte[] buf) {
        return getEncodedString(buf, "ISO-8859-1");
    } /* getIsoLatin1String */

    /*------------------------------------------------------------------*/

    /**
     * converts a Windows-1252-encoded byte buffer to a string.
     *
     * @param buf byte buffer.
     * @return Windows-1252-encoded string.
     */
    public static String getWindows1252String(byte[] buf) {
        return getEncodedString(buf, "Windows-1252");
    } /* getWindows1252String */

    /*------------------------------------------------------------------*/

    /**
     * prepare string for a CSV cell by replacing all special characters
     * by escaping their hex value using the \x prefix.
     *
     * @param sText string to be prepared.
     * @return prepared string
     */
    public static String toCsv(String sText) {
        /* code \ as \\ */
        sText = SU.replace(sText, "\\", "\\\\");
        sText = SU.replace(sText, "\n", "\\n");
        sText = SU.replace(sText, "\r", "\\r");
        sText = SU.replace(sText, "\t", "\\t");
        /* code \x00 - \x1F */
        for (int i = 0; i < 32; i++) {
            char c = (char) i;
            String s = String.valueOf(c);
            String sEntity = "\\x" + BU.toHex((byte) i);
            sText = SU.replace(sText, s, sEntity);
        }
        return sText;
    } /* toCsv */

    /*------------------------------------------------------------------*/

    /**
     * prepare string for HTML by replacing all special characters.
     *
     * @param sText string to be prepared.
     * @return prepared string.
     */
    public static String toHtml(String sText) {
        sText = SU.replace(sText, "<", "&lt;");
        sText = SU.replace(sText, ">", "&gt;");
        sText = SU.replace(sText, "&", "&amp;");
        sText = SU.replace(sText, "\"", "&quot;");
        return sText;
    } /* toHtml */

} /* SU */
