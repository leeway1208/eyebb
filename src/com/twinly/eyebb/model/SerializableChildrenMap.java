package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.Map;

public class SerializableChildrenMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Long, ChildForLocator> map;

	public Map<Long, ChildForLocator> getMap() {
		return map;
	}

	public void setMap(Map<Long, ChildForLocator> childrenMap) {
		this.map = childrenMap;
	}
}
