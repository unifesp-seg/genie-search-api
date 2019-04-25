package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.ArgumentManager;
import edu.uci.ics.sourcerer.utils.db.DatabaseConnectionFactory;

public class GenieSearchAPIConfig {

	private static Properties properties;
	private static final String configFileName = "geniesearchapi.properties";

	private static final String P_INPUT_REPO = "input-repo";
	private static final String P_DATABASE_URL = "database-url";
	private static final String P_DATABASE_USER = "database-user";
	private static final String P_DATABASE_PASSWORD = "database-password";
	private static final String P_WEBSERVER_URL = "webserver-url";

	private GenieSearchAPIConfig() {
	}

	private static boolean loadFromConfigFileName() throws IOException {
		properties = new Properties();

		URL url = ClassLoader.getSystemResource(configFileName);
		if (url == null) {
			return false;
		}
		properties.load(url.openStream());
		return true;
	}

	public static void loadProperties() throws IOException {
		boolean hasFile = loadFromConfigFileName();
		
		if(!hasFile) {
			throw new RuntimeException("\nFile not found: " + ClassLoader.getSystemResource("") + configFileName + "\n");
		}
		
		postLoadProperties();
	}

	public static void loadProperties(String[] mainMethodArgs) throws IOException {
		
		loadFromConfigFileName();

		for(String arg : mainMethodArgs) {
			
			arg = arg.trim();
			if(!arg.startsWith("--") || !arg.contains("="))
				continue;
			
			arg = StringUtils.removeStart(arg, "--");
			String[] map = StringUtils.split(arg,"=");
			
			if(map.length != 2)
				continue;
			
			String key = map[0].trim();
			String value = map[1].trim();
			
			if (P_INPUT_REPO.equals(key))
				properties.setProperty(P_INPUT_REPO, value);

			else if (P_DATABASE_URL.equals(key))
				properties.setProperty(P_DATABASE_URL, value);

			else if (P_DATABASE_USER.equals(key))
				properties.setProperty(P_DATABASE_USER, value);

			else if (P_DATABASE_PASSWORD.equals(key))
				properties.setProperty(P_DATABASE_PASSWORD, value);

			else if (P_WEBSERVER_URL.equals(key))
				properties.setProperty(P_WEBSERVER_URL, value);
		}
		
		postLoadProperties();
	}

	private static void postLoadProperties() {
		try {

			// Properties keys verification
			if (!isValidProperties()) {
				throw new RuntimeException("\n\nError loading properties.\n");
			}

			// crawled-projects folder name verification
			if (!"crawled-projects".equals(getCrawledProjectsPath().getFileName() + "")) {
				LogUtils.getLogger().error("");
				LogUtils.getLogger().error("A pasta dos códigos-fonte do repositório precisa chamar-se 'crawled-projects' no arquivo de propriedades: "
						+ ClassLoader.getSystemResource("") + configFileName);
				LogUtils.getLogger().error("O valor atualmente usado é inválido: " + P_INPUT_REPO + " = " + getCrawledProjectsPath());
				throw new RuntimeException();
			}

			// Load Sourcerer parameters from properties
			loadSourcererParamsFromProperties();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logConfigStatus();
			createDefaultFolders();
		}
	}
	
	private static boolean isValidProperties() {

		if (properties == null)
			return false;

		// Properties keys verification
		boolean checkProperties = properties.size() == 5;
		checkProperties = properties.containsKey(P_INPUT_REPO);
		checkProperties = properties.containsKey(P_DATABASE_URL);
		checkProperties = properties.containsKey(P_DATABASE_USER);
		checkProperties = properties.containsKey(P_DATABASE_PASSWORD);
		checkProperties = properties.containsKey(P_WEBSERVER_URL);

		return checkProperties;
	}

