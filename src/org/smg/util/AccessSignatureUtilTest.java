package org.smg.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccessSignatureUtilTest {

  @Test
  public void testGetAccessSignature() {
    System.out.println(AccessSignatureUtil.getAccessSignature("1ksdhfkjha"));
    System.out.println(AccessSignatureUtil.getHashedPassword("1ksdhfkjha"));
    System.out.println(AccessSignatureUtil.getHashedPassword("1ksdhfkjha"));
  }

}
