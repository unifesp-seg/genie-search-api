package br.unifesp.ict.seg.geniesearchapi.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;

public class MethodRepositoryTest extends BaseRepository {

	private GenieMethodRepository repository = new GenieMethodRepository();

	@Test
	public void countAllInterfaceMetrics() throws Exception {
		int total = repository.countAllInterfaceMetrics();
		assertEquals(6319, total);
	}
	
	@Test
	public void  findByEntityId() throws Exception {
		
		GenieMethod genieMethod = repository.findByEntityId(12709553);

		assertEquals(Long.valueOf(3178472), genieMethod.getId());
		assertEquals("CRAWLED", genieMethod.getProjectType());
		assertEquals(Long.valueOf(1489), genieMethod.getProjectId());
		assertEquals("javathena", genieMethod.getProjectName());
		assertEquals("METHOD", genieMethod.getEntityType());
		assertEquals(Long.valueOf(12709553), genieMethod.getEntityId());
		assertEquals("PUBLIC,STATIC", genieMethod.getModifiers());
		assertEquals("org.javathena.core.utiles.Functions.parseIntToByte", genieMethod.getFqn());
		assertEquals("int", genieMethod.getParams());
		assertEquals("byte", genieMethod.getReturnType());
		assertTrue(genieMethod.isProcessed());
		assertTrue(genieMethod.isProcessed());
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
