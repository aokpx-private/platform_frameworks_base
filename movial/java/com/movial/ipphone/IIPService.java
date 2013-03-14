// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.os.*;

public interface IIPService
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IIPService
    {

        public static IIPService asInterface(IBinder ibinder)
        {
            if(ibinder == null)
                return null;
            IInterface iinterface = ibinder.queryLocalInterface("com.movial.ipphone.IIPService");
            if(iinterface != null && (iinterface instanceof IIPService))
                return (IIPService)iinterface;
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
                parcel1.writeString("com.movial.ipphone.IIPService");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                test();
                parcel1.writeNoException();
                return true;

            case 2: // '\002'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                boolean flag5 = connect(parcel.readString(), parcel.readInt());
                parcel1.writeNoException();
                int i3 = 0;
                if(flag5)
                    i3 = 1;
                parcel1.writeInt(i3);
                return true;

            case 3: // '\003'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                disconnect();
                parcel1.writeNoException();
                return true;

            case 4: // '\004'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                String s4 = queryLastRegisterFailureCode();
                parcel1.writeNoException();
                parcel1.writeString(s4);
                return true;

            case 5: // '\005'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                int l2 = dial(parcel.readString(), parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(l2);
                return true;

            case 6: // '\006'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                int k2 = dialEmergencyCall(parcel.readString());
                parcel1.writeNoException();
                parcel1.writeInt(k2);
                return true;

            case 7: // '\007'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                hangup(parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 8: // '\b'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                hangupForegroundResumeBackground();
                parcel1.writeNoException();
                return true;

            case 9: // '\t'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                accept();
                parcel1.writeNoException();
                return true;

            case 10: // '\n'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                reject(parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 11: // '\013'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                boolean flag4;
                int j2;
                if(parcel.readInt() != 0)
                    flag4 = true;
                else if
                    flag4 = false;
                j2 = hold(flag4);
                parcel1.writeNoException();
                parcel1.writeInt(j2);
                return true;

            case 12: // '\f'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                conference();
                parcel1.writeNoException();
                return true;

            case 13: // '\r'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                boolean flag3;
                if(parcel.readInt() != 0)
                    flag3 = true;
                else if
                    flag3 = false;
                setMute(flag3);
                parcel1.writeNoException();
                return true;

            case 14: // '\016'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                int j1 = parcel.readInt();
                int k1 = parcel.readInt();
                String s3 = parcel.readString();
                int l1 = parcel.readInt();
                Messenger messenger9;
                int i2;
                if(parcel.readInt() != 0)
                    messenger9 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger9 = null;
                i2 = setCallForward(j1, k1, s3, l1, messenger9);
                parcel1.writeNoException();
                parcel1.writeInt(i2);
                return true;

            case 15: // '\017'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                boolean flag2;
                Messenger messenger8;
                int i1;
                if(parcel.readInt() != 0)
                    flag2 = true;
                else if
                    flag2 = false;
                if(parcel.readInt() != 0)
                    messenger8 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger8 = null;
                i1 = setCW(flag2, messenger8);
                parcel1.writeNoException();
                parcel1.writeInt(i1);
                return true;

            case 16: // '\020'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                Messenger messenger7;
                if(parcel.readInt() != 0)
                    messenger7 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger7 = null;
                registerForIncomingUSSD(messenger7);
                parcel1.writeNoException();
                return true;

            case 17: // '\021'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                String s2 = parcel.readString();
                Messenger messenger6;
                int l;
                if(parcel.readInt() != 0)
                    messenger6 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger6 = null;
                l = sendUssd(s2, messenger6);
                parcel1.writeNoException();
                parcel1.writeInt(l);
                return true;

            case 18: // '\022'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                Messenger messenger5;
                if(parcel.readInt() != 0)
                    messenger5 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger5 = null;
                registerForConnectionState(messenger5);
                parcel1.writeNoException();
                return true;

            case 19: // '\023'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                Messenger messenger4;
                if(parcel.readInt() != 0)
                    messenger4 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger4 = null;
                registerForSubscription(messenger4);
                parcel1.writeNoException();
                return true;

            case 20: // '\024'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                Messenger messenger3;
                if(parcel.readInt() != 0)
                    messenger3 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger3 = null;
                registerForCallStates(messenger3);
                parcel1.writeNoException();
                return true;

            case 21: // '\025'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                Messenger messenger2;
                boolean flag1;
                int k;
                if(parcel.readInt() != 0)
                    messenger2 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger2 = null;
                flag1 = registerForEmergencyCallPref(messenger2);
                parcel1.writeNoException();
                k = 0;
                if(flag1)
                    k = 1;
                parcel1.writeInt(k);
                return true;

            case 22: // '\026'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                Messenger messenger1;
                if(parcel.readInt() != 0)
                    messenger1 = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger1 = null;
                registerForIncomingSMS(messenger1);
                parcel1.writeNoException();
                return true;

            case 23: // '\027'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                String s = parcel.readString();
                String s1 = parcel.readString();
                Messenger messenger;
                if(parcel.readInt() != 0)
                    messenger = (Messenger)Messenger.CREATOR.createFromParcel(parcel);
                else if
                    messenger = null;
                sendSMS(s, s1, messenger, parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 24: // '\030'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                boolean flag;
                if(parcel.readInt() != 0)
                    flag = true;
                else if
                    flag = false;
                acknowledgeLastIncomingIpSms(flag, parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 25: // '\031'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                sendDtmf(parcel.readInt(), (char)parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 26: // '\032'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                startDtmf(parcel.readInt(), (char)parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 27: // '\033'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                stopDtmf(parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 28: // '\034'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                setCellLocation(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 29: // '\035'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                clearDisconnected();
                parcel1.writeNoException();
                return true;

            case 30: // '\036'
                parcel.enforceInterface("com.movial.ipphone.IIPService");
                String as[] = getContactUri();
                parcel1.writeNoException();
                parcel1.writeStringArray(as);
                return true;
            }
        }

        private static final String DESCRIPTOR = "com.movial.ipphone.IIPService";
        static final int TRANSACTION_accept = 9;
        static final int TRANSACTION_acknowledgeLastIncomingIpSms = 24;
        static final int TRANSACTION_clearDisconnected = 29;
        static final int TRANSACTION_conference = 12;
        static final int TRANSACTION_connect = 2;
        static final int TRANSACTION_dial = 5;
        static final int TRANSACTION_dialEmergencyCall = 6;
        static final int TRANSACTION_disconnect = 3;
        static final int TRANSACTION_getContactUri = 30;
        static final int TRANSACTION_hangup = 7;
        static final int TRANSACTION_hangupForegroundResumeBackground = 8;
        static final int TRANSACTION_hold = 11;
        static final int TRANSACTION_queryLastRegisterFailureCode = 4;
        static final int TRANSACTION_registerForCallStates = 20;
        static final int TRANSACTION_registerForConnectionState = 18;
        static final int TRANSACTION_registerForEmergencyCallPref = 21;
        static final int TRANSACTION_registerForIncomingSMS = 22;
        static final int TRANSACTION_registerForIncomingUSSD = 16;
        static final int TRANSACTION_registerForSubscription = 19;
        static final int TRANSACTION_reject = 10;
        static final int TRANSACTION_sendDtmf = 25;
        static final int TRANSACTION_sendSMS = 23;
        static final int TRANSACTION_sendUssd = 17;
        static final int TRANSACTION_setCW = 15;
        static final int TRANSACTION_setCallForward = 14;
        static final int TRANSACTION_setCellLocation = 28;
        static final int TRANSACTION_setMute = 13;
        static final int TRANSACTION_startDtmf = 26;
        static final int TRANSACTION_stopDtmf = 27;
        static final int TRANSACTION_test = 1;

        public Stub()
        {
            attachInterface(this, "com.movial.ipphone.IIPService");
        }
    }

    private static class Stub.Proxy
        implements IIPService
    {

        public void accept()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            mRemote.transact(9, parcel, parcel1, 0);
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

        public void acknowledgeLastIncomingIpSms(boolean flag, int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            int j;
            j = 0;
            if(flag)
                j = 1;
            parcel.writeInt(j);
            parcel.writeInt(i);
            mRemote.transact(24, parcel, parcel1, 0);
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

        public IBinder asBinder()
        {
            return mRemote;
        }

        public void clearDisconnected()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            mRemote.transact(29, parcel, parcel1, 0);
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

        public void conference()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            mRemote.transact(12, parcel, parcel1, 0);
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

        public boolean connect(String s, int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeString(s);
            parcel.writeInt(i);
            mRemote.transact(2, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            boolean flag = false;
            if(j != 0)
                flag = true;
            parcel1.recycle();
            parcel.recycle();
            return flag;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int dial(String s, int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeString(s);
            parcel.writeInt(i);
            mRemote.transact(5, parcel, parcel1, 0);
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

        public int dialEmergencyCall(String s)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeString(s);
            mRemote.transact(6, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void disconnect()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
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

        public String[] getContactUri()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String as[];
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            mRemote.transact(30, parcel, parcel1, 0);
            parcel1.readException();
            as = parcel1.createStringArray();
            parcel1.recycle();
            parcel.recycle();
            return as;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String getInterfaceDescriptor()
        {
            return "com.movial.ipphone.IIPService";
        }

        public void hangup(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeInt(i);
            mRemote.transact(7, parcel, parcel1, 0);
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

        public void hangupForegroundResumeBackground()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            mRemote.transact(8, parcel, parcel1, 0);
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

        public int hold(boolean flag)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            int i;
            i = 0;
            if(flag)
                i = 1;
            int j;
            parcel.writeInt(i);
            mRemote.transact(11, parcel, parcel1, 0);
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

        public String queryLastRegisterFailureCode()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String s;
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            mRemote.transact(4, parcel, parcel1, 0);
            parcel1.readException();
            s = parcel1.readString();
            parcel1.recycle();
            parcel.recycle();
            return s;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void registerForCallStates(Messenger messenger)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            if(messenger == null)
                break MISSING_BLOCK_LABEL_57;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            mRemote.transact(20, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void registerForConnectionState(Messenger messenger)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            if(messenger == null)
                break MISSING_BLOCK_LABEL_57;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            mRemote.transact(18, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public boolean registerForEmergencyCallPref(Messenger messenger)
            throws RemoteException
        {
            boolean flag;
            Parcel parcel;
            Parcel parcel1;
            flag = true;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            if(messenger == null)
                break MISSING_BLOCK_LABEL_76;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            int i;
            mRemote.transact(21, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            Exception exception;
            if(i == 0)
                flag = false;
            parcel1.recycle();
            parcel.recycle();
            return flag;
            parcel.writeInt(0);
              goto _L1
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void registerForIncomingSMS(Messenger messenger)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            if(messenger == null)
                break MISSING_BLOCK_LABEL_57;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            mRemote.transact(22, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void registerForIncomingUSSD(Messenger messenger)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            if(messenger == null)
                break MISSING_BLOCK_LABEL_57;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            mRemote.transact(16, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void registerForSubscription(Messenger messenger)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            if(messenger == null)
                break MISSING_BLOCK_LABEL_57;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            mRemote.transact(19, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void reject(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeInt(i);
            mRemote.transact(10, parcel, parcel1, 0);
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

        public void sendDtmf(int i, char c)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeInt(i);
            parcel.writeInt(c);
            mRemote.transact(25, parcel, parcel1, 0);
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

        public void sendSMS(String s, String s1, Messenger messenger, int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeString(s);
            parcel.writeString(s1);
            if(messenger == null)
                break MISSING_BLOCK_LABEL_86;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            parcel.writeInt(i);
            mRemote.transact(23, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int sendUssd(String s, Messenger messenger)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeString(s);
            if(messenger == null)
                break MISSING_BLOCK_LABEL_75;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            int i;
            mRemote.transact(17, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setCW(boolean flag, Messenger messenger)
            throws RemoteException
        {
            int i;
            Parcel parcel;
            Parcel parcel1;
            i = 1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            int j;
            if(!flag)
                i = 0;
            parcel.writeInt(i);
            if(messenger == null)
                break MISSING_BLOCK_LABEL_93;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            mRemote.transact(15, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setCallForward(int i, int j, String s, int k, Messenger messenger)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            parcel.writeString(s);
            parcel.writeInt(k);
            if(messenger == null)
                break MISSING_BLOCK_LABEL_103;
            parcel.writeInt(1);
            messenger.writeToParcel(parcel, 0);
_L1:
            int l;
            mRemote.transact(14, parcel, parcel1, 0);
            parcel1.readException();
            l = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return l;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void setCellLocation(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(28, parcel, parcel1, 0);
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

        public void setMute(boolean flag)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            int i;
            i = 0;
            if(flag)
                i = 1;
            parcel.writeInt(i);
            mRemote.transact(13, parcel, parcel1, 0);
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

        public void startDtmf(int i, char c)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeInt(i);
            parcel.writeInt(c);
            mRemote.transact(26, parcel, parcel1, 0);
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

        public void stopDtmf(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
            parcel.writeInt(i);
            mRemote.transact(27, parcel, parcel1, 0);
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

        public void test()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.movial.ipphone.IIPService");
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

        private IBinder mRemote;

        Stub.Proxy(IBinder ibinder)
        {
            mRemote = ibinder;
        }
    }


    public abstract void accept()
        throws RemoteException;

    public abstract void acknowledgeLastIncomingIpSms(boolean flag, int i)
        throws RemoteException;

    public abstract void clearDisconnected()
        throws RemoteException;

    public abstract void conference()
        throws RemoteException;

    public abstract boolean connect(String s, int i)
        throws RemoteException;

    public abstract int dial(String s, int i)
        throws RemoteException;

    public abstract int dialEmergencyCall(String s)
        throws RemoteException;

    public abstract void disconnect()
        throws RemoteException;

    public abstract String[] getContactUri()
        throws RemoteException;

    public abstract void hangup(int i)
        throws RemoteException;

    public abstract void hangupForegroundResumeBackground()
        throws RemoteException;

    public abstract int hold(boolean flag)
        throws RemoteException;

    public abstract String queryLastRegisterFailureCode()
        throws RemoteException;

    public abstract void registerForCallStates(Messenger messenger)
        throws RemoteException;

    public abstract void registerForConnectionState(Messenger messenger)
        throws RemoteException;

    public abstract boolean registerForEmergencyCallPref(Messenger messenger)
        throws RemoteException;

    public abstract void registerForIncomingSMS(Messenger messenger)
        throws RemoteException;

    public abstract void registerForIncomingUSSD(Messenger messenger)
        throws RemoteException;

    public abstract void registerForSubscription(Messenger messenger)
        throws RemoteException;

    public abstract void reject(int i)
        throws RemoteException;

    public abstract void sendDtmf(int i, char c)
        throws RemoteException;

    public abstract void sendSMS(String s, String s1, Messenger messenger, int i)
        throws RemoteException;

    public abstract int sendUssd(String s, Messenger messenger)
        throws RemoteException;

    public abstract int setCW(boolean flag, Messenger messenger)
        throws RemoteException;

    public abstract int setCallForward(int i, int j, String s, int k, Messenger messenger)
        throws RemoteException;

    public abstract void setCellLocation(int i, int j)
        throws RemoteException;

    public abstract void setMute(boolean flag)
        throws RemoteException;

    public abstract void startDtmf(int i, char c)
        throws RemoteException;

    public abstract void stopDtmf(int i)
        throws RemoteException;

    public abstract void test()
        throws RemoteException;
}
