
package org.smg.server.servlet.container;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.smg.server.servlet.container.GameApi.GameState;
import org.smg.server.servlet.container.GameApi.Operation;
import org.smg.server.util.CORSUtil;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("serial")
public class MatchOperationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        PrintWriter res = resp.getWriter();
        MatchInfoManager mim = MatchInfoManager.getInstance();
        GameStateManager gsm = GameStateManager.getInstance();

        CORSUtil.addCORSHeader(resp);
        int matchId = Integer.parseInt(req.getPathInfo().substring(1));

        resp.setHeader("Content-Type", "application/json");
        String accessSignature = req.getParameter(Constants.JSON_ACCESS_SIGNATURE);

        MatchInfo mi = mim.getMatchInfo(matchId);
        Map<String, Object> returnJsonMap = mi.toMap();
        returnJsonMap.put(Constants.JSON_HISTORY, gsm.getHistoryState(matchId, accessSignature));

        String rtnJson = new ObjectMapper().writeValueAsString(returnJsonMap);
        res.print(rtnJson);
        res.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PrintWriter res = resp.getWriter();
        CORSUtil.addCORSHeader(resp);

        /*
         * TODO Query info with matchId.
         */
        String matchId = req.getPathInfo().substring(1);

        resp.setHeader("Content-Type", "application/json");
        try {
            String jsonString = Utils.getBody(req);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(jsonString,
                    new TypeReference<Map<String, Object>>() {
                    });

            // Get GameState for current player.
            GameStateManager gsm = GameStateManager.getInstance();
            String accessSignature = (String) jsonMap.get(Constants.JSON_ACCESS_SIGNATURE);
            GameState gameState = gsm.getGameStateByAS(accessSignature);

            // Convert json string to Operation list.
            List<Map<String, String>> operationsList;

            operationsList = (List<Map<String, String>>) jsonMap.get(Constants.JSON_OPERATIONS);
            List<Operation> operations = GameStateManager
                    .messageToOperationList(operationsList);

            // Generate return data.
            gameState.makeMove(operations);
            int playerId = gsm.getPlayerIdByAccessSignature(accessSignature);
            Map<String, Object> returnState = gameState.getStateForPlayerId(playerId);

            Map<String, Object> returnJsonMap = ImmutableMap.<String, Object> of(
                    Constants.JSON_MATCH_ID, matchId, Constants.JSON_GAME_STATE, returnState);

            String rtnJson = mapper.writeValueAsString(returnJsonMap);
            res.print(rtnJson);
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Parse failed. Json not valid.
            res.print(Constants.JSON_FAILED);
            res.close();
            return;
        }
    }
}
