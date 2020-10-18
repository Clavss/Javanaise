package burst;

import jvn.JvnException;
import jvn.JvnServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;


public class Burst extends UnicastRemoteObject implements IBurst {
    public static long numIter = 100000000;
    public static Random random =  new Random();

    public static void main(String argv[]) throws Exception {
        new Burst(Integer.parseInt(argv[0]));
    }

    public Burst(int num) throws Exception{
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("Burst" + num, this);
        System.out.println("Burst number "+ num+" ready");
    }

    public void run() throws JvnException, InterruptedException, java.rmi.RemoteException {
        JvnServerImpl js = JvnServerImpl.jvnGetServer();
        ICounter jo = (ICounter) js.jvnLookupObject("burst", new Counter());
        System.out.println("Valeur debut: " + jo.getCounter());
        for (int i = 0; i < numIter; i++){
            //Random read
//            if(random.nextBoolean()){
//                jo.getCounter();
//            }
            jo.increaseCounter();
        }
        System.out.println("Valeur fin: " + jo.getCounter());
        JvnServerImpl.jvnGetServer().jvnTerminate();
    }

    public long read() throws JvnException, java.rmi.RemoteException{
        JvnServerImpl js = JvnServerImpl.jvnGetServer();
        ICounter jo = (ICounter) js.jvnLookupObject("burst", new Counter());
        JvnServerImpl.jvnGetServer().jvnTerminate();
        return jo.getCounter();
    }

}
