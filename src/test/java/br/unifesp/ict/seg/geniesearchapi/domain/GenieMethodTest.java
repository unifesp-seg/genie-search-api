package br.unifesp.ict.seg.geniesearchapi.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.GenieMethodRepository;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;

public class GenieMethodTest {

	private long entityId1 = 536227;
	private long entityId2 = 536516;

	private GenieMethodRepository genieMethodRepository = new GenieMethodRepository();

	@Before
	public void initialize() throws IOException {
		GenieSearchAPIConfig.loadProperties();
	}

	@Test
	public void slice1() throws Exception {
		GenieMethod genieMethod = new GenieMethod(entityId1);
		boolean result = genieMethod.slice();
		assertTrue(result);
		assertTrue(genieMethod.isContainsSlicedFile());
	}

	@Test
	public void slice2() throws Exception {
		GenieMethod genieMethod = new GenieMethod(entityId2);
		boolean result = genieMethod.slice();
		assertTrue(result);
		assertTrue(genieMethod.isContainsSlicedFile());
	}

	@Test
	public void generateJar() throws Exception {

		// interface_metrics_id: 3178472 = 12709553: entity_id
		int entityId = 12709553;

		GenieMethod genieMethod = genieMethodRepository.findByEntityId(entityId);
		boolean result = genieMethod.generateJar();
		assertTrue(genieMethod.isContainsCompiledJar());

		assertTrue(result);
	}

	@Test
	public void getReflectionParams() throws Exception {
		// (java.lang.String,java.lang.Integer)
		GenieMethod genieMethod = genieMethodRepository.findByEntityId(13031687);
		assertEquals("java.lang.String,java.lang.Integer", genieMethod.getParams());
		assertEquals(2, genieMethod.getTotalParams());
		Class<?>[] reflectionParams = genieMethod.getReflectionParams();
		assertEquals(2, reflectionParams.length);
		assertEquals(String.class, reflectionParams[0]);
		assertEquals(Integer.class, reflectionParams[1]);

		genieMethod = new GenieMethod(1L);
		genieMethod.setParams(
				"java.lang.String, java.lang.Boolean, java.lang.Character, java.lang.Byte, java.lang.Short, java.lang.Integer, java.lang.Long, java.lang.Float, java.lang.Double");
		reflectionParams = genieMethod.getReflectionParams();
		assertEquals(9, reflectionParams.length);
		assertEquals(String.class, reflectionParams[0]);
		assertEquals(Boolean.class, reflectionParams[1]);
		assertEquals(Character.class, reflectionParams[2]);
		assertEquals(Byte.class, reflectionParams[3]);
		assertEquals(Short.class, reflectionParams[4]);
		assertEquals(Integer.class, reflectionParams[5]);
		assertEquals(Long.class, reflectionParams[6]);
		assertEquals(Float.class, reflectionParams[7]);
		assertEquals(Double.class, reflectionParams[8]);

		genieMethod.setParams("String,Boolean,Character,Byte,Short,Integer,Long,Float,Double");
		reflectionParams = genieMethod.getReflectionParams();
		assertEquals(9, reflectionParams.length);
		assertEquals(String.class, reflectionParams[0]);
		assertEquals(String.class, reflectionParams[0]);
		assertEquals(Boolean.class, reflectionParams[1]);
		assertEquals(Character.class, reflectionParams[2]);
		assertEquals(Byte.class, reflectionParams[3]);
		assertEquals(Short.class, reflectionParams[4]);
		assertEquals(Integer.class, reflectionParams[5]);
		assertEquals(Long.class, reflectionParams[6]);
		assertEquals(Float.class, reflectionParams[7]);
		assertEquals(Double.class, reflectionParams[8]);

		genieMethod.setParams("String,boolean,char,byte,short,int,long,float,double,   string   ");
		reflectionParams = genieMethod.getReflectionParams();
		assertEquals(10, reflectionParams.length);
		assertEquals(String.class, reflectionParams[0]);
		assertEquals(boolean.class, reflectionParams[1]);
		assertEquals(char.class, reflectionParams[2]);
		assertEquals(byte.class, reflectionParams[3]);
		assertEquals(short.class, reflectionParams[4]);
		assertEquals(int.class, reflectionParams[5]);
		assertEquals(long.class, reflectionParams[6]);
		assertEquals(float.class, reflectionParams[7]);
		assertEquals(double.class, reflectionParams[8]);
		assertEquals(String.class, reflectionParams[9]);

		genieMethod.setParams("String, java.lang.boolean, Char, byte, short, Int, java.long.Long, float, double");
		reflectionParams = genieMethod.getReflectionParams();
		assertEquals(9, reflectionParams.length);
		assertEquals(String.class, reflectionParams[0]);
		assertNull(reflectionParams[1]);
		assertNull(reflectionParams[2]);
		assertEquals(byte.class, reflectionParams[3]);
		assertEquals(short.class, reflectionParams[4]);
		assertNull(reflectionParams[5]);
		assertNull(reflectionParams[6]);
		assertEquals(float.class, reflectionParams[7]);
		assertEquals(double.class, reflectionParams[8]);
	}

