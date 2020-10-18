package burst;

import annotation.Lock;

public interface ICounter {

	@Lock(type = "r")
	long getCounter();

	@Lock(type = "w")
	void increaseCounter();

}
