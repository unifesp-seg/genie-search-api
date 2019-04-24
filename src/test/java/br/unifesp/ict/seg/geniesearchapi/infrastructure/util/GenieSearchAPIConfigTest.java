package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class GenieSearchAPIConfigTest {
	
	@Test
	public void loadFromFile() throws Exception {
		assertEquals(Paths.get("D:/Sourcerer_portable/repositories/sf_110/crawled-projects")+"", GenieSearchAPIConfig.getCrawledProjectsPath()+"");
		assertEquals("jdbc:mysql://localhost:3306/sf_110", GenieSearchAPIConfig.getDatabaseURL());
		assertEquals("root", GenieSearchAPIConfig.getDatabaseUser());
		assertEquals("123", GenieSearchAPIConfig.getDatabasePassword());
		assertEquals("http://localhost:8080", GenieSearchAPIConfig.getWebServerURL());
	}

	@Test
	public void checkFoldersExistence() throws Exception {
		
		assertTrue(GenieSearchAPIConfig.getRepoPath().toFile().isDirectory());
		assertTrue(GenieSearchAPIConfig.getCrawledProjectsPath().toFile().isDirectory());
		assertTrue(GenieSearchAPIConfig.getSolrConfigPath().toFile().isDirectory());
		assertTrue(GenieSearchAPIConfig.getSolrIndexPath().toFile().isDirectory());
		assertTrue(GenieSearchAPIConfig.getSlicedPath().toFile().isDirectory());
		assertTrue(GenieSearchAPIConfig.getExtractTempPath().toFile().isDirectory());
		assertTrue(GenieSearchAPIConfig.getJarPath().toFile().isDirectory());
		assertTrue(GenieSearchAPIConfig.getThesauriPath().toFile().isDirectory());
		
		assertEquals(GenieSearchAPIConfig.getCrawledProjectsPath().getParent()+"", GenieSearchAPIConfig.getRepoPath().toString());
		assertEquals("crawled-projects", GenieSearchAPIConfig.getCrawledProjectsPath().getFileName().toString());
	}

	@Test
	public void checkOnlineURLs() {
		
		URL url;
		try {
			url = new URL(GenieSearchAPIConfig.getWebServerURL());
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			assertEquals(200, connection.getResponseCode());
			
			url = new URL(GenieSearchAPIConfig.getSolrURL());
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
	
			connection.connect();
			assertEquals(200, connection.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		
		}
	}

	@Test
	public void checkSolrHomeFolder() throws Exception {
		Path solrHomePath = GenieSearchAPIConfig.getSolrConfigPath();
		assertNotNull(solrHomePath);
		assertEquals(solrHomePath, GenieSearchAPIConfig.getSolrConfigPath());
	}

	@Test
	public void checkSolrReaderDir() throws Exception {
		Path solrReaderDirPath = GenieSearchAPIConfig.getSolrReaderDirPath();
		assertNotNull(solrReaderDirPath);
		assertEquals(solrReaderDirPath, GenieSearchAPIConfig.getSolrIndexPath());
	}

	@Test
	public void checkSolrNumDocs() throws Exception {
		assertEquals(new Integer(192421), GenieSearchAPIConfig.getSolrNumDocs());
	}
}