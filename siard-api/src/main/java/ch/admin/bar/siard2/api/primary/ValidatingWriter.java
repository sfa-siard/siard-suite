package ch.admin.bar.siard2.api.primary;

import ch.enterag.utils.xml.XU;
import org.w3c.dom.Element;

import java.io.*;

public class ValidatingWriter
        extends Writer {
    private Element _el = null;
    private Writer _wr = null;
    private long _lWritten = 0;

    public ValidatingWriter(Element el, OutputStream os) {
        XU.clearElement(el);
        _el = el;
        if (os == null)
            _wr = new StringWriter();
        else {
            try {
                _wr = new OutputStreamWriter(new ValidatingOutputStream(el, os),
                                             ArchiveImpl._sDEFAULT_ENCODING);
            } catch (UnsupportedEncodingException usee) {
            }
        }
    } 

    @Override
    public void write(int iChar)
            throws IOException {
        _wr.write(iChar);
        _lWritten++;
    } 

    @Override
    public void write(char[] cbuf)
            throws IOException {
        _wr.write(cbuf);
        _lWritten = _lWritten + cbuf.length;
    } 

    @Override
    public void write(char[] cbuf, int iOffset, int iLength)
            throws IOException {
        _wr.write(cbuf, iOffset, iLength);
        _lWritten = _lWritten + iLength;
    } 

    @Override
    public void write(String s)
            throws IOException {
        _wr.write(s);
        _lWritten = _lWritten + s.length();
    } 

    @Override
    public void write(String s, int iOffset, int iLength)
            throws IOException {
        _wr.write(s, iOffset, iLength);
        _lWritten = _lWritten + iLength;
    } 

    @Override
    public void flush() throws IOException {
        _wr.flush();
    } 

    @Override
    public void close() throws IOException {
        _wr.close();
        if (_wr instanceof StringWriter) {
            StringWriter swr = (StringWriter) _wr;
            XU.toXml(swr.toString(), _el);
        }
        _el.setAttribute(ArchiveImpl._sATTR_LENGTH, String.valueOf(_lWritten));
    } 

} 
