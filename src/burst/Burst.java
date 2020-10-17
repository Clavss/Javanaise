package burst;

import jvn.JvnException;
import jvn.JvnServerImpl;


public class Burst {
    public static int numProcess = 2;
    public static int numIter = 1000;

    public static void main(String argv[]) throws JvnException {
            run();
            read();
    }

    protected static void run() throws JvnException {
        JvnServerImpl js = JvnServerImpl.jvnGetServer();
        ICounter jo = (ICounter) js.jvnLookupObject("burst");
        if (jo == null) {
            jo = (ICounter) js.jvnCreateObject("burst", new Counter());
        }
        for (int i = 0; i < numIter; i++){
            jo.increaseCounter();
        }
        JvnServerImpl.jvnGetServer().jvnTerminate();
    }



    public static int read() throws JvnException{
        JvnServerImpl js = JvnServerImpl.jvnGetServer();
        ICounter jo = (ICounter) js.jvnLookupObject("burst");
        if (jo == null) {
            jo = (ICounter) js.jvnCreateObject("burst", new Counter());
        }
        System.out.println(jo.getCounter());
        JvnServerImpl.jvnGetServer().jvnTerminate();
        return jo.getCounter();
    }

}
