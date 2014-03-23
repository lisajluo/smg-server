
package org.smg.server.servlet.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smg.server.servlet.container.GameApi.GameState;
import org.smg.server.servlet.container.GameApi.Operation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GameStateManager {
    private static GameStateManager instance = null;

    private Map<String, GameState> gameStateMap;
    private Map<String, Integer> playerAsIdMap;

    private GameStateManager() {
        gameStateMap = Maps.newConcurrentMap();
        playerAsIdMap = Maps.newConcurrentMap();

        // For test only.
        generateTestData();
    }

    private void generateTestData() {
        String as = "HASHACCESSSIGNATURE";
        gameStateMap.put(as, new GameState());
        playerAsIdMap.put(as, 42);
    }

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public GameState getGameStateByAS(String accessSignature) throws NotFoundGameStateException {
        if (!gameStateMap.containsKey(accessSignature)) {
            throw new NotFoundGameStateException();
        }
        return gameStateMap.get(accessSignature);
    }

    public void addGameState(String accessSignature, GameState gs) {
        gameStateMap.put(accessSignature, gs);
    }

    public void removeGameState(String accessSignature) {
        gameStateMap.remove(accessSignature);
    }

    public static class NotFoundGameStateException extends Exception {
    }

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

    public int getPlayerIdByAccessSignature(String as) {
        return playerAsIdMap.get(as);
    }

    /*
     * TODO implement this.
     */
    public List<GameState> getHistoryState(int matchId, String accessSignature) {
        return Lists.newArrayList();
    }
}
