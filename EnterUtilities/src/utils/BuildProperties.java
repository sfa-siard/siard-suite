package ch.enterag.utils;

import java.io.*;
import java.util.*;

public class BuildProperties
  extends Properties
{
  private static final long serialVersionUID = 1L;

  private void readProperties()
    throws IOException
  {
    Reader rdr = new FileReader("build.properties");
    load(rdr);
    rdr.close();
  }
  
  public BuildProperties()
    throws IOException
  {
    readProperties();
  }
  
}
