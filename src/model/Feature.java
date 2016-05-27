package model;

import java.io.Serializable;

public class Feature implements Serializable {
	private static final long serialVersionUID = 1L;
	private int index;
	private String grupo;
	private String item;

	public Feature(int index, String grupo, String item) {
		this.index = index;
		this.grupo = grupo;
		this.item = item;
	}

	public int getIndex() {
		return index;
	}

	public String getGrupo() {
		return grupo;
	}

	public String getItem() {
		return item;
	}
}
