package burst;

import annotation.Lock;

public class Counter implements ICounter, java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private int val;

    public Counter(){
        val = 0;
    }
    @Lock(type="r")
    public int getCounter() {
        return val;
    }

    @Lock(type="w")
    public void setCounter(int counter){
        val = counter;
    }

}
