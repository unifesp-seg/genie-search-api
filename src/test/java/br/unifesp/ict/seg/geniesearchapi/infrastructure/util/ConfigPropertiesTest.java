package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class ConfigPropertiesTest {
	
	@Test
	public void loadFromFile() throws Exception {
		assertEquals("D:/Sourcerer_portable/repositories/test_repo", GenieSearchAPIConfig.REPO_PATH());
		assertEquals("jdbc:mysql://localhost:3306/test_repo", GenieSearchAPIConfig.DATABASE_URL());
		assertEquals("root", GenieSearchAPIConfig.DATABASE_USER());
		assertEquals("123", GenieSearchAPIConfig.DATABASE_PASSWORD());
		assertEquals("http://localhost:8080", GenieSearchAPIConfig.WEBSERVER_URL());
		assertFalse(GenieSearchAPIConfig.AQE_TAG_CLOUD());
		assertEquals("WordNet , CodeVocabulary , Type", GenieSearchAPIConfig.AQE_EXPANDERS());
		assertFalse(GenieSearchAPIConfig.AQE_RELAX_RETURN());
		assertFalse(GenieSearchAPIConfig.AQE_RELAX_PARAMS());
		assertTrue(GenieSearchAPIConfig.AQE_CONTEXT_RELEVANTS());
		assertTrue(GenieSearchAPIConfig.AQE_FILTER_METHOD_NAME_TERMS_BY_PARAMETER());
		assertTrue(GenieSearchAPIConfig.AQE_MORE_ONE_RELAVANT());
	}
	
//TODO pode-se remover este bloco comentado?
//	@Test
//	public void loadFromProperties() throws Exception {
//		Properties properties = new Properties();
//		properties.put("repo.path", "D:/Sourcerer_portable");
//		properties.put("database.url", "jdbc:mysql://localhost:3306");
//		properties.put("database.user", "root2");
//		properties.put("database.password", "1234");
//		properties.put("webserver.url", "http://localhost");
//		properties.put("aqe.tagCloud", "true");
//		properties.put("aqe.expanders", "WordNet , CodeVocabulary");
//		properties.put("aqe.relaxReturn", "true");
//		properties.put("aqe.relaxParams", "true");
//		properties.put("aqe.contextRelevants", "true");
//		properties.put("aqe.filterMethodNameTermsByParameter", "true");
//		properties.put("aqe.moreOneRelevant", "true");
//
//		ConfigProperties.clearProperties();
//		ConfigProperties.setProperties(properties);
//		
//		assertEquals("D:/Sourcerer_portable", ConfigProperties.REPO_PATH());
//		assertEquals("jdbc:mysql://localhost:3306", ConfigProperties.DATABASE_URL());
//		assertEquals("root2", ConfigProperties.DATABASE_USER());
//		assertEquals("1234", ConfigProperties.DATABASE_PASSWORD());
//		assertEquals("http://localhost", ConfigProperties.WEBSERVER_URL());
//		assertTrue(ConfigProperties.AQE_TAG_CLOUD());
//		assertEquals("WordNet , CodeVocabulary", ConfigProperties.AQE_EXPANDERS());
//		assertTrue(ConfigProperties.AQE_RELAX_RETURN());
//		assertTrue(ConfigProperties.AQE_RELAX_PARAMS());
//		assertTrue(ConfigProperties.AQE_CONTEXT_RELEVANTS());
//		assertTrue(ConfigProperties.AQE_FILTER_METHOD_NAME_TERMS_BY_PARAMETER());
//		assertTrue(ConfigProperties.AQE_MORE_ONE_RELAVANT());
//	}
//
//	@Test
//	public void loadFromResourceFileName() throws Exception {
//
//		ConfigProperties.clearProperties();
//		ConfigProperties.setProperties("geniesearchapi2.properties");
//		
//		assertEquals("D:/Sourcerer_portable/repositories/test_repo2", ConfigProperties.REPO_PATH());
//		assertEquals("jdbc:mysql://localhost:3306/test_repo2", ConfigProperties.DATABASE_URL());
//		assertEquals("root2", ConfigProperties.DATABASE_USER());
//		assertEquals("1232", ConfigProperties.DATABASE_PASSWORD());
//		assertEquals("http://localhost:80802", ConfigProperties.WEBSERVER_URL());
//		assertTrue(ConfigProperties.AQE_TAG_CLOUD());
//		assertEquals("WordNet , CodeVocabulary , Type2", ConfigProperties.AQE_EXPANDERS());
//		assertTrue(ConfigProperties.AQE_RELAX_RETURN());
//		assertTrue(ConfigProperties.AQE_RELAX_PARAMS());
//		assertFalse(ConfigProperties.AQE_CONTEXT_RELEVANTS());
//		assertFalse(ConfigProperties.AQE_FILTER_METHOD_NAME_TERMS_BY_PARAMETER());
//		assertFalse(ConfigProperties.AQE_MORE_ONE_RELAVANT());
//	}

	@Test
	public void checkFoldersExistence() throws Exception {
		
		assertTrue(new File(GenieSearchAPIConfig.REPO_PATH()).isDirectory());
		assertTrue(new File(GenieSearchAPIConfig.REPO_PATH(), "crawled-projects").isDirectory());
		assertTrue(new File(GenieSearchAPIConfig.REPO_PATH(), "solr-repo/conf").isDirectory());
		assertTrue(new File(GenieSearchAPIConfig.REPO_PATH(), "solr-repo/data/index").isDirectory());
		
	}

	@Test
	public void checkOnlineURLs() throws Exception {
		
		URL url = new URL(GenieSearchAPIConfig.WEBSERVER_URL());
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		assertEquals(200, connection.getResponseCode());
		
		url = new URL(GenieSearchAPIConfig.WEBSERVER_URL()+"/solr");
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		assertEquals(200, connection.getResponseCode());

		url = new URL(GenieSearchAPIConfig.getRelatedWordsServiceURL());
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		assertEquals(200, connection.getResponseCode());
	}

	@Test
	//TODO implementar
	public void checkDatabaseConnection() throws Exception {
		

		//assert
	}
}