	@Test
	public void isAllowsExecution() throws Exception {
		// (java.lang.String,java.lang.Integer)
		GenieMethod genieMethod = genieMethodRepository.findByEntityId(13031687);
		assertTrue(genieMethod.isAllowsExecution());

		genieMethod.setParams("String, int");
		assertTrue(genieMethod.isAllowsExecution());

		genieMethod.setParams("String, Int");
		assertFalse(genieMethod.isAllowsExecution());

		// return type
		genieMethod = genieMethodRepository.findByEntityId(12692246);
		assertFalse(genieMethod.isAllowsExecution());
	}

	@Test
	public void execute() throws Exception {
		// interface_metrics_id: 3178472 = 12709553: entity_id
		long entityId = 12709553;

		GenieMethod genieMethod = genieMethodRepository.findByEntityId(entityId);
		assertTrue(genieMethod.isAllowsExecution());
		Object result = genieMethod.execute("300");
		assertEquals(Byte.class, result.getClass());
		assertEquals(new Byte("44"), result);
	}

	@Test
	public void execute2() throws Exception {
		Object result = null;
		GenieMethod genieMethod = genieMethodRepository.findByEntityId(12955908);
		assertTrue(genieMethod.isAllowsExecution());
		result = genieMethod.execute("25,C:/_mestrado/smis-test/tempfile.txt");
		assertEquals(Long.class, result.getClass());
		assertEquals(new Long("25"), result);
	}

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void executeVarargs() throws Exception {
		Object result = null;
		GenieMethod genieMethod = genieMethodRepository.findByEntityId(12955908);
		assertTrue(genieMethod.isAllowsExecution());

		result = genieMethod.execute(25, "C:/_mestrado/smis-test/tempfile.txt");
		assertEquals(Long.class, result.getClass());
		assertEquals(new Long("25"), result);

		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage("Parâmetros inválidos. Considere: long,java.lang.String");
		result = genieMethod.execute();
	}

	@Test
	public void getSourceCode() throws Exception {
		String code = "";
		code += "public static byte parseIntToByte(int value) throws Exception\n";
		code += "    {\n";
		code += "        \n";
		code += "        byte val = 0;\n";
		code += "        if(value > 127)\n";
		code += "            value = (value - 256);\n";
		code += "        if(value > Byte.MIN_VALUE && value < Byte.MAX_VALUE)\n";
		code += "            val =(byte)value;\n";
		code += "        else\n";
		code += "            throw new Exception(\"Value doit etre inferieur a 256 et superieur a -128\");\n";
		code += "        return val;\n";
		code += "    }";

		GenieMethod genieMethod = genieMethodRepository.findByEntityId(12709553);
		String sourceCode = genieMethod.getSourceCode();
		assertNotNull(sourceCode);
		assertEquals(code, sourceCode);
	}
}
