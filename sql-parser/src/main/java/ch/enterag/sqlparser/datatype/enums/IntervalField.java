package ch.enterag.sqlparser.datatype.enums;
import ch.enterag.sqlparser.*;
public enum IntervalField
{
  YEAR(K.YEAR.getKeyword()),
  MONTH(K.MONTH.getKeyword()),
  DAY(K.DAY.getKeyword()),
  HOUR(K.HOUR.getKeyword()),
  MINUTE(K.MINUTE.getKeyword()),
  SECOND(K.SECOND.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private IntervalField(String sKeywords) { _sKeywords = sKeywords; }
  public static IntervalField getByKeywords(String sKeywords)
  {
    IntervalField ifResult = null;
    IntervalField[] aif = IntervalField.values();
    for (int i = 0; (ifResult == null) && (i < aif.length); i++)
    {
      if (aif[i].getKeywords().equals(sKeywords))
        ifResult = aif[i];
    }
    return ifResult;
  }
}
