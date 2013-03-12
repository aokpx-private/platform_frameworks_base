// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.Context;
import android.os.*;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import com.android.internal.telephony.*;

// Referenced classes of package com.movial.ipphone:
//            IPCallTracker, SessionCall, IPCall, IPPhone

public class IPConnection extends Connection
{
    class MyHandler extends Handler
    {

        public void handleMessage(Message message)
        {
            switch(message.what)
            {
            default:
                return;

            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
                processNextPostDialChar();
                return;

            case 4: // '\004'
                releaseWakeLock();
                break;
            }
        }

        final IPConnection this$0;

        MyHandler(Looper looper)
        {
            this$0 = IPConnection.this;
            super(looper);
        }
    }


    IPConnection(Context context, IPCallTracker ipcalltracker, SessionCall sessioncall)
    {
        TAG = "IPConnection";
        DBG = true;
        cause = com.android.internal.telephony.Connection.DisconnectCause.NOT_DISCONNECTED;
        postDialState = com.android.internal.telephony.Connection.PostDialState.NOT_STARTED;
        numberPresentation = Connection.PRESENTATION_ALLOWED;
        cnapNamePresentation = Connection.PRESENTATION_ALLOWED;
        Log.d("IPConnection", (new StringBuilder()).append("MT IPconnection. ").append(((DriverCall) (sessioncall)).index).toString());
        createWakeLock(context);
        acquireWakeLock();
        owner = ipcalltracker;
        h = new MyHandler(owner.getLooper());
        address = ((DriverCall) (sessioncall)).number;
        cnapName = ((DriverCall) (sessioncall)).name;
        isIncoming = true;
        disconnected = false;
        createTime = System.currentTimeMillis();
        index = ((DriverCall) (sessioncall)).index;
        inConf = false;
        numberPresentation = ((DriverCall) (sessioncall)).numberPresentation;
        parent = parentFromSessionState(sessioncall.state);
        parent.attach(this, parseSessionState(sessioncall.state));
    }

    IPConnection(Context context, String s, IPCallTracker ipcalltracker, IPCall ipcall)
    {
        TAG = "IPConnection";
        DBG = true;
        cause = com.android.internal.telephony.Connection.DisconnectCause.NOT_DISCONNECTED;
        postDialState = com.android.internal.telephony.Connection.PostDialState.NOT_STARTED;
        numberPresentation = Connection.PRESENTATION_ALLOWED;
        cnapNamePresentation = Connection.PRESENTATION_ALLOWED;
        createWakeLock(context);
        acquireWakeLock();
        owner = ipcalltracker;
        h = new MyHandler(owner.getLooper());
        dialString = s;
        address = PhoneNumberUtils.extractNetworkPortion(s);
        postDialString = PhoneNumberUtils.extractPostDialPortion(s);
        isIncoming = false;
        disconnected = false;
        createTime = System.currentTimeMillis();
        index = -1;
        inConf = false;
        parent = ipcall;
        ipcall.attach(this, com.android.internal.telephony.Call.State.DIALING);
    }

    private void acquireWakeLock()
    {
        log("acquireWakeLock");
        mPartialWakeLock.acquire();
    }

    private void createWakeLock(Context context)
    {
        mPartialWakeLock = ((PowerManager)context.getSystemService("power")).newWakeLock(1, "IPConnection");
    }

    private boolean isConnectingInOrOut()
    {
        return parent == null || parent == owner.ringingCall || parent.state == com.android.internal.telephony.Call.State.DIALING || parent.state == com.android.internal.telephony.Call.State.ALERTING;
    }

    private void log(String s)
    {
        Log.d("IPConnection", s);
    }

    private void onConnectedInOrOut()
    {
        connectTime = System.currentTimeMillis();
        connectTimeReal = SystemClock.elapsedRealtime();
        duration = 0L;
        log((new StringBuilder()).append("onConnectedInOrOut: connectTime=").append(connectTime).toString());
        if(!isIncoming)
            processNextPostDialChar();
        releaseWakeLock();
    }

    private void onStartedHolding()
    {
        holdingStartTime = SystemClock.elapsedRealtime();
    }

