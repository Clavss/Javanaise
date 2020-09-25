/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;
import java.io.*;


public class JvnServerImpl
		extends UnicastRemoteObject
		implements JvnLocalServer, JvnRemoteServer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;

	/**
	 * Default constructor
	 *
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		// to be completed
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
		// to be completed
		return null;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
		// to be completed
	}

	/**
	 * Provide the reference of a JVN object being given its symbolic name
	 *
	 * @param jon : the JVN object name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	public JvnObject jvnLookupObject(String jon) throws jvn.JvnException {
		// to be completed
		return null;
	}

	/**
	 * Get a Read lock on a JVN object
	 *
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException {
		// to be completed 
		return null;

	}

	/**
	 * Get a Write lock on a JVN object
	 *
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		// to be completed 
		return null;
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
		// to be completed 
	}

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 *
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		// to be completed 
		return null;
	}

	/**
	 * Reduce the Write lock of the JVN object identified by id
	 *
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		// to be completed 
		return null;
	}

}

 
