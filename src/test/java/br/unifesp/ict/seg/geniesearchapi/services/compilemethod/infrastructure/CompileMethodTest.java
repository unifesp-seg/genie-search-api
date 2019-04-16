package br.unifesp.ict.seg.geniesearchapi.services.compilemethod.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;

public class CompileMethodTest {

	private int entityId1 = 536227;
	private int entityId2 = 536516;
	
	@Test
	public void slice1() throws Exception {
		boolean result = CompileMethod.slice(entityId1);
		Path zipFilePath = Paths.get(GenieSearchAPIConfig.getSlicedPath().toString(), entityId1 + ".zip");

		assertTrue(result);
		assertTrue(zipFilePath.toFile().isFile());
	}

	@Test
	public void slice2() throws Exception {
		boolean result = CompileMethod.slice(entityId2);
		Path zipFilePath = Paths.get(GenieSearchAPIConfig.getSlicedPath().toString(), entityId2 + ".zip");

		assertTrue(result);
		assertTrue(zipFilePath.toFile().isFile());
	}

	@Test
	public void generateBuildXml() throws Exception {

		//interface_metrics_id: 3178472 = 12709553: entity_id
		int entityId = 12709553;
		
		System.out.println(GenieSearchAPIConfig.getDatabaseURL());
		boolean //result = CompileMethod.slice(entityId);
		result = CompileMethod.extractSlicedZipFile(entityId);
		result = CompileMethod.generateBuildXml(entityId);

		assertTrue(result);
	}

	@Test
	public void generateJar() throws Exception {

		//interface_metrics_id: 3178472 = 12709553: entity_id
		int entityId = 12709553;
		
		System.out.println(GenieSearchAPIConfig.getDatabaseURL());
		boolean result = CompileMethod.generateJar(entityId);

		assertTrue(result);
	}
}

