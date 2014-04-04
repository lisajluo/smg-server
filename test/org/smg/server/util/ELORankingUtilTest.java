package org.smg.server.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ELORankingUtilTest {

  @Test
  public void test() {
    System.out.println(ELORankingUtil.expectScore(1500,1500));
    System.out.println(ELORankingUtil.expectScore(1600,1400));
    System.out.println(ELORankingUtil.expectScore(1400,1600));
  }
}
