// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.telephony.DriverCall;

public class SessionCall extends DriverCall
    implements Parcelable
{
    public static final class State extends Enum
    {

        public static State valueOf(String s)
        {
            return (State)Enum.valueOf(com/movial/ipphone/SessionCall$State, s);
        }

        public static State[] values()
        {
            return (State[])$VALUES.clone();
        }

        private static final State $VALUES[];
        public static final State ACTIVE;
        public static final State ALERTING;
        public static final State DIALING;
        public static final State DISCONNECTED;
        public static final State HOLDING;
        public static final State INCOMING;
        public static final State UNKNOWN;
        public static final State WAITING;

        static 
        {
            ACTIVE = new State("ACTIVE", 0);
            HOLDING = new State("HOLDING", 1);
            DIALING = new State("DIALING", 2);
            ALERTING = new State("ALERTING", 3);
            INCOMING = new State("INCOMING", 4);
            WAITING = new State("WAITING", 5);
            DISCONNECTED = new State("DISCONNECTED", 6);
            UNKNOWN = new State("UNKNOWN", 7);
            State astate[] = new State[8];
            astate[0] = ACTIVE;
            astate[1] = HOLDING;
            astate[2] = DIALING;
            astate[3] = ALERTING;
            astate[4] = INCOMING;
            astate[5] = WAITING;
            astate[6] = DISCONNECTED;
            astate[7] = UNKNOWN;
            $VALUES = astate;
        }

        private State(String s, int i)
        {
            super(s, i);
        }
    }


    public SessionCall()
    {
    }

    private SessionCall(Parcel parcel)
    {
        readFromParcel(parcel);
    }


    public int describeContents()
    {
        return 0;
    }

    public void readFromParcel(Parcel parcel)
    {
        boolean flag = true;
        super.index = parcel.readInt();
        boolean flag1;
        if(parcel.readByte() == flag)
            flag1 = flag;
        else
            flag1 = false;
        super.isMT = flag1;
        if(parcel.readByte() != flag)
            flag = false;
        inConf = flag;
        super.number = parcel.readString();
        super.name = parcel.readString();
        super.numberPresentation = parcel.readInt();
        state = (State)parcel.readSerializable();
        cause = (com.android.internal.telephony.Connection.DisconnectCause)parcel.readSerializable();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        byte byte0 = 1;
        parcel.writeInt(super.index);
        byte byte1;
        if(super.isMT)
            byte1 = byte0;
        else
            byte1 = 0;
        parcel.writeByte(byte1);
        if(!inConf)
            byte0 = 0;
        parcel.writeByte(byte0);
        parcel.writeString(super.number);
        parcel.writeString(super.name);
        parcel.writeInt(super.numberPresentation);
        parcel.writeSerializable(state);
        parcel.writeSerializable(cause);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public SessionCall createFromParcel(Parcel parcel)
        {
            return new SessionCall(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public SessionCall[] newArray(int i)
        {
            return new SessionCall[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    public static final int MSG_CALL_ANSWERED = 2;
    public static final int MSG_CALL_RINGBACK = 6;
    public static final int MSG_CALL_RINGING = 1;
    public static final int MSG_INCOMING_CALL = 3;
    public static final int MSG_UPDATE_CNAM = 5;
    public static final int MSG_UPDATE_CONNECTION = 4;
    public com.android.internal.telephony.Connection.DisconnectCause cause;
    public boolean inConf;
    public State state;

}
