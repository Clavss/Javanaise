/***
 * JAVANAISE API
 * JvnLocalServer interface
 * Defines the local interface provided by a JVN server 
 * An application uses the Javanaise service through the local interface provided by the Jvn server 
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;

/**
 * Local interface of a JVN server  (used by the applications).
 * An application can get the reference of a JVN server through the static
 * method jvnGetServer() (see  JvnServerImpl).
 */

public interface JvnLocalServer {

	/**
	 * create of a JVN object
	 *
	 * @param jos : the JVN object state
	 * @return the JVN object
	 * @throws JvnException
	 **/
	JvnObject jvnCreateObject(Serializable jos) throws jvn.JvnException;

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @throws JvnException
	 **/
	void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException;

	/**
	 * Get the reference of a JVN object associated to a symbolic name
	 *
	 * @param jon : the JVN object symbolic name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	JvnObject jvnLookupObject(String jon) throws jvn.JvnException;


	/**
	 * Get a Read lock on a JVN object
	 *
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	Serializable jvnLockRead(int joi) throws JvnException;

	/**
	 * Get a Write lock on a JVN object
	 *
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	Serializable jvnLockWrite(int joi) throws JvnException;

	
	void jvnUnLock() throws JvnException;

	/**
	 * The JVN service is not used anymore by the application
	 *
	 * @throws JvnException
	 **/
	void jvnTerminate() throws jvn.JvnException;

}

 
