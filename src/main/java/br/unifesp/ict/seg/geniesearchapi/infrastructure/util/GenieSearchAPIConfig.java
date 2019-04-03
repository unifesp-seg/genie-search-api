package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import java.io.File;
import java.net.URL;
import java.util.Properties;

public class GenieSearchAPIConfig {

	private static Properties properties;

	private GenieSearchAPIConfig() {
	}


//TODO pode-se remover este bloco comentado?
//	public static void setProperties(Properties externalProperties) {
//		if (properties != null)
//			return;
//		
//		properties = externalProperties;
//	}
//	
//	public static void setProperties(String resourceFileName) throws Exception {
//		if (properties != null)
//			return;
//
//		properties = new Properties();
//        URL url = ClassLoader.getSystemResource(resourceFileName);
//        properties.load(url.openStream());
//	}
//
//	public static void clearProperties() {
//		properties = null;
//	}

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

	public static boolean AQE_TAG_CLOUD() throws Exception {
		return new Boolean(getProperty("aqe.tagCloud"));
	}

	public static String AQE_EXPANDERS() throws Exception {
		return getProperty("aqe.expanders");
	}

	public static boolean AQE_RELAX_RETURN() throws Exception {
		return new Boolean(getProperty("aqe.relaxReturn"));
	}

	public static boolean AQE_RELAX_PARAMS() throws Exception {
		return new Boolean(getProperty("aqe.relaxParams"));
	}

	public static boolean AQE_CONTEXT_RELEVANTS() throws Exception {
		return new Boolean(getProperty("aqe.contextRelevants"));
	}

	public static boolean AQE_FILTER_METHOD_NAME_TERMS_BY_PARAMETER() throws Exception {
		return new Boolean(getProperty("aqe.filterMethodNameTermsByParameter"));
	}

	public static boolean AQE_MORE_ONE_RELAVANT() throws Exception {
		return new Boolean(getProperty("aqe.moreOneRelevant"));
	}
	
	public static String getCrawledProjectsPath() throws Exception {
		return REPO_PATH() + File.pathSeparator + "crawled-projects";
	}

	public static String getRelatedWordsServiceURL() throws Exception {
		return WEBSERVER_URL() + "/related-words-service";
	}

}