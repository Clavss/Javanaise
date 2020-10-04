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
	int count = 0;
	Map<String, JvnObject> jvnObjectsMap = new HashMap<>();
	Map<Integer, Pair<List<JvnRemoteServer>, JvnLockEnum>> jvnLockMap = new HashMap<>();
	Map<Integer, String> jvnJoinMap = new HashMap<>();
	private static final long serialVersionUID = 1L;

	//Thread
	final Object waiter = new Object();

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
		System.out.println("Server ready");
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
				jvnObjectsMap.put(jon, jo);
				jvnJoinMap.put(joi, jon);
				jvnLockMap.put(joi, new Pair<List<JvnRemoteServer>, JvnLockEnum>(getListFromRemoteServer(js), JvnLockEnum.W));
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
			
		case W:
			if (!jvnLockMap.get(joi).getVal1().get(0).getID().equals(js.getID())) {
				jvnObjectsMap.put(jvnJoinMap.get(joi), (JvnObject) jvnLockMap.get(joi).getVal1().get(0).jvnInvalidateWriter(joi));
				jvnLockMap.get(joi).setVal2(JvnLockEnum.R);
				jvnLockMap.get(joi).setVal1(getListFromRemoteServer(js));
			} else {
				jvnLockMap.get(joi).setVal2(JvnLockEnum.RW);
			}
			break;
		case RW:
			if (!jvnLockMap.get(joi).getVal1().get(0).getID().equals(js.getID())) {
				jvnObjectsMap.put(jvnJoinMap.get(joi), (JvnObject) jvnLockMap.get(joi).getVal1().get(0).jvnInvalidateWriterForReader(joi));
				jvnLockMap.get(joi).setVal2(JvnLockEnum.R);
				jvnLockMap.get(joi).setVal1(getListFromRemoteServer(js));
			}
			break;
		default:
			//Exception
			break;
		
		}
		System.out.println(js.toString() + " got the lock " + jvnLockMap.get(joi).getVal2() + " on the object " + joi);
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
		JvnLockEnum state = jvnLockMap.get(joi).getVal2();
		switch (state) {
			case NL:
				break;
			case R:
			case W:
			case RW:
				if (!jvnLockMap.get(joi).getVal1().get(0).getID().equals(js.getID())) {
					switch(state){
						case R:
							for(JvnRemoteServer readersJs : jvnLockMap.get(joi).getVal1()){
								readersJs.jvnInvalidateReader(joi);
							}
							break;
						case W:
							jvnObjectsMap.put(jvnJoinMap.get(joi), (JvnObject) jvnLockMap.get(joi).getVal1().get(0).jvnInvalidateWriter(joi));
							break;
						default:
							jvnObjectsMap.put(jvnJoinMap.get(joi), (JvnObject) jvnLockMap.get(joi).getVal1().get(0).jvnInvalidateWriterForReader(joi));
					}
				}
				break;
			default:
				//Exception
				break;
		}
		jvnLockMap.get(joi).setVal1(getListFromRemoteServer(js));
		jvnLockMap.get(joi).setVal2(JvnLockEnum.W);
		System.out.println(js.toString() + " got the lock " + jvnLockMap.get(joi).getVal2() + " on the object " + joi);
		return jvnObjectsMap.get(jvnJoinMap.get(joi));
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

 
