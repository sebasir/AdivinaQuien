package controller;

public enum Urls {
	databaseUrl("0xRTAuJesiavOHp4hP0I2XmzEpmcc6rsaAgLvvm6Wt3ZVHR8uWBtCJD8npztxfvTXUAJ7qP0Lew="),
	databaseUser("U5mBQVJDzW6SfiNi9L+YcA=="),
	databasePass("GSTlwkQaf5D0qLHYV/sA1Q=="),
	publicKey("_4d1v1n4Qu13n"),
	algorithm("PBEWithMD5AndDES"),
	dot("."),
	basicPass("123"),
	charset("ISO-8859-1");

	private String url;

	private Urls(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}