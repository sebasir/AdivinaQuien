package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Personage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int index;
	private String nombre;
	private byte[] imagen;
	private ArrayList<Feature> features;

	public Personage(int index, String nombre) {
		this.index = index;
		this.nombre = nombre;
	}

	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}

	public ArrayList<Feature> getFeature() {
		return features;
	}

	public int getIndex() {
		return index;
	}

	public String getNombre() {
		return nombre;
	}

	public byte[] getImagen() {
		return imagen;
	}

	public void setImagen(byte[] imagen) {
		this.imagen = imagen;
	}
}
