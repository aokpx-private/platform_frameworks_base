// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.PhoneBase;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.SmsResponse;
import com.android.internal.telephony.SmsStorageMonitor;
import com.android.internal.telephony.SmsUsageMonitor;
import com.android.internal.telephony.gsm.GSMPhone;
import com.android.internal.telephony.gsm.SmsMessage;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import java.util.ArrayList;
import java.util.HashMap;

// Referenced classes of package com.movial.ipphone:
//            IPPhone, IIPService, IPSmsMessage, IPManager

public class IPSMSDispatcher extends SMSDispatcher
{
    class IPSmsTracker
    {

        void send(int i)
        {
            mSwitcher.obj = mSmsTracker;
            Log.i("IPSMSDispatcher", (new StringBuilder()).append("serial = ").append(Serial).append(" error = ").append(i).toString());
            SmsResponse smsresponse;
            CommandException commandexception;
            if(i != 0)
            {
                smsresponse = new SmsResponse(Serial, "test PDU", 20);
                commandexception = CommandException.fromRilErrno(2);
            } else
            {
                smsresponse = new SmsResponse(Serial, "test PDU", 0);
                commandexception = null;
            }
            AsyncResult.forMessage(mSwitcher, smsresponse, commandexception);
            mSwitcher.sendToTarget();
        }

        public int Serial;
        private com.android.internal.telephony.SMSDispatcher.SmsTracker mSmsTracker;
        private Message mSwitcher;
        final IPSMSDispatcher this$0;

        IPSmsTracker(com.android.internal.telephony.SMSDispatcher.SmsTracker smstracker, Message message, int i)
        {
            this$0 = IPSMSDispatcher.this;
            super();
            mSmsTracker = smstracker;
            Serial = i;
            mSwitcher = message;
        }
    }


    public IPSMSDispatcher(PhoneBase phonebase, SmsStorageMonitor smsstoragemonitor, SmsUsageMonitor smsusagemonitor)
    {
        super(phonebase, smsstoragemonitor, smsusagemonitor);
        useIPPhone = false;
        mUiccController = null;
        mUiccApplication = null;
        mIccRecords = null;
        mTrackerList = new ArrayList();
        mReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent)
            {
                useIPPhone = intent.getBooleanExtra("IMS_REG_STATUS", false);
                Log.d("IPSMSDispatcher", (new StringBuilder()).append("onReceive: ").append(useIPPhone).toString());
                if(!useIPPhone)
                    break MISSING_BLOCK_LABEL_100;
                mIPService = mIPPhone.getService();
                mIPService.registerForIncomingSMS(new Messenger(mIPSmsTrackerHandler));
                return;
                Exception exception;
                exception;
                Log.e("IPSMSDispatcher", (new StringBuilder()).append("Register for incoming SMS failed: ").append(exception).toString());
                return;
            }

