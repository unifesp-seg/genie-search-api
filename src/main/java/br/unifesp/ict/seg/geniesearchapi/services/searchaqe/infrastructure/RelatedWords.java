package br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;
import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain.RelatedWordsResult;
import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public class RelatedWords {

	public static RelatedWordsResult getRelated(String word) throws Exception {

		RelatedWordsResult relatedWordsResult = new RelatedWordsResult();

		loadWordnetRelated(relatedWordsResult, word);
		loadCodeRelated(relatedWordsResult, word);

		return relatedWordsResult;
	}

	private static void loadWordnetRelated(RelatedWordsResult relatedWordsResult, String word) {

		if(StringUtils.isBlank(word))
			return;

		String wordNetDatabasePath = Paths.get(GenieSearchAPIConfig.getThesauriPath()+"", "wordnet-database")+"";
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

	private static void loadCodeRelated(RelatedWordsResult relatedWordsResult, String word) throws Exception {
		RelatedWordsResult relatedCodeResult = getSynonymsAndAntonymsCodeRelated(word);
		
		List<String> syns = new ArrayList<>(relatedCodeResult.getCodeRelatedSyns());
		List<String> ants = new ArrayList<>(relatedCodeResult.getCodeRelatedAntons());
		
		for (String syn : relatedCodeResult.getCodeRelatedSyns()) {
			relatedCodeResult = getSynonymsAndAntonymsCodeRelated(syn);
			syns.addAll(relatedCodeResult.getCodeRelatedSyns());
			ants.addAll(relatedCodeResult.getCodeRelatedAntons());
		}
		for (String ant : relatedCodeResult.getCodeRelatedAntons()) {
			relatedCodeResult = getSynonymsAndAntonymsCodeRelated(ant);
			ants.addAll(relatedCodeResult.getCodeRelatedSyns());
			syns.addAll(relatedCodeResult.getCodeRelatedAntons());
		}
		
		//Remove parameter word
		while(syns.remove(word));
		while(ants.remove(word));

		//Remove duplicates
		syns = syns.stream().distinct().collect(Collectors.toList());
		ants = ants.stream().distinct().collect(Collectors.toList());
		
		
		relatedWordsResult.getCodeRelatedSyns().addAll(syns);
		relatedWordsResult.getCodeRelatedAntons().addAll(ants);
	}
	
	private static RelatedWordsResult getSynonymsAndAntonymsCodeRelated(String word) throws Exception {
		
		if(StringUtils.isBlank(word))
			return null;
		
		Reader codeDatabase = new FileReader(Paths.get(GenieSearchAPIConfig.getThesauriPath()+"", "code-database.csv").toFile());
		Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(codeDatabase);

		List<String> syns = new ArrayList<String>();
		List<String> ants = new ArrayList<String>();
		
		for (CSVRecord record : records) {
		    String word1 = record.get(0).trim();
		    String word2 = record.get(1).trim();
		    String type = record.get(2).trim();
		    
		    boolean isSynonymous = "s".equalsIgnoreCase(type) || "b".equalsIgnoreCase(type);
		    
		    String related = null;
		    if(word.equalsIgnoreCase(word1)) {
		    	related = word2;
		    }else if (word.equalsIgnoreCase(word2)) {
		    	related = word1;
		    }
		    
		    if(related != null) {
		    	if(isSynonymous) {
		    		syns.add(related);
		    	}else {
		    		ants.add(related);
		    	}
		    }
		}
		
		RelatedWordsResult relatedCodeResults = new RelatedWordsResult();
		
		relatedCodeResults.setCodeRelatedSyns(syns);
		relatedCodeResults.setCodeRelatedAntons(ants);
		
		return relatedCodeResults;
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
