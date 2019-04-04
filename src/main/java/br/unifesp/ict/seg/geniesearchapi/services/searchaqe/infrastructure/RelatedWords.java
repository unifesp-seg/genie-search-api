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

		String wordNetDatabasePath = ClassLoader.getSystemResource("thesauri/wordnet").getPath();
		System.setProperty("wordnet.database.dir", "C:\\GitHub\\genie-search-api\\src\\main\\resources\\thesauri\\wordnet");
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

//TODO Código obsoleto? 4/4/2019
//	public static String getRelatedAsQueryPart(String relatedWordsServiceUrl, String terms, boolean enSyn,
//			boolean codeSyn, boolean enAnt, boolean codeAnt) throws Exception {
//		String ret = "";
//		StringTokenizer tkn = new StringTokenizer(terms);
//
//		boolean useAndBetweenSyns = true;
//
//		if (!enSyn && !codeSyn && !codeAnt && !enAnt)
//			return terms;
//
//		while (tkn.hasMoreTokens()) {
//			// iterate through terms
//			String tok = tkn.nextToken();
//			String url = relatedWordsServiceUrl + "/GetRelated?word=" + tok;
//			InputStream ins = new URL(url).openStream();
//			JAXBContext context = JAXBContext.newInstance(RelatedSearchResult.class);
//			Unmarshaller marshaller = context.createUnmarshaller();
//			RelatedSearchResult result = (RelatedSearchResult) marshaller.unmarshal(ins);
//
//			ArrayList<String> v = new ArrayList<String>(result.getVerbs());
//			ArrayList<String> n = new ArrayList<String>(result.getNouns());
//			ArrayList<String> a = new ArrayList<String>(result.getNouns());
//			ArrayList<String> va = new ArrayList<String>(result.getVerbAntonyms());
//			ArrayList<String> na = new ArrayList<String>(result.getNounAntonyms());
//			ArrayList<String> aa = new ArrayList<String>(result.getAdjectiveAntonyms());
//			ArrayList<String> cs = new ArrayList<String>(result.getCodeRelatedSyns());
//			ArrayList<String> ca = new ArrayList<String>(result.getCodeRelatedAntons());
//
//			// // taking wordnet related words out... TODO: rollback.
//			// enSyn = false;
//			// enAnt = false;
//
//			if (enSyn) {
//
//				if (!ret.equals(""))
//					ret += useAndBetweenSyns ? " AND " : " OR ";
//				ret += "(" + tok;
//
//				if (!v.isEmpty()) {
//					ret += " OR ";
//					for (int i = 0; i < v.size() - 1; i++)
//						ret += v.get(i) + " OR ";
//					ret += v.get(v.size() - 1);
//				} else if (!n.isEmpty()) {
//					ret += " OR ";
//					for (int i = 0; i < n.size() - 1; i++)
//						ret += n.get(i) + " OR ";
//					ret += n.get(n.size() - 1);
//				} else {
//					if (!a.isEmpty()) {
//						ret += " OR ";
//						for (int i = 0; i < a.size() - 1; i++)
//							ret += a.get(i) + " OR ";
//						ret += a.get(a.size() - 1);
//					}
//				}
//			}
//
//			// // taking code related words out... TODO: rollback.
//			// codeSyn = false;
//			// codeAnt = false;
//
//			if (codeSyn) {
//				if (!enSyn) {
//					if (!ret.equals(""))
//						ret += useAndBetweenSyns ? " AND " : " OR ";
//					ret += "(" + tok;
//				}
//				if (!cs.isEmpty()) {
//					ret += " OR ";
//					for (int i = 0; i < cs.size() - 1; i++)
//						ret += cs.get(i) + " OR ";
//					ret += cs.get(cs.size() - 1);
//				}
//			}
//
//			if (enSyn || codeSyn)
//				ret += ")";
//
//			if (enAnt) {
//				if (!v.isEmpty()) {
//					if (!va.isEmpty()) {
//						if (!ret.equals(""))
//							ret += " AND ";
//						else
//							ret = tok + " AND ";
//						ret += "!(";
//						for (int i = 0; i < va.size() - 1; i++)
//							ret += va.get(i) + " OR ";
//						ret += va.get(va.size() - 1) + ")";
//					}
//				} else if (!na.isEmpty()) {
//					if (!ret.equals(""))
//						ret += " AND ";
//					else
//						ret = tok + " AND ";
//					ret += "!(";
//					for (int i = 0; i < na.size() - 1; i++)
//						ret += na.get(i) + " OR ";
//					ret += na.get(na.size() - 1) + ")";
//				} else {
//					if (!aa.isEmpty()) {
//						if (!ret.equals(""))
//							ret += " AND ";
//						else
//							ret = tok + " AND ";
//						ret += "!(";
//						for (int i = 0; i < aa.size() - 1; i++)
//							ret += aa.get(i) + " OR ";
//						ret += aa.get(aa.size() - 1) + ")";
//					}
//				}
//			}
//
//			if (codeAnt) {
//				if (!ca.isEmpty()) {
//					if (!ret.equals(""))
//						ret += " AND ";
//					else
//						ret = tok + " AND ";
//					ret += "!(";
//					for (int i = 0; i < ca.size() - 1; i++)
//						ret += ca.get(i) + " OR ";
//					ret += ca.get(ca.size() - 1) + ")";
//				}
//			}
//		}
//
//		if (ret.equals(""))
//			ret = terms;
//
//		return ret;
//	}

}
