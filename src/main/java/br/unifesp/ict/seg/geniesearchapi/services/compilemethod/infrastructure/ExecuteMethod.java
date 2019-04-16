package br.unifesp.ict.seg.geniesearchapi.services.compilemethod.infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.MissingResourceException;

import javax.management.RuntimeErrorException;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.GenieMethodRepository;

public class ExecuteMethod {

	public ExecuteMethod() {
	}

	private static Class<?> getClassByName(String className) {

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
		if ("string".equals(className))
			return String.class;

		return null;
	}

}
