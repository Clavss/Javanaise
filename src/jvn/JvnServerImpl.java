/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import utils.Pair;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


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
		Registry registry = LocateRegistry.getRegistry(null);

		// Looking up the registry for the remote object
		jvnRemoteCoord = (JvnRemoteCoord) registry.lookup("IRC");
		
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
				throw new JvnException();
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
		// to be completed
	}

	/**
	 * creation of a JVN object
	 *
	 * @param o : the JVN object state
	 * @throws JvnException
	 **/
	public JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException {
		JvnObject jo = new JvnObjectImpl(o);
		return jo;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
		try {
			int id = jvnRemoteCoord.jvnGetObjectId();
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
	public JvnObject jvnLookupObject(String jon) throws jvn.JvnException {
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

}

 
