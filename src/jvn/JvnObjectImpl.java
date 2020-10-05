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
        lockState = JvnLockEnum.W;
    }

    public void setID(int id) {
    	this.id = id;
        lockState = JvnLockEnum.NL;
    }

    @Override
    public JvnLockEnum getLock() {
        return lockState;
    }

    @Override
    public void setLock(JvnLockEnum lock) {
        this.lockState = lock;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        if(lockState == JvnLockEnum.WC){
            JvnServerImpl.getServer().jvnLockRead(id);
            lockState = JvnLockEnum.RWC;
        }
        else if (lockState != JvnLockEnum.R && lockState != JvnLockEnum.RC){
            this.o = ((JvnObject)JvnServerImpl.getServer().jvnLockRead(id)).jvnGetSharedObject();
            lockState = JvnLockEnum.R;
        } else {
            lockState = JvnLockEnum.R;
        }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        if(lockState != JvnLockEnum.WC && lockState != JvnLockEnum.RWC && lockState != JvnLockEnum.W){
            this.o =  ((JvnObject)JvnServerImpl.getServer().jvnLockWrite(id)).jvnGetSharedObject();
        }
        lockState = JvnLockEnum.W;
    }

    @Override
    public void jvnUnLock() throws JvnException {
        switch(lockState){
            case R:
                lockState = JvnLockEnum.RC;
                break;
            case W:
            case RWC:
                lockState = JvnLockEnum.WC;
                break;
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
        System.out.println("Invalidate reader");
        lockState = JvnLockEnum.NL;
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        System.out.println("Invalidate writer");
        lockState = JvnLockEnum.NL;
        return this;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        System.out.println("Invalidate writer for reader");
        lockState = JvnLockEnum.NL;
        return this;
    }
}
