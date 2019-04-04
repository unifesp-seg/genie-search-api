package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain;

import java.util.ArrayList;
import java.util.List;

public class RelatedWordsResult {

	private List<String> verbs = new ArrayList<String>();
	private List<String> nouns = new ArrayList<String>();
	private List<String> adjectives = new ArrayList<String>();
	private List<String> nounAntonyms = new ArrayList<String>();
	private List<String> verbAntonyms = new ArrayList<String>();
	private List<String> adjectiveAntonyms = new ArrayList<String>();
	private List<String> codeRelatedSyns = new ArrayList<String>();
	private List<String> codeRelatedAntons = new ArrayList<String>();

	public List<String> getVerbAntonyms() {
		return verbAntonyms;
	}

	public void setVerbAntonyms(List<String> verbAntonyms) {
		this.verbAntonyms = verbAntonyms;
	}

	public List<String> getNounAntonyms() {
		return nounAntonyms;
	}

	public void setNounAntonyms(List<String> nounAntonyms) {
		this.nounAntonyms = nounAntonyms;
	}

	public List<String> getCodeRelatedAntons() {
		return codeRelatedAntons;
	}

	public void setCodeRelatedAntons(List<String> codeRelatedAntons) {
		this.codeRelatedAntons = codeRelatedAntons;
	}

	public List<String> getCodeRelatedSyns() {
		return codeRelatedSyns;
	}

	public void setCodeRelatedSyns(List<String> codeRelated) {
		this.codeRelatedSyns = codeRelated;
	}

	public List<String> getNouns() {
		return nouns;
	}

	public void setNouns(List<String> nounSynonyms) {
		this.nouns = nounSynonyms;
	}

	public List<String> getVerbs() {
		return verbs;
	}

	public void setVerbs(List<String> verbSynonyms) {
		this.verbs = verbSynonyms;
	}

	public List<String> getAdjectives() {
		return adjectives;
	}

	public void setAdjectives(List<String> adjSynonyms) {
		this.adjectives = adjSynonyms;
	}

	public List<String> getAdjectiveAntonyms() {
		return adjectiveAntonyms;
	}

	public void setAdjectiveAntonyms(List<String> adjAnt) {
		this.adjectiveAntonyms = adjAnt;
	}

}
