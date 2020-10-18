package burst;

import annotation.Lock;

public interface ICounter {
    @Lock(type="r")
    public long getCounter();

    @Lock(type="w")
    public void increaseCounter();
}
