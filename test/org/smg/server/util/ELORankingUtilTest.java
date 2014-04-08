package org.smg.server.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ELORankingUtilTest {

  @Test
  public void test() {
    System.out.println(ELORankingUtil.expectScore(1500,1575));
    System.out.println(ELORankingUtil.expectScore(1484,1500));
    System.out.println(ELORankingUtil.expectScore(1600,1500));
  }
}