            final IPSMSDispatcher this$0;

            
            {
                this$0 = IPSMSDispatcher.this;
                super();
            }
        };
        mIPSmsTrackerHandler = new Handler() {

            public void handleMessage(Message message)
            {
                Log.i("IPSMSDispatcher", (new StringBuilder()).append("Handler Message Received: ").append(message.what).toString());
                Bundle bundle;
                switch(message.what)
                {
                default:
                    return;

                case 0: // '\0'
                    findAndRemoveTrackerWithSerial(message.arg1).send(message.arg2);
                    return;

                case 1: // '\001'
                    bundle = message.getData();
                    break;
                }
                bundle.setClassLoader(com/movial/ipphone/IPSMSDispatcher.getClassLoader());
                String as[] = new String[2];
                as[1] = IccUtils.bytesToHexString(bundle.getByteArray("pdu"));
                IPSmsMessage ipsmsmessage = IPSmsMessage.newFromCMT(as);
                if(ipsmsmessage.isStatusReportMessage())
                {
                    Message message2 = obtainMessage(100);
                    message2.obj = new AsyncResult(null, as[1], null);
                    message2.sendToTarget();
                    return;
                } else
                {
                    Message message1 = obtainMessage(1);
                    String as1[] = new String[2];
                    as1[1] = "039188F804039188F80000208062917314080CC8F71D14969741F977FD07";
                    android.telephony.SmsMessage smsmessage = android.telephony.SmsMessage.newFromCMT(as1);
                    smsmessage.mWrappedSmsMessage = ipsmsmessage;
                    message1.obj = new AsyncResult(null, smsmessage, null);
                    message1.sendToTarget();
                    return;
                }
            }

            final IPSMSDispatcher this$0;

            
            {
                this$0 = IPSMSDispatcher.this;
                super();
            }
        };
        Log.i("IPSMSDispatcher", "IPSMSDispatcher initialization");
        mIPPhone = (IPPhone)phonebase;
        mGsmPhone = (GSMPhone)mIPPhone.getGsmPhone();
        mGsmPhone.getContext().registerReceiver(mReceiver, new IntentFilter("IMS_REGISTRATION"));
        mUiccController = UiccController.getInstance();
        mUiccController.registerForIccChanged(this, 12, null);
    }

    private IPSmsTracker findAndRemoveTrackerWithSerial(int i)
    {
        for(int j = 0; j < mTrackerList.size(); j++)
            if(((IPSmsTracker)mTrackerList.get(j)).Serial == i)
            {
                IPSmsTracker ipsmstracker = (IPSmsTracker)mTrackerList.get(j);
                mTrackerList.remove(j);
                return ipsmstracker;
            }

        return null;
    }

    private void handleStatusReport(AsyncResult asyncresult)
    {
        String s;
        SmsMessage smsmessage;
        s = (String)asyncresult.result;
        smsmessage = SmsMessage.newFromCDS(s);
        if(smsmessage == null) goto _L2; else goto _L1
_L1:
        int i;
        int j;
        int k;
        int l;
        i = smsmessage.getStatus();
        j = smsmessage.messageRef;
        Log.i("IPSMSDispatcher", (new StringBuilder()).append("Received SMS StatusReport with MR = ").append(j).toString());
        k = 0;
        l = deliveryPendingList.size();
_L7:
        if(k >= l) goto _L2; else goto _L3
_L3:
        com.android.internal.telephony.SMSDispatcher.SmsTracker smstracker = (com.android.internal.telephony.SMSDispatcher.SmsTracker)deliveryPendingList.get(k);
        if(smstracker.mMessageRef != j) goto _L5; else goto _L4
_L4:
        if(i >= 64 || i < 32)
            deliveryPendingList.remove(k);
        PendingIntent pendingintent = smstracker.mDeliveryIntent;
        Intent intent = new Intent();
        intent.putExtra("pdu", IccUtils.hexStringToBytes(s));
        intent.putExtra("format", "3gpp");
        try
        {
            pendingintent.send(mContext, -1, intent);
        }
        catch(android.app.PendingIntent.CanceledException canceledexception) { }
_L2:
        acknowledgeLastIncomingSms(true, 1, null);
        return;
_L5:
        k++;
        if(true) goto _L7; else goto _L6
_L6:
    }

    private static int resultToCause(int i)
    {
        switch(i)
        {
        case 0: // '\0'
        case 2: // '\002'
        default:
            return 255;

        case -1: 
        case 1: // '\001'
            return 0;

        case 3: // '\003'
            return 211;

        case 4: // '\004'
            return 145;
        }
    }

    protected void acknowledgeLastIncomingSms(boolean flag, int i, Message message)
    {
        Log.i("IPSMSDispatcher", (new StringBuilder()).append("sending ack success:").append(flag).append(" result:").append(i).toString());
        if(!useIPPhone)
            break MISSING_BLOCK_LABEL_86;
        mIPService.acknowledgeLastIncomingIpSms(flag, resultToCause(i));
_L1:
        return;
        RemoteException remoteexception;
        remoteexception;
        Log.e("IPSMSDispatcher", (new StringBuilder()).append("acknowledgeLastIncomingSms failed: ").append(remoteexception).toString());
        return;
        if(mCm != null)
        {
            mCm.acknowledgeLastIncomingGsmSms(flag, resultToCause(i), message);
            return;
        }
          goto _L1
    }

    protected com.android.internal.telephony.SmsMessageBase.TextEncodingDetails calculateLength(CharSequence charsequence, boolean flag)
    {
        return SmsMessage.calculateLength(charsequence, flag);
    }

    protected void clearDuplicatedCbMessages()
    {
    }

    protected void dispatchBroadcastPdus(byte abyte0[][], boolean flag)
    {
        if(flag)
        {
            Intent intent = new Intent("android.provider.Telephony.SMS_EMERGENCY_CB_RECEIVED");
            intent.putExtra("pdus", abyte0);
            Log.d("IPSMSDispatcher", (new StringBuilder()).append("Dispatching ").append(abyte0.length).append(" emergency SMS CB pdus").toString());
            dispatch(intent, "android.permission.RECEIVE_EMERGENCY_BROADCAST");
            return;
        } else
        {
            Intent intent1 = new Intent("android.provider.Telephony.SMS_CB_RECEIVED");
            intent1.putExtra("pdus", abyte0);
            Log.d("IPSMSDispatcher", (new StringBuilder()).append("Dispatching ").append(abyte0.length).append(" SMS CB pdus").toString());
            dispatch(intent1, "android.permission.RECEIVE_SMS");
            return;
        }
    }

    public int dispatchMessage(SmsMessageBase smsmessagebase)
    {
        byte byte0 = 1;
        if(smsmessagebase != null) goto _L2; else goto _L1
_L1:
        Log.e("IPSMSDispatcher", "dispatchMessage: message is null");
        byte0 = 2;
_L5:
        return byte0;
_L2:
        IPSmsMessage ipsmsmessage;
        ipsmsmessage = (IPSmsMessage)smsmessagebase;
        if(ipsmsmessage.getMessageClass() == android.telephony.SmsMessage.MessageClass.CLASS_2)
        {
            Log.i("IPSMSDispatcher", "CLASS2 SMS not supported");
            return 4;
        }
        if(ipsmsmessage.isTypeZero())
        {
            Log.d("IPSMSDispatcher", "Received short message type 0, Don't display or store it. Send Ack");
            return byte0;
        }
        if(mSmsReceiveDisabled)
        {
            Log.d("IPSMSDispatcher", "Received short message on device which doesn't support SMS service. Ignored.");
            return byte0;
        }
        if(!ipsmsmessage.isMWISetMessage()) goto _L4; else goto _L3
_L3:
        boolean flag1;
        updateMessageWaitingIndicator(-1);
        flag1 = ipsmsmessage.isMwiDontStore();
_L6:
        if(!flag1)
            if(!mStorageMonitor.isStorageAvailable() && ipsmsmessage.getMessageClass() != android.telephony.SmsMessage.MessageClass.CLASS_0)
                return 3;
            else
                return dispatchNormalMessage(smsmessagebase);
        if(true) goto _L5; else goto _L4
_L4:
        boolean flag = ipsmsmessage.isMWIClearMessage();
        flag1 = false;
        if(flag)
        {
            updateMessageWaitingIndicator(0);
            flag1 = ipsmsmessage.isMwiDontStore();
        }
          goto _L6
    }

    public void dispose()
    {
        mUiccController.unregisterForIccChanged(this);
    }

    protected String getFormat()
    {
        return "3gpp";
    }

    protected UiccCardApplication getUiccCardApplication()
    {
        return mUiccController.getUiccCardApplication(1);
    }

    protected void handleGetIccSmsDone(AsyncResult asyncresult)
    {
        Log.d("IPSMSDispatcher", "handleGetIccSmsDone function is not applicable for IP");
    }

    public void handleMessage(Message message)
    {
        AsyncResult asyncresult;
        switch(message.what)
        {
        default:
            super.handleMessage(message);
            return;

        case 100: // 'd'
            handleStatusReport((AsyncResult)message.obj);
            return;

        case 101: // 'e'
            Log.e("IPSMSDispatcher", "CB SMS not supported");
            return;

        case 102: // 'f'
            asyncresult = (AsyncResult)message.obj;
            break;
        }
        if(asyncresult.exception == null)
        {
            Log.d("IPSMSDispatcher", "Successfully wrote SMS-PP message to UICC");
            mCm.acknowledgeLastIncomingGsmSms(true, 0, null);
            return;
        } else
        {
            Log.d("IPSMSDispatcher", "Failed to write SMS-PP message to UICC", asyncresult.exception);
            mCm.acknowledgeLastIncomingGsmSms(false, 255, null);
            return;
        }
    }

    protected void handleSmsOnIcc(AsyncResult asyncresult)
    {
        Log.d("IPSMSDispatcher", "handleSmsOnIcc function is not applicable for IP");
    }

    protected boolean isDuplicatedSms(SmsMessageBase smsmessagebase)
    {
        Log.d("IPSMSDispatcher", "isDuplicatedSms function is not applicable for IP");
        return false;
    }

    protected void kddiDispatchPdus(byte abyte0[][], SmsMessageBase smsmessagebase)
    {
    }

    protected void sendData(String s, String s1, int i, byte abyte0[], PendingIntent pendingintent, PendingIntent pendingintent1)
    {
        boolean flag;
        com.android.internal.telephony.gsm.SmsMessage.SubmitPdu submitpdu;
        if(pendingintent1 != null)
            flag = true;
        else
            flag = false;
        submitpdu = IPSmsMessage.getSubmitPdu(s1, s, i, abyte0, flag);
        if(submitpdu != null)
        {
            sendRawPdu(SmsTrackerFactory(SmsTrackerMapFactory(s, s1, i, abyte0, submitpdu), pendingintent, pendingintent1, getFormat()));
            return;
        } else
        {
            Log.e("IPSMSDispatcher", "GsmSMSDispatcher.sendData(): getSubmitPdu() returned null");
            return;
        }
    }

    protected void sendMultipartText(String s, String s1, ArrayList arraylist, ArrayList arraylist1, ArrayList arraylist2, String s2, int i)
    {
        int j = 0xff & getNextConcatenatedRef();
        int k = arraylist.size();
        int l = 0;
        mRemainingMessages = k;
        com.android.internal.telephony.SmsMessageBase.TextEncodingDetails atextencodingdetails[] = new com.android.internal.telephony.SmsMessageBase.TextEncodingDetails[k];
        for(int i1 = 0; i1 < k; i1++)
        {
            com.android.internal.telephony.SmsMessageBase.TextEncodingDetails textencodingdetails = SmsMessage.calculateLength((CharSequence)arraylist.get(i1), false);
            if(l != textencodingdetails.codeUnitSize && (l == 0 || l == 1))
                l = textencodingdetails.codeUnitSize;
            atextencodingdetails[i1] = textencodingdetails;
        }

        int j1 = 0;
        while(j1 < k) 
        {
            com.android.internal.telephony.SmsHeader.ConcatRef concatref = new com.android.internal.telephony.SmsHeader.ConcatRef();
            concatref.refNumber = j;
            concatref.seqNumber = j1 + 1;
            concatref.msgCount = k;
            concatref.isEightBits = true;
            SmsHeader smsheader = new SmsHeader();
            smsheader.concatRef = concatref;
            if(l == 1)
            {
                smsheader.languageTable = atextencodingdetails[j1].languageTable;
                smsheader.languageShiftTable = atextencodingdetails[j1].languageShiftTable;
            }
            PendingIntent pendingintent = null;
            if(arraylist1 != null)
            {
                int i2 = arraylist1.size();
                int j2 = j1;
                pendingintent = null;
                if(i2 > j2)
                    pendingintent = (PendingIntent)arraylist1.get(j1);
            }
            PendingIntent pendingintent1 = null;
            if(arraylist2 != null)
            {
                int k1 = arraylist2.size();
                int l1 = j1;
                pendingintent1 = null;
                if(k1 > l1)
                    pendingintent1 = (PendingIntent)arraylist2.get(j1);
            }
            String s3 = (String)arraylist.get(j1);
            boolean flag;
            com.android.internal.telephony.gsm.SmsMessage.SubmitPdu submitpdu;
            HashMap hashmap;
            String s4;
            if(pendingintent1 != null)
                flag = true;
            else
                flag = false;
            submitpdu = SmsMessage.getSubmitPdu(s1, s, s3, flag, SmsHeader.toByteArray(smsheader), l, smsheader.languageTable, smsheader.languageShiftTable);
            hashmap = SmsTrackerMapFactory(s, s1, (String)arraylist.get(j1), submitpdu);
            s4 = getFormat();
            sendSms(SmsTrackerFactory(hashmap, pendingintent, pendingintent1, s4));
            j1++;
        }
    }

    protected void sendMultipartTextwithOptions(String s, String s1, ArrayList arraylist, ArrayList arraylist1, ArrayList arraylist2, boolean flag, int i, 
            int j, int k)
    {
        sendMultipartText(s, s1, arraylist, arraylist1, arraylist2);
    }

    protected void sendNewSubmitPdu(String s, String s1, String s2, SmsHeader smsheader, int i, PendingIntent pendingintent, PendingIntent pendingintent1, 
            boolean flag)
    {
        boolean flag1;
        com.android.internal.telephony.gsm.SmsMessage.SubmitPdu submitpdu;
        if(pendingintent1 != null)
            flag1 = true;
        else
            flag1 = false;
        submitpdu = SmsMessage.getSubmitPdu(s1, s, s2, flag1, SmsHeader.toByteArray(smsheader), i, smsheader.languageTable, smsheader.languageShiftTable);
        if(submitpdu != null)
        {
            sendRawPdu(SmsTrackerFactory(SmsTrackerMapFactory(s, s1, s2, submitpdu), pendingintent, pendingintent1, getFormat()));
            return;
        } else
        {
            Log.e("IPSMSDispatcher", "GsmSMSDispatcher.sendNewSubmitPdu(): getSubmitPdu() returned null");
            return;
        }
    }

    protected void sendOTADomestic(String s, String s1, String s2)
    {
        Log.d("IPSMSDispatcher", "sendOTADomestic");
    }

    public void sendRetrySms(com.android.internal.telephony.SMSDispatcher.SmsTracker smstracker)
    {
        Log.e("IPSMSDispatcher", "Error, sendRetrySms, should not be here");
    }

    protected void sendSms(com.android.internal.telephony.SMSDispatcher.SmsTracker smstracker)
    {
        HashMap hashmap = smstracker.mData;
        byte[] _tmp = (byte[])(byte[])hashmap.get("smsc");
        byte abyte0[] = (byte[])(byte[])hashmap.get("pdu");
        String s = PhoneNumberUtils.calledPartyBCDToString(abyte0, 3, (3 + abyte0[2]) / 2);
        IPSmsTracker ipsmstracker = new IPSmsTracker(smstracker, obtainMessage(2, smstracker), IPSmsMessage.getPreviousMessageReference());
        mTrackerList.add(ipsmstracker);
        Messenger messenger = new Messenger(mIPSmsTrackerHandler);
        String s1 = IccUtils.bytesToHexString(abyte0);
        try
        {
            mIPService.sendSMS(s, s1, messenger, ipsmstracker.Serial);
            return;
        }
        catch(Exception exception)
        {
            Log.e("IPSMSDispatcher", (new StringBuilder()).append("SendSMS failed: ").append(exception).toString());
        }
    }

    protected void sendText(String s, String s1, String s2, PendingIntent pendingintent, PendingIntent pendingintent1)
    {
        boolean flag;
        com.android.internal.telephony.gsm.SmsMessage.SubmitPdu submitpdu;
        if(pendingintent1 != null)
            flag = true;
        else
            flag = false;
        submitpdu = IPSmsMessage.getSubmitPdu(s1, s, s2, flag);
        if(submitpdu != null)
        {
            if(s1 == null)
                s1 = Sim_Smsc;
            sendRawPdu(SmsTrackerFactory(SmsTrackerMapFactory(s, s1, s2, submitpdu), pendingintent, pendingintent1, getFormat()));
            return;
        } else
        {
            Log.e("IPSMSDispatcher", "GsmSMSDispatcher.sendText(): getSubmitPdu() returned null");
            return;
        }
    }

    protected void sendText(String s, String s1, String s2, PendingIntent pendingintent, PendingIntent pendingintent1, String s3, int i)
    {
        sendText(s, s1, s2, pendingintent, pendingintent1);
    }

    protected void sendTextwithOptions(String s, String s1, String s2, PendingIntent pendingintent, PendingIntent pendingintent1, boolean flag, int i, 
            int j, int k)
    {
        sendText(s, s1, s2, pendingintent, pendingintent1);
    }

    protected void sendTextwithOptions(String s, String s1, String s2, PendingIntent pendingintent, PendingIntent pendingintent1, boolean flag, int i, 
            int j, int k, int l)
    {
        sendText(s, s1, s2, pendingintent, pendingintent1);
    }

    protected void sendscptResult(String s, int i, int j, int k, int l, PendingIntent pendingintent, PendingIntent pendingintent1)
    {
        Log.d("IPSMSDispatcher", "SCPT-submit pdu is null");
    }

    protected void updateIccAvailability()
    {
        UiccCardApplication uicccardapplication;
        if(mUiccController != null)
            if((uicccardapplication = getUiccCardApplication()) != null && mUiccApplication != uicccardapplication)
            {
                if(mUiccApplication != null)
                {
                    Log.d("IPSMSDispatcher", "Removing stale icc objects.");
                    mIccRecords = null;
                    mUiccApplication = null;
                }
                if(uicccardapplication != null)
                {
                    Log.d("IPSMSDispatcher", "New Uicc application found");
                    mUiccApplication = uicccardapplication;
                    mIccRecords = mUiccApplication.getIccRecords();
                    return;
                }
            }
    }

    void updateMessageWaitingIndicator(int i)
    {
        if(i < 0)
            i = -1;
        else
        if(i > 255)
            i = 255;
        mGsmPhone.setVoiceMessageCount(i);
        if(mIccRecords != null)
        {
            Message message = obtainMessage(22);
            mIccRecords.setVoiceMessageWaiting(1, i, message);
            return;
        } else
        {
            Log.d("IPSMSDispatcher", "SIM Records not found, MWI not updated");
            return;
        }
    }

    private static final int EVENT_NEW_BROADCAST_SMS = 101;
    private static final int EVENT_NEW_SMS_STATUS_REPORT = 100;
    private static final int EVENT_WRITE_SMS_COMPLETE = 102;
    public static final int MSG_IP_NEW_SMS = 1;
    public static final int MSG_IP_SMS_SENT = 0;
    private static final String TAG = "IPSMSDispatcher";
    private GSMPhone mGsmPhone;
    private IPManager mIPManager;
    private IPPhone mIPPhone;
    private IIPService mIPService;
    private Handler mIPSmsTrackerHandler;
    protected IccRecords mIccRecords;
    private BroadcastReceiver mReceiver;
    private ArrayList mTrackerList;
    protected UiccCardApplication mUiccApplication;
    protected UiccController mUiccController;
    private boolean useIPPhone;



/*
    static boolean access$002(IPSMSDispatcher ipsmsdispatcher, boolean flag)
    {
        ipsmsdispatcher.useIPPhone = flag;
        return flag;
    }

*/



/*
    static IIPService access$102(IPSMSDispatcher ipsmsdispatcher, IIPService iipservice)
    {
        ipsmsdispatcher.mIPService = iipservice;
        return iipservice;
    }

*/



}
