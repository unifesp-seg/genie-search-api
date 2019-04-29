package br.unifesp.ict.seg.geniesearchapi.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;

public class MethodRepositoryTest extends BaseRepository {

	private GenieMethodRepository repository = new GenieMethodRepository();

	@Before
	public void initialize() throws IOException {
		GenieSearchAPIConfig.loadProperties();
	}
	
	@Test
	public void countAllInterfaceMetrics() throws Exception {
		int total = repository.countAllInterfaceMetrics();
		assertEquals(266941, total);
	}
	
	@Test
	public void  findByEntityId() throws Exception {
		
		String fqn = "org.javathena.core.utiles.Functions.parseIntToByte";
		String params = "(int)"; 
		String returnType = "byte";
		GenieMethod genieMethodAux = repository.findByInterfaceElements(fqn, params, returnType);
		assertNotNull(genieMethodAux);

		GenieMethod genieMethod = repository.findByEntityId(genieMethodAux.getEntityId());

		assertEquals(genieMethodAux.getId(), genieMethod.getId());
		assertEquals("CRAWLED", genieMethod.getProjectType());
		assertEquals(genieMethodAux.getProjectId(), genieMethod.getProjectId());
		assertEquals("javathena", genieMethod.getProjectName());
		assertEquals("METHOD", genieMethod.getEntityType());
		assertEquals(genieMethodAux.getEntityId(), genieMethod.getEntityId());
		assertEquals("PUBLIC,STATIC", genieMethod.getModifiers());
		assertEquals("org.javathena.core.utiles.Functions.parseIntToByte", genieMethod.getFqn());
		assertEquals("int", genieMethod.getParams());
		assertEquals("byte", genieMethod.getReturnType());
		assertTrue(genieMethod.isProcessed());
		assertTrue(genieMethod.isProcessedParams());
		assertEquals(1, genieMethod.getTotalParams());
		assertEquals(4, genieMethod.getTotalWordsMethod());
		assertEquals(1, genieMethod.getTotalWordsClass());
		assertTrue(genieMethod.isOnlyPrimitiveTypes());
		assertTrue(genieMethod.isStatic());
		assertFalse(genieMethod.isHasTypeSamePackage());

		assertEquals("org.javathena.core.utiles", genieMethod.getPackage());
		assertEquals(1, genieMethod.getParamsNames().length);
		assertEquals("int", genieMethod.getParamsNames()[0]);
		assertEquals(4, genieMethod.getWordsMethod().length);
		assertEquals("parse", genieMethod.getWordsMethod()[0]);
		assertEquals("int", genieMethod.getWordsMethod()[1]);
		assertEquals("to", genieMethod.getWordsMethod()[2]);
		assertEquals("byte", genieMethod.getWordsMethod()[3]);
		assertEquals(1, genieMethod.getWordsClassName().length);
		assertEquals("parseIntToByte", genieMethod.getMethodName());
		assertEquals("Functions", genieMethod.getClassName());
		assertEquals("org.javathena.core.utiles.Functions", genieMethod.getAbsoluteClassName());
		assertTrue(genieMethod.isCrawled());
		assertFalse(genieMethod.isJavaLibrary());
	}

}
