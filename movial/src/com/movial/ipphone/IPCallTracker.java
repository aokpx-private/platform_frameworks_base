// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.*;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import com.android.internal.telephony.*;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.movial.ipphone:
//            IPCall, IPPhone, IPConnection, SessionCall, 
//            IIPService

public final class IPCallTracker extends CallTracker
{
    class pendingConnection
    {

        public void clear()
        {
            connIndex = -1;
            clirMode = -1;
        }

        public boolean isNull()
        {
            return connIndex == -1;
        }

        public void set(int i, int j)
        {
            connIndex = i;
            clirMode = j;
        }

        public int clirMode;
        public int connIndex;
        final IPCallTracker this$0;

        public pendingConnection()
        {
            this$0 = IPCallTracker.this;
            super();
            clear();
        }
    }


    IPCallTracker(IPPhone ipphone)
    {
        connections = new ArrayList(7);
        voiceCallEndedRegistrants = new RegistrantList();
        voiceCallStartedRegistrants = new RegistrantList();
        pendingMOIndex = -1;
        pendingConf = false;
        pendingMOConn = new pendingConnection();
        swapCall = -1;
        ringingCall = new IPCall(this);
        foregroundCall = new IPCall(this);
        backgroundCall = new IPCall(this);
        state = com.android.internal.telephony.Phone.State.IDLE;
        ringingIndex = -1;
        DELAYED_REMOVE_CONNECTION = 2000;
        DELAYED_KEEP_RINGING = 3000;
        mHandler = new Handler() {

            public void handleMessage(Message message)
            {
                switch(message.what)
                {
                default:
                    updateConnection(message);
                    return;

                case 41: // ')'
                    log("EVENT_REMOVE_CONNECTION");
                    removeConnection(message.arg2, com.android.internal.telephony.Connection.DisconnectCause.ERROR_UNSPECIFIED);
                    if(message.arg1 == 1)
                        pendingMOIndex = -1;
                    else
                    if(message.arg1 == 2)
                        pendingMOConn.clear();
                    updatePhoneState();
                    phone.notifyPreciseCallStateChanged();
                    return;

                case 42: // '*'
                    log("EVENT_KEEP_RINGING");
                    phone.notifyIncomingRing();
                    sendEmptyMessageDelayed(42, DELAYED_KEEP_RINGING);
                    return;

                case 43: // '+'
                    log("EVENT_CONFERENCE_FINISHED");
                    pendingConf = false;
                    phone.notifyPreciseCallStateChanged();
                    return;

                case 44: // ','
                    log("EVENT_CLEAR_CONNECTIONS");
                    clearConnections();
                    return;
                }
            }

            final IPCallTracker this$0;

            
            {
                this$0 = IPCallTracker.this;
                super();
            }
        };
        phone = ipphone;
        mMsnger = new Messenger(mHandler);
        mWifiLock = ((WifiManager)ipphone.getContext().getSystemService("wifi")).createWifiLock(3, "WifiCalling");
        mWifiLock.setReferenceCounted(false);
    }

    private boolean canDial()
    {
        String s = SystemProperties.get("ro.telephony.disable-call", "false");
        return pendingMOIndex == -1 && !pendingConf && !ringingCall.isRinging() && !s.equals("true") && (!foregroundCall.getState().isAlive() || !backgroundCall.getState().isAlive());
    }

    private void clear()
    {
        pendingMOIndex = -1;
        pendingConf = false;
        pendingMOConn.clear();
    }

    private void clearConnections()
    {
        Log.d("IPCallTracker", "clearConnections");
        for(int i = -1 + connections.size(); i > -1; i--)
            if(((IPConnection)connections.get(i)).cause == com.android.internal.telephony.Connection.DisconnectCause.LOCAL)
                removeConnection(i, com.android.internal.telephony.Connection.DisconnectCause.ERROR_UNSPECIFIED);

        updatePhoneState();
        phone.notifyPreciseCallStateChanged();
    }

