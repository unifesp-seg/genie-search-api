package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain.RelatedWordsResult;
import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public class RelatedWords {

	public static RelatedWordsResult getRelated(String word) {

		RelatedWordsResult relatedWordsResult = new RelatedWordsResult();

		loadWordnetRelated(relatedWordsResult, word);
		loadCodeRelated(relatedWordsResult, word);

		return relatedWordsResult;
	}

	private static void loadWordnetRelated(RelatedWordsResult relatedWordsResult, String word) {

		String wordNetDatabasePath = ClassLoader.getSystemResource("thesauri/wordnet-database").getPath();
		System.setProperty("wordnet.database.dir", wordNetDatabasePath);
		VerbSynset verbSynset;
		NounSynset nounSynset;
		AdjectiveSynset adjSynset;

		if (detectCamel(word))
			word = camelCaseSplit(word);

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsetsV = database.getSynsets(word, SynsetType.VERB);
		Synset[] synsetsN = database.getSynsets(word, SynsetType.NOUN);
		Synset[] synsetsA = database.getSynsets(word, SynsetType.ADJECTIVE);

		List<String> verbSyns = new ArrayList<String>();
		List<String> verbAntonyms = new ArrayList<String>();

		for (int i = 0; i < synsetsV.length; i++) {
			verbSynset = (VerbSynset) (synsetsV[i]);
			String[] syns = verbSynset.getWordForms();
			for (int j = 0; j < syns.length; j++) {
				String syn = syns[j];
				if (!verbSyns.contains(syn) && !syn.equals(word)) {
					if (syn.contains(" "))
						syn = camelCaseJoin(syn);
					cleanAdd(verbSyns, syn);
				}
				WordSense[] ants = verbSynset.getAntonyms(syn);
				for (WordSense w : ants)
					cleanAdd(verbAntonyms, w.getWordForm());
			}
		}

		List<String> nounSyns = new ArrayList<String>();
		List<String> nounAntonyms = new ArrayList<String>();

		for (int i = 0; i < synsetsN.length; i++) {
			nounSynset = (NounSynset) (synsetsN[i]);
			String[] syns = nounSynset.getWordForms();
			for (int j = 0; j < syns.length; j++) {
				String syn = syns[j];
				if (!nounSyns.contains(syn) && !syn.equals(word)) {
					if (syn.contains(" "))
						syn = camelCaseJoin(syn);
					cleanAdd(nounSyns, syn);
				}
				WordSense[] ants = nounSynset.getAntonyms(syn);
				for (WordSense w : ants)
					cleanAdd(nounAntonyms, w.getWordForm());
			}
		}

		List<String> adjSyns = new ArrayList<String>();
		List<String> adjAntonyms = new ArrayList<String>();

		for (int i = 0; i < synsetsA.length; i++) {
			adjSynset = (AdjectiveSynset) (synsetsA[i]);
			String[] syns = adjSynset.getWordForms();
			for (int j = 0; j < syns.length; j++) {
				String syn = syns[j];
				if (!adjSyns.contains(syn) && !syn.equals(word)) {
					if (syn.contains(" "))
						syn = camelCaseJoin(syn);
					cleanAdd(adjSyns, syn);
				}
				WordSense[] ants = adjSynset.getAntonyms(syn);
				for (WordSense w : ants)
					cleanAdd(adjAntonyms, w.getWordForm());
			}
		}

		relatedWordsResult.getVerbs().addAll(verbSyns);
		relatedWordsResult.getNouns().addAll(nounSyns);
		relatedWordsResult.getAdjectives().addAll(adjSyns);
		relatedWordsResult.getVerbAntonyms().addAll(verbAntonyms);
		relatedWordsResult.getNounAntonyms().addAll(nounAntonyms);
		relatedWordsResult.getAdjectiveAntonyms().addAll(adjAntonyms);
	}

	private static void loadCodeRelated(RelatedWordsResult relatedWordsResult, String word) {
//
//		List<String> synList = new LinkedList<String>();
//		List<String> antonymList = new LinkedList<String>();
//
//		Query query = session.createQuery(GET_RELATED_BY_WORD);
//		query.setParameter("word", word);
//		List<Object[]> resultList = query.list();
//
//		for (Object[] lo : resultList) {
//			if (((String) lo[2]).equals("s")) {
//				String syn = "";
//				if (((String) lo[1]).equals(word))
//					syn = (String) lo[0];
//				else
//					syn = (String) lo[1];
//				synList.add(syn);
//				addAntonyms(syn, antonymList, session);
//			} else if (((String) lo[2]).equals("a")) {
//				String ant = "";
//				if (((String) lo[1]).equals(word))
//					ant = (String) lo[0];
//				else
//					ant = (String) lo[1];
//				if (!word.contains(ant))
//					antonymList.add(ant);
//				addSynonyms(ant, antonymList, session);
//			}
//		}
//
//		session.close();
//
//		relatedWordsResult.getCodeRelatedSyns().addAll(synList);
//		relatedWordsResult.getCodeRelatedAntons().addAll(antonymList);

	}

	private static String camelCaseSplit(String word) {
		return word.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}

	private static boolean detectCamel(String word) {
		boolean switched = false;
		boolean lastLow = false;
		for (int i = 0; i < word.length(); i++) {
			if (Character.isLowerCase(word.charAt(i)))
				lastLow = true;
			if (Character.isUpperCase(word.charAt(i)) && lastLow)
				switched = true;
		}
		return switched;
	}

	private static String camelCaseJoin(String s) {
		String result = new String();
		StringTokenizer tkn = new StringTokenizer(s);
		String second;
		while (tkn.hasMoreTokens()) {
			result += tkn.nextToken();
			if (tkn.hasMoreTokens()) {
				second = tkn.nextToken();
				result += second.substring(0, 1).toUpperCase();
				result += second.substring(1);
			}
		}
		return result;
	}

	private static void cleanAdd(List<String> list, String w) {
		if (!w.contains(".") && !list.contains(w))
			list.add(w);
	}

}
