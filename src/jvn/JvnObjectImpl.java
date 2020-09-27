package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{
    Serializable o;
    boolean read, write;

    public JvnObjectImpl(Serializable serializableObject) {
        this.o = serializableObject;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        read = true;


    }

    @Override
    public void jvnLockWrite() throws JvnException {
        write = true;

    }

    @Override
    public void jvnUnLock() throws JvnException {
        if(read) {

            read = false;
        }
        if(write){

            write = false;
        }
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return 0;
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
