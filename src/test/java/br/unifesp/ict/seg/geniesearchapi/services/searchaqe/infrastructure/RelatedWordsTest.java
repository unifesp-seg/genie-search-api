package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;
import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain.RelatedWordsResult;

public class RelatedWordsTest {

	@Before
	public void initialize() throws IOException {
		GenieSearchAPIConfig.loadProperties();
	}

	@Test
	public void relatedWords() throws Exception {

		RelatedWordsResult relatedWordsResult = RelatedWords.getRelated("compress");

		assertEquals(6, relatedWordsResult.getVerbs().size());
		assertEquals(0, relatedWordsResult.getNouns().size());
		assertEquals(0, relatedWordsResult.getAdjectives().size());
		assertEquals(0, relatedWordsResult.getNounAntonyms().size());
		assertEquals(1, relatedWordsResult.getVerbAntonyms().size());
		assertEquals(0, relatedWordsResult.getAdjectiveAntonyms().size());
		assertEquals(1, relatedWordsResult.getCodeRelatedSyns().size());
		assertEquals(4, relatedWordsResult.getCodeRelatedAntons().size());
	
		assertEquals("compact", relatedWordsResult.getVerbs().get(0));
		assertEquals("packTogether", relatedWordsResult.getVerbs().get(1));
		assertEquals("constrict", relatedWordsResult.getVerbs().get(2));
		assertEquals("squeeze", relatedWordsResult.getVerbs().get(3));
		assertEquals("contract", relatedWordsResult.getVerbs().get(4));
		assertEquals("press", relatedWordsResult.getVerbs().get(5));

		assertEquals("decompress", relatedWordsResult.getVerbAntonyms().get(0));

		assertEquals("zip", relatedWordsResult.getCodeRelatedSyns().get(0));

		assertEquals("extract", relatedWordsResult.getCodeRelatedAntons().get(0));
		assertEquals("decompress", relatedWordsResult.getCodeRelatedAntons().get(1));
		assertEquals("uncompress", relatedWordsResult.getCodeRelatedAntons().get(2));
		assertEquals("unzip", relatedWordsResult.getCodeRelatedAntons().get(3));
	}

	@Test
	public void relatedWords2() throws Exception {

		RelatedWordsResult relatedWordsResult = RelatedWords.getRelated("unzip");

		assertEquals(0, relatedWordsResult.getVerbs().size());
		assertEquals(0, relatedWordsResult.getNouns().size());
		assertEquals(0, relatedWordsResult.getAdjectives().size());
		assertEquals(0, relatedWordsResult.getNounAntonyms().size());
		assertEquals(1, relatedWordsResult.getVerbAntonyms().size());
		assertEquals(0, relatedWordsResult.getAdjectiveAntonyms().size());
		assertEquals(3, relatedWordsResult.getCodeRelatedSyns().size());
		assertEquals(1, relatedWordsResult.getCodeRelatedAntons().size());
	
		assertEquals("zip up", relatedWordsResult.getVerbAntonyms().get(0));

		assertEquals("extract", relatedWordsResult.getCodeRelatedSyns().get(0));
		assertEquals("decompress", relatedWordsResult.getCodeRelatedSyns().get(1));
		assertEquals("uncompress", relatedWordsResult.getCodeRelatedSyns().get(2));

		assertEquals("zip", relatedWordsResult.getCodeRelatedAntons().get(0));
	}

	@Test
	public void relatedWords3() throws Exception {

		RelatedWordsResult relatedWordsResult = RelatedWords.getRelated("remove");

		assertEquals(15, relatedWordsResult.getVerbs().size());
		assertEquals(0, relatedWordsResult.getNouns().size());
		assertEquals(0, relatedWordsResult.getAdjectives().size());
		assertEquals(0, relatedWordsResult.getNounAntonyms().size());
		assertEquals(0, relatedWordsResult.getVerbAntonyms().size());
		assertEquals(0, relatedWordsResult.getAdjectiveAntonyms().size());
		assertEquals(2, relatedWordsResult.getCodeRelatedSyns().size());
		assertEquals(0, relatedWordsResult.getCodeRelatedAntons().size());
	
		assertEquals("take", relatedWordsResult.getVerbs().get(0));
		assertEquals("takeAway", relatedWordsResult.getVerbs().get(1));
		assertEquals("withdraw", relatedWordsResult.getVerbs().get(2));
		assertEquals("getRidof", relatedWordsResult.getVerbs().get(3));
		assertEquals("takeOut", relatedWordsResult.getVerbs().get(4));
		assertEquals("moveOut", relatedWordsResult.getVerbs().get(5));
		assertEquals("transfer", relatedWordsResult.getVerbs().get(6));
		assertEquals("absent", relatedWordsResult.getVerbs().get(7));
		assertEquals("murder", relatedWordsResult.getVerbs().get(8));
		assertEquals("slay", relatedWordsResult.getVerbs().get(9));
		assertEquals("hit", relatedWordsResult.getVerbs().get(10));
		assertEquals("dispatch", relatedWordsResult.getVerbs().get(11));
		assertEquals("bumpOff", relatedWordsResult.getVerbs().get(12));
		assertEquals("off", relatedWordsResult.getVerbs().get(13));
		assertEquals("polishOff", relatedWordsResult.getVerbs().get(14));

		assertEquals("cleanup", relatedWordsResult.getCodeRelatedSyns().get(0));
		assertEquals("delete", relatedWordsResult.getCodeRelatedSyns().get(1));
	}

	@Test
	public void relatedWords4() throws Exception {

		RelatedWordsResult relatedWordsResult = RelatedWords.getRelated("msg");

		assertEquals(0, relatedWordsResult.getVerbs().size());
		assertEquals(2, relatedWordsResult.getNouns().size());
		assertEquals(0, relatedWordsResult.getAdjectives().size());
		assertEquals(0, relatedWordsResult.getNounAntonyms().size());
		assertEquals(0, relatedWordsResult.getVerbAntonyms().size());
		assertEquals(0, relatedWordsResult.getAdjectiveAntonyms().size());
		assertEquals(1, relatedWordsResult.getCodeRelatedSyns().size());
		assertEquals(0, relatedWordsResult.getCodeRelatedAntons().size());
	
		assertEquals("monosodiumGlutamate", relatedWordsResult.getNouns().get(0));
		assertEquals("MSG", relatedWordsResult.getNouns().get(1));

		assertEquals("message", relatedWordsResult.getCodeRelatedSyns().get(0));
	}

	@Test
	public void relatedWords5() throws Exception {

		RelatedWordsResult relatedWordsResult = RelatedWords.getRelated("message");

		assertEquals(0, relatedWordsResult.getVerbs().size());
		assertEquals(3, relatedWordsResult.getNouns().size());
		assertEquals(0, relatedWordsResult.getAdjectives().size());
		assertEquals(0, relatedWordsResult.getNounAntonyms().size());
		assertEquals(0, relatedWordsResult.getVerbAntonyms().size());
		assertEquals(0, relatedWordsResult.getAdjectiveAntonyms().size());
		assertEquals(1, relatedWordsResult.getCodeRelatedSyns().size());
		assertEquals(0, relatedWordsResult.getCodeRelatedAntons().size());
	
		assertEquals("content", relatedWordsResult.getNouns().get(0));
		assertEquals("subjectMatter", relatedWordsResult.getNouns().get(1));
		assertEquals("substance", relatedWordsResult.getNouns().get(2));

		assertEquals("msg", relatedWordsResult.getCodeRelatedSyns().get(0));
	}
}
