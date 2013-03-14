// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.os.*;

// Referenced classes of package com.movial.ipphone:
//            IIPStateListener

public interface IIPRegistry
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IIPRegistry
    {

        public static IIPRegistry asInterface(IBinder ibinder)
        {
            if(ibinder == null)
                return null;
            IInterface iinterface = ibinder.queryLocalInterface("com.movial.ipphone.IIPRegistry");
            if(iinterface != null && (iinterface instanceof IIPRegistry))
                return (IIPRegistry)iinterface;
            else if
                return new Proxy(ibinder);
        }

        public IBinder asBinder()
        {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel1, int j)
            throws RemoteException
        {
            switch(i)
            {
            default:
                return super.onTransact(i, parcel, parcel1, j);

            case 1598968902: 
                parcel1.writeString("com.movial.ipphone.IIPRegistry");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.movial.ipphone.IIPRegistry");
                listen(IIPStateListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 2: // '\002'
                parcel.enforceInterface("com.movial.ipphone.IIPRegistry");
                int k = checkAudioMode(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(k);
                return true;

            case 3: // '\003'
                parcel.enforceInterface("com.movial.ipphone.IIPRegistry");
                notifyRssiChange(parcel.readInt());
                parcel1.writeNoException();
                return true;
            }
        }

        private static final String DESCRIPTOR = "com.movial.ipphone.IIPRegistry";
        static final int TRANSACTION_checkAudioMode = 2;
        static final int TRANSACTION_listen = 1;
        static final int TRANSACTION_notifyRssiChange = 3;

        public Stub()
        {
            attachInterface(this, "com.movial.ipphone.IIPRegistry");
        }
    }

    private static class Stub.Proxy
        implements IIPRegistry
    {

        public IBinder asBinder()
        {
            return mRemote;
        }

        public int checkAudioMode(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.movial.ipphone.IIPRegistry");
            parcel.writeInt(i);
            mRemote.transact(2, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String getInterfaceDescriptor()
        {
            return "com.movial.ipphone.IIPRegistry";
        }

        public void listen(IIPStateListener iipstatelistener, int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPRegistry");
            if(iipstatelistener == null)
                break MISSING_BLOCK_LABEL_68;
            IBinder ibinder = iipstatelistener.asBinder();
_L1:
            parcel.writeStrongBinder(ibinder);
            parcel.writeInt(i);
            mRemote.transact(1, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            ibinder = null;
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void notifyRssiChange(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPRegistry");
            parcel.writeInt(i);
            mRemote.transact(3, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        private IBinder mRemote;

        Stub.Proxy(IBinder ibinder)
        {
            mRemote = ibinder;
        }
    }


    public abstract int checkAudioMode(int i)
        throws RemoteException;

    public abstract void listen(IIPStateListener iipstatelistener, int i)
        throws RemoteException;

    public abstract void notifyRssiChange(int i)
        throws RemoteException;
}
