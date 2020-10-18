package burst;

import jvn.JvnException;

import java.rmi.Remote;

public interface IBurst extends Remote {

    void run() throws JvnException, InterruptedException, java.rmi.RemoteException;

    long read() throws JvnException, java.rmi.RemoteException;
}
