package ch.enterag.utils.test;

public class TestRandomNReader
        extends TestRandomReader {
    public TestRandomNReader(long lSize) {
        super(lSize);
        _cbuf = TestUtils.getRandomFixedNString(_iBUFSIZ)
                         .toCharArray();
    }

}
