package jvn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import annotation.Lock;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class JvnProxy implements InvocationHandler {
	private JvnObject jo;
	
	private JvnProxy(JvnObject jo) {
		this.jo = jo;
	}
	
	public static Object newInstance(JvnObject jo) throws JvnException {
		Object obj = jo.jvnGetSharedObject();
		return java.lang.reflect.Proxy.newProxyInstance(
				obj.getClass().getClassLoader(),
				obj.getClass().getInterfaces(),
				new JvnProxy(jo));
	}
	
	public Object invoke(Object proxy, Method m, Object args[]) throws JvnException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object result;
		if (m.isAnnotationPresent(Lock.class)) {
			Lock lock = m.getAnnotation(Lock.class);
			if (lock.type().equals("r")) {
				jo.jvnLockRead();
			} else if (lock.type().equals("w")) {
				jo.jvnLockWrite();
			} else {
				throw new JvnException("Invalid annotation, please use 'r' or 'w'");
			}
			result = m.invoke(jo.jvnGetSharedObject(), args);
			jo.jvnUnLock();
		} else {
			result = m.invoke(jo.jvnGetSharedObject(), args);
		}
		return result;
	}
}

