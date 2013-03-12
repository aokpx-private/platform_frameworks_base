// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.app.ActivityManagerNative;
import android.content.*;
import android.database.ContentObserver;
import android.net.*;
import android.os.*;
import android.telephony.*;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.*;
import com.android.internal.telephony.cat.CatService;
import com.android.internal.telephony.gsm.GSMPhone;
import com.android.internal.telephony.test.SimulatedRadioControl;
import com.android.internal.telephony.uicc.*;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.movial.ipphone:
//            IPPhone, IPPhoneSettings, EmergencyCallController, IPSMSDispatcher, 
//            IIPRegistry, IPStateListener, IIPService

public class IPPhoneProxy extends Handler
    implements Phone
{

    public IPPhoneProxy(Context context, CommandsInterface commandsinterface, PhoneNotifier phonenotifier)
    {
        mResetModemOnRadioTechnologyChange = false;
        forceEmergency = false;
        mReceiver = new BroadcastReceiver() {

            public void onReceive(Context context1, Intent intent)
            {
                Log.i("IPPhoneProxy", (new StringBuilder()).append("receive intent ").append(intent.getAction()).toString());
                String s = intent.getAction();
                if(s.equals("com.movial.terminate_stack"))
                {
                    unbindService(intent.getBooleanExtra("restart_service", false));
                } else
                {
                    if("android.provider.Telephony.SPN_STRINGS_UPDATED".equals(s))
                    {
                        updateNetworkName(intent);
                        return;
                    }
                    if("com.movial.gba_initialized".equals(s))
                    {
                        Log.i("IPPhoneProxy", "GBA init intent received");
                        IPPhoneSettings.putBoolean(getContext().getContentResolver(), "GBA_INIT", true);
                        Intent intent1 = new Intent("com.movial.reg_check");
                        getContext().sendBroadcast(intent1);
                        return;
                    }
                    if("com.movial.reread_isim_records".equals(s))
                    {
                        IPPhoneSettings.putBoolean(getContext().getContentResolver(), "GBA_INIT", false);
                        if(getIsimRecords() != null)
                        {
                            Log.d("IPPhoneProxy", "IPUtils.ACTION_REREAD_ISIM : reReadISimRecords: ");
                            ((IsimUiccRecords)getIsimRecords()).reReadIsimRecords();
                            return;
                        } else
                        {
                            Log.d("IPPhoneProxy", "IPUtils.ACTION_REREAD_ISIM : ISIM not found");
                            return;
                        }
                    }
                }
            }

            final IPPhoneProxy this$0;

            
            {
                this$0 = IPPhoneProxy.this;
                super();
            }
        };
        mObserver = new ContentObserver(new Handler()) {

            public void onChange(boolean flag)
            {
                Log.d("IPPhoneProxy", "ContentObserver onChange");
                if(!IPPhoneSettings.getBoolean(getContext().getContentResolver(), "CELL_ONLY", true))
                    bindService();
            }

            final IPPhoneProxy this$0;

            
            {
                this$0 = IPPhoneProxy.this;
                super(handler);
            }
        };
        mIPStateListener = new IPStateListener() {

            public void onRegisteredStateChanged(boolean flag, int i)
            {
                int j;
                boolean flag1;
                j = 1;
                flag1 = false;
                if(!flag)
                    break MISSING_BLOCK_LABEL_87;
                flag1 = false;
                if(i != j)
                    break MISSING_BLOCK_LABEL_87;
                flag1 = true;
                if(mIPPhone.getService().registerForEmergencyCallPref(new Messenger(IPPhoneProxy.this)))
                    j = 0;
                try
                {
                    Log.i("IPPhoneProxy", (new StringBuilder()).append("set mEmergencyCallPref to ").append(j).toString());
                    mEmergencyCallController.setEmergencyPreference(j);
                }
                catch(Exception exception)
                {
                    Log.e("IPPhoneProxy", (new StringBuilder()).append("register EmergencyCallPref fail").append(exception).toString());
                }
                setIPSMSDispatcher(flag);
                if(IPPhoneProxy.mRegistered != flag1)
                    setCall(flag1);
                IPPhoneProxy.mRegistered = flag1;
                if(!IPPhoneProxy.mRegistered)
                    broadcastOperatorName();
                if(IPPhoneProxy.mRegistered)
                    setEmergencyState(IPUtils.EmergencyState.IDLE);
                else
                    setEmergencyState(IPUtils.EmergencyState.NOT_INITIALIZED);
                IPPhoneProxy.logd((new StringBuilder()).append("onRegisteredStateChanged. mRegistered: ").append(IPPhoneProxy.mRegistered).toString());
                return;
            }

            final IPPhoneProxy this$0;

            
            {
                this$0 = IPPhoneProxy.this;
                super();
            }
        };
        mConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName componentname, IBinder ibinder)
            {
                Log.d("IPPhoneProxy", "IPPolicy ServiceConnected");
                mIPRegistry = IIPRegistry.Stub.asInterface(ibinder);
                binded.set(true);
                try
                {
                    cyclicBarrier.await(10000L, TimeUnit.MILLISECONDS);
                }
                catch(Exception exception)
                {
                    Log.e("IPPhoneProxy", exception.toString());
                }
                mIPPhone.bindService();
                try
                {
                    mIPRegistry.listen(mIPStateListener.listener, 1);
                    return;
                }
                catch(Exception exception1)
                {
                    Log.e("IPPhoneProxy", (new StringBuilder()).append("register to LISTEN_EVENT_REG_STATUS FAILED. ").append(exception1.toString()).toString());
                }
            }

            public void onServiceDisconnected(ComponentName componentname)
            {
                Log.d("IPPhoneProxy", "IPPolicy ServiceDisconnected");
                try
                {
                    mIPRegistry.listen(mIPStateListener.listener, 0);
                }
                catch(Exception exception)
                {
                    Log.e("IPPhoneProxy", (new StringBuilder()).append("register to LISTEN_EVENT_NONE FAILED. ").append(exception.toString()).toString());
                }
                unbindService(false);
                if(!IPPhoneSettings.getBoolean(getContext().getContentResolver(), "CELL_ONLY", true))
                    bindService();
            }

            final IPPhoneProxy this$0;

            
            {
                this$0 = IPPhoneProxy.this;
                super();
            }
        };
        logd("IPPhoneProxy");
        mActivePhone = new GSMPhone(context, commandsinterface, phonenotifier);
        mIPPhone = new IPPhone(mActivePhone, commandsinterface, phonenotifier);
        setCall(mRegistered);
        if(!IPPhoneSettings.getBoolean(getContext().getContentResolver(), "CELL_ONLY", true))
            bindService();
        getContext().getContentResolver().registerContentObserver(Uri.withAppendedPath(IPPhoneSettings.CONTENT_URI, "CELL_ONLY"), false, mObserver);
        IPPhoneSettings.putBoolean(getContext().getContentResolver(), "GBA_INIT", false);
        mEmergencyCallController = new EmergencyCallController(this, mActivePhone);
        mIccSmsInterfaceManager = new IccSmsInterfaceManager((PhoneBase)mActivePhone);
        mIPSMSDispatcher = new IPSMSDispatcher(mIPPhone, mIPPhone.mSmsStorageMonitor, mIPPhone.mSmsUsageMonitor);
        mIccPhoneBookInterfaceManagerProxy = new IccPhoneBookInterfaceManagerProxy(mActivePhone.getIccPhoneBookInterfaceManager());
        mPhoneSubInfoProxy = new PhoneSubInfoProxy(mActivePhone.getPhoneSubInfo());
        mCommandsInterface = ((PhoneBase)mActivePhone).mCM;
        mIccCardProxy = new IccCardProxy(getContext(), mCommandsInterface);
        ((GSMPhone)mActivePhone).setIccCardProxy(mIccCardProxy);
        mCommandsInterface.registerForRilConnected(this, 4, null);
        mCommandsInterface.registerForOn(this, 2, null);
        mCommandsInterface.registerForVoiceRadioTechChanged(this, 1, null);
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.movial.gba_initialized");
        intentfilter.addAction("com.movial.terminate_stack");
        intentfilter.addAction("com.movial.reread_isim_records");
        intentfilter.addAction("android.provider.Telephony.SPN_STRINGS_UPDATED");
        getContext().registerReceiver(mReceiver, intentfilter);
    }

    private void bindService()
    {
        if(IPPhoneSettings.getBoolean(getContext().getContentResolver(), "CELL_ONLY", true))
        {
            return;
        } else
        {
            (new Thread() {

                public void run()
                {
                    bindToIPRegistry();
                }

                final IPPhoneProxy this$0;

            
            {
                this$0 = IPPhoneProxy.this;
                super();
            }
            }).start();
            return;
        }
    }

    private void bindToIPRegistry()
    {
        AtomicBoolean atomicboolean = binded;
        atomicboolean;
        JVM INSTR monitorenter ;
        if(binded.get())
            break MISSING_BLOCK_LABEL_81;
        Log.d("IPPhoneProxy", "bindToIPRegistry");
        cyclicBarrier.reset();
        Intent intent = new Intent(com/movial/ipphone/IIPRegistry.getName());
        intent.setClassName("com.movial.ipservice", "com.movial.ipservice.IPService");
        if(getContext().bindService(intent, mConnection, 1))
            waitConnectionResponse();
_L1:
        return;
        Exception exception1;
        exception1;
        Log.e("IPPhoneProxy", (new StringBuilder()).append("bindService FAILED. ").append(exception1.toString()).toString());
          goto _L1
        Exception exception;
        exception;
        atomicboolean;
        JVM INSTR monitorexit ;
        throw exception;
    }

    private void broadcastOperatorName()
    {
    }

    private void deleteAndCreatePhone(int i)
    {
        String s = "Unknown";
        Phone phone = mActivePhone;
        if(phone != null)
            s = ((PhoneBase)phone).getPhoneName();
        StringBuilder stringbuilder = (new StringBuilder()).append("Switching Voice Phone : ").append(s).append(" >>> ");
        String s1;
        if(ServiceState.isGsm(i))
            s1 = "GSM";
        else
            s1 = "CDMA";
        logd(stringbuilder.append(s1).toString());
        if(phone != null)
        {
            CallManager.getInstance().unregisterPhone(phone);
            logd("Disposing old phone..");
            phone.dispose();
        }
        if(!ServiceState.isCdma(i)) goto _L2; else goto _L1
_L1:
        mActivePhone = PhoneFactory.getCdmaPhone();
_L4:
        if(phone != null)
            phone.removeReferences();
        if(mActivePhone != null)
            CallManager.getInstance().registerPhone(mActivePhone);
        return;
_L2:
        if(ServiceState.isGsm(i))
            mActivePhone = PhoneFactory.getGsmPhone();
        if(true) goto _L4; else goto _L3
_L3:
    }

    public static boolean getRegister()
    {
        return mRegistered;
    }

    private static void logd(String s)
    {
        Log.d("IPPhoneProxy", (new StringBuilder()).append("[PhoneProxy] ").append(s).toString());
    }

    private void loge(String s)
    {
        Log.e("IPPhoneProxy", (new StringBuilder()).append("[PhoneProxy] ").append(s).toString());
    }

    private void logw(String s)
    {
        Log.w("IPPhoneProxy", (new StringBuilder()).append("[PhoneProxy] ").append(s).toString());
    }

    private void setCall(boolean flag)
    {
        Call _tmp = mForegroundCall;
        Call _tmp1 = mBackgroundCall;
        Call _tmp2 = mRingingCall;
        if(flag)
        {
            mForegroundCall = mIPPhone.getForegroundCall();
            mBackgroundCall = mIPPhone.getBackgroundCall();
            mRingingCall = mIPPhone.getRingingCall();
        } else
        {
            mForegroundCall = mActivePhone.getForegroundCall();
            mBackgroundCall = mActivePhone.getBackgroundCall();
            mRingingCall = mActivePhone.getRingingCall();
        }
    }

    private void setIPSMSDispatcher(boolean flag)
    {
        Log.i("IPPhoneProxy", (new StringBuilder()).append("setIPSMSDispatcher ").append(flag).toString());
        if(flag)
        {
            mIccSmsInterfaceManager.setIPSMSDispatcher(mIPSMSDispatcher);
            return;
        } else
        {
            mIccSmsInterfaceManager.setIPSMSDispatcher(null);
            return;
        }
    }

    private void unbindService(boolean flag)
    {
        Log.d("IPPhoneProxy", (new StringBuilder()).append("unbindService. ").append(binded.get()).toString());
        if(binded.get())
            getContext().unbindService(mConnection);
        mIPRegistry = null;
        binded.set(false);
        mRegistered = false;
        setCall(false);
        mIPPhone.unbindService();
        if(flag)
            sendEmptyMessageDelayed(5, 5000L);
    }

    private void updateNetworkName(Intent intent)
    {
        boolean flag = intent.getBooleanExtra("showSpn", false);
        String s = intent.getStringExtra("spn");
        boolean flag1 = intent.getBooleanExtra("showPlmn", false);
        String s1 = intent.getStringExtra("plmn");
        if(!intent.getBooleanExtra("IMS_WIFICALL", false))
        {
            StringBuilder stringbuilder = new StringBuilder();
            String s2;
            StringBuilder stringbuilder1;
            String s3;
            StringBuilder stringbuilder2;
            String s4;
            StringBuilder stringbuilder3;
            String s5;
            String s6;
            StringBuilder stringbuilder4;
            StringBuilder stringbuilder5;
            StringBuilder stringbuilder6;
            StringBuilder stringbuilder7;
            if(flag)
                s2 = "1";
            else
                s2 = "0";
            stringbuilder1 = stringbuilder.append(s2).append(",");
            if(TextUtils.isEmpty(s))
                s3 = "null";
            else
                s3 = s;
            stringbuilder2 = stringbuilder1.append(s3).append(",");
            if(flag1)
                s4 = "1";
            else
                s4 = "0";
            stringbuilder3 = stringbuilder2.append(s4).append(",");
            if(TextUtils.isEmpty(s1))
                s5 = "null";
            else
                s5 = s1;
            mRealName = stringbuilder3.append(s5).toString();
        }
        s6 = "";
        if(!TextUtils.isEmpty(s1) || !TextUtils.isEmpty(s))
        {
            stringbuilder4 = new StringBuilder();
            String s7;
            String s8;
            if(flag)
                s7 = "1";
            else
                s7 = "0";
            stringbuilder5 = stringbuilder4.append(s7).append(",");
            if(TextUtils.isEmpty(s))
                s = "null";
            stringbuilder6 = stringbuilder5.append(s).append(",");
            if(flag1)
                s8 = "1";
            else
                s8 = "0";
            stringbuilder7 = stringbuilder6.append(s8).append(",");
            if(TextUtils.isEmpty(s1))
                s1 = "null";
            s6 = stringbuilder7.append(s1).toString();
        }
        if(!TextUtils.isEmpty(s6))
        {
            if(!s6.trim().equals(mOperatorName))
                mOperatorName = s6;
        } else
        if(!intent.getBooleanExtra("IMS_WIFICALL", false))
        {
            sendEmptyMessageDelayed(6, 1000L);
            return;
        }
    }

    private void updatePhoneObject(int i)
    {
        if(mActivePhone != null)
            if(mRilVersion == 6 && getLteOnCdmaMode() == 1)
            {
                if(mActivePhone.getPhoneType() == 2)
                {
                    logd((new StringBuilder()).append("LTE ON CDMA property is set. Use CDMA Phone newVoiceRadioTech = ").append(i).append(" Active Phone = ").append(mActivePhone.getPhoneName()).toString());
                    return;
                }
                logd((new StringBuilder()).append("LTE ON CDMA property is set. Switch to CDMALTEPhone newVoiceRadioTech = ").append(i).append(" Active Phone = ").append(mActivePhone.getPhoneName()).toString());
                i = 6;
            } else
            if(ServiceState.isCdma(i) && mActivePhone.getPhoneType() == 2 || ServiceState.isGsm(i) && mActivePhone.getPhoneType() == 1)
            {
                logd((new StringBuilder()).append("Ignoring voice radio technology changed message. newVoiceRadioTech = ").append(i).append(" Active Phone = ").append(mActivePhone.getPhoneName()).toString());
                return;
            }
        if(i == 0)
        {
            logd((new StringBuilder()).append("Ignoring voice radio technology changed message. newVoiceRadioTech = Unknown. Active Phone = ").append(mActivePhone.getPhoneName()).toString());
            return;
        }
        boolean flag = mResetModemOnRadioTechnologyChange;
        boolean flag1 = false;
        if(flag)
        {
            boolean flag2 = mCommandsInterface.getRadioState().isOn();
            flag1 = false;
            if(flag2)
            {
                flag1 = true;
                logd("Setting Radio Power to Off");
                mCommandsInterface.setRadioPower(false, null);
            }
        }
        deleteAndCreatePhone(i);
        if(mResetModemOnRadioTechnologyChange && flag1)
        {
            logd("Resetting Radio");
            mCommandsInterface.setRadioPower(flag1, null);
        }
        mIccPhoneBookInterfaceManagerProxy.setmIccPhoneBookInterfaceManager(mActivePhone.getIccPhoneBookInterfaceManager());
        mPhoneSubInfoProxy.setmPhoneSubInfo(mActivePhone.getPhoneSubInfo());
        mCommandsInterface = ((PhoneBase)mActivePhone).mCM;
        Intent intent = new Intent("android.intent.action.RADIO_TECHNOLOGY");
        intent.addFlags(0x20000000);
        intent.putExtra("phoneName", mActivePhone.getPhoneName());
        ActivityManagerNative.broadcastStickyIntent(intent, null);
    }

    private void waitConnectionResponse()
    {
        try
        {
            cyclicBarrier.await(10000L, TimeUnit.MILLISECONDS);
            return;
        }
        catch(Exception exception)
        {
            Log.e("IPPhoneProxy", (new StringBuilder()).append("waitConnectionResponse FAILED. ").append(exception.toString()).toString());
        }
        sendEmptyMessage(5);
    }

    public boolean IsDomesticRoaming()
    {
        return mActivePhone.IsDomesticRoaming();
    }

    public boolean IsInternationalRoaming()
    {
        return mActivePhone.IsInternationalRoaming();
    }

    public void SimSlotActivation(boolean flag)
    {
    }

    public void acceptCall()
        throws CallStateException
    {
        if(mRegistered)
        {
            mIPPhone.acceptCall();
            return;
        } else
        {
            mActivePhone.acceptCall();
            return;
        }
    }

    public void activateCellBroadcastSms(int i, Message message)
    {
        mActivePhone.activateCellBroadcastSms(i, message);
    }

    public Connection addUserToConfCall(String s)
        throws CallStateException
    {
        return null;
    }

    public void akaAuthenticate(byte abyte0[], byte abyte1[], Message message)
    {
        mActivePhone.akaAuthenticate(abyte0, abyte1, message);
    }

    public boolean canConference()
    {
        if(mRegistered)
            return mIPPhone.canConference();
        else
            return mActivePhone.canConference();
    }

    public boolean canTransfer()
    {
        if(mRegistered)
            return mIPPhone.canTransfer();
        else
            return mActivePhone.canTransfer();
    }

    public boolean changeBarringPassword(String s, String s1, String s2, Message message)
    {
        if(mRegistered)
            return mIPPhone.changeBarringPassword(s, s1, s2, message);
        else
            return mActivePhone.changeBarringPassword(s, s1, s2, message);
    }

    public boolean changeBarringPassword(String s, String s1, String s2, String s3, Message message)
    {
        if(mRegistered)
            return mIPPhone.changeBarringPassword(s, s1, s2, s3, message);
        else
            return mActivePhone.changeBarringPassword(s, s1, s2, s3, message);
    }

    public void clearDisconnected()
    {
        mIPPhone.clearDisconnected();
        mActivePhone.clearDisconnected();
    }

    public void conference()
        throws CallStateException
    {
        if(mRegistered)
        {
            mIPPhone.conference();
            return;
        } else
        {
            mActivePhone.conference();
            return;
        }
    }

    public Connection dial(String s)
        throws CallStateException
    {
        return dial(s, null);
    }

    public Connection dial(String s, UUSInfo uusinfo)
        throws CallStateException
    {
        if(PhoneNumberUtils.isEmergencyNumber(s) && (mRegistered || forceEmergency))
            mEmergencyCallController.transitToDialingState(s);
        if(mRegistered)
        {
            Log.i("IPPhoneProxy", "ims call");
            return mIPPhone.dial(s);
        } else
        {
            Log.i("IPPhoneProxy", "gsm call");
            return mActivePhone.dial(s);
        }
    }

    public Connection dialConferenceCall(String s)
        throws CallStateException
    {
        return null;
    }

    public Connection dialVideoCall(String s)
        throws CallStateException
    {
        if(mRegistered)
            return mIPPhone.dialVideoCall(s);
        else
            return mActivePhone.dialVideoCall(s);
    }

    public int disableApnType(String s)
    {
        return mActivePhone.disableApnType(s);
    }

    public boolean disableDataConnectivity()
    {
        return mActivePhone.disableDataConnectivity();
    }

    public void disableDnsCheck(boolean flag)
    {
        mActivePhone.disableDnsCheck(flag);
    }

    public void disableLocationUpdates()
    {
        mActivePhone.disableLocationUpdates();
    }

    public int disableQos(int i)
    {
        return mActivePhone.disableQos(i);
    }

    public void dispose()
    {
        mCommandsInterface.unregisterForOn(this);
        mCommandsInterface.unregisterForVoiceRadioTechChanged(this);
        mCommandsInterface.unregisterForRilConnected(this);
    }

    public int enableApnType(String s)
    {
        return mActivePhone.enableApnType(s);
    }

    public boolean enableDataConnectivity()
    {
        return mActivePhone.enableDataConnectivity();
    }

    public void enableEnhancedVoicePrivacy(boolean flag, Message message)
    {
        mActivePhone.enableEnhancedVoicePrivacy(flag, message);
    }

    public void enableLocationUpdates()
    {
        mActivePhone.enableLocationUpdates();
    }

    public int enableQos(QosSpec qosspec, String s)
    {
        return mActivePhone.enableQos(qosspec, s);
    }

    public void exitEmergencyCallbackMode()
    {
        if(mRegistered)
        {
            mActivePhone.exitEmergencyCallbackMode();
            return;
        } else
        {
            mIPPhone.exitEmergencyCallbackMode();
            return;
        }
    }

    public void explicitCallTransfer()
        throws CallStateException
    {
        if(mRegistered)
        {
            mIPPhone.explicitCallTransfer();
            return;
        } else
        {
            mActivePhone.explicitCallTransfer();
            return;
        }
    }

    public void gbaAuthenticateBootstrap(byte abyte0[], byte abyte1[], Message message)
    {
        mActivePhone.gbaAuthenticateBootstrap(abyte0, abyte1, message);
    }

    public void gbaAuthenticateNaf(byte abyte0[], Message message)
    {
        mActivePhone.gbaAuthenticateNaf(abyte0, message);
    }

    public String getActiveApnHost(String s)
    {
        return mActivePhone.getActiveApnHost(s);
    }

    public String[] getActiveApnTypes()
    {
        return mActivePhone.getActiveApnTypes();
    }

    public Phone getActivePhone()
    {
        return mActivePhone;
    }

    public void getAvailableNetworks(Message message)
    {
        mActivePhone.getAvailableNetworks(message);
    }

    public Call getBackgroundCall()
    {
        return mBackgroundCall;
    }

    public void getCallBarringOption(String s, Message message)
    {
        if(mRegistered)
            mIPPhone.getCallBarringOption(s, message);
        mActivePhone.getCallBarringOption(s, message);
    }

    public boolean getCallForwardingIndicator()
    {
        return mActivePhone.getCallForwardingIndicator();
    }

    public void getCallForwardingOption(int i, Message message)
    {
        if(mRegistered)
        {
            mIPPhone.getCallForwardingOption(i, message);
            return;
        } else
        {
            mActivePhone.getCallForwardingOption(i, message);
            return;
        }
    }

    public void getCallWaiting(Message message)
    {
        if(mRegistered)
        {
            mIPPhone.getCallWaiting(message);
            return;
        } else
        {
            mActivePhone.getCallWaiting(message);
            return;
        }
    }

    public CatService getCatService()
    {
        return null;
    }

    public String getCdmaCurrIdd()
    {
        return null;
    }

    public int getCdmaEriIconIndex()
    {
        return mActivePhone.getCdmaEriIconIndex();
    }

    public int getCdmaEriIconMode()
    {
        return mActivePhone.getCdmaEriIconMode();
    }

    public String getCdmaEriText()
    {
        return mActivePhone.getCdmaEriText();
    }

    public String getCdmaMin()
    {
        return mActivePhone.getCdmaMin();
    }

    public String getCdmaPrlVersion()
    {
        return mActivePhone.getCdmaPrlVersion();
    }

    public void getCellBroadcastSmsConfig(Message message)
    {
        mActivePhone.getCellBroadcastSmsConfig(message);
    }

    public CellLocation getCellLocation()
    {
        return mActivePhone.getCellLocation();
    }

    public Context getContext()
    {
        return mActivePhone.getContext();
    }

    public com.android.internal.telephony.Phone.DataActivityState getDataActivityState()
    {
        return mActivePhone.getDataActivityState();
    }

    public void getDataCallList(Message message)
    {
        mActivePhone.getDataCallList(message);
    }

    public com.android.internal.telephony.Phone.DataState getDataConnectionState()
    {
        return mActivePhone.getDataConnectionState("default");
    }

    public com.android.internal.telephony.Phone.DataState getDataConnectionState(String s)
    {
        return mActivePhone.getDataConnectionState(s);
    }

    public boolean getDataRoamingEnabled()
    {
        return mActivePhone.getDataRoamingEnabled();
    }

    public int getDataServiceState()
    {
        return 0;
    }

    public String getDeviceId()
    {
        return mActivePhone.getDeviceId();
    }

    public String getDeviceSvn()
    {
        return mActivePhone.getDeviceSvn();
    }

    public boolean getEmergencyCallPowerState()
    {
        if(mRegistered)
            return mIPPhone.getDesiredPowerState();
        else
            return true;
    }

    public int getEmergencyPreference()
    {
        return mEmergencyCallController.getEmergencyPreference();
    }

    public IPUtils.EmergencyState getEmergencyState()
    {
        return mEmergencyCallController.getEmergencyState();
    }

    public void getEnhancedVoicePrivacy(Message message)
    {
        mActivePhone.getEnhancedVoicePrivacy(message);
    }

    public String getEsn()
    {
        return mActivePhone.getEsn();
    }

    public boolean getFDNavailable()
    {
        return mActivePhone.getFDNavailable();
    }

    public boolean getForceEmergencyMode()
    {
        return forceEmergency;
    }

    public Call getForegroundCall()
    {
        return mForegroundCall;
    }

    public String getHandsetInfo(String s)
    {
        return mActivePhone.getHandsetInfo(s);
    }

    public IccCard getIccCard()
    {
        return mIccCardProxy;
    }

    public IccPhoneBookInterfaceManager getIccPhoneBookInterfaceManager()
    {
        return mActivePhone.getIccPhoneBookInterfaceManager();
    }

    public boolean getIccRecordsLoaded()
    {
        return mIccCardProxy.getIccRecordsLoaded();
    }

    public String getIccSerialNumber()
    {
        return mActivePhone.getIccSerialNumber();
    }

    public String getImei()
    {
        return mActivePhone.getImei();
    }

    public String getImeiInCDMAGSMPhone()
    {
        return mActivePhone.getImeiInCDMAGSMPhone();
    }

    public Call getImsBackgroundCall()
    {
        return mIPPhone.getBackgroundCall();
    }

    public Call getImsForegroundCall()
    {
        return mIPPhone.getForegroundCall();
    }

    public Call getImsRingingCall()
    {
        return mIPPhone.getRingingCall();
    }

    public IsimRecords getIsimRecords()
    {
        return mActivePhone.getIsimRecords();
    }

    public String getLine1AlphaTag()
    {
        return mActivePhone.getLine1AlphaTag();
    }

    public String getLine1Number()
    {
        return mActivePhone.getLine1Number();
    }

    public LinkCapabilities getLinkCapabilities(String s)
    {
        return mActivePhone.getLinkCapabilities(s);
    }

    public LinkProperties getLinkProperties(String s)
    {
        return mActivePhone.getLinkProperties(s);
    }

    public int getLteOnCdmaMode()
    {
        return mActivePhone.getLteOnCdmaMode();
    }

    public String getMeid()
    {
        return mActivePhone.getMeid();
    }

    public boolean getMessageWaitingIndicator()
    {
        return mActivePhone.getMessageWaitingIndicator();
    }

    public String getMsisdn()
    {
        return mActivePhone.getMsisdn();
    }

    public boolean getMute()
    {
        if(mRegistered)
            return mIPPhone.getMute();
        else
            return mActivePhone.getMute();
    }

    public void getNeighboringCids(Message message)
    {
        mActivePhone.getNeighboringCids(message);
    }

    public void getOutgoingCallerIdDisplay(Message message)
    {
        if(mRegistered)
        {
            mIPPhone.getOutgoingCallerIdDisplay(message);
            return;
        } else
        {
            mActivePhone.getOutgoingCallerIdDisplay(message);
            return;
        }
    }

    public List getPendingMmiCodes()
    {
        if(mRegistered)
            return mIPPhone.getPendingMmiCodes();
        else
            return mActivePhone.getPendingMmiCodes();
    }

    public int getPhoneIndex()
    {
        return 0;
    }

    public String getPhoneName()
    {
        return mActivePhone.getPhoneName();
    }

    public PhoneSubInfo getPhoneSubInfo()
    {
        return mActivePhone.getPhoneSubInfo();
    }

    public int getPhoneType()
    {
        return mActivePhone.getPhoneType();
    }

    public void getPreferredNetworkList(Message message)
    {
    }

    public void getPreferredNetworkType(Message message)
    {
        mActivePhone.getPreferredNetworkType(message);
    }

    public int getQosStatus(int i)
    {
        return mActivePhone.getQosStatus(i);
    }

    public Call getRingingCall()
    {
        return mRingingCall;
    }

    public boolean getSMSPavailable()
    {
        return mActivePhone.getSMSPavailable();
    }

    public boolean getSMSavailable()
    {
        return mActivePhone.getSMSavailable();
    }

    public ServiceState getServiceState()
    {
        if(mRegistered)
            return mIPPhone.getServiceState();
        else
            return mActivePhone.getServiceState();
    }

    public SignalStrength getSignalStrength()
    {
        return mActivePhone.getSignalStrength();
    }

    public SimulatedRadioControl getSimulatedRadioControl()
    {
        return mActivePhone.getSimulatedRadioControl();
    }

    public String getSktImsiM()
    {
        return mActivePhone.getSktImsiM();
    }

    public String getSktIrm()
    {
        return mActivePhone.getSktIrm();
    }

    public void getSmscAddress(Message message)
    {
        mActivePhone.getSmscAddress(message);
    }

    public String[] getSponImsi()
    {
        return null;
    }

    public com.android.internal.telephony.Phone.State getState()
    {
        com.android.internal.telephony.Phone.State state = mActivePhone.getState();
        com.android.internal.telephony.Phone.State state1 = mIPPhone.getState();
        if(state == com.android.internal.telephony.Phone.State.RINGING || state1 == com.android.internal.telephony.Phone.State.RINGING)
            return com.android.internal.telephony.Phone.State.RINGING;
        if(state == com.android.internal.telephony.Phone.State.OFFHOOK || state1 == com.android.internal.telephony.Phone.State.OFFHOOK)
            return com.android.internal.telephony.Phone.State.OFFHOOK;
        else
            return com.android.internal.telephony.Phone.State.IDLE;
    }

    public String getSubscriberId()
    {
        return mActivePhone.getSubscriberId();
    }

    public int getSubscription()
    {
        return mActivePhone.getSubscription();
    }

    public boolean getUnitTestMode()
    {
        return mActivePhone.getUnitTestMode();
    }

    public UsimServiceTable getUsimServiceTable()
    {
        return mActivePhone.getUsimServiceTable();
    }

    public String getVoiceMailAlphaTag()
    {
        return mActivePhone.getVoiceMailAlphaTag();
    }

    public String getVoiceMailNumber()
    {
        return mActivePhone.getVoiceMailNumber();
    }

    public int getVoiceMessageCount()
    {
        return mActivePhone.getVoiceMessageCount();
    }

    public boolean handleInCallMmiCommands(String s)
        throws CallStateException
    {
        return mActivePhone.handleInCallMmiCommands(s);
    }

    public void handleMessage(Message message)
    {
        AsyncResult asyncresult = (AsyncResult)message.obj;
        message.what;
        JVM INSTR tableswitch 1 7: default 56
    //                   1 151
    //                   2 89
    //                   3 151
    //                   4 106
    //                   5 322
    //                   6 338
    //                   7 277;
           goto _L1 _L2 _L3 _L2 _L4 _L5 _L6 _L7
_L1:
        loge((new StringBuilder()).append("Error! This handler was not registered for this message type. Message: ").append(message.what).toString());
_L9:
        super.handleMessage(message);
        return;
_L3:
        mCommandsInterface.getVoiceRadioTechnology(obtainMessage(3));
        continue; /* Loop/switch isn't completed */
_L4:
        if(asyncresult.exception == null && asyncresult.result != null)
        {
            mRilVersion = ((Integer)asyncresult.result).intValue();
        } else
        {
            logd("Unexpected exception on EVENT_RIL_CONNECTED");
            mRilVersion = -1;
        }
        continue; /* Loop/switch isn't completed */
_L2:
        if(asyncresult.exception == null)
        {
            if(asyncresult.result != null && ((int[])(int[])asyncresult.result).length != 0)
                updatePhoneObject(((int[])(int[])asyncresult.result)[0]);
            else
                loge((new StringBuilder()).append("Voice Radio Technology event ").append(message.what).append(" has no tech!").toString());
        } else
        {
            loge((new StringBuilder()).append("Voice Radio Technology event ").append(message.what).append(" exception!").append(asyncresult.exception).toString());
        }
        continue; /* Loop/switch isn't completed */
_L7:
        int i = message.arg1;
        mEmergencyCallController.setEmergencyPreference(i);
        Log.i("IPPhoneProxy", (new StringBuilder()).append("update mEmergencyCallPref to ").append(i).toString());
        continue; /* Loop/switch isn't completed */
_L5:
        Log.i("IPPhoneProxy", "EVENT_BIND_TO_IPSERVICE");
        bindService();
        continue; /* Loop/switch isn't completed */
_L6:
        broadcastOperatorName();
        if(true) goto _L9; else goto _L8
_L8:
    }

    public boolean handlePinMmi(String s)
    {
        return mActivePhone.handlePinMmi(s);
    }

    public void invokeOemRilRequestRaw(byte abyte0[], Message message)
    {
        mActivePhone.invokeOemRilRequestRaw(abyte0, message);
    }

    public void invokeOemRilRequestStrings(String as[], Message message)
    {
        mActivePhone.invokeOemRilRequestStrings(as, message);
    }

    public boolean isCspPlmnEnabled()
    {
        return mActivePhone.isCspPlmnEnabled();
    }

    public boolean isDataConnectivityPossible()
    {
        return mActivePhone.isDataConnectivityPossible("default");
    }

    public boolean isDataConnectivityPossible(String s)
    {
        return mActivePhone.isDataConnectivityPossible(s);
    }

    public boolean isDnsCheckDisabled()
    {
        return mActivePhone.isDnsCheckDisabled();
    }

    public boolean isMMICode(String s)
    {
        return false;
    }

    public boolean isManualNetSelAllowed()
    {
        if(mRegistered)
            return mIPPhone.isManualNetSelAllowed();
        else
            return mActivePhone.isManualNetSelAllowed();
    }

    public boolean isMinInfoReady()
    {
        return mActivePhone.isMinInfoReady();
    }

    public boolean isOtaSpNumber(String s)
    {
        return mActivePhone.isOtaSpNumber(s);
    }

    public int modifyQos(int i, QosSpec qosspec)
    {
        return mActivePhone.modifyQos(i, qosspec);
    }

    public boolean needsOtaServiceProvisioning()
    {
        return mActivePhone.needsOtaServiceProvisioning();
    }

    public void notifyDataActivity()
    {
        mActivePhone.notifyDataActivity();
    }

    public void queryAvailableBandMode(Message message)
    {
        mActivePhone.queryAvailableBandMode(message);
    }

    public void queryCdmaRoamingPreference(Message message)
    {
        mActivePhone.queryCdmaRoamingPreference(message);
    }

    public void queryTTYMode(Message message)
    {
        mActivePhone.queryTTYMode(message);
    }

    public void registerFoT53ClirlInfo(Handler handler, int i, Object obj)
    {
        mActivePhone.registerFoT53ClirlInfo(handler, i, obj);
    }

    public void registerForCallWaiting(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForCallWaiting(handler, i, obj);
    }

    public void registerForCdmaOtaStatusChange(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForCdmaOtaStatusChange(handler, i, obj);
    }

    public void registerForDisconnect(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForDisconnect(handler, i, obj);
        mIPPhone.registerForDisconnect(handler, i, obj);
    }

    public void registerForDisplayInfo(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForDisplayInfo(handler, i, obj);
    }

    public void registerForEcmTimerReset(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForEcmTimerReset(handler, i, obj);
        mIPPhone.registerForEcmTimerReset(handler, i, obj);
    }

    public void registerForInCallVoicePrivacyOff(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForInCallVoicePrivacyOff(handler, i, obj);
    }

    public void registerForInCallVoicePrivacyOn(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForInCallVoicePrivacyOn(handler, i, obj);
    }

    public void registerForIncomingRing(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForIncomingRing(handler, i, obj);
        mIPPhone.registerForIncomingRing(handler, i, obj);
    }

    public void registerForLineControlInfo(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForLineControlInfo(handler, i, obj);
    }

    public void registerForMmiComplete(Handler handler, int i, Object obj)
    {
        mIPPhone.registerForMmiComplete(handler, i, obj);
        mActivePhone.registerForMmiComplete(handler, i, obj);
    }

    public void registerForMmiInitiate(Handler handler, int i, Object obj)
    {
        mIPPhone.registerForMmiInitiate(handler, i, obj);
        mActivePhone.registerForMmiInitiate(handler, i, obj);
    }

    public void registerForNewIMSCall(Handler handler, int i, Object obj)
    {
    }

    public void registerForNewRingingConnection(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForNewRingingConnection(handler, i, obj);
        mIPPhone.registerForNewRingingConnection(handler, i, obj);
    }

    public void registerForNumberInfo(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForNumberInfo(handler, i, obj);
    }

    public void registerForPreciseCallStateChanged(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForPreciseCallStateChanged(handler, i, obj);
        mIPPhone.registerForPreciseCallStateChanged(handler, i, obj);
    }

    public void registerForRedirectedNumberInfo(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForRedirectedNumberInfo(handler, i, obj);
    }

    public void registerForResendIncallMute(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForResendIncallMute(handler, i, obj);
    }

    public void registerForRingbackTone(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForRingbackTone(handler, i, obj);
        mIPPhone.registerForRingbackTone(handler, i, obj);
    }

    public void registerForServiceStateChanged(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForServiceStateChanged(handler, i, obj);
        mIPPhone.registerForServiceStateChanged(handler, i, obj);
    }

    public void registerForSignalInfo(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForSignalInfo(handler, i, obj);
    }

    public void registerForSimRecordsLoaded(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForSimRecordsLoaded(handler, i, obj);
    }

    public void registerForSubscriptionInfoReady(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForSubscriptionInfoReady(handler, i, obj);
    }

    public void registerForSuppServiceFailed(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForSuppServiceFailed(handler, i, obj);
    }

    public void registerForSuppServiceNotification(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForSuppServiceNotification(handler, i, obj);
    }

    public void registerForT53AudioControlInfo(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForT53AudioControlInfo(handler, i, obj);
    }

    public void registerForUnknownConnection(Handler handler, int i, Object obj)
    {
        mActivePhone.registerForUnknownConnection(handler, i, obj);
        mIPPhone.registerForUnknownConnection(handler, i, obj);
    }

    public void rejectCall()
        throws CallStateException
    {
        if(mRegistered)
        {
            mIPPhone.rejectCall();
            return;
        } else
        {
            mActivePhone.rejectCall();
            return;
        }
    }

    public void removeReferences()
    {
        mActivePhone = null;
        mIPPhone = null;
        mCommandsInterface = null;
    }

    public void requestIsimAuthentication(String s, Message message)
    {
        mActivePhone.requestIsimAuthentication(s, message);
    }

    public int resumeQos(int i)
    {
        return mActivePhone.resumeQos(i);
    }

    public void selectNetworkManually(OperatorInfo operatorinfo, Message message)
    {
        mActivePhone.selectNetworkManually(operatorinfo, message);
    }

    public void selectNetworkManually(String s, String s1, Message message)
    {
        mActivePhone.selectNetworkManually(s, s1, message);
    }

    public void sendBurstDtmf(String s, int i, int j, Message message)
    {
        mActivePhone.sendBurstDtmf(s, i, j, message);
    }

    public void sendDtmf(char c)
    {
        if(mRegistered)
        {
            mIPPhone.sendDtmf(c);
            return;
        } else
        {
            mActivePhone.sendDtmf(c);
            return;
        }
    }

    public void sendUssdResponse(String s)
    {
        if(mRegistered)
        {
            mIPPhone.sendUssdResponse(s);
            return;
        } else
        {
            mActivePhone.sendUssdResponse(s);
            return;
        }
    }

    public void setBandMode(int i, Message message)
    {
        mActivePhone.setBandMode(i, message);
    }

    public boolean setCallBarringOption(boolean flag, String s, String s1, int i, Message message)
    {
        if(mRegistered)
            return mIPPhone.setCallBarringOption(flag, s, s1, i, message);
        else
            return mActivePhone.setCallBarringOption(flag, s, s1, i, message);
    }

    public boolean setCallBarringOption(boolean flag, String s, String s1, Message message)
    {
        if(mRegistered)
            return mIPPhone.setCallBarringOption(flag, s, s1, message);
        else
            return mActivePhone.setCallBarringOption(flag, s, s1, message);
    }

    public void setCallForwardingOption(int i, int j, String s, int k, int l, Message message)
    {
        if(mRegistered)
        {
            mIPPhone.setCallForwardingOption(i, j, s, k, l, message);
            return;
        } else
        {
            mActivePhone.setCallForwardingOption(i, j, s, k, l, message);
            return;
        }
    }

    public void setCallForwardingOption(int i, int j, String s, int k, Message message)
    {
        if(mRegistered)
        {
            mIPPhone.setCallForwardingOption(i, j, s, k, message);
            return;
        } else
        {
            mActivePhone.setCallForwardingOption(i, j, s, k, message);
            return;
        }
    }

    public void setCallWaiting(boolean flag, Message message)
    {
        if(mRegistered)
        {
            mIPPhone.setCallWaiting(flag, message);
            return;
        } else
        {
            mActivePhone.setCallWaiting(flag, message);
            return;
        }
    }

    public void setCdmaRoamingPreference(int i, Message message)
    {
        mActivePhone.setCdmaRoamingPreference(i, message);
    }

    public void setCdmaSubscription(int i, Message message)
    {
        mActivePhone.setCdmaSubscription(i, message);
    }

    public void setCellBroadcastSmsConfig(int ai[], Message message)
    {
        mActivePhone.setCellBroadcastSmsConfig(ai, message);
    }

    public void setDataReadinessChecks(boolean flag, boolean flag1, boolean flag2)
    {
        mActivePhone.setDataReadinessChecks(flag, flag1, flag2);
    }

    public void setDataRoamingEnabled(boolean flag)
    {
        mActivePhone.setDataRoamingEnabled(flag);
    }

    public void setEchoSuppressionEnabled(boolean flag)
    {
        mActivePhone.setEchoSuppressionEnabled(flag);
    }

    public void setEmergencyState(IPUtils.EmergencyState emergencystate)
    {
        mEmergencyCallController.setEmergencyState(emergencystate);
    }

    public void setForceEmergencyMode(boolean flag)
    {
        boolean flag1 = true;
        Log.i("IPPhoneProxy", (new StringBuilder()).append("setForceEmergencyMode ").append(flag).toString());
        if(!IPPhoneSettings.getBoolean(getContext().getContentResolver(), "CELL_ONLY", flag1))
        {
            Intent intent;
            boolean flag2;
            if(!flag)
                flag2 = flag1;
            else
                flag2 = false;
            mRegistered = flag2;
            if(flag)
                flag1 = false;
            setCall(flag1);
        }
        if(flag != forceEmergency)
        {
            intent = new Intent("com.movial.force_emergency_changed");
            intent.putExtra("force", flag);
            mActivePhone.getContext().sendBroadcast(intent);
            mEmergencyCallController.notifyForceEmergencyModeChanged(flag);
        }
        forceEmergency = flag;
    }

    public void setGbaBootstrappingParams(byte abyte0[], String s, String s1, Message message)
    {
        mActivePhone.setGbaBootstrappingParams(abyte0, s, s1, message);
    }

    public void setLine1Number(String s, String s1, Message message)
    {
        mActivePhone.setLine1Number(s, s1, message);
    }

    public void setMute(boolean flag)
    {
        if(mRegistered)
        {
            mIPPhone.setMute(flag);
            return;
        } else
        {
            mActivePhone.setMute(flag);
            return;
        }
    }

    public void setNetworkSelectionModeAutomatic(Message message)
    {
        mActivePhone.setNetworkSelectionModeAutomatic(message);
    }

    public void setOnEcbModeExitResponse(Handler handler, int i, Object obj)
    {
        mActivePhone.setOnEcbModeExitResponse(handler, i, obj);
        mIPPhone.setOnEcbModeExitResponse(handler, i, obj);
    }

    public void setOnPostDialCharacter(Handler handler, int i, Object obj)
    {
        mActivePhone.setOnPostDialCharacter(handler, i, obj);
        mIPPhone.setOnPostDialCharacter(handler, i, obj);
    }

    public void setOnUnsolOemHookExtApp(Handler handler, int i, Object obj)
    {
        mActivePhone.setOnUnsolOemHookExtApp(handler, i, obj);
    }

    public void setOutgoingCallerIdDisplay(int i, Message message)
    {
        if(mRegistered)
        {
            mIPPhone.setOutgoingCallerIdDisplay(i, message);
            return;
        } else
        {
            mActivePhone.setOutgoingCallerIdDisplay(i, message);
            return;
        }
    }

    public void setPreferredNetworkList(int i, String s, String s1, int j, int k, int l, int i1, 
            Message message)
    {
    }

    public void setPreferredNetworkType(int i, Message message)
    {
        mActivePhone.setPreferredNetworkType(i, message);
    }

    public void setRadioPower(boolean flag)
    {
        mActivePhone.setRadioPower(flag);
    }

    public void setSmscAddress(String s, Message message)
    {
        mActivePhone.setSmscAddress(s, message);
    }

    public void setTTYMode(int i, Message message)
    {
        mActivePhone.setTTYMode(i, message);
    }

    public void setTransmitPower(int i, Message message)
    {
        mCommandsInterface.setTransmitPower(i, message);
    }

    public void setUnitTestMode(boolean flag)
    {
        mActivePhone.setUnitTestMode(flag);
    }

    public void setVoiceMailNumber(String s, String s1, Message message)
    {
        mActivePhone.setVoiceMailNumber(s, s1, message);
    }

    public void startDtmf(char c)
    {
        if(mRegistered)
        {
            mIPPhone.startDtmf(c);
            return;
        } else
        {
            mActivePhone.startDtmf(c);
            return;
        }
    }

    public void stopDtmf()
    {
        if(mRegistered)
        {
            mIPPhone.stopDtmf();
            return;
        } else
        {
            mActivePhone.stopDtmf();
            return;
        }
    }

    public int suspendQos(int i)
    {
        return mActivePhone.suspendQos(i);
    }

    public void switchHoldingAndActive()
        throws CallStateException
    {
        if(mRegistered)
        {
            mIPPhone.switchHoldingAndActive();
            return;
        } else
        {
            mActivePhone.switchHoldingAndActive();
            return;
        }
    }

    public void unSetOnUnsolOemHookExtApp(Handler handler)
    {
        mActivePhone.unSetOnUnsolOemHookExtApp(handler);
    }

    public void unregisterForCallWaiting(Handler handler)
    {
        mActivePhone.unregisterForCallWaiting(handler);
    }

    public void unregisterForCdmaOtaStatusChange(Handler handler)
    {
        mActivePhone.unregisterForCdmaOtaStatusChange(handler);
    }

    public void unregisterForDisconnect(Handler handler)
    {
        mActivePhone.unregisterForDisconnect(handler);
        mIPPhone.unregisterForDisconnect(handler);
    }

    public void unregisterForDisplayInfo(Handler handler)
    {
        mActivePhone.unregisterForDisplayInfo(handler);
    }

    public void unregisterForEcmTimerReset(Handler handler)
    {
        mActivePhone.unregisterForEcmTimerReset(handler);
        mIPPhone.unregisterForEcmTimerReset(handler);
    }

    public void unregisterForInCallVoicePrivacyOff(Handler handler)
    {
        mActivePhone.unregisterForInCallVoicePrivacyOff(handler);
    }

    public void unregisterForInCallVoicePrivacyOn(Handler handler)
    {
        mActivePhone.unregisterForInCallVoicePrivacyOn(handler);
    }

    public void unregisterForIncomingRing(Handler handler)
    {
        mActivePhone.unregisterForIncomingRing(handler);
        mIPPhone.unregisterForIncomingRing(handler);
    }

    public void unregisterForLineControlInfo(Handler handler)
    {
        mActivePhone.unregisterForLineControlInfo(handler);
    }

    public void unregisterForMmiComplete(Handler handler)
    {
        mIPPhone.unregisterForMmiComplete(handler);
        mActivePhone.unregisterForMmiComplete(handler);
    }

    public void unregisterForMmiInitiate(Handler handler)
    {
        mIPPhone.unregisterForMmiInitiate(handler);
        mActivePhone.unregisterForMmiInitiate(handler);
    }

    public void unregisterForNewIMSCall(Handler handler)
    {
    }

    public void unregisterForNewRingingConnection(Handler handler)
    {
        mActivePhone.unregisterForNewRingingConnection(handler);
        mIPPhone.unregisterForNewRingingConnection(handler);
    }

    public void unregisterForNumberInfo(Handler handler)
    {
        mActivePhone.unregisterForNumberInfo(handler);
    }

    public void unregisterForPreciseCallStateChanged(Handler handler)
    {
        mActivePhone.unregisterForPreciseCallStateChanged(handler);
        mIPPhone.unregisterForPreciseCallStateChanged(handler);
    }

    public void unregisterForRedirectedNumberInfo(Handler handler)
    {
        mActivePhone.unregisterForRedirectedNumberInfo(handler);
    }

    public void unregisterForResendIncallMute(Handler handler)
    {
        mActivePhone.unregisterForResendIncallMute(handler);
    }

    public void unregisterForRingbackTone(Handler handler)
    {
        mActivePhone.unregisterForRingbackTone(handler);
        mIPPhone.unregisterForRingbackTone(handler);
    }

    public void unregisterForServiceStateChanged(Handler handler)
    {
        mActivePhone.unregisterForServiceStateChanged(handler);
        mIPPhone.unregisterForServiceStateChanged(handler);
    }

    public void unregisterForSignalInfo(Handler handler)
    {
        mActivePhone.unregisterForSignalInfo(handler);
    }

    public void unregisterForSimRecordsLoaded(Handler handler)
    {
        mActivePhone.unregisterForSimRecordsLoaded(handler);
    }

    public void unregisterForSubscriptionInfoReady(Handler handler)
    {
        mActivePhone.unregisterForSubscriptionInfoReady(handler);
    }

    public void unregisterForSuppServiceFailed(Handler handler)
    {
        mActivePhone.unregisterForSuppServiceFailed(handler);
    }

    public void unregisterForSuppServiceNotification(Handler handler)
    {
        mActivePhone.unregisterForSuppServiceNotification(handler);
    }

    public void unregisterForT53AudioControlInfo(Handler handler)
    {
        mActivePhone.unregisterForT53AudioControlInfo(handler);
    }

    public void unregisterForT53ClirInfo(Handler handler)
    {
        mActivePhone.unregisterForT53ClirInfo(handler);
    }

    public void unregisterForUnknownConnection(Handler handler)
    {
        mActivePhone.unregisterForUnknownConnection(handler);
        mIPPhone.unregisterForUnknownConnection(handler);
    }

    public void unsetOnEcbModeExitResponse(Handler handler)
    {
        mActivePhone.unsetOnEcbModeExitResponse(handler);
        mIPPhone.unsetOnEcbModeExitResponse(handler);
    }

    public void updateServiceLocation()
    {
        mActivePhone.updateServiceLocation();
    }

    private static final int DEFAULT_TIMEOUT = 10000;
    private static final int DELAY_BROADCAST_OPERATORNAME = 1000;
    private static final int DELAY_REBIND_IPSERVICE = 5000;
    private static final int EVENT_BIND_TO_IPSERVICE = 5;
    private static final int EVENT_BROADCAST_OPERATORNAME = 6;
    public static final int EVENT_EMERGENCY_PREF_CHANGED = 7;
    private static final int EVENT_RADIO_ON = 2;
    private static final int EVENT_REQUEST_VOICE_RADIO_TECH_DONE = 3;
    private static final int EVENT_RIL_CONNECTED = 4;
    private static final int EVENT_VOICE_RADIO_TECH_CHANGED = 1;
    private static final String LOG_TAG = "IPPhoneProxy";
    private static final String TAG = "IPPhoneProxy";
    public static final Object lockForRadioTechnologyChange = new Object();
    private static boolean mRegistered = false;
    private final AtomicBoolean binded = new AtomicBoolean(false);
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
    private boolean forceEmergency;
    private Phone mActivePhone;
    private Call mBackgroundCall;
    private CommandsInterface mCommandsInterface;
    private ServiceConnection mConnection;
    private EmergencyCallController mEmergencyCallController;
    private Call mForegroundCall;
    private IPPhone mIPPhone;
    private IIPRegistry mIPRegistry;
    private IPSMSDispatcher mIPSMSDispatcher;
    private IPStateListener mIPStateListener;
    protected IccCardProxy mIccCardProxy;
    private IccPhoneBookInterfaceManagerProxy mIccPhoneBookInterfaceManagerProxy;
    private IccSmsInterfaceManager mIccSmsInterfaceManager;
    private ContentObserver mObserver;
    private String mOperatorName;
    private String mOutgoingPhone;
    private PhoneSubInfoProxy mPhoneSubInfoProxy;
    private String mRealName;
    private BroadcastReceiver mReceiver;
    private boolean mResetModemOnRadioTechnologyChange;
    private int mRilVersion;
    private Call mRingingCall;






/*
    static IIPRegistry access$1002(IPPhoneProxy ipphoneproxy, IIPRegistry iipregistry)
    {
        ipphoneproxy.mIPRegistry = iipregistry;
        return iipregistry;
    }

*/











/*
    static boolean access$602(boolean flag)
    {
        mRegistered = flag;
        return flag;
    }

*/



}
