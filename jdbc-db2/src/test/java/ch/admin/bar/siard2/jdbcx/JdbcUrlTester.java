package ch.admin.bar.siard2.jdbcx;

import java.util.regex.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class JdbcUrlTester
{

  // see: http://www.ibm.com/support/knowledgecenter/SSEPGG_9.7.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_r0052342.html
  @Test
  public void test()
  {
    // Pattern patUrl = Pattern.compile("jdbc:((db2:)|(db2j:net:)|(ids:))//(\\.*?)(:\\d*)?/(\\.*)");
    // Matcher matcher = patUrl.matcher("jdbc:db2://localhost:50000/testdb");
    Pattern patUrl = Pattern.compile("^jdbc:((db2:)|(db2j:net:)|(ids:))//(.*?)(:(\\d*))?/(.*)$");
    String sUrl = "jdbc:db2://localhost:50000/testdb";
    Matcher matcher = patUrl.matcher(sUrl);
    if (matcher.matches())
    {
      System.out.println("matches: "+matcher.group(0));
      for (int i = 0; i < matcher.groupCount();i++)
      {
        String s = matcher.group(i+1);
        System.out.println(String.valueOf(i+1)+": "+s);
      }
      String sHost = matcher.group(5);
      String sPort = matcher.group(7);
      int iPort = 50000;
      if (sPort != null)
        iPort = Integer.parseInt(sPort);
      String sDatabase = matcher.group(8);
      assertEquals("Parsing error!",sUrl,"jdbc:db2://"+sHost+":"+String.valueOf(iPort)+"/"+sDatabase);
    }
    else
      fail("Does not match!");
  }

}
