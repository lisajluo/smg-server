
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
    /**
     * Parse the operation message list to operation list.
     * 
     * @param operationMessagesObj An object of a list of message.
     * @return Operation list.
     */
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

    /**
     * Based on the field "visibleTo" to generate a operation list for specific
     * player.
     * 
     * @param ops original operation list.
     * @param visibleTo visiableTo list for operations.
     * @param playerId The id of player who you want get a operation list for.
     * @return operation list for specific player.
     */
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
                            set.setValue(null);
                        }
                    }
                }
            }
            rtn.add(op);
        }
        return rtn;
    }

    /**
     * Based on the field "visibleTo" to generate a operation list for specific
     * player.
     * 
     * @param original operation list
     * @param playerId The id of player who you want get a operation list for.
     * @return operation list for specific player.
     */
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
