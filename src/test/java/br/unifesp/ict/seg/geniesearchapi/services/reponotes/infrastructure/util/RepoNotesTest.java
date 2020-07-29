package br.unifesp.ict.seg.geniesearchapi.services.reponotes.infrastructure.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;
import br.unifesp.ict.seg.geniesearchapi.services.reponotes.infrastructure.RepoNotesRepository;

public class RepoNotesTest {

	private RepoNotesRepository repoNotesRepository = new RepoNotesRepository();

	@Before
	public void initialize() throws Exception {
		GenieSearchAPIConfig.loadProperties();
	}

	@Test
	public void checkFileExistence() throws Exception {
		assertTrue(GenieSearchAPIConfig.getRepoNotesPath().toFile().isFile());
	}

	@Test
	public void checkPropertiesKeys() throws Exception {
		assertEquals(30, RepoNotes.getProperties().size());
		assertTrue(RepoNotes.getProperties().containsKey("[sourcerer_portable]"));
		assertTrue(RepoNotes.getProperties().containsKey("[novo_repo]"));
		assertTrue(RepoNotes.getProperties().containsKey("[repo-path]"));
		assertTrue(RepoNotes.getProperties().containsKey("Indexar os projetos no Solr"));
		assertTrue(RepoNotes.getProperties().containsKey("crawled-projects-files"));
		assertTrue(RepoNotes.getProperties().containsKey("crawled-projects-folders"));
		assertTrue(RepoNotes.getProperties().containsKey("solr-repo-files"));
		assertTrue(RepoNotes.getProperties().containsKey("solr-repo-folders"));
		assertTrue(RepoNotes.getProperties().containsKey("comments"));
		assertTrue(RepoNotes.getProperties().containsKey("entities"));
		assertTrue(RepoNotes.getProperties().containsKey("entity_metrics"));
		assertTrue(RepoNotes.getProperties().containsKey("file_metrics"));
		assertTrue(RepoNotes.getProperties().containsKey("files"));
		assertTrue(RepoNotes.getProperties().containsKey("imports"));
		assertTrue(RepoNotes.getProperties().containsKey("problems"));
		assertTrue(RepoNotes.getProperties().containsKey("project_metrics"));
		assertTrue(RepoNotes.getProperties().containsKey("projects"));
		assertTrue(RepoNotes.getProperties().containsKey("relations"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_filter"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_inner"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_pairs"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_pairs_clone_10"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_pairs_inner"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_params"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_top"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_types"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_test"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_params_test"));
		assertTrue(RepoNotes.getProperties().containsKey("interface_metrics_pairs_test"));
	}

	@Test
	public void checkPropertiesPathValues() throws Exception {
		assertEquals(Paths.get(RepoNotes.getProperties().getProperty("[sourcerer_portable]")) + "", GenieSearchAPIConfig.getRepoPath().getParent().getParent() + "");
		assertEquals(RepoNotes.getProperties().getProperty("[novo_repo]"), GenieSearchAPIConfig.getRepoName());
		assertEquals(Paths.get(RepoNotes.getProperties().getProperty("[repo-path]")) + "", GenieSearchAPIConfig.getRepoPath() + "");
	}

	@Test
	public void checkPropertiesSolrDocuments() throws Exception {
		assertEquals(RepoNotes.getProperties().getProperty("Indexar os projetos no Solr"), GenieSearchAPIConfig.getSolrNumDocs()+"");
	}

	@Test
	public void checkPropertiesFileValues() throws Exception {
		
		//crawledProjectsPath
		Path crawledProjectsPath = GenieSearchAPIConfig.getCrawledProjectsPath();
		long totalFiles = Files.walk(crawledProjectsPath).parallel().filter(p -> !p.toFile().isDirectory()).count();
//		assertEquals(totalFiles+"",RepoNotes.getProperties().getProperty("crawled-projects-files"));
		long totalFoldes = Files.walk(crawledProjectsPath).filter(p -> Files.isDirectory(p) && ! p.equals(crawledProjectsPath)).count();
//		assertEquals(totalFoldes+"",RepoNotes.getProperties().getProperty("crawled-projects-folders"));
		
		//solrHomePath
		Path solrHomePath = GenieSearchAPIConfig.getSolrHomePath();
		totalFiles = Files.walk(solrHomePath).parallel().filter(p -> !p.toFile().isDirectory()).count();
		assertEquals(totalFiles+"",RepoNotes.getProperties().getProperty("solr-repo-files"));
		totalFoldes = Files.walk(solrHomePath).filter(p -> Files.isDirectory(p) && ! p.equals(solrHomePath)).count();
		assertEquals(totalFoldes+"",RepoNotes.getProperties().getProperty("solr-repo-folders"));
	}

	@Test
	public void checkPropertiesBDValues() throws Exception {
		Map<String, Integer> map = repoNotesRepository.findAllCountTablesAndViews();
		assertEquals(22,map.size());
		for (String key : map.keySet()) {
			System.out.println(key + " = " + map.get(key));
			assertEquals(map.get(key), new Integer(RepoNotes.getProperties().get(key)+""));
		}
	}
}