    private IPCall parentFromSessionState(SessionCall.State state)
    {
        static class _cls1
        {

            static final int $SwitchMap$com$movial$ipphone$SessionCall$State[];

            static 
            {
                $SwitchMap$com$movial$ipphone$SessionCall$State = new int[SessionCall.State.values().length];
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.ACTIVE.ordinal()] = 1;
                }
                catch(NoSuchFieldError nosuchfielderror) { }
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.DIALING.ordinal()] = 2;
                }
                catch(NoSuchFieldError nosuchfielderror1) { }
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.ALERTING.ordinal()] = 3;
                }
                catch(NoSuchFieldError nosuchfielderror2) { }
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.HOLDING.ordinal()] = 4;
                }
                catch(NoSuchFieldError nosuchfielderror3) { }
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.INCOMING.ordinal()] = 5;
                }
                catch(NoSuchFieldError nosuchfielderror4) { }
                try
                {
                    $SwitchMap$com$movial$ipphone$SessionCall$State[SessionCall.State.WAITING.ordinal()] = 6;
                }
                catch(NoSuchFieldError nosuchfielderror5)
                {
                    return;
                }
            }
        }

        switch(_cls1..SwitchMap.com.movial.ipphone.SessionCall.State[state.ordinal()])
        {
        default:
            throw new RuntimeException((new StringBuilder()).append("illegal call state: ").append(state).toString());

        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
            return owner.foregroundCall;

        case 4: // '\004'
            return owner.backgroundCall;

        case 5: // '\005'
        case 6: // '\006'
            return owner.ringingCall;
        }
    }

    private void processNextPostDialChar()
    {
        if(postDialState != com.android.internal.telephony.Connection.PostDialState.CANCELLED) goto _L2; else goto _L1
_L1:
        log("processNextPostDialChar: postDialState == CANCELLED");
_L4:
        return;
_L2:
        int i;
        if(postDialString != null && postDialString.length() > nextPostDialChar)
            break; /* Loop/switch isn't completed */
        setPostDialState(com.android.internal.telephony.Connection.PostDialState.COMPLETE);
        i = 0;
_L5:
        Registrant registrant = owner.phone.mPostDialHandler;
        if(registrant != null)
        {
            Message message = registrant.messageForRegistrant();
            if(message != null)
            {
                com.android.internal.telephony.Connection.PostDialState postdialstate = postDialState;
                AsyncResult asyncresult = AsyncResult.forMessage(message);
                asyncresult.result = this;
                asyncresult.userObj = postdialstate;
                message.arg1 = i;
                message.sendToTarget();
                return;
            }
        }
        if(true) goto _L4; else goto _L3
_L3:
        setPostDialState(com.android.internal.telephony.Connection.PostDialState.STARTED);
        String s = postDialString;
        int j = nextPostDialChar;
        nextPostDialChar = j + 1;
        i = s.charAt(j);
        if(!processPostDialChar(i))
        {
            h.obtainMessage(3).sendToTarget();
            Log.e("IPConnection", (new StringBuilder()).append("processNextPostDialChar: c=").append(i).append(" isn't valid!").toString());
            return;
        }
          goto _L5
        if(true) goto _L4; else goto _L6
_L6:
    }

    private boolean processPostDialChar(char c)
    {
        if(PhoneNumberUtils.is12Key(c))
        {
            owner.sendDtmf(index, c);
            h.sendEmptyMessageDelayed(1, 400L);
            return true;
        }
        if(c == ',')
            if(nextPostDialChar == 1)
            {
                h.sendMessageDelayed(h.obtainMessage(2), 100L);
                return true;
            } else
            {
                h.sendMessageDelayed(h.obtainMessage(2), 3000L);
                return true;
            }
        if(c == ';')
        {
            setPostDialState(com.android.internal.telephony.Connection.PostDialState.WAIT);
            return true;
        }
        if(c == 'N')
        {
            setPostDialState(com.android.internal.telephony.Connection.PostDialState.WILD);
            return true;
        } else
        {
            return false;
        }
    }

    private void releaseWakeLock()
    {
        synchronized(mPartialWakeLock)
        {
            if(mPartialWakeLock.isHeld())
            {
                log("releaseWakeLock");
                mPartialWakeLock.release();
            }
        }
        return;
        exception;
        wakelock;
        JVM INSTR monitorexit ;
        throw exception;
    }

    private void setPostDialState(com.android.internal.telephony.Connection.PostDialState postdialstate)
    {
        if(postDialState == com.android.internal.telephony.Connection.PostDialState.STARTED || postdialstate != com.android.internal.telephony.Connection.PostDialState.STARTED) goto _L2; else goto _L1
_L1:
        acquireWakeLock();
        Message message = h.obtainMessage(4);
        h.sendMessageDelayed(message, 60000L);
_L4:
        postDialState = postdialstate;
        return;
_L2:
        if(postDialState == com.android.internal.telephony.Connection.PostDialState.STARTED && postdialstate != com.android.internal.telephony.Connection.PostDialState.STARTED)
        {
            h.removeMessages(4);
            releaseWakeLock();
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    public void cancelPostDial()
    {
        setPostDialState(com.android.internal.telephony.Connection.PostDialState.CANCELLED);
    }

    void fakeHoldBeforeDial()
    {
        if(parent != null)
            parent.detach(this);
        parent = owner.backgroundCall;
        parent.attach(this, com.android.internal.telephony.Call.State.HOLDING);
        onStartedHolding();
    }

    protected void finalize()
    {
        if(mPartialWakeLock.isHeld())
            Log.e("IPConnection", "[IPConn] UNEXPECTED; mPartialWakeLock is held when finalizing.");
        releaseWakeLock();
    }

    public String getAddress()
    {
        return address;
    }

    public Call getCall()
    {
        return parent;
    }

    public int getCallFailCause()
    {
        return 0;
    }

    public String getCnapName()
    {
        return cnapName;
    }

    public int getCnapNamePresentation()
    {
        return cnapNamePresentation;
    }

    public long getConnectTime()
    {
        return connectTime;
    }

    public long getCreateTime()
    {
        return createTime;
    }

    public String getDialString()
    {
        return dialString;
    }

    public com.android.internal.telephony.Connection.DisconnectCause getDisconnectCause()
    {
        return cause;
    }

    public long getDisconnectTime()
    {
        return disconnectTime;
    }

    public long getDurationMillis()
    {
        if(connectTimeReal == 0L)
            return 0L;
        if(duration == 0L)
            return SystemClock.elapsedRealtime() - connectTimeReal;
        else
            return duration;
    }

    public long getHoldDurationMillis()
    {
        if(getState() != com.android.internal.telephony.Call.State.HOLDING)
            return 0L;
        else
            return SystemClock.elapsedRealtime() - holdingStartTime;
    }

    public int getIndex()
    {
        return index;
    }

    public int getNumberPresentation()
    {
        return numberPresentation;
    }

    public com.android.internal.telephony.Connection.PostDialState getPostDialState()
    {
        return postDialState;
    }

    public String getRemainingPostDialString()
    {
        if(postDialState == com.android.internal.telephony.Connection.PostDialState.CANCELLED || postDialState == com.android.internal.telephony.Connection.PostDialState.COMPLETE || postDialString == null || postDialString.length() <= nextPostDialChar)
            return "";
        else
            return postDialString.substring(nextPostDialChar);
    }

    public com.android.internal.telephony.Call.State getState()
    {
        if(disconnected)
            return com.android.internal.telephony.Call.State.DISCONNECTED;
        else
            return super.getState();
    }

    public UUSInfo getUUSInfo()
    {
        return null;
    }

    public void hangup()
        throws CallStateException
    {
        if(!disconnected)
        {
            owner.hangup(this);
            return;
        } else
        {
            throw new CallStateException("disconnected");
        }
    }

    public boolean isIncoming()
    {
        return isIncoming;
    }

    boolean onDisconnect(com.android.internal.telephony.Connection.DisconnectCause disconnectcause)
    {
        cause = disconnectcause;
        boolean flag = disconnected;
        boolean flag1 = false;
        if(!flag)
        {
            disconnectTime = System.currentTimeMillis();
            duration = SystemClock.elapsedRealtime() - connectTimeReal;
            disconnected = true;
            log((new StringBuilder()).append("onDisconnect: cause=").append(disconnectcause).toString());
            owner.phone.notifyDisconnect(this);
            IPCall ipcall = parent;
            flag1 = false;
            if(ipcall != null)
                flag1 = parent.connectionDisconnected(this);
        }
        releaseWakeLock();
        return flag1;
    }

    void onHangupLocal()
    {
        cause = com.android.internal.telephony.Connection.DisconnectCause.LOCAL;
    }

    com.android.internal.telephony.Call.State parseSessionState(SessionCall.State state)
    {
        switch(_cls1..SwitchMap.com.movial.ipphone.SessionCall.State[state.ordinal()])
        {
        default:
            throw new RuntimeException((new StringBuilder()).append("illegal call state:").append(state).toString());

        case 1: // '\001'
            return com.android.internal.telephony.Call.State.ACTIVE;

        case 4: // '\004'
            return com.android.internal.telephony.Call.State.HOLDING;

        case 2: // '\002'
            return com.android.internal.telephony.Call.State.DIALING;

        case 3: // '\003'
            return com.android.internal.telephony.Call.State.ALERTING;

        case 5: // '\005'
            return com.android.internal.telephony.Call.State.INCOMING;

        case 6: // '\006'
            return com.android.internal.telephony.Call.State.WAITING;
        }
    }

    public void proceedAfterWaitChar()
    {
        if(postDialState != com.android.internal.telephony.Connection.PostDialState.WAIT)
        {
            log((new StringBuilder()).append("IPConnection.proceedAfterWaitChar(): Expected getPostDialState() to be WAIT but was ").append(postDialState).toString());
            return;
        } else
        {
            setPostDialState(com.android.internal.telephony.Connection.PostDialState.STARTED);
            processNextPostDialChar();
            return;
        }
    }

    public void proceedAfterWildChar(String s)
    {
        if(postDialState != com.android.internal.telephony.Connection.PostDialState.WILD)
        {
            log((new StringBuilder()).append("IPConnection.proceedAfterWaitChar(): Expected getPostDialState() to be WILD but was ").append(postDialState).toString());
            return;
        } else
        {
            setPostDialState(com.android.internal.telephony.Connection.PostDialState.STARTED);
            StringBuilder stringbuilder = new StringBuilder(s);
            stringbuilder.append(postDialString.substring(nextPostDialChar));
            postDialString = stringbuilder.toString();
            nextPostDialChar = 0;
            Log.d("IPConnection", (new StringBuilder()).append("proceedAfterWildChar: new postDialString is ").append(postDialString).toString());
            processNextPostDialChar();
            return;
        }
    }

    public void separate()
        throws CallStateException
    {
    }

    public void setIndex(int i)
    {
        index = i;
    }

    public boolean update(SessionCall.State state)
    {
        boolean flag = true;
        if(getState() == com.android.internal.telephony.Call.State.DISCONNECTING && state != SessionCall.State.DISCONNECTED)
        {
            try
            {
                hangup();
            }
            catch(Exception exception)
            {
                log((new StringBuilder()).append("update HANGUP FAILED. ").append(exception.toString()).toString());
                return false;
            }
            return false;
        }
        boolean flag1 = isConnectingInOrOut();
        boolean flag2;
        IPCall ipcall;
        boolean flag4;
        StringBuilder stringbuilder;
        if(getState() == com.android.internal.telephony.Call.State.HOLDING)
            flag2 = flag;
        else
            flag2 = false;
        ipcall = parentFromSessionState(state);
        if(ipcall != parent)
        {
            if(parent != null)
                parent.detach(this);
            ipcall.attach(this, parseSessionState(state));
            parent = ipcall;
            flag4 = true;
        } else
        {
            boolean flag3 = parent.update(this, parseSessionState(state));
            if(false || flag3)
                flag4 = flag;
            else
                flag4 = false;
        }
        stringbuilder = (new StringBuilder()).append("update: parent=").append(parent).append(", hasNewParent=");
        if(ipcall == parent)
            flag = false;
        log(stringbuilder.append(flag).append(", wasConnectingInOrOut=").append(flag1).append(", wasHolding=").append(flag2).append(", isConnectingInOrOut=").append(isConnectingInOrOut()).append(", changed=").append(flag4).toString());
        if(flag1 && !isConnectingInOrOut())
            onConnectedInOrOut();
        if(flag4 && !flag2 && getState() == com.android.internal.telephony.Call.State.HOLDING)
            onStartedHolding();
        return flag4;
    }

    boolean updateParent()
    {
        return parent.update(this, com.android.internal.telephony.Call.State.DISCONNECTED);
    }

    static final int DTMF_DELAY_MELLIS = 400;
    static final int EVENT_DTMF_DONE = 1;
    static final int EVENT_NEXT_POST_DIAL = 3;
    static final int EVENT_PAUSE_DONE = 2;
    static final int EVENT_WAKE_LOCK_TIMEOUT = 4;
    static final int PAUSE_DELAY_FIRST_MILLIS = 100;
    static final int PAUSE_DELAY_MILLIS = 3000;
    static final int WAKE_LOCK_TIMEOUT_MILLIS = 60000;
    private final boolean DBG;
    private final String TAG;
    String address;
    com.android.internal.telephony.Connection.DisconnectCause cause;
    String cnapName;
    int cnapNamePresentation;
    long connectTime;
    long connectTimeReal;
    long createTime;
    String dialString;
    long disconnectTime;
    boolean disconnected;
    long duration;
    Handler h;
    long holdingStartTime;
    boolean inConf;
    int index;
    boolean isIncoming;
    private android.os.PowerManager.WakeLock mPartialWakeLock;
    int nextPostDialChar;
    int numberPresentation;
    IPCallTracker owner;
    IPCall parent;
    com.android.internal.telephony.Connection.PostDialState postDialState;
    String postDialString;


}
