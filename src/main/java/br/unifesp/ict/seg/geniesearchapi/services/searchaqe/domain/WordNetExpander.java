package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain;

import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure.QueryTerm;
import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure.RelatedWords;

public class WordNetExpander extends Expander {

	public WordNetExpander(String relatedWordsServiceUrl) {
		super.setName(WORDNET_EXPANDER);
		super.setClassNameExpander(true);
		super.setMethodNameExpander(true);
		super.setParamExpander(false);
		super.setReturnExpander(false);
	}
	
	public void expandTerm(QueryTerm queryTerm) throws Exception {

		RelatedWordsResult result = RelatedWords.getRelated(queryTerm.getExpandedTerms().get(0));

		queryTerm.getExpandedTerms().addAll(result.getVerbs());
		queryTerm.getExpandedTerms().addAll(result.getNouns());
		queryTerm.getExpandedTerms().addAll(result.getAdjectives());
		
		queryTerm.getExpandedTermsNot().addAll(result.getVerbAntonyms());
		queryTerm.getExpandedTermsNot().addAll(result.getNounAntonyms());
		queryTerm.getExpandedTermsNot().addAll(result.getAdjectiveAntonyms());
	}
}
