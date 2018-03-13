package json.parser;

import org.json.simple.JSONObject;
import org.json.JSONException;
import org.json.simple.JSONArray;

/**
 * Created by sohailpatel on 3/11/18.
 */

public class ParseJson {

    protected String getJSONString(JSONObject jsonObject, String key){
        return jsonObject.get(key).toString();
    }

    protected int getJSONInt(JSONObject jsonObject, String key){
        return (int)jsonObject.get(key);
    }

    protected JSONArray getJSONArray(JSONObject jsonObject, String key){
        return (JSONArray)jsonObject.get(key);
    }

    protected JSONObject getJSONObject(JSONObject jsonObject, String key){
        return (JSONObject)jsonObject.get(key);
    }
}
