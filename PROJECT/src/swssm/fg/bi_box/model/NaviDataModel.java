package swssm.fg.bi_box.model;

import java.util.List;

public class NaviDataModel {
	
	String type="";
	List<Feature> features;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

}
