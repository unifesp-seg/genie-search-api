package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure;

import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain.RelatedWordsResult;

public class RelatedWordsTest {

	@Test
	public void wordnetRelated() {

		RelatedWordsResult relatedWordsResult = RelatedWords.getRelated("compress");

		System.out.println("");
	}

}
