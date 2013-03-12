// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.os.*;

public interface IIPStateListener
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IIPStateListener
    {

        public static IIPStateListener asInterface(IBinder ibinder)
        {
            if(ibinder == null)
                return null;
            IInterface iinterface = ibinder.queryLocalInterface("com.movial.ipphone.IIPStateListener");
            if(iinterface != null && (iinterface instanceof IIPStateListener))
                return (IIPStateListener)iinterface;
            else
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
                parcel1.writeString("com.movial.ipphone.IIPStateListener");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.movial.ipphone.IIPStateListener");
                boolean flag;
                if(parcel.readInt() != 0)
                    flag = true;
                else
                    flag = false;
                onRegisteredStateChanged(flag, parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 2: // '\002'
                parcel.enforceInterface("com.movial.ipphone.IIPStateListener");
                onWifiCallStateChanged(parcel.readInt(), parcel.readString());
                parcel1.writeNoException();
                return true;
            }
        }

        private static final String DESCRIPTOR = "com.movial.ipphone.IIPStateListener";
        static final int TRANSACTION_onRegisteredStateChanged = 1;
        static final int TRANSACTION_onWifiCallStateChanged = 2;

        public Stub()
        {
            attachInterface(this, "com.movial.ipphone.IIPStateListener");
        }
    }

    private static class Stub.Proxy
        implements IIPStateListener
    {

        public IBinder asBinder()
        {
            return mRemote;
        }

        public String getInterfaceDescriptor()
        {
            return "com.movial.ipphone.IIPStateListener";
        }

        public void onRegisteredStateChanged(boolean flag, int i)
            throws RemoteException
        {
            int j;
            Parcel parcel;
            Parcel parcel1;
            j = 1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPStateListener");
            if(!flag)
                j = 0;
            parcel.writeInt(j);
            parcel.writeInt(i);
            mRemote.transact(1, parcel, parcel1, 0);
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

        public void onWifiCallStateChanged(int i, String s)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPStateListener");
            parcel.writeInt(i);
            parcel.writeString(s);
            mRemote.transact(2, parcel, parcel1, 0);
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


    public abstract void onRegisteredStateChanged(boolean flag, int i)
        throws RemoteException;

    public abstract void onWifiCallStateChanged(int i, String s)
        throws RemoteException;
}
