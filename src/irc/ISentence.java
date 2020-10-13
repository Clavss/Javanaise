package irc;

import annotation.Lock;

public interface ISentence {

	@Lock(type="w")
	public void write(String text);

	@Lock(type="r")
	public String read();
	
}
