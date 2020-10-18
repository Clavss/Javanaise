package burst;

import jvn.JvnException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BurstRunner {
    static long before = 0;
    static IBurst saveBurst;

    public static void main(String[] args) throws RemoteException, InterruptedException, JvnException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(null);

        ExecutorService es =  Executors.newFixedThreadPool(2);
        saveBurst = (IBurst) registry.lookup("Burst0");
        before = saveBurst.read();
        for (int i = 0; i < 2; i++) {
            int finalI = i;
            es.execute(() -> {
                int personnalI = finalI;
                try {
                    if(personnalI != 0) {
                        IBurst burst = (IBurst) registry.lookup("Burst"+ personnalI);
                        burst.run();
                    } else {
                        IBurst burst = saveBurst;
                        burst.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("IRC problem : " + e.getMessage());
                }
            });
        }
        es.shutdown();
        while(!es.awaitTermination(20, TimeUnit.MILLISECONDS));
        // Process finished
        System.out.println("fini, origine: " + before + ", terminaison: " + saveBurst.read());
    }
}
