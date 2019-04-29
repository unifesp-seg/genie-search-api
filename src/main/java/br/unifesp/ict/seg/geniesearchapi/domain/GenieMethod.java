package br.unifesp.ict.seg.geniesearchapi.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.LogUtils;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.ManipulateFile;
import br.unifesp.ict.seg.geniesearchapi.services.compilemethod.infrastructure.ThreadExecObject;
import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure.JavaTermExtractor;
import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure.QueryTerm;
import edu.uci.ics.sourcerer.services.file.adapter.FileAdapter;
import edu.uci.ics.sourcerer.services.slicer.SlicerFactory;
import edu.uci.ics.sourcerer.services.slicer.internal.SliceImpl;
import edu.uci.ics.sourcerer.services.slicer.internal.SlicerDebug;
import edu.uci.ics.sourcerer.services.slicer.model.Slice;
import edu.uci.ics.sourcerer.services.slicer.model.Slicer;

public class GenieMethod {

	private Long id;
	private String projectType;
	private Long projectId;
	private String projectName;
	private String entityType;
	private Long entityId;
	private String modifiers;
	private String fqn;
	private String params;
	private String returnType;
	private String relationType;
	private boolean processed;
	private boolean processedParams;

	private int totalParams;
	private int totalWordsMethod;
	private int totalWordsClass;
	private boolean onlyPrimitiveTypes;
	private boolean isStatic;
	private boolean hasTypeSamePackage;

	private List<QueryTerm> expandedParams = new ArrayList<QueryTerm>();

	private boolean isLoadedDB = false;

	public GenieMethod(Long entityId) {
		this.entityId = entityId;
	}

	public GenieMethod(ResultSet rs) throws Exception {
		id = rs.getLong("id");
		projectType = rs.getString("project_type");
		projectId = rs.getLong("project_id");
		projectName = rs.getString("project_name");
		entityType = rs.getString("entity_type");
		entityId = rs.getLong("entity_id");
		modifiers = rs.getString("modifiers");
		fqn = rs.getString("fqn");
		params = rs.getString("params");
		returnType = rs.getString("return_type");
		relationType = rs.getString("relation_type");
		processed = rs.getInt("processed") == 1 ? true : false;
		processedParams = rs.getInt("processed_params") == 1 ? true : false;

		totalParams = rs.getInt("total_params");
		totalWordsMethod = rs.getInt("total_words_method");
		totalWordsClass = rs.getInt("total_words_class");
		onlyPrimitiveTypes = rs.getInt("only_primitive_types") == 1 ? true : false;
		isStatic = rs.getInt("is_static") == 1 ? true : false;
		hasTypeSamePackage = rs.getInt("has_type_same_package") == 1 ? true : false;

		params = StringUtils.replace(params, "(", "");
		params = StringUtils.replace(params, ")", "");

		isLoadedDB = true;
	}

	// bussiness
	void setParams(String params) {
		this.params = params;
		totalParams = getParamsNames().length;
	}

	public String getPackage() {
		String packageName = StringUtils.substringBeforeLast(fqn, ".");
		packageName = StringUtils.substringBeforeLast(packageName, ".");

		return packageName;
	}

	public String[] getParamsNames() {
		String p = this.params;
		p = StringUtils.replace(p, "(", "");
		p = StringUtils.replace(p, ")", "");
		p = StringUtils.replace(p, " ", "");
		String[] names = StringUtils.split(p, ",");

		// Fix Generic situation. i.e.: Map<java.lang.String, <?>, Path>, Map<A, B, C,
		// D, <E>>
		List<String> aux = new ArrayList<String>();
		for (int i = 0; i < names.length; i++) {
			String n = names[i];
			while (StringUtils.countMatches(n, "<") != StringUtils.countMatches(n, ">")) {
				if (i + 1 == names.length)
					break;
				n += "," + names[++i];
			}
			aux.add(n);
		}

		String[] paramsNames = new String[aux.size()];
		paramsNames = aux.toArray(paramsNames);
		return paramsNames;
	}

	public String[] getWordsMethod() {
		return this.getWords(getMethodName());
	}

	public String[] getWordsClassName() {
		return this.getWords(getClassName());
	}

