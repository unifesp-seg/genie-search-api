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

	private GenieMethod genieMethodTest1 = null;
	private GenieMethod genieMethodTest2 = null;
	private GenieMethod genieMethodTest3 = null;

	private GenieMethodRepository genieMethodRepository = new GenieMethodRepository();

	@Before
	public void initialize() throws IOException {
		GenieSearchAPIConfig.loadProperties();
	}

	@Test
	public void slice1() throws Exception {
		GenieMethod genieMethod = this.getGenieMethodTest1();
		boolean result = genieMethod.slice();
		assertTrue(result);
		assertTrue(genieMethod.isContainsSlicedFile());
	}

	@Test
	public void slice2() throws Exception {
		GenieMethod genieMethod = this.getGenieMethodTest2();
		boolean result = genieMethod.slice();
		assertTrue(result);
		assertTrue(genieMethod.isContainsSlicedFile());
	}

	@Test
	public void generateJar() throws Exception {
		GenieMethod genieMethod = this.getGenieMethodTest1();
		boolean result = genieMethod.generateJar();
		assertTrue(genieMethod.isContainsCompiledJar());

		assertTrue(result);
	}

	@Test
	public void getReflectionParams() throws Exception {
		GenieMethod genieMethod = this.getGenieMethodTest3();

		assertEquals("java.lang.String,java.lang.Integer", genieMethod.getParams());
		assertEquals(2, genieMethod.getTotalParams());
		Class<?>[] reflectionParams = genieMethod.getReflectionParams();
		assertEquals(2, reflectionParams.length);
		assertEquals(String.class, reflectionParams[0]);
		assertEquals(Integer.class, reflectionParams[1]);

		genieMethod = new GenieMethod(1L);
		genieMethod.setParams("java.lang.String, java.lang.Boolean, java.lang.Character, java.lang.Byte, java.lang.Short, java.lang.Integer, java.lang.Long, java.lang.Float, java.lang.Double");
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
		GenieMethod genieMethod = this.getGenieMethodTest3();

		assertTrue(genieMethod.isAllowsExecution());

		genieMethod.setParams("String, int");
		assertTrue(genieMethod.isAllowsExecution());

		genieMethod.setParams("String, Int");
		assertFalse(genieMethod.isAllowsExecution());

		genieMethod = this.getGenieMethodTest2();
		assertTrue(genieMethod.isAllowsExecution());
	}

	@Test
	public void execute() throws Exception {
		GenieMethod genieMethod = this.getGenieMethodTest1();
		assertTrue(genieMethod.isAllowsExecution());
		Object result = genieMethod.execute("300");
		assertEquals(Byte.class, result.getClass());
		assertEquals(new Byte("44"), result);
	}

	@Test
	public void execute2() throws Exception {
		GenieMethod genieMethod = this.getGenieMethodTest2();
		assertTrue(genieMethod.isAllowsExecution());
		Object result = genieMethod.execute("25,C:/_mestrado/smis-test/tempfile.txt");
		assertEquals(Long.class, result.getClass());
		assertEquals(new Long("25"), result);
	}

	@SuppressWarnings("deprecation")
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void executeVarargs() throws Exception {
		GenieMethod genieMethod = this.getGenieMethodTest2();
		assertTrue(genieMethod.isAllowsExecution());

		Object result = genieMethod.execute(25, "C:/_mestrado/smis-test/tempfile.txt");
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

		GenieMethod genieMethod = this.getGenieMethodTest1();
		String sourceCode = genieMethod.getSourceCode();
		assertNotNull(sourceCode);
		assertEquals(code, sourceCode);
	}

	private GenieMethod getGenieMethodTest1() {

		if (genieMethodTest1 != null)
			return genieMethodTest1;

		String fqn = "org.javathena.core.utiles.Functions.parseIntToByte";
		String params = "(int)";
		String returnType = "byte";
		GenieMethod genieMethod = genieMethodRepository.findByInterfaceElements(fqn, params, returnType);
		return genieMethod;
	}

	private GenieMethod getGenieMethodTest2() {

		if (genieMethodTest2 != null)
			return genieMethodTest2;

		String fqn = "com.eteks.sweethome3d.model.Camera.convertTimeToTimeZone";
		String params = "(long,java.lang.String)";
		String returnType = "long";
		GenieMethod genieMethod = genieMethodRepository.findByInterfaceElements(fqn, params, returnType);
		return genieMethod;
	}

	private GenieMethod getGenieMethodTest3() {

		if (genieMethodTest3 != null)
			return genieMethodTest3;

		String fqn = "framework.ApplicationParameters.getAsInteger";
		String params = "(java.lang.String,java.lang.Integer)";
		String returnType = "java.lang.Integer";
		GenieMethod genieMethod = genieMethodRepository.findByInterfaceElements(fqn, params, returnType);
		return genieMethod;
	}

}
