/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


public class JvnServerImpl
		extends UnicastRemoteObject
		implements JvnLocalServer, JvnRemoteServer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton
	private static JvnServerImpl js = null;
	private JvnRemoteCoord jvnRemoteCoord;
	private Object obj = new Object();
	private boolean waiting = false;

	Map<String, JvnObject> jvnObjectsMap = new HashMap<>();
	Map<Integer, String> jvnJoinMap = new HashMap<>();

	/**
	 * Default constructor
	 *
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		// Getting the registry
		System.out.println("serverimpl A");
		Registry registry = LocateRegistry.getRegistry(null);
		System.out.println("serverimpl B");
		// Looking up the registry for the remote object
		jvnRemoteCoord = (JvnRemoteCoord) registry.lookup("IRC");
		System.out.println("serverimpl C");
		js = this;
	}
	
	public static JvnServerImpl getServer() {
		return js;
	}

	/**
	 * Static method allowing an application to get a reference to
	 * a JVN server instance
	 *
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() throws jvn.JvnException{
		if (js == null) {
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				throw new JvnException(e.getMessage());
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 *
	 * @throws JvnException
	 **/
	public void jvnTerminate() throws jvn.JvnException {
		try {
			jvnRemoteCoord.jvnTerminate(this);
		} catch (RemoteException | JvnException e) {
			throw new JvnException();
		}
	}

	/**
	 * creation of a JVN object
	 *
	 * @param o : the JVN object state
	 * @throws JvnException
	 **/
	public Object jvnCreateObject(Serializable o) throws jvn.JvnException {
		Object obj = newInstance(o);
		// after creation, I have a write lock on the object
		((JvnServerImpl) obj).jvnUnLock();
		return obj;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, Object obj) throws jvn.JvnException {
		try {
			int id = jvnRemoteCoord.jvnGetObjectId();
			JvnObject jo = (JvnObject)obj;
			jo.setID(id);
			jvnObjectsMap.put(jon, jo);
			jvnJoinMap.put(id, jon);
			jvnRemoteCoord.jvnRegisterObject(jon, jo, id, this);
		} catch (RemoteException e) {
			throw new jvn.JvnException();
		}
	}
	
	/**
	 * Provide the reference of a JVN object being given its symbolic name
	 *
	 * @param jon : the JVN object name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	public Object jvnLookupObject(String jon) throws jvn.JvnException {
		try {
			JvnObject jo = jvnRemoteCoord.jvnLookupObject(jon, this);
			if(jo != null){
				jvnObjectsMap.put(jon, jo);
				jvnJoinMap.put(jo.jvnGetObjectId(), jon);
				jo.setLock(JvnLockEnum.NL);
			}
			return jo;
		} catch (RemoteException e) {
			throw new jvn.JvnException();
		}
	}

	/**
	 * Get a Read lock on a JVN object
	 *
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException {
		try {
			return jvnRemoteCoord.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			throw new JvnException();
		}
	}

	/**
	 * Get a Write lock on a JVN object
	 *
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		try {
			return jvnRemoteCoord.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			throw new JvnException();
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		if(waiting){
			waiting = false;
			obj.notify();
		}
	}

	/**
	 * Invalidate the Read lock of the JVN object identified by id
	 * called by the JvnCoord
	 *
	 * @param joi : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		jvnObjectsMap.get(jvnJoinMap.get(joi)).jvnInvalidateReader();
	}

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 *
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		if(jvnObjectsMap.get(jvnJoinMap.get(joi)).getLock() == JvnLockEnum.W){
			try {
				waiting = true;
				obj.wait();
			} catch (InterruptedException e) {
				throw new jvn.JvnException();
			}
		}
		return jvnObjectsMap.get(jvnJoinMap.get(joi)).jvnInvalidateWriter();
	}

	/**
	 * Reduce the Write lock of the JVN object identified by id
	 *
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		jvnInvalidateReader(joi);
		return jvnObjectsMap.get(jvnJoinMap.get(joi)).jvnInvalidateWriterForReader();
	}

	@Override
	public String getID() throws RemoteException, JvnException {
		return this.toString();
	}
	
	
	private Object newInstance(Serializable obj) {
		System.out.println("Objet créé");
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new JvnObjectImpl(obj));
	}

}

 
