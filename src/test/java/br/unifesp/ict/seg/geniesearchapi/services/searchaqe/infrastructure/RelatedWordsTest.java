package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain.RelatedWordsResult;

public class RelatedWordsTest {

	@Test
	public void relatedWords() {

		RelatedWordsResult relatedWordsResult = RelatedWords.getRelated("compress");

		assertEquals(6, relatedWordsResult.getVerbs().size());
		assertEquals(0, relatedWordsResult.getNouns().size());
		assertEquals(0, relatedWordsResult.getAdjectives().size());
		assertEquals(0, relatedWordsResult.getNounAntonyms().size());
		assertEquals(1, relatedWordsResult.getVerbAntonyms().size());
		assertEquals(0, relatedWordsResult.getAdjectiveAntonyms().size());
		assertEquals(1, relatedWordsResult.getCodeRelatedSyns().size());
		assertEquals(1, relatedWordsResult.getCodeRelatedAntons().size());
	
		assertEquals("compact", relatedWordsResult.getVerbs().get(0));
		assertEquals("packTogether", relatedWordsResult.getVerbs().get(1));
		assertEquals("constrict", relatedWordsResult.getVerbs().get(2));
		assertEquals("squeeze", relatedWordsResult.getVerbs().get(3));
		assertEquals("contract", relatedWordsResult.getVerbs().get(4));
		assertEquals("press", relatedWordsResult.getVerbs().get(5));

		assertEquals("decompress", relatedWordsResult.getVerbAntonyms().get(0));

		assertEquals("zip", relatedWordsResult.getCodeRelatedSyns().get(0));
		assertEquals("unzip", relatedWordsResult.getCodeRelatedAntons().get(0));

	
	}

}
