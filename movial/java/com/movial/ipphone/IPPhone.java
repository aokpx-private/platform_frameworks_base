// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.preference.PreferenceManager;
import android.telephony.*;
import android.util.Log;
import com.android.internal.telephony.*;
import com.android.internal.telephony.cat.CatService;
import com.android.internal.telephony.gsm.GSMPhone;
import com.android.internal.telephony.uicc.IccFileHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.movial.ipphone:
//            IPCallTracker, IIPService, IPMmiCode, IPPhoneSettings

public class IPPhone extends PhoneBase
{

    public IPPhone(Phone phone, CommandsInterface commandsinterface, PhoneNotifier phonenotifier)
    {
        super(phonenotifier, phone.getContext(), commandsinterface);
        mPendingMMIs = new ArrayList();
        mEcmTimerResetRegistrants = new RegistrantList();
        mRingbackToneRegistrants = new RegistrantList();
        mMmiMessages = new Message[8];
        mCallWaitingOnPregress = false;
        mCallWaitingDone = false;
        mHandler = new Handler() {

            public void handleMessage(Message message)
            {
                message.what;
                JVM INSTR tableswitch 0 4: default 40
            //                           0 41
            //                           1 116
            //                           2 40
            //                           3 213
            //                           4 288;
                   goto _L1 _L2 _L3 _L1 _L4 _L5
_L1:
                return;
_L2:
                Message message3 = mMmiMessages[message.arg2];
                if(message3 == null || message3.getTarget() == null)
                {
                    Log.w("IPPhone", "No pending CF request");
                    return;
                }
                Exception exception2;
                if(message.arg1 != 0)
                    exception2 = new Exception("setCallForward Failed");
                else if
                    exception2 = null;
                AsyncResult.forMessage(message3, null, exception2);
                message3.sendToTarget();
                return;
_L3:
                Message message2 = mMmiMessages[message.arg2];
                if(message2 == null || message2.getTarget() == null)
                {
                    Log.w("IPPhone", "No pending Call Waiting request");
                    return;
                }
                Exception exception1;
                if(message.arg1 != 0)
                    exception1 = new Exception("setCallWaiting Failed");
                else if
                    exception1 = null;
                AsyncResult.forMessage(message2, null, exception1);
                message2.sendToTarget();
                if(message.arg1 == 0)
                {
                    mCallWaitingDone = mCallWaitingOnPregress;
                    return;
                }
                  goto _L1
_L4:
                Message message1 = mMmiMessages[message.arg2];
                if(message1 == null || message1.getTarget() == null)
                {
                    Log.w("IPPhone", "No pending USSD request");
                    return;
                }
                Exception exception;
                if(message.arg1 != 0)
                    exception = new Exception("send USSD Failed");
                else if
                    exception = null;
                AsyncResult.forMessage(message1, null, exception);
                message1.sendToTarget();
                return;
_L5:
                try
                {
                    Bundle bundle = message.getData();
                    bundle.setClassLoader(com/movial/ipphone/IPPhone.getClassLoader());
                    String s = bundle.getString("ussd");
                    onIncomingUSSD(0, s);
                    return;
                }
                catch(NumberFormatException numberformatexception)
                {
                    Log.w("IPPhone", "error parsing USSD");
                }
                return;
            }

            final IPPhone this$0;

            
            {
                this$0 = IPPhone.this;
                super();
            }
        };
        mConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName componentname, IBinder ibinder)
            {
                mIPService = IIPService.Stub.asInterface(ibinder);
                binded.set(true);
                registerHandler();
                try
                {
                    cyclicBarrier.await(3000L, TimeUnit.MILLISECONDS);
                    return;
                }
                catch(Exception exception)
                {
                    Log.e("IPPhone", exception.toString());
                }
            }

            public void onServiceDisconnected(ComponentName componentname)
            {
                mIPService = null;
                binded.set(false);
            }

