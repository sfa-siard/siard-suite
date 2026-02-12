package ch.admin.bar.siard2.api.primary;

import ch.enterag.utils.BU;
import ch.enterag.utils.xml.XU;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ValidatingOutputStream
        extends OutputStream {
    private OutputStream _os;
    private MessageDigest _md;
    private final Element _el;
    private long _lWritten = 0;

    public ValidatingOutputStream(Element el, OutputStream os) {
        XU.clearElement(el);
        _el = el;
        if (os == null)
            _os = new ByteArrayOutputStream();
        else {
            _os = os;
            try {
                _md = MessageDigest.getInstance(ArchiveImpl._dttDEFAULT_DIGEST_ALGORITHM.value());
                _os = new DigestOutputStream(os, _md);
            } catch (NoSuchAlgorithmException nsae) {
            }
        }
    } 

    @Override
    public void write(int b)
            throws IOException {
        _os.write(b);
        _lWritten++;
    } 

    @Override
    public void write(byte[] buf)
            throws IOException {
        _os.write(buf);
        _lWritten = _lWritten + buf.length;
    } 

    @Override
    public void write(byte[] buf, int iOffset, int iLength)
            throws IOException {
        _os.write(buf, iOffset, iLength);
        _lWritten = _lWritten + iLength;
    } 

    @Override
    public void close()
            throws IOException {
        _os.close();
        if (_os instanceof ByteArrayOutputStream) {
            ByteArrayOutputStream baos = (ByteArrayOutputStream) _os;
            _el.setTextContent(BU.toHex(baos.toByteArray()));
        } else {
            _el.setAttribute(ArchiveImpl._sATTR_LENGTH, String.valueOf(_lWritten));
            if (_md != null) {
                _el.setAttribute(ArchiveImpl._sATTR_DIGEST_TYPE, _md.getAlgorithm());
                _el.setAttribute(ArchiveImpl._sATTR_MESSAGE_DIGEST, BU.toHex(_md.digest()));
            }
        }
    } 

} 
