package swssm.fg.bi_box.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class GeometryDeserializer implements JsonDeserializer<Geometry> {

	public Geometry deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		/*
		 * Geometry r = null;
		 * 
		 * JsonElement coordiField = json.getAsJsonObject().get("coordinates");
		 * JsonElement coordiField2 = coordiField.getAsJsonArray();
		 * System.out.println("coordiField2 : "+coordiField2);
		 * 
		 * if (coordiField.isJsonNull() || coordiField.isJsonPrimitive()){
		 * System.out.println("1"); }else if (coordiField.isJsonObject()) {
		 * System.out.println("2"); r = new Geometry();
		 * r.coordinates.add((Number) context.deserialize(coordiField,
		 * String.class)); } else if (coordiField.isJsonArray()) {
		 * System.out.println("3"); System.out.println("coordiField1 : "
		 * +coordiField.getAsInt()); System.out.println("coordiField2: "
		 * +coordiField.getAsString()); System.out.println("coordiField3 : "
		 * +coordiField.getAsJsonArray()); System.out.println("coordiField4 : "
		 * +coordiField.getAsJsonObject()); Type listOfUserType = new
		 * TypeToken<List<Number>>() {}.getType(); r = new Geometry();
		 * 
		 * 
		 * } return r;
		 */
		Geometry geometry = new Geometry();
		JsonObject jsonObject = json.getAsJsonObject();
		String type = "";
		List<String> list = new ArrayList<String>();
		type = jsonObject.get("type").toString();
		type = type.substring(1, type.length() - 1);
		if (type.equals("LineString")) {
			JsonArray coordinates = jsonObject.get("coordinates").getAsJsonArray();
//			System.out.println("coordinates.get(0).getAsJsonArray().toString()"
//					+ coordinates.get(0).getAsJsonArray().toString());
//			System.out.println("coordinates.size()" + coordinates.size());
//			System.out.println("coordinates.get(0).getAsJsonArray().size()"
//					+ coordinates.get(0).getAsJsonArray().size());
			/**
			 * "coordinates":[ "127.032680", "37.260549", "128.032680","37.260549" ]
			 */
			
			 for (int i = 0; i < coordinates.size(); i++) { 
				 for(int j=0;j<coordinates.get(i).getAsJsonArray().size();j++){
					 list.add(coordinates.get(i).getAsJsonArray().get(j).getAsString());
				 }
			 }
			 
			
		/*	
			for (int i = 0; i < coordinates.size(); i++) {
				//for (int j = 0; j < coordinates.get(i).getAsJsonArray().size(); j++) {
					list.add(coordinates.get(i).getAsJsonArray().toString());
				//}
			}
		 */
		} else {
			JsonArray coordinates = jsonObject.get("coordinates").getAsJsonArray();
//			System.out.println("coordinates" + coordinates);
//			System.out.println("coordinates" + coordinates.size());
			for (int i = 0; i < coordinates.size(); i++) {
				list.add(coordinates.get(i).getAsString());
			}
		}
		geometry.setCoordinates(list);
		geometry.setType(type);
		return geometry;

	}
}
