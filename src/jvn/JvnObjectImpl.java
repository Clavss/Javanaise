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
    public JvnLockEnum getLock() {
        return lockState;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        if (lockState != JvnLockEnum.R){
            this.o = JvnServerImpl.getServer().jvnLockRead(id);
            if(lockState == JvnLockEnum.WC){
                lockState = JvnLockEnum.RWC;
            }
            else {
                lockState = JvnLockEnum.R;
            }
        }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        this.o = JvnServerImpl.getServer().jvnLockWrite(id);
        lockState = JvnLockEnum.W;
    }

    @Override
    public void jvnUnLock() throws JvnException {
        switch(lockState){
            case R:
                lockState = JvnLockEnum.RC;
            case W:
                lockState = JvnLockEnum.WC;
            case RWC:
                lockState = JvnLockEnum.RC;
        }
    	JvnServerImpl.getServer().jvnUnLock();
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return o;
    }

    @Override
    public void jvnInvalidateReader() throws JvnException {
        //TODO Exception si deja nolock -> l'utilisateur à mis deux jvnLockRead()
        lockState = JvnLockEnum.NL;
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        return this;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        jvnInvalidateReader();
        return this;
    }
}
