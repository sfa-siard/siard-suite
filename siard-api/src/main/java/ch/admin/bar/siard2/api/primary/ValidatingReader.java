package ch.admin.bar.siard2.api.primary;

import ch.enterag.utils.xml.XU;
import org.w3c.dom.Element;

import java.io.*;

public class ValidatingReader
        extends Reader {
    private Reader _rdr = null;
    private long _lLength = -1;
    private long _lRead = 0;
    private boolean _bValidated = false;

    public ValidatingReader(Element el, InputStream is) {
        if (is == null)
            _rdr = new StringReader(XU.fromXml(el));
        else {
            try {
                String sLength = el.getAttribute(ArchiveImpl._sATTR_LENGTH);
                if (sLength != null)
                    _lLength = Long.parseLong(sLength);
                _rdr = new InputStreamReader(new ValidatingInputStream(el, is, false),
                                             ArchiveImpl._sDEFAULT_ENCODING);
            } catch (UnsupportedEncodingException usee) {
            }
        }
    } /* constructor validating Reader */

    private void validateAtEof()
            throws IOException {
        if (!_bValidated) {
            _bValidated = true;
            if (_lLength >= 0) {
                if (_lLength != _lRead)
                    throw new IOException("Unexpected length " + _lRead + " instead of " + _lLength + " detected!");
            }
        }
    } 

    @Override
    public int read()
            throws IOException {
        int iRead = _rdr.read();
        if (iRead != -1)
            _lRead++;
        else
            validateAtEof();
        return iRead;
    } 

    @Override
    public int read(char[] cbuf)
            throws IOException {
        int iRead = _rdr.read(cbuf);
        if (iRead != -1)
            _lRead = _lRead + iRead;
        else
            validateAtEof();
        return iRead;
    } 

    @Override
    public int read(char[] cbuf, int iOffset, int iLength)
            throws IOException {
        int iRead = 0;
        if (iLength > 0) {
            iRead = _rdr.read(cbuf, iOffset, iLength);
            if (iRead != -1)
                _lRead = _lRead + iRead;
            else
                validateAtEof();
        }
        return iRead;
    } 

    @Override
    public long skip(long lSkip)
            throws IOException {
        long lSkipped = _rdr.skip(lSkip);
        _lRead = _lRead + lSkipped;
        return lSkipped;
    } 

    @Override
    public void close() throws IOException {
        if (!_bValidated) {
            if (_lLength > _lRead)
                skip(_lLength - _lRead);
            while (!_bValidated)
                read();
        }
        _rdr.close();
    } 

} 
