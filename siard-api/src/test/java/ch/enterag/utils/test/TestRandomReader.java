package ch.enterag.utils.test;

import java.io.IOException;
import java.io.Reader;
import java.util.Random;

public class TestRandomReader extends Reader {
    private static final Random _random = new Random(47);
    protected static int _iBUFSIZ = 32768;
    protected long _lSize = -1;
    protected long _lPosition = 0;
    protected char[] _cbuf = null;
    protected int _iOffset = 0;

    public TestRandomReader(long lSize) {
        _lSize = (long) (lSize * _random.nextDouble());
        _cbuf = TestUtils.getRandomFixedString(_iBUFSIZ)
                         .toCharArray();
    }

    @Override
    public int read() throws IOException {
        int iChar = -1;
        if (_lPosition < _lSize) {
            if (_iOffset >= _cbuf.length) {
                _cbuf = TestUtils.getRandomFixedString(_iBUFSIZ)
                                 .toCharArray();
                _iOffset = 0;
            }
            iChar = _cbuf[_iOffset];
            _iOffset++;
            _lPosition++;
        }
        return iChar;
    }

    @Override
    public int read(char[] cbufRead, int iOffset, int iLength)
            throws IOException {
        int iRead = 0;
        if (iLength > 0) {
            long l = _lPosition;
            int i = _iOffset;
            for (int iChar = read(); (iChar != -1) && (iRead < iLength); iChar = read()) {
                cbufRead[iOffset + iRead] = (char) iChar;
                iRead++;
                l = _lPosition;
                i = _iOffset;
            }
            if (iRead == 0)
                iRead = -1;
            _lPosition = l;
            _iOffset = i;
        }
        return iRead;
    }

    @Override
    public int read(char[] cbufRead)
            throws IOException {
        return read(cbufRead, 0, cbufRead.length);
    }

    @Override
    public void close() throws IOException {
    }

}
