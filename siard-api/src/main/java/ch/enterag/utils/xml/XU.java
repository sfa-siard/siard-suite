/*== XU.java ===========================================================
XU implements a number of often used XML utilities.
Application : Utilities
Description : XU implements a number of often used XML utilities.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 15.02.2008, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.xml;

import ch.enterag.utils.BU;
import ch.enterag.utils.SU;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.StringCharacterIterator;


/**
 * XU implements XML conversion utilities.
 *
 */
public class XU {
    public static final String sXML_VERSION_1_0 = "1.0";
    private static final int m_iCODES = 256;
    private static String[] m_asToDom = null;

    /**
     * creates the lookup table for conversion of a string to DOM.
     */
    private static void buildToDom() {
        /* allocate character look-up table */
        m_asToDom = new String[m_iCODES];
        /* initialize each character with itself */
        for (int i = 0; i < m_iCODES; i++)
            m_asToDom[i] = String.valueOf((char) i);
        /* replace all characters 0 - 31 by \\u00HH */
        for (int i = 0; i < 32; i++)
            m_asToDom[i] = "\\u00" + BU.toHex((byte) i);
        /* we use \\u00HH for exotic non-characters */
        m_asToDom['\\'] = "\\u005C";
        /* replace all characters 127-159 by \\u00HH */
        for (int i = 127; i < 160; i++) {
            byte b = (byte) (0xFFFFFF00 | i);
            if (i < 128)
                b = (byte) i;
            m_asToDom[i] = "\\u00" + BU.toHex(b);
        }
    } 
  
  /*====================================================================
  (static, public) Methods
  ====================================================================*/

    /**
     * prepare text for xs:string by replacing all "critical" but
     * permitted characters by escapes. It is assumed the the text will be
     * further handled by DOM creating character references where needed.
     *
     * @param sText text to be prepared.
     * @param el    DOM element to be filled with text.
     */
    public static String toXml(String sText) {
        if (XU.m_asToDom == null)
            XU.buildToDom();
        if (SU.isNotEmpty(sText)) {
            StringBuffer sb = new StringBuffer();
            String[] asLookup = null;
            asLookup = m_asToDom;
            StringCharacterIterator sci = new StringCharacterIterator(sText);
            boolean bSpace = true;
            for (char c = sci.first(); c != StringCharacterIterator.DONE; c = sci.next()) {
                if (bSpace && (c == ' ')) /* replace leading or multiple spaces by \\u0020 */
                    sb.append("\\u0020");
                else {
                    if (c < m_iCODES)
                        sb.append(asLookup[c]);
                    else
                        sb.append(c);
                    bSpace = (c == ' ');
                }
            }
            /* replace trailing spaces by \\u0020 */
            if (sb.charAt(sb.length() - 1) == ' ') {
                sb.setLength(sb.length() - 1);
                sb.append("\\u0020");
            }
            sText = sb.toString();
        }
        return sText;
    } 

    /**
     * prepare text for xs:string by replacing all "critical" but
     * permitted characters by escapes. It is assumed the the text will be
     * further handled by DOM creating character references where needed.
     *
     * @param sText text to be prepared.
     * @param el    DOM element to be filled with text.
     */
    public static void toXml(String sText, Element el) {
        if (sText != null)
            el.appendChild(el.getOwnerDocument()
                             .createTextNode(toXml(sText)));
    } 

    /**
     * prepare text from xs:string by removing all "ignorable" white space.
     * Text is assumed to come from a DOM element (interpreting character
     * entities being handled by DOM).
     *
     * @param sText text to be prepared.
     * @return prepared text.
     */
    public static String fromXml(String sText) {
        if (SU.isNotEmpty(sText)) {
            StringBuffer sb = new StringBuffer();
            StringCharacterIterator sci = new StringCharacterIterator(sText);
            boolean bSpace = false;
            for (char c = sci.first(); c != StringCharacterIterator.DONE; ) {
                if (Character.isWhitespace(c))
                    c = ' '; /* replace white space by spaces */
                boolean bIncrement = true;
                if (bSpace && (c == ' ')) {
                    /* replace multiple white space by single space */
                } else if (c == '\\') {
                    c = sci.next();
                    if (c == 'u') {
                        char[] acHex4 = new char[4];
                        /* examine next 4 characters */
                        for (int i = 0; i < 4; i++) {
                            if (c != StringCharacterIterator.DONE) {
                                c = sci.next();
                                if (((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F')) || ((c >= 'a') && (c <= 'f')))
                                    acHex4[i] = c;
                                else
                                    acHex4[i] = '0';
                            } else
                                acHex4[i] = '0';
                        }
                        sb.append((char) BU.fromHex(acHex4[0], acHex4[1], acHex4[2], acHex4[3]));
                    } else // should not occur, if toXml was used ...
                    {
                        sb.append('\\');
                        bIncrement = false;
                    }
                } else
                    sb.append(c);
                if (bIncrement) {
                    bSpace = (c == ' ');
                    c = sci.next();
                }
            }
            sText = sb.toString();
        }
        return sText;
    } 

    /**
     * prepare text from xs:string by removing all "ignorable" white space.
     * Text is assumed to come from a DOM element (interpreting character
     * entities being handled by DOM).
     *
     * @param el text element to be prepared.
     * @return prepared text.
     */
    public static String fromXml(Element el) {
        String sText = null;
        if (el != null)
            sText = el.getTextContent();
        return fromXml(sText);
    } 

    /**
     * remove all child elements of a DOM element.
     *
     * @param el parent element to be cleared.
     */
    public static void clearElement(Element el) {
        /* remove all children of elCell */
        for (int iChild = el.getChildNodes()
                            .getLength() - 1; iChild >= 0; iChild--) {
            Node node = el.getChildNodes()
                          .item(iChild);
            el.removeChild(node);
        }
    } 

} 
