package burst;

import jvn.JvnException;
import jvn.JvnServerImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Burst {
    private static int numThreads = 2;

    public static void main(String argv[]) throws InterruptedException {
        ExecutorService es =  Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            es.execute(() -> {
                try {
                    // initialize JVN instance
                    JvnServerImpl js = JvnServerImpl.jvnGetServer();

                    // look up the IRC object in the JVN server
                    // if not found, create it, and register it in the JVN server
                    ICounter jo = (ICounter) js.jvnLookupObject("burst");

                    if (jo == null) {
                        jo = (ICounter) js.jvnCreateObject("burst", new Counter());
                    }
                    // create the graphical part of the Chat application
                    new Burst(jo);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("IRC problem : " + e.getMessage());
                }
            });
        }
        es.shutdown();
        while(!es.awaitTermination(20, TimeUnit.MILLISECONDS));
        /*
        *  TODO : tests reussite
        *
        * */
        System.exit(0);
    }

    public Burst(ICounter counter) throws JvnException {
        JvnServerImpl.jvnGetServer().jvnTerminate();

    }
}
