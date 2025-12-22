package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum ParameterMode
{
  IN(K.IN.getKeyword()),
  OUT(K.OUT.getKeyword()),
  INOUT(K.INOUT.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private ParameterMode(String sKeywords) { _sKeywords = sKeywords; }
  public static ParameterMode getByKeywords(String sKeywords)
  {
    ParameterMode pmResult = null;
    ParameterMode[] apm = ParameterMode.values();
    for (int i = 0; (pmResult == null) && (i < apm.length); i++)
    {
      if (apm[i].getKeywords().equals(sKeywords))
        pmResult = apm[i];
    }
    return pmResult;
  }
}
