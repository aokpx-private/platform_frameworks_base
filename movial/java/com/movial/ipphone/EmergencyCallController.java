// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.*;
import android.net.Uri;
import android.os.*;
import android.telephony.*;
import android.util.Log;
import com.android.internal.telephony.*;

// Referenced classes of package com.movial.ipphone:
//            IPPhoneSettings, IPPhoneProxy

public class EmergencyCallController
{

    public EmergencyCallController(IPPhoneProxy ipphoneproxy, Phone phone)
    {
        mEmergencyTimeoutCanceled = false;
        mSuccessfulGsmEmergency = false;
        mRetryEmergencyIMSRegistration = false;
        cachedEmergencyNumber = "911";
        mEmergencyState = IPUtils.EmergencyState.NOT_INITIALIZED;
        mEmergencyCallPref = 1;
        mPhoneStateListener = new PhoneStateListener() {

            public void onCallStateChanged(int i, String s)
            {
                Log.i("EmergencyCallController", (new StringBuilder()).append("onCallStateChanged ").append(i).append(" mEmergencyState ").append(mEmergencyState).append(" force ").append(mIPPhoneProxy.getForceEmergencyMode()).toString());
                if (mEmergencyState != IPUtils.EmergencyState.CS_CALL_DIALING || i != 2) {
                    if (mEmergencyState == IPUtils.EmergencyState.CS_CALL_CONNECTED && i == 0) {
                        mIPPhoneProxy.setForceEmergencyMode(false);
                        Log.i("EmergencyCallController", (new StringBuilder()).append("mEmergencyState = ").append(mEmergencyState).toString());
                        return;
                    }
                } else {
                mEmergencyState = IPUtils.EmergencyState.CS_CALL_CONNECTED;
                }
            }
_L4:
                Log.i("EmergencyCallController", (new StringBuilder()).append("mEmergencyState = ").append(mEmergencyState).toString());
                return;
_L2:
                if(mEmergencyState == IPUtils.EmergencyState.CS_CALL_CONNECTED && i == 0)
                    mIPPhoneProxy.setForceEmergencyMode(false);
                if(true) goto _L4; else if goto _L3
_L3:
            }

            final EmergencyCallController this$0;

            
            {
                this$0 = EmergencyCallController.this;
                super();
            }
        };
        mHandler = new Handler() {

            public void handleMessage(Message message)
            {
                message.what;
                JVM INSTR tableswitch 2 5: default 36
            //                           2 65
            //                           3 391
            //                           4 482
            //                           5 817;
                   goto _L1 _L2 _L3 _L4 _L5
_L1:
                Log.e("EmergencyCallController", (new StringBuilder()).append("No such event defined: ").append(message.what).toString());
_L7:
                return;
_L2:
                Connection connection;
                com.android.internal.telephony.Connection.DisconnectCause disconnectcause;
                Log.i("EmergencyCallController", "recv EVENT_CALL_DISCONNECT");
                connection = (Connection)((AsyncResult)message.obj).result;
                disconnectcause = connection.getDisconnectCause();
                if((!PhoneNumberUtils.isEmergencyNumber(connection.getAddress()) || mEmergencyState == IPUtils.EmergencyState.NOT_INITIALIZED || disconnectcause != com.android.internal.telephony.Connection.DisconnectCause.OUT_OF_SERVICE && disconnectcause != com.android.internal.telephony.Connection.DisconnectCause.ERROR_UNSPECIFIED) && (!mEmergencyTimeoutCanceled || disconnectcause != com.android.internal.telephony.Connection.DisconnectCause.LOCAL))
                    continue; /* Loop/switch isn't completed */
                Log.i("EmergencyCallController", (new StringBuilder()).append("GSM E911 Failed cause = ").append(disconnectcause).toString());
                if (mEmergencyState != IPUtils.EmergencyState.CS_CALL_CONNECTED || mEmergencyCallPref != 0) {
                return; 
                } else if goto _L6
_L6:
                Log.i("EmergencyCallController", "Trigger CS->IMS fallback");
                mEmergencyState = IPUtils.EmergencyState.CS_CALL_FAILED;
                mIPPhoneProxy.setForceEmergencyMode(false);
                mIPPhoneProxy.setRadioPower(false);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL_PRIVILEGED");
                intent.addFlags(0x10000000);
                intent.setData(Uri.parse((new StringBuilder()).append("tel:").append(cachedEmergencyNumber).toString()));
                mContext.startActivity(intent);
                return;
                if(!PhoneNumberUtils.isEmergencyNumber(connection.getAddress()) || mEmergencyState == IPUtils.EmergencyState.NOT_INITIALIZED || mEmergencyTimeoutCanceled || disconnectcause != com.android.internal.telephony.Connection.DisconnectCause.LOCAL) goto _L7; else if goto _L8
_L8:
                mEmergencyState = IPUtils.EmergencyState.IDLE;
                mIPPhoneProxy.setForceEmergencyMode(false);
                mIPPhoneProxy.setRadioPower(false);
                return;
_L3:
                Log.i("EmergencyCallController", "EVENT_CS_EMERGENCY_TIMEOUT");
                if(mPhone.getForegroundCall().getState() != com.android.internal.telephony.Call.State.DIALING) goto _L7; else if goto _L9
_L9:
                Log.i("EmergencyCallController", "hangup gsm emergency call...");
                mEmergencyTimeoutCanceled = true;
                try
                {
                    mPhone.getForegroundCall().hangup();
                    return;
                }
                catch(CallStateException callstateexception)
                {
                    Log.i("EmergencyCallController", (new StringBuilder()).append("hangup call failed: ").append(callstateexception).toString());
                }
                return;
_L4:
label0:
                {
                    com.android.internal.telephony.Call.State state = mPhone.getForegroundCall().getState();
                    boolean flag = mIPPhoneProxy.getForceEmergencyMode();
                    Log.i("EmergencyCallController", (new StringBuilder()).append("EVENT_PHONE_STATE_CHANGED: ").append(state).toString());
                    com.android.internal.telephony.Call.State state1 = com.android.internal.telephony.Call.State.IDLE;
                    boolean flag1 = false;
                    if(state == state1)
                        break label0;
                    boolean flag2;
                    boolean flag3;
                    if(!IPPhoneSettings.getBoolean(mContext.getContentResolver(), "CELL_ONLY", true))
                        flag2 = true;
                    else if
                        flag2 = false;
                    flag3 = PhoneNumberUtils.isEmergencyNumber(mPhone.getForegroundCall().getLatestConnection().getAddress());
                    flag1 = false;
                    if(flag)
                        break label0;
                    flag1 = false;
                    if(!flag3)
                        break label0;
                    if(!flag2)
                    {
                        com.android.internal.telephony.Call.State state2 = com.android.internal.telephony.Call.State.DISCONNECTING;
                        flag1 = false;
                        if(state != state2)
                            break label0;
                    }
                    flag1 = true;
                }
                if((flag || flag1) && state == com.android.internal.telephony.Call.State.ACTIVE)
                {
                    Log.i("EmergencyCallController", "disabling wifi call...");
                    IPPhoneSettings.putBoolean(mContext.getContentResolver(), "CELL_ONLY", true);
                    IPPhoneSettings.putBoolean(mContext.getContentResolver(), "ECM", true);
                    mSuccessfulGsmEmergency = true;
                    mIPPhoneProxy.setForceEmergencyMode(false);
                }
                if((!flag && !flag1 || !mSuccessfulGsmEmergency || state != com.android.internal.telephony.Call.State.DISCONNECTING) && (!mRetryEmergencyIMSRegistration || state != com.android.internal.telephony.Call.State.DISCONNECTING)) goto _L7; else if goto _L10
_L10:
                Log.i("EmergencyCallController", "start EMERGENCY_DELAYED_IMS_REGISTRATION timer: 180000");
                mRetryEmergencyIMSRegistration = false;
                removeMessages(5);
                mHandler.sendMessageDelayed(Message.obtain(mHandler, 5), 0x2bf20L);
                return;
_L5:
                Log.i("EmergencyCallController", "Receive EVENT_DELAYED_IMS_REGISTRATION");
                if(mPhone.getState() == com.android.internal.telephony.Phone.State.IDLE)
                {
                    IPPhoneSettings.putBoolean(mContext.getContentResolver(), "ECM", false);
                    IPPhoneSettings.putBoolean(mContext.getContentResolver(), "CELL_ONLY", false);
                    mSuccessfulGsmEmergency = false;
                    return;
                } else if
                {
                    Log.i("EmergencyCallController", "Phone not idle, delay the time to resume IMS");
                    mRetryEmergencyIMSRegistration = true;
                    return;
                }
            }

            final EmergencyCallController this$0;

            
            {
                this$0 = EmergencyCallController.this;
                super();
            }
        };
        mReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent)
            {
                String s;
                Log.i("EmergencyCallController", (new StringBuilder()).append("receive intent ").append(intent.getAction()).toString());
                s = intent.getAction();
                if(!s.equals("com.movial.ims_emergency_fail")) goto _L2; else if goto _L1
_L1:
                boolean flag = intent.getBooleanExtra("canceled_by_user", false);
                if(mEmergencyCallPref != 1 || flag) goto _L4; else if goto _L3
_L3:
                mEmergencyState = IPUtils.EmergencyState.IMS_CALL_FAILED;
                mIPPhoneProxy.setForceEmergencyMode(true);
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.CALL_EMERGENCY");
                intent1.addFlags(0x10000000);
                intent1.setData(Uri.parse((new StringBuilder()).append("tel:").append(cachedEmergencyNumber).toString()));
                mContext.startActivity(intent1);
_L6:
                return;
_L4:
                mEmergencyState = IPUtils.EmergencyState.IDLE;
                return;
_L2:
                if(s.equals("com.movial.ims_emergency_start"))
                {
                    mEmergencyState = IPUtils.EmergencyState.IDLE;
                    return;
                }
                if(true) goto _L6; else if goto _L5
_L5:
            }

            final EmergencyCallController this$0;

            
            {
                this$0 = EmergencyCallController.this;
                super();
            }
        };
        mIPPhoneProxy = ipphoneproxy;
        mPhone = phone;
        mContext = phone.getContext();
        if (IPPhoneSettings.getBoolean(mContext.getContentResolver(), "ECM", false))
        {
            IPPhoneSettings.putBoolean(mContext.getContentResolver(), "CELL_ONLY", false);
            IPPhoneSettings.putBoolean(mContext.getContentResolver(), "ECM", false);
        }
        mPhone.registerForDisconnect(mHandler, 2, null);
        mPhone.registerForPreciseCallStateChanged(mHandler, 4, null);
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.movial.ims_emergency_fail");
        intentfilter.addAction("com.movial.ims_emergency_start");
        mContext.registerReceiver(mReceiver, intentfilter);
        ((TelephonyManager)mContext.getSystemService("phone")).listen(mPhoneStateListener, 32);
    }

    protected int getEmergencyPreference()
    {
        return mEmergencyCallPref;
    }

    protected IPUtils.EmergencyState getEmergencyState()
    {
        return mEmergencyState;
    }

    protected void notifyForceEmergencyModeChanged(boolean flag)
    {
        Log.i("EmergencyCallController", "notifyForceEmergencyModeChanged");
        if (!flag)
        {
            if (!mSuccessfulGsmEmergency) {
                IPPhoneSettings.putBoolean(mContext.getContentResolver(), "CELL_ONLY", false);
            }
            if (mEmergencyCallPref == 1) {
                mEmergencyState = IPUtils.EmergencyState.IDLE;
            }
        }
    }

    protected void setEmergencyPreference(int i)
    {
        mEmergencyCallPref = i;
    }

    protected void setEmergencyState(IPUtils.EmergencyState emergencystate)
    {
        mEmergencyState = emergencystate;
    }

    protected void transitToDialingState(String s)
    {
        cachedEmergencyNumber = s;
        if (mEmergencyState == IPUtils.EmergencyState.CS_TURNING_ON_RADIO && mEmergencyCallPref == 0)
        {
            mEmergencyTimeoutCanceled = false;
            int i = SystemProperties.getInt("gsm.ecc.timeout", 5000);
            Log.i("EmergencyCallController", (new StringBuilder()).append("Emergency timeout after ").append(i).append(" msecs").toString());
            mHandler.sendMessageDelayed(Message.obtain(mHandler, 3), i);
        }
        IPUtils.EmergencyState emergencystate;
        if (mEmergencyState == IPUtils.EmergencyState.CS_TURNING_ON_RADIO) {
            emergencystate = IPUtils.EmergencyState.CS_CALL_DIALING;
        } else {
            emergencystate = IPUtils.EmergencyState.IMS_CALL_DIALING;
        }
        mEmergencyState = emergencystate;
    }

    private static final int EMERGENCY_DELAYED_IMS_REGISTRATION = 0x2bf20;
    private static final int EVENT_CALLSTATE_CHANGED = 1;
    private static final int EVENT_CALL_DISCONNECT = 2;
    private static final int EVENT_CS_EMERGENCY_TIMEOUT = 3;
    private static final int EVENT_DELAYED_IMS_REGISTRATION = 5;
    private static final int EVENT_PHONE_STATE_CHANGED = 4;
    private static final String TAG = "EmergencyCallController";
    private String cachedEmergencyNumber;
    private Context mContext;
    private int mEmergencyCallPref;
    private IPUtils.EmergencyState mEmergencyState;
    private boolean mEmergencyTimeoutCanceled;
    private Handler mHandler;
    private IPPhoneProxy mIPPhoneProxy;
    private Phone mPhone;
    private PhoneStateListener mPhoneStateListener;
    private BroadcastReceiver mReceiver;
    private boolean mRetryEmergencyIMSRegistration;
    private boolean mSuccessfulGsmEmergency;



/*
    static IPUtils.EmergencyState access$002(EmergencyCallController emergencycallcontroller, IPUtils.EmergencyState emergencystate)
    {
        emergencycallcontroller.mEmergencyState = emergencystate;
        return emergencystate;
    }

*/




/*
    static boolean access$202(EmergencyCallController emergencycallcontroller, boolean flag)
    {
        emergencycallcontroller.mEmergencyTimeoutCanceled = flag;
        return flag;
    }

*/







/*
    static boolean access$702(EmergencyCallController emergencycallcontroller, boolean flag)
    {
        emergencycallcontroller.mSuccessfulGsmEmergency = flag;
        return flag;
    }

*/



/*
    static boolean access$802(EmergencyCallController emergencycallcontroller, boolean flag)
    {
        emergencycallcontroller.mRetryEmergencyIMSRegistration = flag;
        return flag;
    }

*/

}
