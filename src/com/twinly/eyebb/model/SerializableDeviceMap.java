package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.HashMap;

public class SerializableDeviceMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private HashMap<String, Device> map;

	public HashMap<String, Device> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Device> macaronMap) {
		this.map = macaronMap;
	}
}
