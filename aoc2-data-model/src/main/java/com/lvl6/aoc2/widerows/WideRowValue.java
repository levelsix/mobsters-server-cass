package com.lvl6.aoc2.widerows;

public class WideRowValue<Ky, Col, Val> {
	protected Ky key;
	protected Col column;
	protected Val value;
	
	public Ky getKey() {
		return key;
	}
	public WideRowValue<Ky, Col, Val>  setKey(Ky key) {
		this.key = key;
		return this;
	}
	public Col getColumn() {
		return column;
	}
	public WideRowValue<Ky, Col, Val> setColumn(Col column) {
		this.column = column;
		return this;
	}
	public Val getValue() {
		return value;
	}
	public WideRowValue<Ky, Col, Val> setValue(Val value) {
		this.value = value;
		return this;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		WideRowValue<Ky, Col, Val>  other = (WideRowValue<Ky, Col, Val>) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "WideRowValue [key=" + key + ", column=" + column + ", value=" + value + "]";
	}
	
	public WideRowValue(Ky key, Col column, Val value) {
		super();
		this.key = key;
		this.column = column;
		this.value = value;
	}

	public WideRowValue() {
		super();
	}
	
}
