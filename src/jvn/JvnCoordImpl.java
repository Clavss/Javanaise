/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import utils.Pair;


public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

	/**
	 *
	 */
	int count = 0;
	Map<String, JvnObject> jvnObjectsMap = new HashMap<>();
	Map<Integer, Pair<List<JvnRemoteServer>, JvnLockEnum>> jvnLockMap = new HashMap<>();
	Map<Integer, String> jvnJoinMap = new HashMap<>();
	Map<Integer, List<Pair<JvnLockEnum,JvnRemoteServer>>> jvnObjectWaitMap = new HashMap<>();
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws Exception {
		JvnCoordImpl obj = new JvnCoordImpl();
	}

	/**
	 * Default constructor
	 *
	 * @throws JvnException
	 **/
	private JvnCoordImpl() throws Exception {
		Registry registry = LocateRegistry.createRegistry(1099);
		registry.bind("IRC", this);
		System.err.println("Server ready");
	}

	/**
	 * Allocate a NEW JVN object id (usually allocated to a
	 * newly created JVN object)
	 *
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public int jvnGetObjectId()
			throws java.rmi.RemoteException, jvn.JvnException {
		// to be completed
		return count++;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, jvn.JvnException {
				js.jvnInvalidateReader(joi);
				jvnObjectsMap.put(jon, jo);
				jvnJoinMap.put(joi, jon);
				jvnObjectWaitMap.put(joi, new LinkedList<Pair<JvnLockEnum,JvnRemoteServer>>());
				jvnLockMap.put(joi, new Pair<List<JvnRemoteServer>, JvnLockEnum>(getListFromRemoteServer(js), JvnLockEnum.NL));
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server
	 *
	 * @param jon : the JVN object name
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
			throws java.rmi.RemoteException, jvn.JvnException {
		return jvnObjectsMap.get(jon);
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server
	 *
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		switch (jvnLockMap.get(joi).getVal2()) {
		case NL:
			jvnLockMap.get(joi).setVal1(getListFromRemoteServer(js));
			jvnLockMap.get(joi).setVal2(JvnLockEnum.R);
			break;
			
		case R:
			if (!jvnLockMap.get(joi).getVal1().contains(js)) {
				jvnLockMap.get(joi).getVal1().add(js);
			}
			break;
			
		case RC:
			if (!jvnLockMap.get(joi).getVal1().contains(js)) {
				jvnLockMap.get(joi).setVal1(getListFromRemoteServer(js));
			}
			jvnLockMap.get(joi).setVal2(JvnLockEnum.R);
			break;
			
		case W:
			if (jvnLockMap.get(joi).getVal1() != js) {
				jvnLockMap.get(joi).getVal1().get(0).jvnInvalidateWriterForReader(joi);
				jvnObjectWaitMap.get(joi).add(new Pair<>(JvnLockEnum.R, js));
			}
			jvnLockMap.get(joi).setVal2(JvnLockEnum.NL);
			break;
			
		case WC:
		case RWC:
			if (jvnLockMap.get(joi).getVal1() != js) {
				jvnLockMap.get(joi).getVal1().get(0).jvnInvalidateWriterForReader(joi);
				jvnObjectWaitMap.get(joi).add(new Pair<>(JvnLockEnum.R, js));
			}
			jvnLockMap.get(joi).setVal2(JvnLockEnum.NL);
			break;
			
		default:
			break;
		
		}
		
		return jvnObjectsMap.get(jvnJoinMap.get(joi));
	}

	private List<JvnRemoteServer> getListFromRemoteServer(JvnRemoteServer js) {
		List<JvnRemoteServer> list = new ArrayList<>();
		list.add(js);
		return list;
	}
	
	/**
	 * Get a Write lock on a JVN object managed by a given JVN server
	 *
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		// to be completed
		return null;
	}
	
	public void jvnUnLock(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		if (jvnLockMap.get(joi).getVal2() == JvnLockEnum.R) {
			jvnLockMap.get(joi).setVal2(JvnLockEnum.RC);
		} else if (jvnLockMap.get(joi).getVal2() == JvnLockEnum.W) {
			jvnLockMap.get(joi).setVal2(JvnLockEnum.WC);
		}
	}

	/**
	 * A JVN server terminates
	 *
	 * @param js : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public void jvnTerminate(JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		// to be completed
	}

}

 
