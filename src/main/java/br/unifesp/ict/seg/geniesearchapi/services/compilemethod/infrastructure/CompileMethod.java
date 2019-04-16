package br.unifesp.ict.seg.geniesearchapi.services.compilemethod.infrastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.ManipulateFile;
import edu.uci.ics.sourcerer.services.slicer.SlicerFactory;
import edu.uci.ics.sourcerer.services.slicer.internal.SliceImpl;
import edu.uci.ics.sourcerer.services.slicer.internal.SlicerDebug;
import edu.uci.ics.sourcerer.services.slicer.model.Slice;
import edu.uci.ics.sourcerer.services.slicer.model.Slicer;

public class CompileMethod {

	public CompileMethod() {
	}

	public static boolean slice(int entityId) {
		Slicer slicer = SlicerFactory.createSlicer();
		if (slicer == null) {
			return false;
		}

		Slice result = slicer.slice(Collections.singleton(entityId));
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

	public static boolean extractSlicedZipFile(int entityId) {

		String zipFile = Paths.get(GenieSearchAPIConfig.getSlicedPath()+"", entityId + ".zip")+"";
		File outputDir = Paths.get(GenieSearchAPIConfig.getExtractTempPath()+"", entityId+"", "src").toFile();
		if(!outputDir.isDirectory())
			outputDir.mkdirs();
		
		ManipulateFile.extract(zipFile, outputDir.getPath());
		return true;
	}

	public static boolean generateBuildXml(int entityId) {

		Path tempEntityPath = Paths.get(GenieSearchAPIConfig.getExtractTempPath()+"", entityId+"");
		Path buildFilePath = Paths.get(tempEntityPath+"","build.xml");
		Path buildDirPath = Paths.get(tempEntityPath+"","build");
		Path srcDirPath = Paths.get(tempEntityPath+"","src");
		Path jarFileDestPath = Paths.get(GenieSearchAPIConfig.getJarPath()+"", entityId+".jar");
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

	public static boolean generateJar(long entityId) {
		
		GenieMethod genieMethod = new GenieMethod(entityId);
		
		//Dependences
		if(!genieMethod.isContainsSlicedFile()) {
			
		}
		
		
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
	
	public static boolean clearCompileMethdoFiles(int entityId) {
		return false;
	}
}
