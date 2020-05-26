package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;
import edu.uci.ics.sourcerer.services.search.adapter.SearchResult;

public class SourcererQueryBuilderTest {

	@Before
	public void initialize() throws IOException {
		GenieSearchAPIConfig.loadProperties();
	}
	
	@Test
	public void querySintax() throws Exception {
		String m = "getSystemProperty";
		String p = "String";
		String r = "java.security.AccessController.doPrivileged";
		
		String expanders = "";
		boolean relaxReturn = false;
		boolean relaxParams = false;
		boolean contextRelevants = false;
		boolean filterMethodNameTermsByParameter = false;
		
		SourcererQueryBuilder sourcererQueryBuilder = new SourcererQueryBuilder(expanders, relaxReturn, relaxParams, contextRelevants, filterMethodNameTermsByParameter);
		String query = sourcererQueryBuilder.getSourcererExpandedQuery(m, r, p);
		
		String expectedQuery = "sname_contents:(( get^100 ) AND ( system^100 ) AND ( property^100 ))";
		expectedQuery += "\n" + "return_sname_contents:(( java.security.AccessController.doPrivileged ))";
		expectedQuery += "\n" + "param_count:1";
		expectedQuery += "\n" + "params_snames_contents:(( String ))";
		
		assertEquals(expectedQuery,query);
	}

	@Test
	public void aqeSintax() throws Exception {
		String m = "getSystemProperty";
		String p = "String";
		String r = "java.security.AccessController.doPrivileged";
		
		String expanders = "WordNet , CodeVocabulary , Type";
		boolean relaxReturn = false;
		boolean relaxParams = false;
		boolean contextRelevants = false;
		boolean filterMethodNameTermsByParameter = false;
		
		SourcererQueryBuilder sourcererQueryBuilder = new SourcererQueryBuilder(expanders, relaxReturn, relaxParams, contextRelevants, filterMethodNameTermsByParameter);
		String query = sourcererQueryBuilder.getSourcererExpandedQuery(m, r, p);
		
		String expectedQuery = "sname_contents:(( get^100 OR acquire OR become OR go OR let OR have OR receive OR find OR obtain OR incur OR arrive OR come OR bring OR convey OR fetch OR experience OR payBack OR payOff OR fix OR make OR induce OR stimulate OR cause OR catch OR capture OR grow OR develop OR produce OR contract OR take OR drive OR aim OR arrest OR scram OR buzzOff OR fuckOff OR buggerOff OR getUnderone'sSkin OR draw OR perplex OR vex OR stick OR puzzle OR mystify OR baffle OR beat OR pose OR bewilder OR flummox OR stupefy OR nonplus OR gravel OR amaze OR dumbfound OR getDown OR begin OR startOut OR start OR setAbout OR setOut OR commence OR suffer OR sustain OR beget OR engender OR father OR mother OR sire OR generate OR bringForth OR return ) AND !( leave OR take away OR end ) AND ( system^100 OR scheme OR systemOfrules OR arrangement OR organization OR organisation ) AND ( property^100 OR belongings OR holding OR place OR attribute OR dimension OR prop ))";
		expectedQuery += "\n" + "return_sname_contents:(( java.security.AccessController.doPrivileged ))";
		expectedQuery += "\n" + "param_count:1";
		expectedQuery += "\n" + "params_snames_contents:(( String ))";
		
		assertEquals(expectedQuery,query);
	}

	@Test
	public void aqeRelaxSintax() throws Exception {
		String m = "getSystemProperty";
		String p = "String";
		String r = "java.security.AccessController.doPrivileged";
		
		String expanders = "WordNet , CodeVocabulary , Type";
		boolean relaxReturn = true;
		boolean relaxParams = true;
		boolean contextRelevants = false;
		boolean filterMethodNameTermsByParameter = false;
		
		SourcererQueryBuilder sourcererQueryBuilder = new SourcererQueryBuilder(expanders, relaxReturn, relaxParams, contextRelevants, filterMethodNameTermsByParameter);
		String query = sourcererQueryBuilder.getSourcererExpandedQuery(m, r, p);
		
		String expectedQuery = "sname_contents:(( get^100 OR acquire OR become OR go OR let OR have OR receive OR find OR obtain OR incur OR arrive OR come OR bring OR convey OR fetch OR experience OR payBack OR payOff OR fix OR make OR induce OR stimulate OR cause OR catch OR capture OR grow OR develop OR produce OR contract OR take OR drive OR aim OR arrest OR scram OR buzzOff OR fuckOff OR buggerOff OR getUnderone'sSkin OR draw OR perplex OR vex OR stick OR puzzle OR mystify OR baffle OR beat OR pose OR bewilder OR flummox OR stupefy OR nonplus OR gravel OR amaze OR dumbfound OR getDown OR begin OR startOut OR start OR setAbout OR setOut OR commence OR suffer OR sustain OR beget OR engender OR father OR mother OR sire OR generate OR bringForth OR return ) AND !( leave OR take away OR end ) AND ( system^100 OR scheme OR systemOfrules OR arrangement OR organization OR organisation ) AND ( property^100 OR belongings OR holding OR place OR attribute OR dimension OR prop ))";
		
		assertEquals(expectedQuery,query);
	}
	
	@Test
	public void search() throws Exception {
		String m = "getSystemProperty";
		String p = "String";
		String r = "java.security.AccessController.doPrivileged";
		
		String expanders = "WordNet , CodeVocabulary , Type";
		boolean relaxReturn = false;
		boolean relaxParams = false;
		boolean contextRelevants = false;
		boolean filterMethodNameTermsByParameter = false;
		
		SourcererQueryBuilder sourcererQueryBuilder = new SourcererQueryBuilder(expanders, relaxReturn, relaxParams, contextRelevants, filterMethodNameTermsByParameter);
		SearchResult searchResult = sourcererQueryBuilder.search(m, r, p);
		int totalFound = searchResult.getNumFound();
		assertEquals(35,totalFound);
		
		sourcererQueryBuilder = new SourcererQueryBuilder(expanders, true, true, contextRelevants, filterMethodNameTermsByParameter);
		searchResult = sourcererQueryBuilder.search(m, r, p);
		totalFound = searchResult.getNumFound();
		assertEquals(89,totalFound);
	}

}
