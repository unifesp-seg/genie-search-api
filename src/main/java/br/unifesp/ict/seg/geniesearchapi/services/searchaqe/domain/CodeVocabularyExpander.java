package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain;

import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure.QueryTerm;
import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure.RelatedWords;

public class CodeVocabularyExpander extends Expander {

	public CodeVocabularyExpander(String relatedWordsServiceUrl) {
		super.setName(CODE_VOCABULARY_EXPANDER);
		super.setMethodNameExpander(true);
		super.setParamExpander(false);
		super.setReturnExpander(false);
	}
	
	public void expandTerm(QueryTerm queryTerm) throws Exception {

		RelatedWordsResult result = RelatedWords.getRelated(queryTerm.getExpandedTerms().get(0));

		queryTerm.getExpandedTerms().addAll(result.getCodeRelatedSyns());
		queryTerm.getExpandedTermsNot().addAll(result.getCodeRelatedAntons());
	}
}
