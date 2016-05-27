package controller;

public enum Urls {
	databaseUrl("hklxubXscKAHSXKdbl86kZSQ8YFzaHtwJ/5YsCckiUV3Euv0ORKRWQWd4wIgOMYJ590hHnaf68M="),
	databaseUser("SGu9S7t369TgcPbLxZLHjw=="),
	databasePass("w+mWz+7ZLHD61IMz1xhMSw=="),
	publicKey("_4di1v1n4Qu13n"),
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