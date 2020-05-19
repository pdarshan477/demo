package com.orataro.Model;

public class Property_vo {

	String name;
	Object value;
	
	
	public Property_vo(String name, Object value) {
		// TODO Auto-generated constructor stub
		setName(name);
		setValue(value);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
