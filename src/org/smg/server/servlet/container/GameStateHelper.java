
package org.smg.server.servlet.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smg.server.servlet.container.GameApi.Operation;
import org.smg.server.servlet.container.GameApi.Set;
import org.smg.server.util.JSONUtil;

import com.google.common.collect.Lists;

public class GameStateHelper {
  @SuppressWarnings("unchecked")
  public static List<Operation> messageToOperationList(Object operationMessagesObj) {
    List<?> operationMessages = (List<?>) operationMessagesObj;
    List<Operation> operations = new ArrayList<>();
    for (Object operationMessage : operationMessages) {
      operations
          .add((Operation) GameApi.Message
              .messageToHasEquality((Map<String, Object>) operationMessage));
    }
    return operations;
  }

  @SuppressWarnings("unchecked")
  public static List<Operation> getOperationsListForPlayer(List<Operation> ops,
      Map<String, Object> visibleTo, String playerId) {
    List<Operation> rtn = Lists.newArrayList();
    for (Operation op : ops) {
      if (op instanceof Set) {
        Set set = (Set) op;
        String key = set.getKey();
        if (visibleTo.containsKey(key)) {
          Object visibleToObj = visibleTo.get(key);
          if (visibleToObj instanceof List) {
            if (!((List<String>) visibleToObj).contains(playerId)) {
              set.setValue(null);;
            }
          }
        }
      }
      rtn.add(op);
    }
    return rtn;
  }
  
  public static List<Operation> getOperationsListForPlayer(List<Operation> ops, String playerId) {
    List<Operation> rtn = Lists.newArrayList();
    for (Operation op : ops) {
      if (op instanceof Set) {
        Set set = (Set) op;
        String visibleTo = String.valueOf(set.getVisibleToPlayerIds());
        if (!visibleTo.equals("ALL")) {
           List<String> visibleToList = null;
          try {
            visibleToList = JSONUtil.parsePlayerIds(visibleTo);
          } catch (IOException e) {
            e.printStackTrace();
          }
          if (!visibleToList.isEmpty() && !visibleToList.contains(playerId)) {
            set.setValue(null);
          }
        }
      }
      rtn.add(op);
    }
    return rtn;
  }
}
