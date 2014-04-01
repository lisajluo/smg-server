package org.smg.util;

import org.junit.Test;
import org.smg.server.util.AccessSignatureUtil;

public class AccessSignatureUtilTest {

  @Test
  public void testGetAccessSignature() {
    System.out.println(AccessSignatureUtil.getAccessSignature("1ksdhfkjha"));
    System.out.println(AccessSignatureUtil.getHashedPassword("1ksdhfkjha"));
    System.out.println(AccessSignatureUtil.getHashedPassword("1ksdhfkjha"));
  }

}
