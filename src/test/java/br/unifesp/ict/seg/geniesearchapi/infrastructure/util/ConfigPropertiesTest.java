package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import static org.junit.Assert.assertEquals;
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
	}

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
	}

	@Test
	//TODO implementar
	public void checkDatabaseConnection() throws Exception {
		

		//assert
	}
}