package www.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {
	
	public static JSONObject str2json(String InputStr) {
		// TODO Auto-generated method stub
		JSONObject json = null;
		try {
			json = new JSONObject(InputStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
    }
    public static JSONArray objectToArray(JSONObject object) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < object.length(); i++) {
            if (object.has(String.valueOf(i)))
                array.put(object.get(String.valueOf(i)));
        }
        return array;
    }
    public static JSONArray str2jsonArray(String InputStr) throws JSONException{
    	JSONObject json = str2json(InputStr);    
    	JSONArray jsonArray = objectToArray(json);
		return jsonArray;    	
    }
}