            final IPPhone this$0;

            
            {
                this$0 = IPPhone.this;
                super();
            }
        };
        PhoneBase phonebase = (PhoneBase)phone;
        phonebase.mCM.setOnCallRing(phonebase, 14, null);
        mContext = phone.getContext();
        mPhone = phone;
        mRegistry = com.android.internal.telephony.ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
        mCT = new IPCallTracker(this);
        mLooper = Looper.myLooper();
        registerReceiver(mContext);
    }

    private void bindToIPService()
    {
        AtomicBoolean atomicboolean = binded;
        atomicboolean;
        JVM INSTR monitorenter ;
        if(binded.get())
            break MISSING_BLOCK_LABEL_67;
        cyclicBarrier.reset();
        Intent intent = new Intent(com/movial/ipphone/IIPService.getName());
        intent.setClassName("com.movial.ipservice", "com.movial.ipservice.IPService");
        if(getContext().bindService(intent, mConnection, 1))
            waitConnectionResponse(true);
_L1:
        return;
        Exception exception1;
        exception1;
        Log.e("IPPhone", (new StringBuilder()).append("bindService FAILED. ").append(exception1.toString()).toString());
          goto _L1
        Exception exception;
        exception;
        atomicboolean;
        JVM INSTR monitorexit ;
        throw exception;
    }

    private void checkCorrectThread(Handler handler)
    {
        if(handler.getLooper() != mLooper)
            throw new RuntimeException("com.android.internal.telephony.Phone must be used from within one thread");
        else if
            return;
    }

    private int convertCallState(com.android.internal.telephony.Phone.State state)
    {
        static class _cls5
        {

            static final int $SwitchMap$com$android$internal$telephony$Phone$State[];

            static 
            {
                $SwitchMap$com$android$internal$telephony$Phone$State = new int[com.android.internal.telephony.Phone.State.values().length];
                try
                {
                    $SwitchMap$com$android$internal$telephony$Phone$State[com.android.internal.telephony.Phone.State.RINGING.ordinal()] = 1;
                }
                catch(NoSuchFieldError nosuchfielderror) { }
                try
                {
                    $SwitchMap$com$android$internal$telephony$Phone$State[com.android.internal.telephony.Phone.State.OFFHOOK.ordinal()] = 2;
                }
                catch(NoSuchFieldError nosuchfielderror1)
                {
                    return;
                }
            }
        }

        switch(_cls5..SwitchMap.com.android.internal.telephony.Phone.State[state.ordinal()])
        {
        default:
            return 0;

        case 1: // '\001'
            return 1;

        case 2: // '\002'
            return 2;
        }
    }

    private boolean isAirplaneModeOn()
    {
        int i = android.provider.Settings.System.getInt(mContext.getContentResolver(), "airplane_mode_on", 0);
        boolean flag = false;
        if(i != 0)
            flag = true;
        return flag;
    }

    private void log(String s)
    {
        Log.d("IPPhone", s);
    }

    private void onIncomingUSSD(int i, String s)
    {
        int j;
        boolean flag;
        int k;
        IPMmiCode ipmmicode;
        j = 1;
        int l;
        if(i == j)
            flag = j;
        else if
            flag = false;
        if(i == 0 || i == j)
            j = 0;
        k = 0;
        l = mPendingMMIs.size();
_L12:
        ipmmicode = null;
        if(k >= l) goto _L2; else if goto _L1
_L1:
        if(!((IPMmiCode)mPendingMMIs.get(k)).isPendingUSSD()) goto _L4; else if goto _L3
_L3:
        ipmmicode = (IPMmiCode)mPendingMMIs.get(k);
_L2:
        if(ipmmicode == null) goto _L6; else if goto _L5
_L5:
        if(j == 0) goto _L8; else if goto _L7
_L7:
        ipmmicode.onUssdFinishedError();
_L10:
        return;
_L4:
        k++;
        continue; /* Loop/switch isn't completed */
_L8:
        ipmmicode.onUssdFinished(s, flag);
        return;
_L6:
        if(j != 0 || s == null) goto _L10; else if goto _L9
_L9:
        onNetworkInitiatedUssd(IPMmiCode.newNetworkInitiatedUssd(s, flag, this));
        return;
        if(true) goto _L12; else if goto _L11
_L11:
    }

    private void onNetworkInitiatedUssd(IPMmiCode ipmmicode)
    {
        mMmiCompleteRegistrants.notifyRegistrants(new AsyncResult(null, ipmmicode, null));
    }

    private void registerReceiver(Context context)
    {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("ACTION_RADIO_ON");
        if(!IPPhoneSettings.getBoolean(mContext.getContentResolver(), "WIFI_FIRST_TURNON", false))
            intentfilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        context.registerReceiver(mReceiver, intentfilter);
    }

    private void showDialog(int i)
    {
        while(i != 3 || IPPhoneSettings.getBoolean(mContext.getContentResolver(), "WIFI_FIRST_TURNON", false)) 
            return;
        Intent intent;
        try
        {
            mContext.getPackageManager().getPackageInfo("com.movial.ipservice", 1);
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e("IPPhone", "Package com.movial.ipservice is not loaded possibly due to encryption mode");
            return;
        }
        IPPhoneSettings.putBoolean(mContext.getContentResolver(), "WIFI_FIRST_TURNON", true);
        intent = new Intent("android.intent.action.VIEW");
        intent.setClassName("com.movial.ipservice", "com.movial.ipservice.IPDialog");
        intent.putExtra("dialog_type", 1);
        intent.addFlags(0x20000000);
        intent.addFlags(0x10000000);
        mContext.startActivity(intent);
    }

    private void waitConnectionResponse(boolean flag)
    {
        if(flag)
        {
            try
            {
                cyclicBarrier.await(3000L, TimeUnit.MILLISECONDS);
                return;
            }
            catch(Exception exception)
            {
                Log.e("IPPhone", (new StringBuilder()).append("waitConnectionResponse FAILED. ").append(exception.toString()).toString());
            }
            break MISSING_BLOCK_LABEL_58;
        }
        cyclicBarrier.await();
        return;
    }

    public void SimSlotActivation(boolean flag)
    {
    }

    public void acceptCall()
        throws CallStateException
    {
        mCT.acceptCall();
    }

    public void activateCellBroadcastSms(int i, Message message)
    {
        mPhone.activateCellBroadcastSms(i, message);
    }

    public Connection addUserToConfCall(String s)
        throws CallStateException
    {
        return null;
    }

    public void akaAuthenticate(byte abyte0[], byte abyte1[], Message message)
    {
        mPhone.akaAuthenticate(abyte0, abyte1, message);
    }

    void bindService()
    {
        (new Thread() {

            public void run()
            {
                bindToIPService();
            }

            final IPPhone this$0;

            
            {
                this$0 = IPPhone.this;
                super();
            }
        }).start();
    }

    public boolean canConference()
    {
        return mCT.canConference();
    }

    public boolean canTransfer()
    {
        return false;
    }

    public void cancelPendingUssd(Message message)
    {
    }

    public boolean changeBarringPassword(String s, String s1, String s2, Message message)
    {
        return false;
    }

    public boolean changeBarringPassword(String s, String s1, String s2, String s3, Message message)
    {
        return false;
    }

    public void clearDisconnected()
    {
        mCT.clearDisconnected();
    }

    public void conference()
        throws CallStateException
    {
        mCT.conference();
    }

    public Connection dial(String s)
        throws CallStateException
    {
        return dial(s, null);
    }

    public Connection dial(String s, UUSInfo uusinfo)
        throws CallStateException
    {
        String s1 = PhoneNumberUtils.stripSeparators(s);
        if(handleInCallMmiCommands(s1))
            return null;
        IPMmiCode ipmmicode = IPMmiCode.newFromDialString(PhoneNumberUtils.extractNetworkPortion(s1), this);
        Log.d("IPPhone", (new StringBuilder()).append("dialing w/ mmi '").append(ipmmicode).append("'...").toString());
        if(ipmmicode == null)
            return mCT.dial(s1);
        if(ipmmicode.isTemporaryModeCLIR())
        {
            return mCT.dial(ipmmicode.dialingNumber, ipmmicode.getCLIRMode());
        } else if
        {
            mPendingMMIs.add(ipmmicode);
            mMmiRegistrants.notifyRegistrants(new AsyncResult(null, ipmmicode, null));
            ipmmicode.processCode();
            return null;
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
        return null;
    }

    public boolean disableDataConnectivity()
    {
        return mPhone.disableDataConnectivity();
    }

    public void disableLocationUpdates()
    {
        mPhone.disableLocationUpdates();
    }

    public boolean enableDataConnectivity()
    {
        return mPhone.enableDataConnectivity();
    }

    public void enableLocationUpdates()
    {
        mPhone.enableLocationUpdates();
    }

    public void exitEmergencyCallbackMode()
    {
    }

    public void explicitCallTransfer()
        throws CallStateException
    {
    }

    public void gbaAuthenticateBootstrap(byte abyte0[], byte abyte1[], Message message)
    {
        mPhone.gbaAuthenticateBootstrap(abyte0, abyte1, message);
    }

    public void gbaAuthenticateNaf(byte abyte0[], Message message)
    {
        mPhone.gbaAuthenticateNaf(abyte0, message);
    }

    public void getAvailableNetworks(Message message)
    {
        mPhone.getAvailableNetworks(message);
    }

    public Call getBackgroundCall()
    {
        return mCT.backgroundCall;
    }

    public void getCallBarringOption(String s, Message message)
    {
    }

    public boolean getCallForwardingIndicator()
    {
        return false;
    }

    public void getCallForwardingOption(int i, Message message)
    {
        CallForwardInfo acallforwardinfo[] = new CallForwardInfo[1];
        CallForwardInfo callforwardinfo = new CallForwardInfo();
        callforwardinfo.number = "0000";
        callforwardinfo.status = 1;
        acallforwardinfo[0] = callforwardinfo;
        AsyncResult.forMessage(message, acallforwardinfo, new CommandException(com.android.internal.telephony.CommandException.Error.REQUEST_NOT_SUPPORTED));
        message.sendToTarget();
    }

    public void getCallWaiting(Message message)
    {
        int i = 1;
        int ai[] = new int[i];
        if(!mCallWaitingDone)
            i = 0;
        ai[0] = i;
        AsyncResult.forMessage(message, ai, new CommandException(com.android.internal.telephony.CommandException.Error.REQUEST_NOT_SUPPORTED));
        message.sendToTarget();
    }

    public CatService getCatService()
    {
        return null;
    }

    public void getCellBroadcastSmsConfig(Message message)
    {
        mPhone.getCellBroadcastSmsConfig(message);
    }

    public CellLocation getCellLocation()
    {
        return mPhone.getCellLocation();
    }

    public com.android.internal.telephony.Phone.DataActivityState getDataActivityState()
    {
        return mPhone.getDataActivityState();
    }

    public void getDataCallList(Message message)
    {
        mPhone.getDataCallList(message);
    }

    public com.android.internal.telephony.Phone.DataState getDataConnectionState(String s)
    {
        return mPhone.getDataConnectionState(s);
    }

    public boolean getDataRoamingEnabled()
    {
        return mPhone.getDataRoamingEnabled();
    }

    public int getDataServiceState()
    {
        return 0;
    }

    public boolean getDesiredPowerState()
    {
        return ((GSMPhone)mPhone).getDesiredPowerState();
    }

    public String getDeviceId()
    {
        return mPhone.getDeviceId();
    }

    public String getDeviceSvn()
    {
        return mPhone.getDeviceSvn();
    }

    public String getEsn()
    {
        return "0";
    }

    public boolean getFDNavailable()
    {
        return false;
    }

    public Call getForegroundCall()
    {
        return mCT.foregroundCall;
    }

    public Phone getGsmPhone()
    {
        return mPhone;
    }

    public String getHandsetInfo(String s)
    {
        return mPhone.getHandsetInfo(s);
    }

    public IccCard getIccCard()
    {
        return mPhone.getIccCard();
    }

    public IccFileHandler getIccFileHandler()
    {
        return ((GSMPhone)mPhone).getIccFileHandler();
    }

    public IccPhoneBookInterfaceManager getIccPhoneBookInterfaceManager()
    {
        return mPhone.getIccPhoneBookInterfaceManager();
    }

    public boolean getIccRecordsLoaded()
    {
        return mPhone.getIccRecordsLoaded();
    }

    public String getIccSerialNumber()
    {
        return mPhone.getIccSerialNumber();
    }

    public IccSmsInterfaceManager getIccSmsInterfaceManager()
    {
        return null;
    }

    public String getImei()
    {
        return mPhone.getImei();
    }

    public String getImeiInCDMAGSMPhone()
    {
        return mPhone.getImeiInCDMAGSMPhone();
    }

    public String getLine1AlphaTag()
    {
        return mPhone.getLine1AlphaTag();
    }

    public String getLine1Number()
    {
        return mPhone.getLine1Number();
    }

    public String getMeid()
    {
        return "0";
    }

    public boolean getMessageWaitingIndicator()
    {
        return mPhone.getMessageWaitingIndicator();
    }

    public boolean getMute()
    {
        return mCT.getMute();
    }

    public void getNeighboringCids(Message message)
    {
        mPhone.getNeighboringCids(message);
    }

    public void getOutgoingCallerIdDisplay(Message message)
    {
        AsyncResult.forMessage(message, new int[] {
            PreferenceManager.getDefaultSharedPreferences(mContext).getInt("clir_key", -1), 1
        }, null);
        message.sendToTarget();
    }

    public List getPendingMmiCodes()
    {
        return mPendingMMIs;
    }

    public String getPhoneName()
    {
        return mPhone.getPhoneName();
    }

    public PhoneSubInfo getPhoneSubInfo()
    {
        return mPhone.getPhoneSubInfo();
    }

    public int getPhoneType()
    {
        return mPhone.getPhoneType();
    }

    public void getPreferredNetworkList(Message message)
    {
    }

    public void getPreferredNetworkType(Message message)
    {
    }

    public Call getRingingCall()
    {
        return mCT.ringingCall;
    }

    public boolean getSMSPavailable()
    {
        return ((GSMPhone)mPhone).getSMSPavailable();
    }

    public boolean getSMSavailable()
    {
        return ((GSMPhone)mPhone).getSMSavailable();
    }

    IIPService getService()
    {
        if(binded.get())
            return mIPService;
        else if
            return null;
    }

    public ServiceState getServiceState()
    {
        ServiceState servicestate = new ServiceState(mPhone.getServiceState());
        if(!isAirplaneModeOn() && servicestate.getState() != 0)
        {
            servicestate.setRoaming(false);
            servicestate.setState(0);
        }
        return servicestate;
    }

    public SignalStrength getSignalStrength()
    {
        return mPhone.getSignalStrength();
    }

    public String getSktImsiM()
    {
        return mPhone.getSktImsiM();
    }

    public String getSktIrm()
    {
        return mPhone.getSktIrm();
    }

    public void getSmscAddress(Message message)
    {
    }

    public String[] getSponImsi()
    {
        return null;
    }

    public com.android.internal.telephony.Phone.State getState()
    {
        return mCT.state;
    }

    public String getSubscriberId()
    {
        return mPhone.getSubscriberId();
    }

    public String getVoiceMailAlphaTag()
    {
        return mPhone.getVoiceMailAlphaTag();
    }

    public String getVoiceMailNumber()
    {
        return mPhone.getVoiceMailNumber();
    }

    public boolean handleInCallMmiCommands(String s)
        throws CallStateException
    {
        return false;
    }

    public boolean handlePinMmi(String s)
    {
        return false;
    }

    public void invokeOemRilRequestRaw(byte abyte0[], Message message)
    {
    }

    public void invokeOemRilRequestStrings(String as[], Message message)
    {
    }

    public boolean isDataConnectivityPossible()
    {
        return mPhone.isDataConnectivityPossible();
    }

    boolean isInCall()
    {
        com.android.internal.telephony.Call.State state = getForegroundCall().getState();
        com.android.internal.telephony.Call.State state1 = getBackgroundCall().getState();
        com.android.internal.telephony.Call.State state2 = getRingingCall().getState();
        return state.isAlive() || state1.isAlive() || state2.isAlive();
    }

    public boolean isMMICode(String s)
    {
        return false;
    }

    void notifyDisconnect(Connection connection)
    {
        log("notifyDisconnect");
        mDisconnectRegistrants.notifyResult(connection);
    }

    void notifyIncomingRing()
    {
        log("notifyIncomingRing");
        AsyncResult asyncresult = new AsyncResult(null, this, null);
        mIncomingRingRegistrants.notifyRegistrants(asyncresult);
    }

    void notifyNewRingingConnection(Connection connection)
    {
        log("notifyNewRingingConnection");
        super.notifyNewRingingConnectionP(connection);
    }

    void notifyPhoneStateChanged()
    {
        log("notifyPhoneStateChanged");
        Call call = getRingingCall();
        String s = "";
        if(call != null && call.getEarliestConnection() != null)
            s = call.getEarliestConnection().getAddress();
        try
        {
            mRegistry.notifyCallState(convertCallState(getState()), s);
            return;
        }
        catch(Exception exception)
        {
            Log.e("IPPhone", exception.toString());
        }
    }

    void notifyPreciseCallStateChanged()
    {
        log("notifyPreciseCallStateChanged");
        super.notifyPreciseCallStateChangedP();
    }

    void notifyRingbackTone(boolean flag)
    {
        mRingbackToneRegistrants.notifyRegistrants(new AsyncResult(null, Boolean.valueOf(flag), null));
    }

    void notifyServiceStateChanged()
    {
        log("notifyServiceStateChanged");
        ServiceState servicestate = getServiceState();
        AsyncResult asyncresult = new AsyncResult(null, servicestate, null);
        mServiceStateRegistrants.notifyRegistrants(asyncresult);
        try
        {
            mRegistry.notifyServiceState(servicestate);
            return;
        }
        catch(Exception exception)
        {
            Log.e("IPPhone", exception.toString());
        }
    }

    void notifyUnknownConnection(Connection connection)
    {
        log("notifyUnknownRingingConnection");
        AsyncResult asyncresult = new AsyncResult(null, connection, null);
        mUnknownConnectionRegistrants.notifyRegistrants(asyncresult);
    }

    void onMMIDone(IPMmiCode ipmmicode)
    {
        if(mPendingMMIs.remove(ipmmicode) || ipmmicode.isUssdRequest())
            mMmiCompleteRegistrants.notifyRegistrants(new AsyncResult(null, ipmmicode, null));
    }

    protected void onUpdateIccAvailability()
    {
        log("onUpdateIccAvailability");
        ((GSMPhone)mPhone).onUpdateIccAvailability();
    }

    public void queryAvailableBandMode(Message message)
    {
    }

    public void queryCdmaRoamingPreference(Message message)
    {
    }

    public void registerFoT53ClirlInfo(Handler handler, int i, Object obj)
    {
    }

    public void registerForDisplayInfo(Handler handler, int i, Object obj)
    {
    }

    public void registerForEcmTimerReset(Handler handler, int i, Object obj)
    {
        mEcmTimerResetRegistrants.addUnique(handler, i, obj);
    }

    public void registerForInCallVoicePrivacyOff(Handler handler, int i, Object obj)
    {
    }

    public void registerForInCallVoicePrivacyOn(Handler handler, int i, Object obj)
    {
    }

    public void registerForLineControlInfo(Handler handler, int i, Object obj)
    {
    }

    public void registerForNumberInfo(Handler handler, int i, Object obj)
    {
    }

    public void registerForRedirectedNumberInfo(Handler handler, int i, Object obj)
    {
    }

    public void registerForResendIncallMute(Handler handler, int i, Object obj)
    {
    }

    public void registerForRingbackTone(Handler handler, int i, Object obj)
    {
        Registrant registrant = new Registrant(handler, i, obj);
        mRingbackToneRegistrants.add(registrant);
    }

    public void registerForSignalInfo(Handler handler, int i, Object obj)
    {
    }

    public void registerForSuppServiceFailed(Handler handler, int i, Object obj)
    {
    }

    public void registerForSuppServiceNotification(Handler handler, int i, Object obj)
    {
    }

    public void registerForT53AudioControlInfo(Handler handler, int i, Object obj)
    {
    }

    void registerHandler()
    {
        mCT.registerHandler();
        try
        {
            mIPService.registerForIncomingUSSD(new Messenger(mHandler));
            return;
        }
        catch(Exception exception)
        {
            Log.e("IPPhone", "register for ussd fail");
        }
    }

    public void rejectCall()
        throws CallStateException
    {
        mCT.rejectCall();
    }

    public void removeReferences()
    {
        log("removeReferences");
        mSimulatedRadioControl = null;
        mCT = null;
        super.removeReferences();
    }

    public void selectNetworkManually(OperatorInfo operatorinfo, Message message)
    {
        mPhone.selectNetworkManually(operatorinfo, message);
    }

    public void selectNetworkManually(String s, String s1, Message message)
    {
    }

    public void sendBurstDtmf(String s, int i, int j, Message message)
    {
        Log.e("IPPhone", "sendBurstDtmf is CDMA method");
    }

    public void sendDtmf(char c)
    {
        if(!PhoneNumberUtils.is12Key(c))
        {
            Log.e("IPPhone", (new StringBuilder()).append("sendDtmf called with invalid character '").append(c).append("'").toString());
            return;
        } else if
        {
            mCT.sendDtmf(c);
            return;
        }
    }

    public void sendUSSD(String s, Message message)
    {
        int i = mIPService.sendUssd(s, new Messenger(mHandler));
        if(i == -1)
        {
            try
            {
                Message message1 = Message.obtain(mHandler, 3, 1, 7);
                mMmiMessages[7] = message;
                message1.sendToTarget();
                return;
            }
            catch(Exception exception)
            {
                Log.e("IPPhone", (new StringBuilder()).append("").append(exception).toString());
            }
            break MISSING_BLOCK_LABEL_92;
        }
        mMmiMessages[i] = message;
        return;
    }

    public void sendUssdResponse(String s)
    {
        IPMmiCode ipmmicode = IPMmiCode.newFromUssdUserInput(s, this);
        mPendingMMIs.add(ipmmicode);
        mMmiRegistrants.notifyRegistrants(new AsyncResult(null, ipmmicode, null));
        ipmmicode.sendUssd(s);
    }

    public void setBandMode(int i, Message message)
    {
    }

    public boolean setCallBarringOption(boolean flag, String s, String s1, int i, Message message)
    {
        return false;
    }

    public boolean setCallBarringOption(boolean flag, String s, String s1, Message message)
    {
        return false;
    }

    public void setCallForwardingOption(int i, int j, String s, int k, int l, Message message)
    {
        setCallForwardingOption(i, j, s, k, message);
    }

    public void setCallForwardingOption(int i, int j, String s, int k, Message message)
    {
        int l = mIPService.setCallForward(i, j, s, k, new Messenger(mHandler));
        if(l == -1)
        {
            try
            {
                Message message1 = Message.obtain(mHandler, 0, 1, 7);
                mMmiMessages[7] = message;
                message1.sendToTarget();
                return;
            }
            catch(Exception exception)
            {
                Log.e("IPPhone", (new StringBuilder()).append("").append(exception).toString());
            }
            break MISSING_BLOCK_LABEL_100;
        }
        mMmiMessages[l] = message;
        return;
    }

    public void setCallWaiting(boolean flag, Message message)
    {
        int i;
        mCallWaitingOnPregress = flag;
        i = mIPService.setCW(flag, new Messenger(mHandler));
        if(i == -1)
        {
            try
            {
                Message message1 = Message.obtain(mHandler, 1, 1, 7);
                mMmiMessages[7] = message;
                message1.sendToTarget();
                return;
            }
            catch(Exception exception)
            {
                Log.e("IPPhone", (new StringBuilder()).append("").append(exception).toString());
            }
            break MISSING_BLOCK_LABEL_97;
        }
        mMmiMessages[i] = message;
        return;
    }

    public void setCdmaRoamingPreference(int i, Message message)
    {
    }

    public void setCdmaSubscription(int i, Message message)
    {
    }

    public void setCellBroadcastSmsConfig(int ai[], Message message)
    {
        mPhone.setCellBroadcastSmsConfig(ai, message);
    }

    public void setDataRoamingEnabled(boolean flag)
    {
        mPhone.setDataRoamingEnabled(flag);
    }

    public void setGbaBootstrappingParams(byte abyte0[], String s, String s1, Message message)
    {
        mPhone.setGbaBootstrappingParams(abyte0, s, s1, message);
    }

    public void setLine1Number(String s, String s1, Message message)
    {
        mPhone.setLine1Number(s, s1, message);
    }

    public void setMute(boolean flag)
    {
        mCT.setMute(flag);
    }

    public void setNetworkSelectionModeAutomatic(Message message)
    {
        mPhone.setNetworkSelectionModeAutomatic(message);
    }

    public void setOnEcbModeExitResponse(Handler handler, int i, Object obj)
    {
        mEcmExitRespRegistrant = new Registrant(handler, i, obj);
    }

    public void setOnPostDialCharacter(Handler handler, int i, Object obj)
    {
        mPostDialHandler = new Registrant(handler, i, obj);
    }

    public void setOutgoingCallerIdDisplay(int i, Message message)
    {
        ((GSMPhone)mPhone).saveClirSetting(i);
        AsyncResult.forMessage(message, null, null);
        message.sendToTarget();
    }

    public void setPreferredNetworkList(int i, String s, String s1, int j, int k, int l, int i1, 
            Message message)
    {
    }

    public void setPreferredNetworkType(int i, Message message)
    {
    }

    public void setRadioPower(boolean flag)
    {
        mPhone.setRadioPower(flag);
    }

    public void setSmscAddress(String s, Message message)
    {
    }

    public void setVoiceMailNumber(String s, String s1, Message message)
    {
        mPhone.setVoiceMailNumber(s, s1, message);
    }

    public void startDtmf(char c)
    {
        if(!PhoneNumberUtils.is12Key(c))
        {
            Log.e("IPPhone", (new StringBuilder()).append("startDtmf called with invalid character '").append(c).append("'").toString());
            return;
        } else if
        {
            mCT.startDtmf(c);
            return;
        }
    }

    public void stopDtmf()
    {
        mCT.stopDtmf();
    }

    public void switchHoldingAndActive()
        throws CallStateException
    {
        mCT.switchWaitingOrHoldingAndActive();
    }

    void unbindService()
    {
        Log.d("IPPhone", (new StringBuilder()).append("unbindService. ").append(binded.get()).toString());
        if(binded.get())
            getContext().unbindService(mConnection);
        mIPService = null;
        binded.set(false);
    }

    public void unregisterForDisplayInfo(Handler handler)
    {
    }

    public void unregisterForEcmTimerReset(Handler handler)
    {
        mEcmTimerResetRegistrants.remove(handler);
    }

    public void unregisterForInCallVoicePrivacyOff(Handler handler)
    {
    }

    public void unregisterForInCallVoicePrivacyOn(Handler handler)
    {
    }

    public void unregisterForLineControlInfo(Handler handler)
    {
    }

    public void unregisterForNumberInfo(Handler handler)
    {
    }

    public void unregisterForRedirectedNumberInfo(Handler handler)
    {
    }

    public void unregisterForResendIncallMute(Handler handler)
    {
    }

    public void unregisterForRingbackTone(Handler handler)
    {
        mRingbackToneRegistrants.remove(handler);
    }

    public void unregisterForSignalInfo(Handler handler)
    {
    }

    public void unregisterForSuppServiceFailed(Handler handler)
    {
    }

    public void unregisterForSuppServiceNotification(Handler handler)
    {
    }

    public void unregisterForT53AudioControlInfo(Handler handler)
    {
    }

    public void unregisterForT53ClirInfo(Handler handler)
    {
    }

    public void unsetOnEcbModeExitResponse(Handler handler)
    {
        mEcmExitRespRegistrant.clear();
    }

    public void updateServiceLocation()
    {
        mPhone.updateServiceLocation();
    }

    static final boolean DBG = true;
    private static final int DEFAULT_TIMEOUT = 3000;
    public static final int EVENT_IP_USSD = 4;
    private static final int PHONE_TYPE_IPPHONE = 3;
    public static final int SEND_IP_USSD_COMPLETE = 3;
    public static final int SET_CALL_FORWARD = 0;
    public static final int SET_CALL_WAITING = 1;
    public static final int SET_CLIR = 2;
    static final String TAG = "IPPhone";
    private final AtomicBoolean binded = new AtomicBoolean(false);
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
    private IPCallTracker mCT;
    private boolean mCallWaitingDone;
    private boolean mCallWaitingOnPregress;
    private ServiceConnection mConnection;
    private Context mContext;
    private Registrant mEcmExitRespRegistrant;
    private RegistrantList mEcmTimerResetRegistrants;
    private Handler mHandler;
    private IIPService mIPService;
    private Looper mLooper;
    private Message mMmiMessages[];
    private ArrayList mPendingMMIs;
    private Phone mPhone;
    Registrant mPostDialHandler;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent)
        {
            String s = intent.getAction();
            Log.d("IPPhone", (new StringBuilder()).append("onReceive: ").append(s).toString());
            if("android.net.wifi.WIFI_STATE_CHANGED".equals(s))
                showDialog(intent.getIntExtra("wifi_state", 4));
            else if
            if("ACTION_RADIO_ON".equals(s))
            {
                setRadioPower(intent.getBooleanExtra("ACTION_RADIO_ON", true));
                return;
            }
        }

        final IPPhone this$0;

            
            {
                this$0 = IPPhone.this;
                super();
            }
    };
    private ITelephonyRegistry mRegistry;
    protected RegistrantList mRingbackToneRegistrants;
    private boolean mUnitTestMode;



/*
    static boolean access$102(IPPhone ipphone, boolean flag)
    {
        ipphone.mCallWaitingDone = flag;
        return flag;
    }

*/




/*
    static IIPService access$402(IPPhone ipphone, IIPService iipservice)
    {
        ipphone.mIPService = iipservice;
        return iipservice;
    }

*/




}
