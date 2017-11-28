package swssm.fg.bi_box.model;

import java.util.List;

public class Geometry {
	String type = "";
	public List<String> coordinates;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<String> coordinates) {
		this.coordinates = coordinates;
	}
	
}