    private void fakeHoldForegroundBeforeDial()
    {
        List list = (List)foregroundCall.connections.clone();
        int i = 0;
        for(int j = list.size(); i < j; i++)
            ((IPConnection)list.get(i)).fakeHoldBeforeDial();

    }

    private int findConnection(SessionCall sessioncall)
    {
        for(int i = 0; i < connections.size(); i++)
        {
            IPConnection ipconnection = (IPConnection)connections.get(i);
            if(ipconnection.getIndex() == ((DriverCall) (sessioncall)).index)
            {
                if(sessioncall.inConf)
                    ipconnection.inConf = true;
                return i;
            }
            if("E911".equals(((DriverCall) (sessioncall)).number) && PhoneNumberUtils.isEmergencyNumber(ipconnection.getAddress()))
            {
                ipconnection.setIndex(((DriverCall) (sessioncall)).index);
                return i;
            }
            if(ipconnection.getAddress().equals(((DriverCall) (sessioncall)).number) && ipconnection.getIndex() == -1)
            {
                ipconnection.setIndex(((DriverCall) (sessioncall)).index);
                return i;
            }
        }

        return -1;
    }

    private int isNewCall(SessionCall sessioncall)
    {
        if(sessioncall.inConf)
            return -1;
        static class _cls2
        {

            static final int $SwitchMap$com$android$internal$telephony$Call$State[];
            static final int $SwitchMap$com$movial$ipphone$SessionCall$State[];

            static 
            {
                $SwitchMap$com$movial$ipphone$SessionCall$State = new int[SessionCall.State.values().length];
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.INCOMING.ordinal()] = 1;
                }
                catch(NoSuchFieldError nosuchfielderror) { }
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.WAITING.ordinal()] = 2;
                }
                catch(NoSuchFieldError nosuchfielderror1) { }
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.ACTIVE.ordinal()] = 3;
                }
                catch(NoSuchFieldError nosuchfielderror2) { }
                $SwitchMap$com$android$internal$telephony$Call$State = new int[com.android.internal.telephony.Call.State.values().length];
                try
                {
                    $SwitchMap$com$android$internal$telephony$Call$State[com.android.internal.telephony.Call.State.WAITING.ordinal()] = 1;
                }
                catch(NoSuchFieldError nosuchfielderror3) { }
                try
                {
                    $SwitchMap$com$android$internal$telephony$Call$State[com.android.internal.telephony.Call.State.INCOMING.ordinal()] = 2;
                }
                catch(NoSuchFieldError nosuchfielderror4)
                {
                    return;
                }
            }
        }

        switch(_cls2..SwitchMap.com.movial.ipphone.SessionCall.State[sessioncall.state.ordinal()])
        {
        default:
            return -1;

        case 1: // '\001'
        case 2: // '\002'
            return 1;

        case 3: // '\003'
            return 0;
        }
    }

    private void loge(String s)
    {
        IPPhone _tmp = phone;
        Log.e("IPCallTracker", s);
    }

    private boolean removeConnection(int i, com.android.internal.telephony.Connection.DisconnectCause disconnectcause)
    {
        IPConnection ipconnection;
        com.android.internal.telephony.Connection.DisconnectCause disconnectcause1;
        ipconnection = (IPConnection)connections.get(i);
        disconnectcause1 = disconnectcause;
        if(!ipconnection.isIncoming() || ipconnection.getConnectTime() != 0L) goto _L2; else goto _L1
_L1:
        boolean flag;
        if(ipconnection.cause == com.android.internal.telephony.Connection.DisconnectCause.LOCAL)
            disconnectcause1 = com.android.internal.telephony.Connection.DisconnectCause.INCOMING_REJECTED;
        else
            disconnectcause1 = com.android.internal.telephony.Connection.DisconnectCause.INCOMING_MISSED;
_L4:
        log((new StringBuilder()).append("removeConnection. cause: ").append(disconnectcause1).toString());
        flag = ipconnection.onDisconnect(disconnectcause1);
        connections.remove(i);
        return flag;
_L2:
        if(ipconnection.cause == com.android.internal.telephony.Connection.DisconnectCause.LOCAL)
            disconnectcause1 = com.android.internal.telephony.Connection.DisconnectCause.LOCAL;
        if(true) goto _L4; else goto _L3
_L3:
    }

    private boolean swapCallUiUpdate(SessionCall sessioncall)
    {
        boolean flag;
        flag = false;
        if(sessioncall.state == SessionCall.State.DISCONNECTED)
            swapCall = -1;
        if(swapCall <= 0) goto _L2; else goto _L1
_L1:
        if(!sessioncall.inConf) goto _L4; else goto _L3
_L3:
        swapCall = -1 + swapCall;
_L6:
        if(swapCall == 0)
        {
            swapCall = -1;
            flag = true;
            phone.notifyPreciseCallStateChanged();
        }
        Log.d("IPCallTracker", (new StringBuilder()).append("swapCallUiUpdate. ").append(flag).append(", swapCall: ").append(swapCall).toString());
        return flag;
_L4:
        int j = 0;
        do
        {
            int k = connections.size();
            flag = false;
            if(j >= k)
                continue; /* Loop/switch isn't completed */
            IPConnection ipconnection = (IPConnection)connections.get(j);
            if(ipconnection.index == ((DriverCall) (sessioncall)).index && ipconnection.getState() != ipconnection.parseSessionState(sessioncall.state))
                swapCall = -1 + swapCall;
            j++;
        } while(true);
_L2:
        int i = swapCall;
        flag = false;
        if(i == -1)
            flag = true;
        if(true) goto _L6; else goto _L5
_L5:
    }

    private void updateConfConnections(SessionCall sessioncall, boolean flag)
    {
        Log.d("IPCallTracker", (new StringBuilder()).append("updateConfConnections. ").append(connections.size()).toString());
        boolean flag1 = false;
        int i = -1 + connections.size();
        SessionCall.State state1;
        int j;
        if(sessioncall.state == SessionCall.State.UNKNOWN)
            state1 = SessionCall.State.ACTIVE;
        else
            state1 = sessioncall.state;
        sessioncall.state = state1;
        j = i;
        while(j > -1) 
        {
            IPConnection ipconnection = (IPConnection)connections.get(j);
            if(ipconnection.inConf)
                if(sessioncall.state == SessionCall.State.DISCONNECTED)
                {
                    if(!flag1)
                        flag1 = removeConnection(j, sessioncall.cause);
                } else
                if(ipconnection.update(sessioncall.state))
                    if(flag)
                        flag1 = true;
                    else
                        flag1 = false;
            j--;
        }
        updatePhoneState();
        if(flag1)
            phone.notifyPreciseCallStateChanged();
    }

    private void updateConnection(Message message)
    {
        SessionCall sessioncall;
        int i;
        Bundle bundle = message.getData();
        bundle.setClassLoader(com/movial/ipphone/IPCallTracker.getClassLoader());
        sessioncall = (SessionCall)bundle.getParcelable("call");
        i = findConnection(sessioncall);
        if(message.what != 1) goto _L2; else goto _L1
_L1:
        Log.d("IPCallTracker", (new StringBuilder()).append("SessionCall.MSG_CALL_RINGING. ").append(ringingIndex).append(", ").append(i).toString());
        if(i != -1 && ((IPConnection)connections.get(i)).getState() == com.android.internal.telephony.Call.State.INCOMING && ringingIndex == -1)
        {
            ringingIndex = i;
            mHandler.sendEmptyMessage(42);
        }
_L6:
        return;
_L2:
        boolean flag;
        boolean flag1;
        boolean flag2;
        if(message.what == 5)
        {
            Log.i("IPCallTracker", "updateConnection: update CNAM");
            ((IPConnection)connections.get(i)).cnapName = ((DriverCall) (sessioncall)).name;
            flag = true;
        } else
        {
            int j = ringingIndex;
            flag = false;
            if(j == i)
            {
                SessionCall.State state1 = sessioncall.state;
                SessionCall.State state2 = SessionCall.State.ALERTING;
                flag = false;
                if(state1 != state2)
                {
                    Log.d("IPCallTracker", "removeMessages");
                    mHandler.removeMessages(42);
                    ringingIndex = -1;
                    flag = false;
                }
            }
        }
        if(message.what == 6)
        {
            IPPhone ipphone = phone;
            boolean flag5;
            if(message.arg1 == 1)
                flag5 = true;
            else
                flag5 = false;
            ipphone.notifyRingbackTone(flag5);
        }
        dialAfterHolding(sessioncall);
        flag1 = acceptAfterHolding(sessioncall);
        flag2 = swapCallUiUpdate(sessioncall);
        if(i != -1)
            break MISSING_BLOCK_LABEL_462;
        isNewCall(sessioncall);
        JVM INSTR tableswitch 0 1: default 256
    //                   0 343
    //                   1 422;
           goto _L3 _L4 _L5
_L3:
        if(sessioncall.inConf)
        {
            updateConfConnections(sessioncall, flag2);
            return;
        }
        break; /* Loop/switch isn't completed */
_L4:
        IPConnection ipconnection1 = new IPConnection(phone.getContext(), this, sessioncall);
        connections.add(ipconnection1);
        ipconnection1.connectTime = System.currentTimeMillis();
        ipconnection1.connectTimeReal = SystemClock.elapsedRealtime();
        ipconnection1.duration = 0L;
        phone.notifyUnknownConnection(ipconnection1);
_L7:
        boolean flag4 = true;
_L8:
        updatePhoneState();
        if(flag4)
        {
            phone.notifyPreciseCallStateChanged();
            return;
        }
          goto _L6
_L5:
        IPConnection ipconnection = new IPConnection(phone.getContext(), this, sessioncall);
        connections.add(ipconnection);
        phone.notifyNewRingingConnection(ipconnection);
          goto _L7
        if(sessioncall.state == SessionCall.State.DISCONNECTED)
            flag4 = removeConnection(i, sessioncall.cause);
        else
        if(flag)
        {
            flag4 = true;
        } else
        {
            boolean flag3 = ((IPConnection)connections.get(i)).update(sessioncall.state);
            flag4 = false;
            if(flag3)
                if(flag2 && !flag1)
                    flag4 = true;
                else
                    flag4 = false;
        }
          goto _L8
    }

    private void updatePhoneState()
    {
        com.android.internal.telephony.Phone.State state1;
        state1 = state;
        if(ringingCall.isRinging())
            state = com.android.internal.telephony.Phone.State.RINGING;
        else
        if(!foregroundCall.isIdle() || !backgroundCall.isIdle())
        {
            state = com.android.internal.telephony.Phone.State.OFFHOOK;
        } else
        {
            state = com.android.internal.telephony.Phone.State.IDLE;
            swapCall = -1;
        }
        if(state != com.android.internal.telephony.Phone.State.IDLE || state1 == state) goto _L2; else goto _L1
_L1:
        voiceCallEndedRegistrants.notifyRegistrants(new AsyncResult(null, null, null));
_L4:
        log((new StringBuilder()).append("updatePhoneState. original: ").append(state1).append(", current: ").append(state).toString());
        if(state != state1)
            phone.notifyPhoneStateChanged();
        return;
_L2:
        if(state1 == com.android.internal.telephony.Phone.State.IDLE && state1 != state)
            voiceCallStartedRegistrants.notifyRegistrants(new AsyncResult(null, null, null));
        if(true) goto _L4; else goto _L3
_L3:
    }

    boolean acceptAfterHolding(SessionCall sessioncall)
    {
        if(sessioncall.state != SessionCall.State.HOLDING || ringingCall.getState() != com.android.internal.telephony.Call.State.WAITING)
            return false;
        try
        {
            mIPService.accept();
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("acceptCall FAILED: ").append(exception.toString()).toString());
            return false;
        }
        return true;
    }

    void acceptCall()
        throws CallStateException
    {
        log((new StringBuilder()).append("acceptCall. ringingcall state: ").append(ringingCall.getState()).toString());
        if(foregroundCall.getState() != com.android.internal.telephony.Call.State.DISCONNECTING) goto _L2; else goto _L1
_L1:
        return;
_L2:
        _cls2..SwitchMap.com.android.internal.telephony.Call.State[ringingCall.getState().ordinal()];
        JVM INSTR tableswitch 1 2: default 80
    //                   1 91
    //                   2 100;
           goto _L3 _L4 _L5
_L3:
        throw new CallStateException("phone not ringing");
_L4:
        if(switchWaitingOrHoldingAndActive(true) != -1) goto _L1; else goto _L5
_L5:
        setMute(false);
        try
        {
            mIPService.accept();
            return;
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("acceptCall FAILED: ").append(exception.toString()).toString());
        }
        return;
    }

    boolean canConference()
    {
        return foregroundCall.getState() == com.android.internal.telephony.Call.State.ACTIVE && backgroundCall.getState() == com.android.internal.telephony.Call.State.HOLDING && !backgroundCall.isFull() && !foregroundCall.isFull() && !pendingConf;
    }

    public void clearDisconnected()
    {
        log("clearDisconnected");
        ringingCall.clearDisconnected();
        foregroundCall.clearDisconnected();
        backgroundCall.clearDisconnected();
        updatePhoneState();
        phone.notifyPreciseCallStateChanged();
        if(connections.size() == 0)
        {
            clear();
            try
            {
                mIPService.clearDisconnected();
            }
            catch(Exception exception)
            {
                loge((new StringBuilder()).append("IPService clear Disconnection FAILED. ").append(exception.toString()).toString());
            }
        }
        if(connections.size() > 0 && !mWifiLock.isHeld())
        {
            mWifiLock.acquire();
            log("acquire wifilock");
            return;
        }
        if(connections.size() == 0 && mWifiLock.isHeld())
        {
            mWifiLock.release();
            log("release wifilock");
            return;
        } else
        {
            log((new StringBuilder()).append("is lock held: ").append(mWifiLock.isHeld()).toString());
            return;
        }
    }

    void conference()
    {
        if(pendingConf)
            return;
        Log.d("IPCallTracker", "conference");
        pendingConf = true;
        phone.notifyPreciseCallStateChanged();
        try
        {
            mIPService.conference();
            return;
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("conference FAILED: ").append(exception.toString()).toString());
        }
    }

    Connection dial(String s)
        throws CallStateException
    {
        return dial(s, PreferenceManager.getDefaultSharedPreferences(phone.getContext()).getInt("clir_key", 0));
    }

    Connection dial(String s, int i)
        throws CallStateException
    {
        clearDisconnected();
        if(!canDial())
            throw new CallStateException("cannot dial in current state");
        boolean flag;
        IPConnection ipconnection;
        if(foregroundCall.getState() == com.android.internal.telephony.Call.State.ACTIVE)
            flag = true;
        else
            flag = false;
        if(flag)
        {
            switchWaitingOrHoldingAndActive();
            fakeHoldForegroundBeforeDial();
        }
        ipconnection = new IPConnection(phone.getContext(), s, this, foregroundCall);
        connections.add(ipconnection);
        setMute(false);
        if(!flag)
            dial(ipconnection, i);
        else
            pendingMOConn.set(-1 + connections.size(), i);
        updatePhoneState();
        phone.notifyPreciseCallStateChanged();
        return ipconnection;
    }

    void dial(IPConnection ipconnection, int i)
    {
        int j = -1;
        if(!PhoneNumberUtils.isEmergencyNumber(ipconnection.getDialString())) goto _L2; else goto _L1
_L1:
        int l = mIPService.dialEmergencyCall(ipconnection.getAddress());
        j = l;
_L3:
        Exception exception;
        int k;
        if(j == -1)
        {
            pendingMOIndex = -1 + connections.size();
            Message message = mHandler.obtainMessage(41, 1, pendingMOIndex);
            mHandler.sendMessageDelayed(message, DELAYED_REMOVE_CONNECTION);
        } else
        {
            ipconnection.index = j;
        }
        updatePhoneState();
        phone.notifyPreciseCallStateChanged();
        return;
_L2:
        k = mIPService.dial(ipconnection.getAddress(), i);
        j = k;
          goto _L3
        exception;
        loge(exception.toString());
          goto _L3
    }

    void dialAfterHolding(SessionCall sessioncall)
    {
        log((new StringBuilder()).append("dialAfterHolding. isnull: ").append(pendingMOConn.isNull()).append(", state: ").append(sessioncall.state).toString());
        if(!pendingMOConn.isNull()) goto _L2; else goto _L1
_L1:
        return;
_L2:
        if(sessioncall.state == SessionCall.State.ACTIVE)
        {
            Message message = mHandler.obtainMessage(41, 2, pendingMOConn.connIndex);
            mHandler.sendMessage(message);
            return;
        }
        if(sessioncall.state != SessionCall.State.HOLDING) goto _L1; else goto _L3
_L3:
        int i = 0;
_L5:
        com.android.internal.telephony.Call.State state1;
        com.android.internal.telephony.Call.State state2;
        try
        {
            if(i < connections.size())
            {
                if(((IPConnection)connections.get(i)).index != ((DriverCall) (sessioncall)).index)
                    break MISSING_BLOCK_LABEL_265;
                Log.d("IPCallTracker", (new StringBuilder()).append("matched index: ").append(((DriverCall) (sessioncall)).index).toString());
            }
            if(sessioncall.inConf || i >= connections.size())
                break; /* Loop/switch isn't completed */
            state1 = ((IPConnection)connections.get(i)).getState();
            state2 = com.android.internal.telephony.Call.State.HOLDING;
        }
        catch(Exception exception)
        {
            return;
        }
        if(state1 != state2) goto _L1; else goto _L4
_L4:
        clearDisconnected();
        IPConnection ipconnection = (IPConnection)connections.get(-1 + connections.size());
        ipconnection.toString();
        dial(ipconnection, pendingMOConn.clirMode);
        pendingMOConn.clear();
        return;
        i++;
          goto _L5
    }

    boolean getMute()
    {
        return desiredMute;
    }

    public void handleMessage(Message message)
    {
    }

    protected void handlePollCalls(AsyncResult asyncresult)
    {
    }

    void hangup(IPCall ipcall)
        throws CallStateException
    {
        log((new StringBuilder()).append("hangup call ").append(ipcall).toString());
        if(ipcall.getConnections().size() == 0)
            throw new CallStateException("no connections in call");
        if(pendingConf)
            throw new CallStateException("cannot hangup: conference call is establishing");
        if(ipcall == ringingCall)
        {
            log("(ringing) hangup ringing or waiting call");
            reject(ipcall);
        } else
        if(ipcall == foregroundCall)
        {
            if(ipcall.isDialingOrAlerting())
            {
                log("(foregnd) hangup dialing or alerting...");
                hangup((IPConnection)(IPConnection)ipcall.getConnections().get(0));
            } else
            {
                hangupForegroundResumeBackground();
            }
        } else
        if(ipcall == backgroundCall)
        {
            if(ringingCall.isRinging())
            {
                log("(backgnd) hangup all rining call");
                reject(ringingCall);
            } else
            {
                log("(backgnd) hangup all conns in background call");
                hangupAll(ipcall);
            }
        } else
        {
            throw new RuntimeException((new StringBuilder()).append("IPCall ").append(ipcall).append("does not belong to IPCallTracker ").append(this).toString());
        }
        ipcall.onHangupLocal();
        phone.notifyPreciseCallStateChanged();
    }

    void hangup(IPConnection ipconnection)
        throws CallStateException
    {
        if(ipconnection.owner != this)
            throw new CallStateException((new StringBuilder()).append("IPConnection ").append(ipconnection).append("does not belong to IPCallTracker ").append(this).toString());
        if(pendingMOConn.isNull() || ipconnection != connections.get(-1 + connections.size())) goto _L2; else goto _L1
_L1:
        removeConnection(-1 + connections.size(), com.android.internal.telephony.Connection.DisconnectCause.ERROR_UNSPECIFIED);
        updatePhoneState();
        pendingMOConn.clear();
_L4:
        ipconnection.onHangupLocal();
        return;
_L2:
        Exception exception;
        if(ipconnection.getState().isRinging())
        {
            mIPService.reject(ipconnection.getIndex());
            continue; /* Loop/switch isn't completed */
        }
        try
        {
            mIPService.hangup(ipconnection.getIndex());
        }
        // Misplaced declaration of an exception variable
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("hangup connection: ").append(ipconnection.getIndex()).append(" FAILED. ").append(exception.toString()).toString());
            clearConnections();
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    void hangupAll(IPCall ipcall)
        throws CallStateException
    {
        int i = 0;
        while(i < ipcall.connections.size()) 
        {
            try
            {
                hangup((IPConnection)ipcall.connections.get(i));
            }
            catch(Exception exception)
            {
                loge((new StringBuilder()).append("hangupAll FAILED. ").append(exception.toString()).toString());
            }
            i++;
        }
    }

    void hangupForegroundResumeBackground()
    {
        try
        {
            mIPService.hangupForegroundResumeBackground();
            return;
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("hangupForegroundResumeBackground FAILED. ").append(exception.toString()).toString());
        }
        clearConnections();
    }

    protected void log(String s)
    {
        IPPhone _tmp = phone;
        Log.d("IPCallTracker", s);
    }

    public void registerForVoiceCallEnded(Handler handler, int i, Object obj)
    {
    }

    public void registerForVoiceCallStarted(Handler handler, int i, Object obj)
    {
    }

    public void registerHandler()
    {
        log("registerHandler");
        try
        {
            mIPService = phone.getService();
            mIPService.registerForCallStates(mMsnger);
            clear();
            return;
        }
        catch(Exception exception)
        {
            loge(exception.toString());
        }
    }

    void reject(IPCall ipcall)
    {
        if(ipcall != ringingCall)
            loge("no ringing or waiting call to reject");
        int i = 0;
        while(i < ipcall.connections.size()) 
        {
            IPConnection ipconnection = (IPConnection)ipcall.connections.get(i);
            try
            {
                mIPService.reject(ipconnection.getIndex());
            }
            catch(Exception exception)
            {
                loge((new StringBuilder()).append("reject ringing or waiting call FAILED. ").append(exception.toString()).toString());
            }
            i++;
        }
    }

    void rejectCall()
        throws CallStateException
    {
        log("reject call");
        try
        {
            mIPService.reject(-1);
            return;
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("reject call FAILED. ").append(exception.toString()).toString());
        }
    }

    void sendDtmf(char c)
    {
        if(state == com.android.internal.telephony.Phone.State.OFFHOOK && foregroundCall.getState() == com.android.internal.telephony.Call.State.ACTIVE)
        {
            int i = ((IPConnection)foregroundCall.getLatestConnection()).getIndex();
            log((new StringBuilder()).append("sendDtmf. ").append(i).append(", ").append(c).toString());
            sendDtmf(i, c);
        }
    }

    void sendDtmf(int i, char c)
    {
        if(i == -1)
            return;
        try
        {
            mIPService.sendDtmf(i, c);
            return;
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("sendDtmf FAILED. ").append(exception.toString()).toString());
        }
    }

    void setMute(boolean flag)
    {
        desiredMute = flag;
        try
        {
            mIPService.setMute(flag);
            return;
        }
        catch(Exception exception)
        {
            loge(exception.toString());
        }
    }

    void startDtmf(char c)
    {
        if(state == com.android.internal.telephony.Phone.State.OFFHOOK && foregroundCall.getState() == com.android.internal.telephony.Call.State.ACTIVE)
        {
            int i = ((IPConnection)foregroundCall.getLatestConnection()).getIndex();
            log((new StringBuilder()).append("startDtmf. ").append(i).append(", ").append(c).toString());
            startDtmf(i, c);
        }
    }

    void startDtmf(int i, char c)
    {
        if(i == -1)
            return;
        try
        {
            mIPService.startDtmf(i, c);
            return;
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("stratDtmf FAILED. ").append(exception.toString()).toString());
        }
    }

    void stopDtmf()
    {
        if(state == com.android.internal.telephony.Phone.State.OFFHOOK && foregroundCall.getState() == com.android.internal.telephony.Call.State.ACTIVE)
        {
            int i = ((IPConnection)foregroundCall.getLatestConnection()).getIndex();
            log((new StringBuilder()).append("stopDtmf. ").append(i).toString());
            stopDtmf(i);
        }
    }

    void stopDtmf(int i)
    {
        if(i == -1)
            return;
        try
        {
            mIPService.stopDtmf(i);
            return;
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("stopDtmf FAILED. ").append(exception.toString()).toString());
        }
    }

    int switchWaitingOrHoldingAndActive(boolean flag)
        throws CallStateException
    {
        log((new StringBuilder()).append("switchWaitingOrHoldingAndActive: ").append(flag).append(", swapCall: ").append(swapCall).toString());
        if(ringingCall.getState() == com.android.internal.telephony.Call.State.INCOMING)
            throw new CallStateException("cannot be in the incoming state");
        if(pendingConf)
            throw new CallStateException("conference call is establishing");
        if(swapCall != -1)
            return -1;
        int i;
        int j;
        try
        {
            i = mIPService.hold(flag);
        }
        catch(Exception exception)
        {
            loge((new StringBuilder()).append("HoldCall FAILED. ").append(exception.toString()).toString());
            return -1;
        }
        if(flag)
            j = -1;
        else
            j = i;
        swapCall = j;
        return i;
    }

    void switchWaitingOrHoldingAndActive()
        throws CallStateException
    {
        switchWaitingOrHoldingAndActive(false);
    }

    public void unregisterForVoiceCallEnded(Handler handler)
    {
    }

    public void unregisterForVoiceCallStarted(Handler handler)
    {
    }

    private static final int EVENT_KEEP_RINGING = 42;
    private static final int EVENT_REMOVE_CONNECTION = 41;
    static final int MAX_CONNECTIONS = 7;
    static final int MAX_CONNECTIONS_PER_CALL = 5;
    static final String TAG = "IPCallTracker";
    private int DELAYED_KEEP_RINGING;
    private int DELAYED_REMOVE_CONNECTION;
    private final int PENDING_MO_BY_DIAL_FAIL = 1;
    private final int PENDING_MO_BY_HOLD_FAIL = 2;
    IPCall backgroundCall;
    ArrayList connections;
    boolean desiredMute;
    IPCall foregroundCall;
    Handler mHandler;
    IIPService mIPService;
    Messenger mMsnger;
    android.net.wifi.WifiManager.WifiLock mWifiLock;
    boolean pendingConf;
    pendingConnection pendingMOConn;
    int pendingMOIndex;
    IPPhone phone;
    IPCall ringingCall;
    int ringingIndex;
    com.android.internal.telephony.Phone.State state;
    int swapCall;
    RegistrantList voiceCallEndedRegistrants;
    RegistrantList voiceCallStartedRegistrants;





}