	private String[] getWords(String strWords) {
		String[] words = StringUtils.split(JavaTermExtractor.getFQNTermsAsString(strWords), " ");
		return words;
	}

	public String getMethodName() {
		return StringUtils.substringAfterLast(fqn, ".");
	}

	public String getClassName() {
		String className = StringUtils.substringBeforeLast(fqn, ".");
		return StringUtils.substringAfterLast(className, ".");
	}

	public String getAbsoluteClassName() {
		return this.getPackage() + "." + this.getClassName();
	}

	public boolean isSameParams(String[] paramsNames) {
		return this.isSameParams(paramsNames, false);
	}

	public boolean isSameParams(String[] paramsNames, boolean isParamsOrder) {
		if (this.getParamsNames().length != paramsNames.length)
			return false;

		if (isParamsOrder) {
			for (int i = 0; i < this.getParamsNames().length; i++)
				if (!this.getParamsNames()[i].equals(paramsNames[i]))
					return false;
			return true;
		} else {
			ArrayList<String> list = new ArrayList<String>(Arrays.asList(paramsNames));
			Stack<String> stack = new Stack<String>();
			stack.addAll(Arrays.asList(this.getParamsNames()));

			while (!stack.isEmpty()) {
				String param = stack.pop();
				int i = list.indexOf(param);
				if (i >= 0)
					list.remove(i);
			}

			return list.isEmpty();
		}
	}

	public boolean isSameExpandedParams(String[] paramsNames) {
		return this.isSameExpandedParams(paramsNames, false);
	}

	public boolean isSameExpandedParams(String[] paramsNames, boolean isParamsOrder) {

		if (this.getExpandedParams().size() != paramsNames.length)
			return false;

		if (isParamsOrder) {
			for (int i = 0; i < this.getExpandedParams().size(); i++) {
				boolean match = false;
				for (String term : this.getExpandedParams().get(i).getExpandedTerms()) {
					if (term.equals(paramsNames[i])) {
						match = true;
						break;
					}
				}
				if (!match)
					return false;
			}
			return true;
		} else {
			ArrayList<String> list = new ArrayList<String>(Arrays.asList(paramsNames));
			Stack<QueryTerm> stack = new Stack<QueryTerm>();
			stack.addAll(this.getExpandedParams());

			while (!stack.isEmpty()) {
				QueryTerm expandedParam = stack.pop();
				for (String term : expandedParam.getExpandedTerms()) {
					int i = list.indexOf(term);
					if (i >= 0) {
						list.remove(i);
						break;
					}
				}
			}

			return list.isEmpty();
		}
	}

	public boolean isCrawled() {
		return "CRAWLED".equals(this.getProjectType());
	}

	public boolean isJavaLibrary() {
		return "JAVA_LIBRARY".equals(this.getProjectType());
	}

	//
	// SOURCERER SOURCE CODE
	//
	public String getSourceCode() {
		LogUtils.getLogger().info("Entity_id: " + this.entityId + " Getting source code...");
        byte[] result = FileAdapter.lookupByEntityID(this.entityId.intValue());
		return new String(result);
	}
	
	//
	// SLICE AND EXECUTION PROCESS
	//
	public Path getSlicedFilePath() {
		return Paths.get(GenieSearchAPIConfig.getSlicedPath() + "", this.getEntityId() + ".zip");
	}

	public boolean isContainsSlicedFile() {
		return this.getSlicedFilePath().toFile().isFile();
	}

	public Path getCompiledJarPath() {
		return Paths.get(GenieSearchAPIConfig.getJarPath() + "", this.getEntityId() + ".jar");
	}

	public boolean isContainsCompiledJar() {
		return this.getCompiledJarPath().toFile().isFile();
	}
	
