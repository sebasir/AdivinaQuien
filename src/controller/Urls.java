package controller;

public enum Urls {
	databaseServer("jdbc:postgresql://192.168.56.101:5432/adivinaQuien");

	private String url;

	private Urls(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}