	private static void loadSourcererParamsFromProperties() throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		properties.store(output, null);
		ByteArrayInputStream is = new ByteArrayInputStream(output.toByteArray());
		ArgumentManager.PROPERTIES_STREAM.setValue(is);
		JavaRepositoryFactory.INPUT_REPO.permit();
		DatabaseConnectionFactory.DATABASE_URL.permit();
		DatabaseConnectionFactory.DATABASE_USER.permit();
		DatabaseConnectionFactory.DATABASE_PASSWORD.permit();
		ArgumentManager.initializeProperties();
	}

	private static void logConfigStatus() {

		if (isValidProperties()) {
			LogUtils.getLogger().info("");
			LogUtils.getLogger().info("Genie Search API");
			LogUtils.getLogger().info("");
			LogUtils.getLogger().info(ClassLoader.getSystemResource(configFileName).getPath());
			LogUtils.getLogger().info("");
			LogUtils.getLogger().info(P_INPUT_REPO + " = " + getRepoPath());
			LogUtils.getLogger().info(P_DATABASE_URL + " = " + getDatabaseURL());
			LogUtils.getLogger().info(P_DATABASE_USER + " = " + getDatabaseUser());
			LogUtils.getLogger().info(P_DATABASE_PASSWORD + " = " + "***");
			LogUtils.getLogger().info(P_WEBSERVER_URL + " = " + getWebServerURL());
			LogUtils.getLogger().info("");
			LogUtils.getLogger().info("Genie Search API");
			LogUtils.getLogger().info("");
		} else {
			String example = "\n\nExemplo para o conteúdo do arquivo:";
			example += "\n\n# Repository paths";
			example += "\n" + P_INPUT_REPO + " = D:/Sourcerer_portable/repositories/test_repo/crawled-projects/";
			example += "\n" + P_DATABASE_URL + " = jdbc:mysql://localhost:3306/test_repo";
			example += "\n" + P_DATABASE_USER + " = root";
			example += "\n" + P_DATABASE_PASSWORD + " = 123";
			example += "\n" + P_INPUT_REPO + " = http://localhost:8080\n";
			example += "\nCaminho esperado: " + ClassLoader.getSystemResource("") + configFileName + "\n";

			LogUtils.getLogger().error("");
			LogUtils.getLogger().error(example);
		}

		// Solr connection
		String solrURL = GenieSearchAPIConfig.getSolrURL();
		try {
			URL url = new URL(solrURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
		} catch (Exception e) {
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Solr OFFLINE: " + solrURL);
			LogUtils.getLogger().error("");
		}

		// Solr config folder
		try {
			String strPathA = GenieSearchAPIConfig.getSolrIndexPath() + "";
			String strPathB = GenieSearchAPIConfig.getSolrReaderDirPath() + "";
			if (!strPathA.equals(strPathB)) {
				LogUtils.getLogger().error("");
				LogUtils.getLogger().error("Solr Index Path esperado   : " + strPathA);
				LogUtils.getLogger().error("Solr Index Path configurado: " + strPathB);
				LogUtils.getLogger().error("");
			}
		} catch (Exception e) {
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Solr OFFLINE: " + solrURL);
			LogUtils.getLogger().error("");
		}
	}

	private static void createDefaultFolders() {
		File dir = getSolrConfigPath().toFile();
		if (!dir.isDirectory())
			dir.mkdirs();

		dir = getSolrIndexPath().toFile();
		if (!dir.isDirectory())
			dir.mkdirs();

		dir = getSlicedPath().toFile();
		if (!dir.isDirectory())
			dir.mkdirs();

		dir = getExtractTempPath().toFile();
		if (!dir.isDirectory())
			dir.mkdirs();

		dir = getJarPath().toFile();
		if (!dir.isDirectory())
			dir.mkdirs();
	}

	public static Path getRepoPath() {
		return Paths.get(properties.getProperty(P_INPUT_REPO)).getParent();
	}

	public static String getDatabaseURL() {
		return properties.getProperty(P_DATABASE_URL);
	}

	public static String getDatabaseUser() {
		return properties.getProperty(P_DATABASE_USER);
	}

	public static String getDatabasePassword() {
		return properties.getProperty(P_DATABASE_PASSWORD);
	}

	public static Path getCrawledProjectsPath() {
		return Paths.get(properties.getProperty(P_INPUT_REPO));
	}

	public static String getWebServerURL() {
		return properties.getProperty(P_WEBSERVER_URL);
	}

	public static String getSolrURL() {
		return getWebServerURL() + "/solr";
	}

	public static Path getSolrConfigPath() {
		return Paths.get(getRepoPath().toString(), "solr-repo", "conf");
	}

	public static Path getSolrIndexPath() {
		return Paths.get(getRepoPath().toString(), "solr-repo", "data", "index");
	}

	public static Path getSlicedPath() {
		return Paths.get(getRepoPath().toString(), "methods", "slice");
	}

	public static Path getExtractTempPath() {
		return Paths.get(getRepoPath().toString(), "methods", "extract-temp");
	}

	public static Path getJarPath() {
		return Paths.get(getRepoPath().toString(), "methods", "jar");
	}

	public static Path getSolrHomePath() throws Exception {
		// Solr connection
		URL url = null;
		String solrURL = GenieSearchAPIConfig.getSolrURL();
		url = new URL(solrURL + "/admin");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		String strToken = "SolrHome=";
		while ((inputLine = br.readLine()) != null) {
			if (inputLine.startsWith("cwd=") && inputLine.contains(strToken))
				break;
		}
		br.close();
		String strPath = StringUtils.substringAfterLast(inputLine, strToken);
		return Paths.get(strPath);
	}

	public static Path getSolrReaderDirPath() throws Exception {
		// Solr connection
		URL url = null;
		String solrURL = GenieSearchAPIConfig.getSolrURL();
		url = new URL(solrURL + "/admin/stats.jsp");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		String strToken = "org.apache.lucene.store.MMapDirectory@";
		while ((inputLine = br.readLine()) != null) {
			inputLine = StringUtils.trim(inputLine);
			if (inputLine.startsWith(strToken))
				break;
		}
		br.close();
		String strPath = StringUtils.substringAfter(inputLine, strToken);
		strPath = StringUtils.substringBefore(strPath, " ");
		return Paths.get(strPath);
	}

	public static Integer getSolrNumDocs() throws Exception {
		// Solr connection
		URL url = null;
		String solrURL = GenieSearchAPIConfig.getSolrURL();
		url = new URL(solrURL + "/admin/stats.jsp");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		String strTokenPreviousLine = "<stat name=\"numDocs\" >";
		while ((inputLine = br.readLine()) != null) {
			inputLine = StringUtils.trim(inputLine);
			if (inputLine.equals(strTokenPreviousLine)) {
				inputLine = StringUtils.trim(br.readLine());
				br.close();
				return new Integer(inputLine);
			}
		}
		br.close();
		return null;
	}

	public static Path getThesauriPath() {
		return Paths.get(getRepoPath().getParent().getParent() + "", "thesauri");
	}

}