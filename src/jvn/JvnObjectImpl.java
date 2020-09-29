package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Serializable o;
    JvnLockEnum lockState;
    private int id;

    public JvnObjectImpl(Serializable serializableObject) {
        this.o = serializableObject;
    }

    public void setID(int id) {
    	this.id = id;
    }
    
    @Override
    public void jvnLockRead() throws JvnException {
    	JvnServerImpl.getServer().jvnLockRead(id);
    }

    @Override
    public void jvnLockWrite() throws JvnException {

    }

    @Override
    public void jvnUnLock() throws JvnException {
    	JvnServerImpl.getServer().jvnUnLock(id);
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return null;
    }

    @Override
    public void jvnInvalidateReader() throws JvnException {

    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        return null;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        return null;
    }
}
