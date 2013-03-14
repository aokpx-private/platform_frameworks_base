// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.Context;
import android.os.*;
import android.telephony.PhoneNumberUtils;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.*;
import com.android.internal.telephony.gsm.GSMPhone;
import com.android.internal.telephony.uicc.IccRecords;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.movial.ipphone:
//            IPPhone, IPPhoneSettings

public final class IPMmiCode extends Handler
    implements MmiCode
{

    IPMmiCode(IPPhone ipphone)
    {
        state = com.android.internal.telephony.MmiCode.State.PENDING;
        phone = ipphone;
        context = ipphone.getContext();
    }

    private CharSequence createQueryCallBarringResultMessage(int i)
    {
        StringBuilder stringbuilder = new StringBuilder(context.getText(0x10400f4));
        for(int j = 1; j <= 128; j <<= 1)
            if((j & i) != 0)
            {
                stringbuilder.append("\n");
                stringbuilder.append(serviceClassToCFString(j & i));
            }

        return stringbuilder;
    }

    private CharSequence createQueryCallWaitingResultMessage(int i)
    {
        StringBuilder stringbuilder = new StringBuilder(context.getText(0x10400f4));
        for(int j = 1; j <= 128; j <<= 1)
            if((j & i) != 0)
            {
                stringbuilder.append("\n");
                stringbuilder.append(serviceClassToCFString(j & i));
            }

        return stringbuilder;
    }

    private CharSequence getScString()
    {
        if(sc != null)
        {
            if(isServiceCodeCallBarring(sc))
                return context.getText(0x104010c);
            if(isServiceCodeCallForwarding(sc))
                return context.getText(0x104010a);
            if(sc.equals("30"))
                return context.getText(0x1040106);
            if(sc.equals("31"))
                return context.getText(0x1040107);
            if(sc.equals("03"))
                return context.getText(0x104010d);
            if(sc.equals("43"))
                return context.getText(0x104010b);
            if(isPinCommand())
                return context.getText(0x104010e);
        }
        return "";
    }

    private void handlePasswordError(int i)
    {
        state = com.android.internal.telephony.MmiCode.State.FAILED;
        StringBuilder stringbuilder = new StringBuilder(getScString());
        stringbuilder.append("\n");
        stringbuilder.append(context.getText(i));
        message = stringbuilder;
        phone.onMMIDone(this);
    }

    private static boolean isEmptyOrNull(CharSequence charsequence)
    {
        return charsequence == null || charsequence.length() == 0;
    }

    static boolean isServiceCodeCallBarring(String s)
    {
        return s != null && (s.equals("33") || s.equals("331") || s.equals("332") || s.equals("35") || s.equals("351") || s.equals("330") || s.equals("333") || s.equals("353"));
    }

    static boolean isServiceCodeCallForwarding(String s)
    {
        return s != null && (s.equals("21") || s.equals("67") || s.equals("61") || s.equals("62") || s.equals("002") || s.equals("004"));
    }

    private static boolean isShortCode(String s, IPPhone ipphone)
    {
        boolean flag;
label0:
        {
            flag = false;
            if(s == null)
                break label0;
            int i = s.length();
            flag = false;
            if(i > 2)
                break label0;
            boolean flag1 = PhoneNumberUtils.isEmergencyNumber(s);
            flag = false;
            if(flag1)
                break label0;
            if(!ipphone.isInCall())
            {
                if(s.length() == 2)
                {
                    char c = s.charAt(0);
                    flag = false;
                    if(c == '1')
                        break label0;
                }
                boolean flag2 = s.equals("0");
                flag = false;
                if(flag2)
                    break label0;
                boolean flag3 = s.equals("00");
                flag = false;
                if(flag3)
                    break label0;
            }
            flag = true;
        }
        return flag;
    }

    private CharSequence makeCFQueryResultMessage(CallForwardInfo callforwardinfo, int i)
    {
        String as[] = {
            "{0}", "{1}", "{2}"
        };
        CharSequence acharsequence[] = new CharSequence[3];
        boolean flag;
        CharSequence charsequence;
        if(callforwardinfo.reason == 2)
            flag = true;
        else if
            flag = false;
        if(callforwardinfo.status == 1)
        {
            if(flag)
                charsequence = context.getText(0x1040160);
            else if
                charsequence = context.getText(0x104015f);
        } else if
        if(callforwardinfo.status == 0 && isEmptyOrNull(callforwardinfo.number))
            charsequence = context.getText(0x104015e);
        else if
        if(flag)
            charsequence = context.getText(0x1040162);
        else if
            charsequence = context.getText(0x1040161);
        acharsequence[0] = serviceClassToCFString(i & callforwardinfo.serviceClass);
        acharsequence[1] = PhoneNumberUtils.stringFromStringAndTOA(callforwardinfo.number, callforwardinfo.toa);
        acharsequence[2] = Integer.toString(callforwardinfo.timeSeconds);
        if(callforwardinfo.reason == 0 && (i & callforwardinfo.serviceClass) == 1)
            if(callforwardinfo.status != 1);
        return TextUtils.replace(charsequence, as, acharsequence);
    }

    private static String makeEmptyNull(String s)
    {
        if(s != null && s.length() == 0)
            s = null;
        return s;
    }

    static IPMmiCode newFromDialString(String s, IPPhone ipphone)
    {
        Matcher matcher = sPatternSuppService.matcher(s);
        IPMmiCode ipmmicode;
        if(matcher.matches())
        {
            ipmmicode = new IPMmiCode(ipphone);
            ipmmicode.poundString = makeEmptyNull(matcher.group(1));
            ipmmicode.action = makeEmptyNull(matcher.group(2));
            ipmmicode.sc = makeEmptyNull(matcher.group(3));
            ipmmicode.sia = makeEmptyNull(matcher.group(5));
            ipmmicode.sib = makeEmptyNull(matcher.group(7));
            ipmmicode.sic = makeEmptyNull(matcher.group(9));
            ipmmicode.pwd = makeEmptyNull(matcher.group(11));
            ipmmicode.dialingNumber = makeEmptyNull(matcher.group(12));
        } else if
        {
            if(s.endsWith("#"))
            {
                IPMmiCode ipmmicode1 = new IPMmiCode(ipphone);
                ipmmicode1.poundString = s;
                return ipmmicode1;
            }
            boolean flag = isShortCode(s, ipphone);
            ipmmicode = null;
            if(flag)
            {
                IPMmiCode ipmmicode2 = new IPMmiCode(ipphone);
                ipmmicode2.dialingNumber = s;
                return ipmmicode2;
            }
        }
        return ipmmicode;
    }

    static IPMmiCode newFromUssdUserInput(String s, IPPhone ipphone)
    {
        IPMmiCode ipmmicode = new IPMmiCode(ipphone);
        ipmmicode.message = s;
        ipmmicode.state = com.android.internal.telephony.MmiCode.State.PENDING;
        ipmmicode.isPendingUSSD = true;
        return ipmmicode;
    }

    static IPMmiCode newNetworkInitiatedUssd(String s, boolean flag, IPPhone ipphone)
    {
        IPMmiCode ipmmicode = new IPMmiCode(ipphone);
        ipmmicode.message = s;
        ipmmicode.isUssdRequest = flag;
        if(flag)
        {
            ipmmicode.isPendingUSSD = true;
            ipmmicode.state = com.android.internal.telephony.MmiCode.State.PENDING;
            return ipmmicode;
        } else if
        {
            ipmmicode.state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            return ipmmicode;
        }
    }

    private void onGetClirComplete(AsyncResult asyncresult)
    {
        StringBuilder stringbuilder;
        stringbuilder = new StringBuilder(getScString());
        stringbuilder.append("\n");
        if(asyncresult.exception == null) goto _L2; else if goto _L1
_L1:
        state = com.android.internal.telephony.MmiCode.State.FAILED;
        stringbuilder.append(context.getText(0x10400ea));
_L17:
        message = stringbuilder;
        phone.onMMIDone(this);
        return;
_L2:
        int ai[] = (int[])(int[])asyncresult.result;
        ai[1];
        JVM INSTR tableswitch 0 4: default 112
    //                   0 115
    //                   1 140
    //                   2 165
    //                   3 190
    //                   4 277;
           goto _L3 _L4 _L5 _L6 _L7 _L8
_L3:
        continue; /* Loop/switch isn't completed */
_L4:
        stringbuilder.append(context.getText(0x104011b));
        state = com.android.internal.telephony.MmiCode.State.COMPLETE;
        continue; /* Loop/switch isn't completed */
_L5:
        stringbuilder.append(context.getText(0x104011c));
        state = com.android.internal.telephony.MmiCode.State.COMPLETE;
        continue; /* Loop/switch isn't completed */
_L6:
        stringbuilder.append(context.getText(0x10400ea));
        state = com.android.internal.telephony.MmiCode.State.FAILED;
        continue; /* Loop/switch isn't completed */
_L7:
        ai[0];
        JVM INSTR tableswitch 1 2: default 216
    //                   1 241
    //                   2 259;
           goto _L9 _L10 _L11
_L9:
        stringbuilder.append(context.getText(0x1040117));
_L12:
        state = com.android.internal.telephony.MmiCode.State.COMPLETE;
        continue; /* Loop/switch isn't completed */
_L10:
        stringbuilder.append(context.getText(0x1040117));
          goto _L12
_L11:
        stringbuilder.append(context.getText(0x1040118));
          goto _L12
_L8:
        ai[0];
        JVM INSTR tableswitch 1 2: default 304
    //                   1 329
    //                   2 347;
           goto _L13 _L14 _L15
_L14:
        break; /* Loop/switch isn't completed */
_L13:
        stringbuilder.append(context.getText(0x104011a));
_L18:
        state = com.android.internal.telephony.MmiCode.State.COMPLETE;
        if(true) goto _L17; else if goto _L16
_L16:
        stringbuilder.append(context.getText(0x1040119));
          goto _L18
_L15:
        stringbuilder.append(context.getText(0x104011a));
          goto _L18
    }

    private void onQueryCfComplete(AsyncResult asyncresult)
    {
        StringBuilder stringbuilder;
        stringbuilder = new StringBuilder(getScString());
        stringbuilder.append("\n");
        if(asyncresult.exception == null) goto _L2; else if goto _L1
_L1:
        state = com.android.internal.telephony.MmiCode.State.FAILED;
        stringbuilder.append(context.getText(0x10400ea));
_L4:
        message = stringbuilder;
        phone.onMMIDone(this);
        return;
_L2:
        CallForwardInfo acallforwardinfo[];
        acallforwardinfo = (CallForwardInfo[])(CallForwardInfo[])asyncresult.result;
        if(acallforwardinfo.length != 0)
            break; /* Loop/switch isn't completed */
        stringbuilder.append(context.getText(0x10400f5));
_L5:
        state = com.android.internal.telephony.MmiCode.State.COMPLETE;
        if(true) goto _L4; else if goto _L3
_L3:
        SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder();
        for(int i = 1; i <= 128; i <<= 1)
        {
            int j = 0;
            for(int k = acallforwardinfo.length; j < k; j++)
                if((i & acallforwardinfo[j].serviceClass) != 0)
                {
                    spannablestringbuilder.append(makeCFQueryResultMessage(acallforwardinfo[j], i));
                    spannablestringbuilder.append("\n");
                }

        }

        stringbuilder.append(spannablestringbuilder);
          goto _L5
        if(true) goto _L4; else if goto _L6
_L6:
    }

    private void onQueryComplete(AsyncResult asyncresult)
    {
        StringBuilder stringbuilder = new StringBuilder(getScString());
        stringbuilder.append("\n");
        if(asyncresult.exception != null)
        {
            state = com.android.internal.telephony.MmiCode.State.FAILED;
            stringbuilder.append(context.getText(0x10400ea));
        } else if
        {
            int ai[] = (int[])(int[])asyncresult.result;
            if(ai.length != 0)
            {
                if(ai[0] == 0)
                    stringbuilder.append(context.getText(0x10400f5));
                else if
                if(sc.equals("43"))
                    stringbuilder.append(createQueryCallWaitingResultMessage(ai[1]));
                else if
                if(isServiceCodeCallBarring(sc))
                    stringbuilder.append(createQueryCallBarringResultMessage(ai[0]));
                else if
                if(ai[0] == 1)
                    stringbuilder.append(context.getText(0x10400f3));
                else if
                    stringbuilder.append(context.getText(0x10400ea));
            } else if
            {
                stringbuilder.append(context.getText(0x10400ea));
            }
            state = com.android.internal.telephony.MmiCode.State.COMPLETE;
        }
        message = stringbuilder;
        phone.onMMIDone(this);
    }

    private void onSetComplete(AsyncResult asyncresult)
    {
        StringBuilder stringbuilder = new StringBuilder(getScString());
        stringbuilder.append("\n");
        if(asyncresult.exception != null)
        {
            state = com.android.internal.telephony.MmiCode.State.FAILED;
            if(asyncresult.exception instanceof CommandException)
            {
                com.android.internal.telephony.CommandException.Error error = ((CommandException)(CommandException)asyncresult.exception).getCommandError();
                if(error == com.android.internal.telephony.CommandException.Error.PASSWORD_INCORRECT)
                {
                    if(isPinCommand())
                    {
                        if(sc.equals("05") || sc.equals("052"))
                            stringbuilder.append(context.getText(0x10400fb));
                        else if
                            stringbuilder.append(context.getText(0x10400fa));
                    } else if
                    {
                        stringbuilder.append(context.getText(0x10400f8));
                    }
                } else if
                if(error == com.android.internal.telephony.CommandException.Error.SIM_PUK2)
                {
                    stringbuilder.append(context.getText(0x10400fa));
                    stringbuilder.append("\n");
                    stringbuilder.append(context.getText(0x1040100));
                } else if
                {
                    stringbuilder.append(context.getText(0x10400ea));
                }
            } else if
            {
                stringbuilder.append(context.getText(0x10400ea));
            }
        } else if
        if(isActivate())
        {
            state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            stringbuilder.append(context.getText(0x10400f3));
            if(!sc.equals("31"));
        } else if
        if(isDeactivate())
        {
            state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            stringbuilder.append(context.getText(0x10400f5));
            if(!sc.equals("31"));
        } else if
        if(isRegister())
        {
            state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            stringbuilder.append(context.getText(0x10400f6));
        } else if
        if(isErasure())
        {
            state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            stringbuilder.append(context.getText(0x10400f7));
        } else if
        {
            state = com.android.internal.telephony.MmiCode.State.FAILED;
            stringbuilder.append(context.getText(0x10400ea));
        }
        message = stringbuilder;
        phone.onMMIDone(this);
    }

    static String scToBarringFacility(String s)
    {
        if(s == null)
            throw new RuntimeException("invalid call barring sc");
        if(s.equals("33"))
            return "AO";
        if(s.equals("331"))
            return "OI";
        if(s.equals("332"))
            return "OX";
        if(s.equals("35"))
            return "AI";
        if(s.equals("351"))
            return "IR";
        if(s.equals("330"))
            return "AB";
        if(s.equals("333"))
            return "AG";
        if(s.equals("353"))
            return "AC";
        else if
            throw new RuntimeException("invalid call barring sc");
    }

    private static int scToCallForwardReason(String s)
    {
        if(s == null)
            throw new RuntimeException("invalid call forward sc");
        if(s.equals("002"))
            return 4;
        if(s.equals("21"))
            return 0;
        if(s.equals("67"))
            return 1;
        if(s.equals("62"))
            return 3;
        if(s.equals("61"))
            return 2;
        if(s.equals("004"))
            return 5;
        else if
            throw new RuntimeException("invalid call forward sc");
    }

    private CharSequence serviceClassToCFString(int i)
    {
        switch(i)
        {
        default:
            return null;

        case 1: // '\001'
            return context.getText(0x1040126);

        case 2: // '\002'
            return context.getText(0x1040127);

        case 4: // '\004'
            return context.getText(0x1040128);

        case 8: // '\b'
            return context.getText(0x1040129);

        case 16: // '\020'
            return context.getText(0x104012b);

        case 32: // ' '
            return context.getText(0x104012a);

        case 64: // '@'
            return context.getText(0x104012c);

        case 128: 
            return context.getText(0x104012d);
        }
    }

    private static int siToServiceClass(String s)
    {
        if(s == null || s.length() == 0)
            return 0;
        switch(Integer.parseInt(s, 10))
        {
        default:
            throw new RuntimeException((new StringBuilder()).append("unsupported MMI service code ").append(s).toString());

        case 10: // '\n'
            return 13;

        case 11: // '\013'
            return 1;

        case 12: // '\f'
            return 12;

        case 13: // '\r'
            return 4;

        case 16: // '\020'
            return 8;

        case 19: // '\023'
            return 5;

        case 20: // '\024'
            return 48;

        case 21: // '\025'
            return 160;

        case 22: // '\026'
            return 80;

        case 24: // '\030'
            return 16;

        case 25: // '\031'
            return 32;

        case 26: // '\032'
            return 17;

        case 99: // 'c'
            return 64;
        }
    }

    private static int siToTime(String s)
    {
        if(s == null || s.length() == 0)
            return 0;
        else if
            return Integer.parseInt(s, 10);
    }

    public void cancel()
    {
        if(state == com.android.internal.telephony.MmiCode.State.COMPLETE || state == com.android.internal.telephony.MmiCode.State.FAILED)
            return;
        state = com.android.internal.telephony.MmiCode.State.CANCELLED;
        if(isPendingUSSD)
        {
            phone.cancelPendingUssd(obtainMessage(7, this));
            return;
        } else if
        {
            phone.onMMIDone(this);
            return;
        }
    }

    int getCLIRMode()
    {
        if(sc != null && sc.equals("31"))
        {
            if(isActivate())
                return 2;
            if(isDeactivate())
                return 1;
        }
        return 0;
    }

    public String getDialString()
    {
        return null;
    }

    public CharSequence getMessage()
    {
        return message;
    }

    public Phone getPhone()
    {
        return phone;
    }

    public com.android.internal.telephony.MmiCode.State getState()
    {
        return state;
    }

    public CharSequence getUssdCode()
    {
        return null;
    }

    public void handleMessage(Message message1)
    {
        message1.what;
        JVM INSTR tableswitch 1 7: default 48
    //                   1 49
    //                   2 171
    //                   3 186
    //                   4 216
    //                   5 201
    //                   6 64
    //                   7 262;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L7 _L8
_L1:
        return;
_L2:
        onSetComplete((AsyncResult)(AsyncResult)message1.obj);
        return;
_L7:
        AsyncResult asyncresult = (AsyncResult)(AsyncResult)message1.obj;
        if(asyncresult.exception == null && message1.arg1 == 1)
        {
            boolean flag;
            if(message1.arg2 == 1)
                flag = true;
            else if
                flag = false;
            if(((GSMPhone)phone.getGsmPhone()).getIccRecords() != null)
            {
                ((GSMPhone)phone.getGsmPhone()).getIccRecords().setVoiceCallForwardingFlag(1, flag);
                ((GSMPhone)phone.getGsmPhone()).setCallForwardingPreference(flag);
            } else if
            {
                Log.w("IPMmiCode", "setVoiceCallForwardingFlag aborted. sim records is null.");
            }
        }
        onSetComplete(asyncresult);
        return;
_L3:
        onGetClirComplete((AsyncResult)(AsyncResult)message1.obj);
        return;
_L4:
        onQueryCfComplete((AsyncResult)(AsyncResult)message1.obj);
        return;
_L6:
        onQueryComplete((AsyncResult)(AsyncResult)message1.obj);
        return;
_L5:
        if(((AsyncResult)(AsyncResult)message1.obj).exception != null)
        {
            state = com.android.internal.telephony.MmiCode.State.FAILED;
            message = context.getText(0x10400ea);
            phone.onMMIDone(this);
            return;
        }
          goto _L1
_L8:
        phone.onMMIDone(this);
        return;
    }

    boolean isActivate()
    {
        return action != null && action.equals("*");
    }

    public boolean isCancelable()
    {
        return isPendingUSSD;
    }

    boolean isDeactivate()
    {
        return action != null && action.equals("#");
    }

    boolean isErasure()
    {
        return action != null && action.equals("##");
    }

    boolean isInterrogate()
    {
        return action != null && action.equals("*#");
    }

    boolean isMMI()
    {
        return poundString != null;
    }

    public boolean isPendingUSSD()
    {
        return isPendingUSSD;
    }

    boolean isPinCommand()
    {
        return sc != null && (sc.equals("04") || sc.equals("042") || sc.equals("05") || sc.equals("052"));
    }

    boolean isRegister()
    {
        return action != null && action.equals("**");
    }

    boolean isShortCode()
    {
        return poundString == null && dialingNumber != null && dialingNumber.length() <= 2;
    }

    boolean isTemporaryModeCLIR()
    {
        return sc != null && sc.equals("31") && dialingNumber != null && (isActivate() || isDeactivate());
    }

    public boolean isUssdRequest()
    {
        return isUssdRequest;
    }

    void onUssdFinished(String s, boolean flag)
    {
        if(state == com.android.internal.telephony.MmiCode.State.PENDING)
        {
            if(s == null)
                message = context.getText(0x10400f9);
            else if
                message = s;
            isUssdRequest = flag;
            if(!flag)
                state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            phone.onMMIDone(this);
        }
    }

    void onUssdFinishedError()
    {
        if(state == com.android.internal.telephony.MmiCode.State.PENDING)
        {
            state = com.android.internal.telephony.MmiCode.State.FAILED;
            message = context.getText(0x10400ea);
            phone.onMMIDone(this);
        }
    }

    void processCode()
    {
        try
        {
            if(isShortCode())
            {
                Log.d("IPMmiCode", "isShortCode");
                sendUssd(dialingNumber);
                return;
            }
        }
        catch(RuntimeException runtimeexception)
        {
            state = com.android.internal.telephony.MmiCode.State.FAILED;
            message = context.getText(0x10400ea);
            phone.onMMIDone(this);
            return;
        }
        if(dialingNumber != null)
            throw new RuntimeException("Invalid or Unsupported MMI Code");
        if(sc == null || !sc.equals("30"))
            break MISSING_BLOCK_LABEL_293;
        Log.d("IPMmiCode", "is CLIP");
        if(isInterrogate())
            throw new RuntimeException("Invalid or Unsupported MMI Code");
        if(isActivate())
        {
            IPPhoneSettings.putBoolean(context.getContentResolver(), "CLIP", true);
            state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            StringBuilder stringbuilder1 = new StringBuilder(getScString());
            stringbuilder1.append("\n");
            stringbuilder1.append(context.getText(0x10400f3));
            message = stringbuilder1;
            phone.onMMIDone(this);
            return;
        }
        if(isDeactivate())
        {
            IPPhoneSettings.putBoolean(context.getContentResolver(), "CLIP", false);
            state = com.android.internal.telephony.MmiCode.State.COMPLETE;
            StringBuilder stringbuilder = new StringBuilder(getScString());
            stringbuilder.append("\n");
            stringbuilder.append(context.getText(0x10400f5));
            message = stringbuilder;
            phone.onMMIDone(this);
            return;
        }
        throw new RuntimeException("Invalid or Unsupported MMI Code");
        if(sc == null || !sc.equals("31"))
            break MISSING_BLOCK_LABEL_397;
        Log.d("IPMmiCode", "is CLIR");
        if(isActivate())
        {
            phone.setOutgoingCallerIdDisplay(1, obtainMessage(1, this));
            return;
        }
        if(isDeactivate())
        {
            phone.setOutgoingCallerIdDisplay(2, obtainMessage(1, this));
            return;
        }
        if(isInterrogate())
        {
            phone.getOutgoingCallerIdDisplay(obtainMessage(1, this));
            return;
        }
        throw new RuntimeException("Invalid or Unsupported MMI Code");
        if(!isServiceCodeCallForwarding(sc)) goto _L2; else if goto _L1
_L1:
        String s;
        int i;
        int j;
        int k;
        Log.d("IPMmiCode", "is CF");
        s = sia;
        i = siToServiceClass(sib);
        j = scToCallForwardReason(sc);
        k = siToTime(sic);
        if(isInterrogate())
            throw new RuntimeException("Invalid or Unsupported MMI Code");
        if(!isActivate()) goto _L4; else if goto _L3
_L3:
        byte byte0 = 1;
          goto _L5
_L15:
        int l;
        int i1;
        Log.d("IPMmiCode", "is CF setCallForward");
        phone.setCallForwardingOption(byte0, j, s, k, obtainMessage(6, l, i1, this));
        return;
_L4:
        if(!isDeactivate()) goto _L7; else if goto _L6
_L6:
        byte0 = 0;
          goto _L5
_L7:
        if(!isRegister()) goto _L9; else if goto _L8
_L8:
        byte0 = 3;
          goto _L5
_L9:
        if(!isErasure()) goto _L11; else if goto _L10
_L10:
        byte0 = 4;
          goto _L5
_L11:
        throw new RuntimeException("invalid action");
_L2:
        if(sc == null || !sc.equals("43")) goto _L13; else if goto _L12
_L12:
        siToServiceClass(sia);
        if(isActivate() || isDeactivate())
        {
            phone.setCallWaiting(isActivate(), obtainMessage(1, this));
            return;
        }
        if(isInterrogate())
            throw new RuntimeException("Invalid or Unsupported MMI Code");
        else if
            throw new RuntimeException("Invalid or Unsupported MMI Code");
_L13:
        if(poundString != null)
        {
            sendUssd(poundString);
            return;
        }
        throw new RuntimeException("Invalid or Unsupported MMI Code");
_L5:
        if((j == 0 || j == 4) && ((i & 1) != 0 || i == 0))
            l = 1;
        else if
            l = 0;
        if(byte0 != 1)
        {
            i1 = 0;
            if(byte0 != 3)
                continue; /* Loop/switch isn't completed */
        }
        i1 = 1;
        if(true) goto _L15; else if goto _L14
_L14:
    }

    void sendUssd(String s)
    {
        isPendingUSSD = true;
        phone.sendUSSD(s, obtainMessage(4, this));
    }

    static final String ACTION_ACTIVATE = "*";
    static final String ACTION_DEACTIVATE = "#";
    static final String ACTION_ERASURE = "##";
    static final String ACTION_INTERROGATE = "*#";
    static final String ACTION_REGISTER = "**";
    static final int EVENT_GET_CLIR_COMPLETE = 2;
    static final int EVENT_QUERY_CF_COMPLETE = 3;
    static final int EVENT_QUERY_COMPLETE = 5;
    static final int EVENT_SET_CFF_COMPLETE = 6;
    static final int EVENT_SET_COMPLETE = 1;
    static final int EVENT_USSD_CANCEL_COMPLETE = 7;
    static final int EVENT_USSD_COMPLETE = 4;
    static final String LOG_TAG = "IPMmiCode";
    static final int MATCH_GROUP_ACTION = 2;
    static final int MATCH_GROUP_DIALING_NUMBER = 12;
    static final int MATCH_GROUP_POUND_STRING = 1;
    static final int MATCH_GROUP_PWD_CONFIRM = 11;
    static final int MATCH_GROUP_SERVICE_CODE = 3;
    static final int MATCH_GROUP_SIA = 5;
    static final int MATCH_GROUP_SIB = 7;
    static final int MATCH_GROUP_SIC = 9;
    static final String SC_BAIC = "35";
    static final String SC_BAICr = "351";
    static final String SC_BAOC = "33";
    static final String SC_BAOIC = "331";
    static final String SC_BAOICxH = "332";
    static final String SC_BA_ALL = "330";
    static final String SC_BA_MO = "333";
    static final String SC_BA_MT = "353";
    static final String SC_CFB = "67";
    static final String SC_CFNR = "62";
    static final String SC_CFNRy = "61";
    static final String SC_CFU = "21";
    static final String SC_CF_All = "002";
    static final String SC_CF_All_Conditional = "004";
    static final String SC_CLIP = "30";
    static final String SC_CLIR = "31";
    static final String SC_PIN = "04";
    static final String SC_PIN2 = "042";
    static final String SC_PUK = "05";
    static final String SC_PUK2 = "052";
    static final String SC_PWD = "03";
    static final String SC_WAIT = "43";
    static Pattern sPatternSuppService = Pattern.compile("((\\*|#|\\*#|\\*\\*|##)(\\d{2,3})(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*))?)?)?)?#)(.*)");
    String action;
    Context context;
    String dialingNumber;
    private boolean isPendingUSSD;
    private boolean isUssdRequest;
    CharSequence message;
    IPPhone phone;
    String poundString;
    String pwd;
    String sc;
    String sia;
    String sib;
    String sic;
    com.android.internal.telephony.MmiCode.State state;

}
