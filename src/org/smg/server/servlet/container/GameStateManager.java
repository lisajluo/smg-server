
package org.smg.server.servlet.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smg.server.servlet.container.GameApi.Operation;

public class GameStateManager {
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
}
