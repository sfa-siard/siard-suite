package ch.enterag.utils.lang;

import java.io.*;
import org.junit.*;
import ch.enterag.utils.*;

public class ExecuteTester
{

  /* text execute with input redirection */
  @Test
  public void testClip()
  {
    try
    {
      Reader rdr = new FileReader("src\\ch\\enterag\\utils\\lang\\Execute.java");
      Execute ex = Execute.execute("C:\\Windows\\System32\\clip.exe",rdr);
      rdr.close();
      System.out.println(ex.getStdOut());
      System.err.println(ex.getStdErr());
      /* now the clip board has the content of the source file */
    }
    catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie)); }
  } /* testFind */

}

