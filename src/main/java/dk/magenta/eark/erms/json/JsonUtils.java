package dk.magenta.eark.erms.json;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import dk.magenta.eark.erms.Constants;

/**
 * Various JSON utilities
 * @author andreas
 *
 */
public class JsonUtils {

	/**
	 * Checks if a JSON object contains the given keys
	 * @param json The JSON object to check
	 * @param keys The keys that should be present
	 * @return
	 */
	public static boolean containsCorrectKeys(JsonObject json, String... keys) {
		boolean success = true;
		for (String key : keys) {
			if (!json.containsKey(key)) {
				return false;
			}
		}
		return success;
	}
	
	public static JsonObjectBuilder addKeyErrorMessage(JsonObjectBuilder builder, String... keys) {
		StringBuilder stringBuilder = new StringBuilder("The following JSON keys are mandatory: ");
		for (String key : keys) {
			stringBuilder.append(key).append(" ,");
		}
		builder.add(Constants.SUCCESS, false);
		builder.add(Constants.ERRORMSG, stringBuilder.toString());
		return builder;
	}
	
	/**
	 * 
	 * @param json
	 * @param key
	 * @return true if value corresponding to key is an JsonArray which is not empty
	 */
	public static boolean isArrayNoneEmpty(JsonObject json, String key) {
		JsonArray array;
		try {
			array = json.getJsonArray(key);
		} catch (ClassCastException e) {
			return false;
		}
		if (array.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param json
	 * @param key
	 * @return true if value corresponding to key is an JsonArray
	 */
	public static boolean isArray(JsonObject json, String key) {
		JsonArray array;
		try {
			array = json.getJsonArray(key);
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}
	
	
	public static JsonObjectBuilder addArrayErrorMessage(JsonObjectBuilder builder, String key) {
		builder.add(Constants.SUCCESS, false);
		builder.add(Constants.ERRORMSG, "The array given in " + key + " is not valid");
		return builder;
	}
	
}
