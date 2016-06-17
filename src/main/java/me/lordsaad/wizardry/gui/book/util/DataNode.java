package me.lordsaad.wizardry.gui.book.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class DataNode {

	protected EnumNodeType type;
	
	protected String stringValue;
	protected Map<String, DataNode> mapValue;
	protected List<DataNode> listValue;
	
	public DataNode(String value) {
		type = EnumNodeType.STRING;
		stringValue = value;
	}
	
	public DataNode(Map<String, DataNode> value) {
		type = EnumNodeType.MAP;
		mapValue = value;
	}
	
	public DataNode(List<DataNode> value) {
		type = EnumNodeType.LIST;
		listValue = value;
	}
	
	public boolean exists() {
		return true;
	}
	
	public boolean isString() {
		return type == EnumNodeType.STRING;
	}
	
	public boolean isList() {
		return type == EnumNodeType.LIST;
	}
	
	public boolean isMap() {
		return type == EnumNodeType.MAP;
	}
	
	public String asStringOr(String defaultValue) {
		if(!isString())
			return defaultValue;
		return stringValue;
	}
	
	public String asString() {
		return asStringOr(null);
	}
	
	public Map<String, DataNode> asMap() {
		if(!isMap())
			return ImmutableMap.of();
		return mapValue;
	}
	
	public List<DataNode> asList() {
		if(!isList())
			return ImmutableList.of();
		return listValue;
	}
	
	public DataNode get(String key) {
		if(!isMap() || !mapValue.containsKey(key))
			return NULL;
		return mapValue.get(key);
	}
	
	public DataNode get(int index) {
		if(!isList() || index < 0 || index >= listValue.size())
			return NULL;
		return listValue.get(index);
	}
	
	public DataNode getValue(String... path) {
		DataNode node = this;
		
		for (String part : path) {
			if(node == NULL)
				break;
			node = node.get(part);
		}
		return node;
	}
	
	@Override
	public String toString() {
		return "DataNode(" + type + ")-[" + ( type == EnumNodeType.MAP ? mapValue : type == EnumNodeType.LIST ? listValue : stringValue ) + "]";
	}
	
	public static final DataNode NULL = new DataNodeNull();
	
	private static class DataNodeNull extends DataNode {
		public DataNodeNull() {
			super("");
			this.type = null;
			this.listValue = null;
			this.mapValue = null;
		}
		
		@Override
		public boolean exists() {
			return false;
		}
		
		public String asStringOr(String defaultValue) {
			return defaultValue;
		}
		
		public String asString() {
			return asStringOr(null);
		}
		
		public Map<String, DataNode> asMap() {
			return ImmutableMap.of();
		}
		
		public List<DataNode> asList() {
			return ImmutableList.of();
		}
		
		public DataNode get(String key) {
			return NULL;
		}
		
		public DataNode get(int index) {
			return NULL;
		}
		
		public DataNode getValue(String... path) {
			return NULL;
		}
	}
	
	private static enum EnumNodeType {
		STRING, LIST, MAP;
	}
	
}