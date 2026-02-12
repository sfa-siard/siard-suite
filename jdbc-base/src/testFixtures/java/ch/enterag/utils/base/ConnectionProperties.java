package ch.enterag.utils.base;

import java.io.*;
import java.util.*;
import static org.junit.Assert.*;

public class ConnectionProperties
  extends Properties
{
  private static final long serialVersionUID = 5204423170460249028L;
  private String _sDb = "";

  private void readProperties()
  {
    try
    {
      Reader rdr = new FileReader("test.properties");
      load(rdr);
      rdr.close();
    }
    catch (IOException ie) { fail(ie.getClass().getName()+": "+ie.getMessage()); }
  }
  
  public ConnectionProperties(String sDb)
  {
    readProperties();
    _sDb = sDb;
  }
  
  public ConnectionProperties()
  {
    readProperties();
  }
  
  public String getHost()
  {
    return getProperty("dbhost"+_sDb);
  }
  
  public String getPort()
  {
    return getProperty("dbport"+_sDb);
  }
  
  public String getInstance()
  {
    return getProperty("dbinstance"+_sDb);
  }
  
  public String getCatalog()
  {
    return getProperty("dbcatalog"+_sDb);
  }
  
  public String getUser()
  {
    return getProperty("dbuser"+_sDb);
  }
  
  public String getPassword()
  {
    return getProperty("dbpassword"+_sDb);
  }

  public String getDbaUser()
  {
    return getProperty("dbauser"+_sDb);
  }
  
  public String getDbaPassword()
  {
    return getProperty("dbapassword"+_sDb);
  }
  
  public String getBlobPng(int iRecord)
  {
    return getProperty("blobpng"+ iRecord);
  }
  
  public String getBlobFlac(int iRecord)
  {
    return getProperty("blobflac"+ iRecord);
  }

}
