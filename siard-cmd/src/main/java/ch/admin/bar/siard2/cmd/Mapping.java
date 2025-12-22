package ch.admin.bar.siard2.cmd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Mapping
{
  /*------------------------------------------------------------------*/
  /** limitations on maximum length of identifiers leads to a need to
   * truncate and disambiguate identifiers in a set which need to be unique. 
   * @param list list of original identifiers.
   * @param iMaxLength maximum length.
   * @return mapping from original to mapped names.
   */
  protected static Map<String,String> getDisambiguated(List<String> list, int iMaxLength)
  {
    Map<String,String> map = new HashMap<String,String>();
    for (Iterator<String> iter = list.iterator(); iter.hasNext(); )
    {
      String sOriginalName = iter.next();
      String sMappedName = sOriginalName;
      if (iMaxLength > 0)
      {
        if (sOriginalName.length() > iMaxLength)
          sMappedName = sOriginalName.substring(0,iMaxLength-1)+"_";
        for (int iCounter = 0; map.containsValue(sMappedName); iCounter++)
        {
          String sSuffix = "_"+String.valueOf(iCounter);
          sMappedName = sOriginalName.substring(0,iMaxLength-sSuffix.length())+sSuffix;
        }
      }
      map.put(sOriginalName, sMappedName);
    }
    return map;
  } /* getDisambiguated */

} /* Mapping */
