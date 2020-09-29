/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;

/**
 * Interface of a JVN object.
 * A JVN object is used to acquire read/write locks to access a given shared object
 */

public interface JvnObject extends Serializable {
	/* A JvnObject should be serializable in order to be able to transfer 
       a reference to a JVN object remotely */

	/**
	 * Get a Read lock on the shared object
	 *
	 * @throws JvnException
	 **/
	void jvnLockRead()
			throws jvn.JvnException;

	/**
	 * Get a Write lock on the object
	 *
	 * @throws JvnException
	 **/
	void jvnLockWrite()
			throws jvn.JvnException;

	/**
	 * Unlock  the object
	 *
	 * @throws JvnException
	 **/
	void jvnUnLock()
			throws jvn.JvnException;


	/**
	 * Get the object identification
	 *
	 * @throws JvnException
	 **/
	int jvnGetObjectId()
			throws jvn.JvnException;

	/**
	 * Get the shared object associated to this JvnObject
	 *
	 * @throws JvnException
	 **/
	Serializable jvnGetSharedObject()
			throws jvn.JvnException;


	/**
	 * Invalidate the Read lock of the JVN object
	 *
	 * @throws JvnException
	 **/
	void jvnInvalidateReader()
			throws jvn.JvnException;

	/**
	 * Invalidate the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	Serializable jvnInvalidateWriter()
			throws jvn.JvnException;

	/**
	 * Reduce the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	Serializable jvnInvalidateWriterForReader()
			throws jvn.JvnException;

	void setID(int id);

}
