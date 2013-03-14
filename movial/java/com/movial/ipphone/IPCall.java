// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.util.Log;
import com.android.internal.telephony.*;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.movial.ipphone:
//            IPConnection, IPCallTracker

class IPCall extends Call
{

    IPCall(IPCallTracker ipcalltracker)
    {
        connections = new ArrayList();
        TAG = "IPCall";
        owner = ipcalltracker;
    }

    void attach(Connection connection, com.android.internal.telephony.Call.State state)
    {
        connections.add(connection);
        this.state = state;
    }

    void clearDisconnected()
    {
        for(int i = -1 + connections.size(); i >= 0; i--)
            if(((IPConnection)connections.get(i)).getState() == com.android.internal.telephony.Call.State.DISCONNECTED)
                connections.remove(i);

        Log.d(TAG, (new StringBuilder()).append("clearDisconnected. ").append(connections.size()).toString());
        if(connections.size() == 0)
            state = com.android.internal.telephony.Call.State.IDLE;
    }

    boolean connectionDisconnected(IPConnection ipconnection)
    {
        com.android.internal.telephony.Call.State state = this.state;
        if(this.state == com.android.internal.telephony.Call.State.DISCONNECTED) goto _L2; else if goto _L1
_L1:
        boolean flag;
        int i;
        int j;
        flag = true;
        i = 0;
        j = connections.size();
_L8:
        if(i >= j) goto _L4; else if goto _L3
_L3:
        if(((Connection)connections.get(i)).getState() == com.android.internal.telephony.Call.State.DISCONNECTED) goto _L6; else if goto _L5
_L5:
        flag = false;
_L4:
        if(flag)
            this.state = com.android.internal.telephony.Call.State.DISCONNECTED;
_L2:
        return state != this.state;
_L6:
        i++;
        if(true) goto _L8; else if goto _L7
_L7:
    }

    void detach(IPConnection ipconnection)
    {
        connections.remove(ipconnection);
        if(connections.size() == 0)
            state = com.android.internal.telephony.Call.State.IDLE;
    }

    public void dispose()
    {
    }

    public List getConnections()
    {
        return connections;
    }

    public Phone getPhone()
    {
        return owner.phone;
    }

    public void hangup()
        throws CallStateException
    {
        owner.hangup(this);
    }

    public void hangupAllCalls()
        throws CallStateException
    {
        Log.e(TAG, "not support hangupAll");
        owner.hangup(this);
    }

    boolean isFull()
    {
        return connections.size() == 5;
    }

    public boolean isMultiparty()
    {
        return connections.size() > 1;
    }

    public boolean isVideoCall()
    {
        return false;
    }

    void onHangupLocal()
    {
        int i = 0;
        for(int j = connections.size(); i < j; i++)
            ((IPConnection)connections.get(i)).onHangupLocal();

        state = com.android.internal.telephony.Call.State.DISCONNECTING;
    }

    public String toString()
    {
        return state.toString();
    }

    boolean update(IPConnection ipconnection, com.android.internal.telephony.Call.State state)
    {
        com.android.internal.telephony.Call.State state1 = this.state;
        boolean flag = false;
        if(state != state1)
        {
            this.state = state;
            flag = true;
        }
        return flag;
    }

    private String TAG;
    ArrayList connections;
    IPCallTracker owner;
}