	public boolean slice() {
		LogUtils.getLogger().info("Entity_id: " + this.entityId + " Slicing...");

		//Dependences
		if(this.isContainsSlicedFile())
			return true;
		
		Slicer slicer = SlicerFactory.createSlicer();
		if (slicer == null) {
			return false;
		}

		Slice result = slicer.slice(Collections.singleton(this.entityId.intValue()));
		if (result == null) {
			return false;
		}

		SliceImpl si = (SliceImpl) result;
		SlicerDebug.debug("[SlicerFactory]slice:\n" + si.getInternalEntities().toString().replace(",", "\n").replace("[", "").replace("]", ""));
		byte[] input = result.toZipFile();

		Path zipFilePath = Paths.get(GenieSearchAPIConfig.getSlicedPath()+"", entityId + ".zip");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(zipFilePath.toFile());
			fos.write(input);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private boolean extractSlicedZipFile() {
		LogUtils.getLogger().info("Entity_id: " + this.entityId + " Extracting sliced zip file...");

		String zipFile = Paths.get(GenieSearchAPIConfig.getSlicedPath()+"", this.entityId + ".zip")+"";
		File outputDir = Paths.get(GenieSearchAPIConfig.getExtractTempPath()+"", this.entityId+"", "src").toFile();
		if(!outputDir.isDirectory())
			outputDir.mkdirs();
		
		ManipulateFile.extract(zipFile, outputDir.getPath());
		return true;
	}

	private boolean generateBuildXml() {
		LogUtils.getLogger().info("Entity_id: " + this.entityId + " Generating Build.xml...");

		Path tempEntityPath = Paths.get(GenieSearchAPIConfig.getExtractTempPath()+"", this.entityId +"");
		Path buildFilePath = Paths.get(tempEntityPath+"","build.xml");
		Path buildDirPath = Paths.get(tempEntityPath+"","build");
		Path srcDirPath = Paths.get(tempEntityPath+"","src");
		Path jarFileDestPath = Paths.get(GenieSearchAPIConfig.getJarPath()+"", this.entityId+".jar");
		BufferedWriter xml;
		try {
			// TODO Path of javac can't be hard coding
			xml = Files.newBufferedWriter(buildFilePath);
			xml.write("<project>\n");
			xml.write("\t<target name=\"compile\">\n");
			xml.write("\t\t<mkdir dir=\"" + buildDirPath + "\" />\n");
			xml.write("\t\t<javac srcdir=\"" + srcDirPath + "\"\n");
			xml.write("\t\t       destdir=\"" + buildDirPath + "\"\n" );
			xml.write("\t\t       executable=\"C:/Program Files/Java/jdk1.8.0_191/bin/javac.exe\" fork=\"true\"  taskname=\"javac1.8\">\n");
			xml.write("\t\t</javac>\n");
			xml.write("\t\t<jar destfile=\"" + jarFileDestPath + "\"\n");
			xml.write("\t\t     basedir=\"" + buildDirPath + "\" />\n");
			xml.write("\t</target>\n");
			xml.write("</project>");
			xml.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean generateJar() throws Exception {
		LogUtils.getLogger().info("Entity_id: " + this.entityId + " Generating compiled jar...");
		
		//Dependences
		boolean resultProess = true;
		resultProess = this.slice();
		resultProess = this.extractSlicedZipFile();
		resultProess = this.generateBuildXml();
		if(!resultProess)
			throw new RuntimeException("Erro na complição do Método com entityId = " + this.entityId);
		
		File buildFile = Paths.get(GenieSearchAPIConfig.getExtractTempPath()+"", entityId+"","build.xml").toFile();

		// Prepare Ant project
		Project project = new Project();
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
 
        // Capture event for Ant script build start / stop / failure
        try {
            project.fireBuildStarted();
            project.init();
            ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
            project.addReference("ant.projectHelper", projectHelper);
            projectHelper.parse(project, buildFile);
             
            // If no target specified then default target will be executed.
            project.executeTarget("compile");
            project.fireBuildFinished(null);
        } catch (BuildException buildException) {
            buildException.printStackTrace();
            return false;
        }
		return true;
	}

	public Object execute(Object... executeParamsValues) throws Exception {
		
		String strArgs = "";
		for(Object arg : executeParamsValues) {
			strArgs += arg+", ";
		}
		strArgs = StringUtils.removeEnd(strArgs, ", ");
		
		return this.execute(strArgs);
	}
	
	public Object execute(String executeParamsValues) throws Exception {
		LogUtils.getLogger().info("Entity_id: " + this.entityId + " Executing method...");

		//Validation
		if (!this.isLoadedDB)
			throw new RuntimeException("Método não carregado no Banco de Dados. EntityId = " + this.entityId);

		//Dependence
		if(!this.isContainsCompiledJar()) {
			boolean resultProess = this.generateJar();
			
			if(!resultProess)
				throw new RuntimeException("Erro na complição do Método com entityId = " + this.entityId);
		}

		Class<?> reflectionClass = this.getReflectionClass();
		Class<?>[] reflectionParams = this.getReflectionParams();
		Method reflectionMethod = reflectionClass.getMethod(this.getMethodName(), reflectionParams);

		//Validation
		if (!this.isAllowsExecution())
			throw new RuntimeException("Execução dinâmica não permitida para o Método com entityId = " + this.entityId);

		//Validation
		if (!this.isEexecuteParamsValuesValid(executeParamsValues))
			throw new RuntimeException("Parâmetros inválidos. Considere: " + this.getParams());

		Object[] executionParamValues = this.getExecutionParamValues(executeParamsValues);
		
		//Validation
		for (Object executionParamValue : executionParamValues) {
			if(executionParamValue == null)
				throw new RuntimeException("Valor de algum parâmetro não pode ser convertido para o respectivo tipo. Considere: " + this.getParams());
		}
		
		//The method is executed by a thread to have a timeout implemented
		//ThreadExecute threadExec = new ThreadExecute(myClass, values, method);
		ThreadExecObject threadExec = new ThreadExecObject(reflectionClass, executionParamValues, reflectionMethod);
		Thread thread = new Thread(threadExec);
		thread.start();
		thread.join(300000);

		if (thread.isAlive()) {
			thread.interrupt();
			throw new RuntimeException("Timeout na execução do Método com entityId = " + this.entityId);
		}
		
		return threadExec.getObj();
	}
	
	Class<?>[] getReflectionParams(){
		Class<?>[] reflectionParams = new Class<?>[this.getTotalParams()];
		for (int i = 0; i < this.getTotalParams(); i++) {
			reflectionParams[i] = this.getExecutionClassByName(this.getParamsNames()[i]);
		}

		return reflectionParams;
	}
	
	private Class<?> getReflectionClass() throws Exception {
		Class<?> reflectionClass = null;

		if (this.isCrawled()) {
			URL[] myJars = { this.getCompiledJarPath().toUri().toURL() };
			URLClassLoader child = new URLClassLoader(myJars, this.getClass().getClassLoader());
			reflectionClass = Class.forName(this.getAbsoluteClassName(), true, child);
			child.close();
		} else if (this.isJavaLibrary()) {
			reflectionClass = Class.forName(this.getAbsoluteClassName());
		}

		return reflectionClass;
	}
	
	public boolean isAllowsExecution() {
		
		//Main rules
		if(!isLoadedDB || !(isCrawled() || isJavaLibrary()) || !isStatic() || this.totalParams == 0)
			return false;
		
		//Valid params
		Class<?>[] reflectionParams = this.getReflectionParams();
		for(Class<?> reflectionParam : reflectionParams) {
			if(reflectionParam == null)
				return false;
		}
		
		//Valid return
		if(getExecutionClassByName(returnType) == null)
			return false;
		
		return true;
	}
	
	boolean isEexecuteParamsValuesValid(String executeParamsValues) {
		String[] executeParamsValuesAsArray = this.getExecuteParamsValuesAsArray(executeParamsValues);
		return executeParamsValuesAsArray != null && executeParamsValuesAsArray.length == this.getTotalParams();
	}
	
	private String[] getExecuteParamsValuesAsArray(String executeParamsValues) {
		if(StringUtils.isBlank(executeParamsValues))
			return null;
		
		String params = StringUtils.trim(executeParamsValues);
		String[] args = StringUtils.split(params, ",");
		
		return args;
	}
	
	private Class<?> getExecutionClassByName(String className) {

		if ("String".equalsIgnoreCase(className) || "java.lang.String".equals(className))
			return String.class;

		if ("Boolean".equals(className) || "java.lang.Boolean".equals(className))
			return Boolean.class;
		if ("Character".equals(className) || "java.lang.Character".equals(className))
			return Character.class;
		if ("Byte".equals(className) || "java.lang.Byte".equals(className))
			return Byte.class;
		if ("Short".equals(className) || "java.lang.Short".equals(className))
			return Short.class;
		if ("Integer".equals(className) || "java.lang.Integer".equals(className))
			return Integer.class;
		if ("Long".equals(className) || "java.lang.Long".equals(className))
			return Long.class;
		if ("Float".equals(className) || "java.lang.Float".equals(className))
			return Float.class;
		if ("Double".equals(className) || "java.lang.Double".equals(className))
			return Double.class;

		if ("boolean".equals(className))
			return boolean.class;
		if ("char".equals(className))
			return char.class;
		if ("byte".equals(className))
			return byte.class;
		if ("short".equals(className))
			return short.class;
		if ("int".equals(className))
			return int.class;
		if ("long".equals(className))
			return long.class;
		if ("float".equals(className))
			return float.class;
		if ("double".equals(className))
			return double.class;

		return null;
	}

	private Object[] getExecutionParamValues(String executeParamsValues){
		String[] values = this.getExecuteParamsValuesAsArray(executeParamsValues);
		Class<?>[] classes = this.getReflectionParams();

		Object[] executionParamValues = new Object[this.getTotalParams()];

		for (int i = 0; i < this.getTotalParams(); i++) {
			executionParamValues[i] = this.getExecutionClassValue(values[i], classes[i]);
		}
		
		return executionParamValues;
	}
	
	private Object getExecutionClassValue(String value, Class<?> className) {

		if (className.getName().toLowerCase().contains("string"))
			return value;
		else if (className.getName().toLowerCase().contains("boolean"))
			return new Boolean(value);
		else if (className.getName().toLowerCase().contains("char"))
			return value.charAt(0);
		else if (className.getName().toLowerCase().contains("byte"))
			return new Byte(value);
		else if (className.getName().toLowerCase().contains("short"))
			return new Short(value);
		else if (className.getName().toLowerCase().contains("int"))
			return new Integer(value);
		else if (className.getName().toLowerCase().contains("long"))
			return new Long(value);
		else if (className.getName().toLowerCase().contains("float"))
			return new Float(value);
		else if (className.getName().toLowerCase().contains("double"))
			return new Double(value);

		return null;
	}
	
	public boolean clearMethodFiles() throws IOException {
		
		boolean cleaned = true;
		
		if(this.isContainsSlicedFile()) {
			LogUtils.getLogger().info("Entity_id: " + this.entityId + " Deleting: " + this.getSlicedFilePath());
			cleaned = this.getSlicedFilePath().toFile().delete();
		}
		
		File tempIdDir = Paths.get(GenieSearchAPIConfig.getExtractTempPath()+"", this.entityId+"").toFile();
		if(tempIdDir.isDirectory()) {
			LogUtils.getLogger().info("Entity_id: " + this.entityId + " Deleting: " + tempIdDir);
			FileUtils.deleteDirectory(tempIdDir);
			cleaned = !tempIdDir.isDirectory();
		}
		
		if(this.isContainsCompiledJar()) {
			LogUtils.getLogger().info("Entity_id: " + this.entityId + " Deleting: " + getCompiledJarPath());
			cleaned = this.getCompiledJarPath().toFile().delete();
		}
		
		return cleaned;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof GenieMethod && this.id.longValue() == ((GenieMethod) o).getId());
	}

	// accessors
	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public boolean isProcessedParams() {
		return processedParams;
	}

	public void setProcessedParams(boolean processedParams) {
		this.processedParams = processedParams;
	}

	public Long getId() {
		return id;
	}

	public String getProjectType() {
		return projectType;
	}

	public String getEntityType() {
		return entityType;
	}

	public String getModifiers() {
		return modifiers;
	}

	public String getFqn() {
		return fqn;
	}

	public String getParams() {
		return params;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getRelationType() {
		return relationType;
	}

	public int getTotalParams() {
		return totalParams;
	}

	public int getTotalWordsMethod() {
		return totalWordsMethod;
	}

	public int getTotalWordsClass() {
		return totalWordsClass;
	}

	public boolean isOnlyPrimitiveTypes() {
		return onlyPrimitiveTypes;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public boolean isHasTypeSamePackage() {
		return hasTypeSamePackage;
	}

	public List<QueryTerm> getExpandedParams() {
		return expandedParams;
	}

	public void setExpandedParams(List<QueryTerm> expandedParams) {
		this.expandedParams = expandedParams;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public Long getEntityId() {
		return entityId;
	}

}
