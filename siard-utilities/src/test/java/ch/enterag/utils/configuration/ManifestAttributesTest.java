package ch.enterag.utils.configuration;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;


public class ManifestAttributesTest
{
  private ManifestAttributes _mf;

  @Test
  @Disabled // does somehow not work anymore with build.gradle.kts - but should anyway be refactored/ fixed
  public void testGetBuiltDate()
  {
    _mf = ManifestAttributes.getInstance();
    String res = _mf.getImplementationVersion();
    System.out.println(res);

    res = _mf.getImplementationTitle();
    System.out.println(res);

    res = _mf.getImplementationVendor();
    System.out.println(res);

    res = _mf.getSpecificationVersion();
    System.out.println(res);

    res = _mf.getSpecificationTitle();
    System.out.println(res);

    res = _mf.getSpecificationVendor();
    System.out.println(res);

    Calendar cal = _mf.getBuiltDate();
    System.out.println(cal.getTime());
  }

}
