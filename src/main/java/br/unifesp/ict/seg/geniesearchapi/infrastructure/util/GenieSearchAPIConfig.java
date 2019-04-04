package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import java.io.File;
import java.net.URL;
import java.util.Properties;

public class GenieSearchAPIConfig {

	private static Properties properties;

	private GenieSearchAPIConfig() {
	}


	private static void loadProperties() throws Exception {
		if (properties != null)
			return;

		properties = new Properties();
        URL url = ClassLoader.getSystemResource("geniesearchapi.properties");
        properties.load(url.openStream());
	}

	private static Properties getProperties() throws Exception {
		loadProperties();
		return properties;
	}
	
	private static String getProperty(String key) throws Exception {
		return getProperties().getProperty(key);
	}
	
	public static String REPO_PATH() throws Exception {
		return getProperty("repo.path");
	}
	
	public static String DATABASE_URL() throws Exception {
		return getProperty("database.url");
	}

	public static String DATABASE_USER() throws Exception {
		return getProperty("database.user");
	}

	public static String DATABASE_PASSWORD() throws Exception {
		return getProperty("database.password");
	}

	public static String WEBSERVER_URL() throws Exception {
		return getProperty("webserver.url");
	}
	
	public static String getCrawledProjectsPath() throws Exception {
		return REPO_PATH() + File.pathSeparator + "crawled-projects";
	}

	public static String getRelatedWordsServiceURL() throws Exception {
		return WEBSERVER_URL() + "/related-words-service";
	}

}