package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableChildrenList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6L;
	private ArrayList<ChildSelectable> list;

	public ArrayList<ChildSelectable> getList() {
		return list;
	}

	public void setList(ArrayList<ChildSelectable> list) {
		this.list = list;
	}
}
