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

import br.unifesp.ict.seg.geniesearchapi.infrastructure.GenieMethodRepository;
import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.ArgumentManager;
import edu.uci.ics.sourcerer.utils.db.DatabaseConnectionFactory;

public class GenieSearchAPIConfig {

	private static Properties properties;
	private static final String configFileName = "geniesearchapi.properties";

	private static final String P_INPUT_REPO = "input-repo";
	private static final String P_DATABASE_URL = "database-url";
	private static final String P_DATABASE_USER = "database-user";
	public static final String P_DATABASE_PASSWORD = "database-password";
	private static final String P_WEBSERVER_URL = "webserver-url";

	private GenieSearchAPIConfig() {
	}

	public static void loadProperties() {

		loadFromConfigFileName();

		postLoadProperties();

	}

	public static boolean hasConfigFileName() {
		URL url = ClassLoader.getSystemResource(configFileName);
		return url != null;
	}

	private static void loadFromConfigFileName() {
		properties = new Properties();

		URL url = ClassLoader.getSystemResource(configFileName);
		if (url != null) {
			try {
				properties.load(url.openStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadProperties(String[] mainMethodArgs) {

		loadFromConfigFileName();

		if (mainMethodArgs == null)
			mainMethodArgs = new String[0];

		for (String arg : mainMethodArgs) {

			arg = arg.trim();
			if (!arg.startsWith("--") || !arg.contains("="))
				continue;

			arg = StringUtils.removeStart(arg, "--");
			String[] map = StringUtils.split(arg, "=");

			if (map.length != 2)
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

	public static Properties getProperties() {
		return properties;
	}

	private static void postLoadProperties() {

		if (isValidProperties()) {
			loadSourcererParamsFromProperties();
			createDefaultFolders();
		}

		logConfigStatus();
	}

	private static void loadSourcererParamsFromProperties() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			properties.store(output, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream is = new ByteArrayInputStream(output.toByteArray());
		ArgumentManager.PROPERTIES_STREAM.setValue(is);
		JavaRepositoryFactory.INPUT_REPO.permit();
		DatabaseConnectionFactory.DATABASE_URL.permit();
		DatabaseConnectionFactory.DATABASE_USER.permit();
		DatabaseConnectionFactory.DATABASE_PASSWORD.permit();
		ArgumentManager.initializeProperties();
	}

	private static void logConfigStatus() {

		if (!isValidProperties()) {
			String example = "\n\n# Repository paths";
			example += "\n" + P_INPUT_REPO + " = D:/Sourcerer_portable/repositories/test_repo/crawled-projects/";
			example += "\n" + P_DATABASE_URL + " = jdbc:mysql://localhost:3306/test_repo";
			example += "\n" + P_DATABASE_USER + " = root";
			example += "\n" + P_DATABASE_PASSWORD + " = 123";
			example += "\n" + P_INPUT_REPO + " = http://localhost:8080\n";
			String expectedPath = ClassLoader.getSystemResource("").getPath() + configFileName;
			expectedPath = StringUtils.replaceOnce(expectedPath, "/", "");
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Genie Search API - Erro ao carregar as propriedades de configuração");
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Use o arquivo de configuração '" + configFileName + "'");
			LogUtils.getLogger().error("Caminho esperado: " + expectedPath + "\n");
			LogUtils.getLogger().error("Exemplo para o conteúdo do arquivo '" + configFileName + "': " + example);
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Genie Search API");
			LogUtils.getLogger().error("");

			return;
		}

		// Properties successfull read
		String loadFrom = "";
		if (hasConfigFileName()) {
			loadFrom = "Arquivo de configuração: " + ClassLoader.getSystemResource(configFileName).getPath();
			loadFrom = StringUtils.replaceOnce(loadFrom, "/", "");
		} else
			loadFrom = "Configurações passadas por parâmetro via String args[]";

		LogUtils.getLogger().info("");
		LogUtils.getLogger().info("Genie Search API");
		LogUtils.getLogger().info("");
		LogUtils.getLogger().info(loadFrom);
		LogUtils.getLogger().info("");
		LogUtils.getLogger().info(P_INPUT_REPO + " = " + getRepoPath());
		LogUtils.getLogger().info(P_DATABASE_URL + " = " + getDatabaseURL());
		LogUtils.getLogger().info(P_DATABASE_USER + " = " + getDatabaseUser());
		LogUtils.getLogger().info(P_DATABASE_PASSWORD + " = " + "***");
		LogUtils.getLogger().info(P_WEBSERVER_URL + " = " + getWebServerURL());
		LogUtils.getLogger().info("");
		LogUtils.getLogger().info("Genie Search API");
		LogUtils.getLogger().info("");

		// crawled-projects folder name verification
		if (!checkCrawledProjectsFolderName()) {
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Pasta diferente de 'crawled-projects': " + ClassLoader.getSystemResource("") + configFileName);
			LogUtils.getLogger().error("O valor atualmente usado é inválido: " + P_INPUT_REPO + " = " + getCrawledProjectsPath());
		}

		// Solr connection
		if (!checkOnlineSolrURL()) {
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Solr OFFLINE: " + getSolrURL());
			LogUtils.getLogger().error("");
		}

		// Solr config folder
		if (!checkSolrReaderDir()) {
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Solr Index Path esperado   : " + getSolrIndexPath());
			LogUtils.getLogger().error("Solr Index Path configurado: " + getSolrReaderDirPath());
			LogUtils.getLogger().error("");
		}
	}

	private static void createDefaultFolders() {

		// Create subfolders, only if repo folder exists
		if (!getRepoPath().toFile().isDirectory())
			return;

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

	public static Path getSolrReaderDirPath() {
		// Solr connection
		URL url = null;
		String solrURL = GenieSearchAPIConfig.getSolrURL();
		try {
			url = new URL(solrURL + "/admin/stats.jsp");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			String strToken = "org.apache.lucene.store.";
			while ((inputLine = br.readLine()) != null) {
				inputLine = StringUtils.trim(inputLine);
				if (inputLine.startsWith(strToken))
					break;
			}
			br.close();
			String strPath = StringUtils.substringAfter(inputLine, "@");
			strPath = StringUtils.substringBefore(strPath, " ");
			return Paths.get(strPath);
		} catch (Exception e) {
			return null;
		}
	}

	public static Integer getSolrNumDocs() {
		try {
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
		} catch (Exception e) {
			return null;
		}
	}

	public static Path getThesauriPath() {
		return Paths.get(getRepoPath().getParent().getParent() + "", "thesauri");
	}

	public static Path getRepoNotesPath() {
		return Paths.get(getRepoPath()+"", getRepoName() + "_notes.xlsx");
	}

	public static boolean isValidProperties() {

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

	public static boolean checkRepoFolderExistence() {
		return getRepoPath().toFile().isDirectory();
	}

	public static boolean checkCrawledProjectsFolderName() {
		return "crawled-projects".equals(getCrawledProjectsPath().getFileName() + "");
	}

	public static boolean checkOnlineWebServerURL() {
		try {
			URL url = new URL(getWebServerURL());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			return connection.getResponseCode() == 200;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean checkOnlineSolrURL() {
		try {
			URL url = new URL(getSolrURL());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			return connection.getResponseCode() == 200;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean checkSolrReaderDir() {
		String strPathA = getSolrIndexPath() + "";
		String strPathB = getSolrReaderDirPath() + "";
		return strPathA.equals(strPathB);
	}

	public static boolean checkDBConnection() {
		return new GenieMethodRepository().checkConnection();
	}

	public static boolean checkAll() {
		return isValidProperties() && checkRepoFolderExistence() && checkCrawledProjectsFolderName() && checkOnlineWebServerURL() && checkOnlineSolrURL() && checkSolrReaderDir() && checkDBConnection();
	}

	public static String getRepoName() {
		if (!isValidProperties() || !getRepoPath().toFile().isDirectory())
			return "";
		return getRepoPath().getFileName() + "";
	}

}