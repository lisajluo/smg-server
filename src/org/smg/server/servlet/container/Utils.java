
package org.smg.server.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class Utils {

    /**
     * Get the content for a post request.
     * 
     * @param request A request object from post request.
     * @return Content as a String.
     * @throws IOException
     */
    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    /**
     * Convert List or Map structure into JSONObject or JSONArray.
     * 
     * @param object A map or list structure.
     * @return JSONObject or JSONArray.
     * @throws JSONException
     */
    @SuppressWarnings("rawtypes")
    public static Object toJSON(Object object) throws JSONException {
        if (object instanceof Map) {
            JSONObject json = new JSONObject();
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), toJSON(map.get(key)));
            }
            return json;
        } else if (object instanceof Iterable) {
            JSONArray json = new JSONArray();
            for (Object value : ((Iterable) object)) {
                json.put(value);
            }
            return json;
        } else {
            return object;
        }
    }

    /**
     * Judge if the names of the JSONObject is empty.
     * 
     * @param object JSON object to be judged.
     * @return
     */
    public static boolean isEmptyObject(JSONObject object) {
        return object.names() == null;
    }

    /**
     * Convert a specific field in JSONObject to map structure.
     * 
     * @param object JSONObject to be processed.
     * @param key Specific field in JSON.
     * @return A parsed Map structure for specific field in JSONObject.
     * @throws JSONException
     */
    public static Map<String, Object> getMap(JSONObject object, String key) throws JSONException {
        return toMap(object.getJSONObject(key));
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    /**
     * Encode playerId and gameId into channel id.
     * 
     * @param playerId
     * @param gameId
     * @return
     */
    public static String encodeToChannelId(String playerId, String gameId) {
        return playerId + "," + gameId;
    }

    /**
     * Decode channel id into original format.
     * 
     * @param encodeId Encoded channel id.
     * @return Decoded original id format.
     */
    public static String[] decodeChannel(String encodeId) {
        return encodeId.split(",");
    }
}
