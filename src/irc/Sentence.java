/***
 * Sentence class : used for keeping the text exchanged between users
 * during a chat application
 * Contact:
 *
 * Authors:
 */

package irc;

import annotation.Lock;

public class Sentence implements ISentence, java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String data;

	public Sentence() {
		data = "";
	}

	@Lock(type="w")
	public void write(String text) {
		data = text;
	}

	@Lock(type="r")
	public String read() {
		return data;
	}

}