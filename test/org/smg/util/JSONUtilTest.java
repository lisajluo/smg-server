package org.smg.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.smg.server.util.JSONUtil;

public class JSONUtilTest {

  @Test
  public void testParsePlayerIds() throws IOException {
    List<String> list = JSONUtil.parsePlayerIds("[12345677777,77889000998]");
    System.out.print(list);
  }
  
  @Test
  public void testParseHistory() throws IOException {
    List<HashMap<String,Object>> list = JSONUtil.parseHistory(
        "[{\"gameState\":{"
            + "\"state\":{},"
            + "\"visibleTo\":{},\"playerIdToNumberOfTokensInPot\":{}},"
         + "\"lastMove\":["
            + "{\"value\":\"sd\",\"type\":\"Set\",\"visibleToPlayerIds\":\"ALL\",\"key\":\"k\"},"
            + "{\"to\":54,\"from\":23,\"type\":\"SetRandomInteger\",\"key\":\"xcv\"}]}]");
    System.out.print(list);
  }
  
}
