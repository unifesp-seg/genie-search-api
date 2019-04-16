package br.unifesp.ict.seg.geniesearchapi.services.compilemethod.infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.LogUtils;

public class ThreadExecObject implements Runnable {
	
	private Class<?> myClass;
	private Object[] values;
	private Method method;
	
	private volatile Object obj = null;
	
	
	public ThreadExecObject(Class<?> myClass, Object[] values, Method method) {
		this.myClass = myClass;
		this.values = values;
		this.method = method;
	}
	
	public void run() {
		try {
    		Object instance = myClass.newInstance();
    		
			LogUtils.getLogger().info("Executing Method: " + method);
			LogUtils.getLogger().info("With values: " + values[0]);
			obj =  method.invoke(instance, values);
			LogUtils.getLogger().info("Result = " + obj);
			LogUtils.getLogger().info("");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException("Exception: (Thread) " + e.getCause());
		}
    }
	
	public Object getObj() {
		return obj;
	}
	

}
