// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.telephony.PhoneNumberUtils;
import android.text.format.Time;
import android.util.Log;
import com.android.internal.telephony.*;
import com.android.internal.telephony.gsm.GsmSmsAddress;
import com.android.internal.telephony.gsm.SmsMessage;
import com.android.internal.telephony.uicc.IccUtils;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class IPSmsMessage extends SmsMessageBase
{
    private static class PduParser
    {

        int constructUserData(boolean flag, boolean flag1)
        {
            int i = cur;
            byte abyte0[] = pdu;
            int j = i + 1;
            int k = 0xff & abyte0[i];
            int l;
            int i1;
            int j1;
            int l1;
            if(flag)
            {
                byte abyte1[] = pdu;
                int j2 = j + 1;
                j1 = 0xff & abyte1[j];
                byte abyte2[] = new byte[j1];
                System.arraycopy(pdu, j2, abyte2, 0, j1);
                userDataHeader = SmsHeader.fromByteArray(abyte2);
                l = j2 + j1;
                int k2 = 8 * (j1 + 1);
                int l2 = k2 / 7;
                int i3;
                if(k2 % 7 > 0)
                    i3 = 1;
                else if
                    i3 = 0;
                i1 = l2 + i3;
                mUserDataSeptetPadding = i1 * 7 - k2;
            } else if
            {
                l = j;
                i1 = 0;
                j1 = 0;
            }
            if(flag1)
            {
                l1 = pdu.length - l;
            } else if
            {
                int k1;
                if(flag)
                    k1 = j1 + 1;
                else if
                    k1 = 0;
                l1 = k - k1;
                if(l1 < 0)
                    l1 = 0;
            }
            userData = new byte[l1];
            System.arraycopy(pdu, l, userData, 0, userData.length);
            cur = l;
            if(flag1)
            {
                int i2 = k - i1;
                if(i2 < 0)
                    return 0;
                else if
                    return i2;
            } else if
            {
                return userData.length;
            }
        }

        GsmSmsAddress getAddress()
        {
            int i = 2 + (1 + (0xff & pdu[cur])) / 2;
            GsmSmsAddress gsmsmsaddress;
            try
            {
                gsmsmsaddress = new GsmSmsAddress(pdu, cur, i);
            }
            catch(ParseException parseexception)
            {
                Log.e("IPSmsMessage", parseexception.getMessage());
                gsmsmsaddress = null;
            }
            cur = i + cur;
            return gsmsmsaddress;
        }

        int getByte()
        {
            byte abyte0[] = pdu;
            int i = cur;
            cur = i + 1;
            return 0xff & abyte0[i];
        }

        String getSCAddress()
        {
            int i = getByte();
            if(i != 0) goto _L2; else if goto _L1
_L1:
            String s = null;
_L4:
            cur = i + cur;
            return s;
_L2:
            String s1 = PhoneNumberUtils.calledPartyBCDToString(pdu, cur, i);
            s = s1;
            continue; /* Loop/switch isn't completed */
            RuntimeException runtimeexception;
            runtimeexception;
            Log.d("IPSmsMessage", "invalid SC address: ", runtimeexception);
            s = null;
            if(true) goto _L4; else if goto _L3
_L3:
        }

        long getSCTimestampMillis()
        {
            byte abyte0[] = pdu;
            int i = cur;
            cur = i + 1;
            int j = IccUtils.gsmBcdByteToInt(abyte0[i]);
            byte abyte1[] = pdu;
            int k = cur;
            cur = k + 1;
            int l = IccUtils.gsmBcdByteToInt(abyte1[k]);
            byte abyte2[] = pdu;
            int i1 = cur;
            cur = i1 + 1;
            int j1 = IccUtils.gsmBcdByteToInt(abyte2[i1]);
            byte abyte3[] = pdu;
            int k1 = cur;
            cur = k1 + 1;
            int l1 = IccUtils.gsmBcdByteToInt(abyte3[k1]);
            byte abyte4[] = pdu;
            int i2 = cur;
            cur = i2 + 1;
            int j2 = IccUtils.gsmBcdByteToInt(abyte4[i2]);
            byte abyte5[] = pdu;
            int k2 = cur;
            cur = k2 + 1;
            int l2 = IccUtils.gsmBcdByteToInt(abyte5[k2]);
            byte abyte6[] = pdu;
            int i3 = cur;
            cur = i3 + 1;
            byte byte0 = abyte6[i3];
            int j3 = IccUtils.gsmBcdByteToInt((byte)(byte0 & -9));
            Time time;
            int k3;
            if((byte0 & 8) != 0)
                j3 = -j3;
            time = new Time("UTC");
            if(j >= 90)
                k3 = j + 1900;
            else if
                k3 = j + 2000;
            time.year = k3;
            time.month = l - 1;
            time.monthDay = j1;
            time.hour = l1;
            time.minute = j2;
            time.second = l2;
            return time.toMillis(true) - (long)(1000 * (60 * (j3 * 15)));
        }

        byte[] getUserData()
        {
            return userData;
        }

        String getUserDataGSM7Bit(int i, int j, int k)
        {
            String s = GsmAlphabet.gsm7BitPackedToString(pdu, cur, i, mUserDataSeptetPadding, j, k);
            cur = cur + (i * 7) / 8;
            return s;
        }

        SmsHeader getUserDataHeader()
        {
            return userDataHeader;
        }

        String getUserDataKSC5601(int i)
        {
            String s;
            try
            {
                s = new String(pdu, cur, i, "KSC5601");
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                s = "";
                Log.e("IPSmsMessage", "implausible UnsupportedEncodingException", unsupportedencodingexception);
            }
            cur = i + cur;
            return s;
        }

        int getUserDataSeptetPadding()
        {
            return mUserDataSeptetPadding;
        }

        String getUserDataUCS2(int i)
        {
            String s;
            try
            {
                s = new String(pdu, cur, i, "utf-16");
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                s = "";
                Log.e("IPSmsMessage", "implausible UnsupportedEncodingException", unsupportedencodingexception);
            }
            cur = i + cur;
            return s;
        }

        String getUserDataUTF8(int i)
        {
            String s;
            try
            {
                s = new String(pdu, cur, i, "utf-8");
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                s = "";
                Log.e("IPSmsMessage", "implausible UnsupportedEncodingException", unsupportedencodingexception);
            }
            cur = i + cur;
            return s;
        }

        boolean moreDataPresent()
        {
            return pdu.length > cur;
        }

        int cur;
        int mUserDataSeptetPadding;
        int mUserDataSize;
        byte pdu[];
        byte userData[];
        SmsHeader userDataHeader;

        PduParser(byte abyte0[])
        {
            pdu = abyte0;
            cur = 0;
            mUserDataSeptetPadding = 0;
        }
    }


    public IPSmsMessage()
    {
        replyPathPresent = false;
        isStatusReportMessage = false;
    }

    public static com.android.internal.telephony.SmsMessageBase.TextEncodingDetails calculateLength(CharSequence charsequence, boolean flag)
    {
        com.android.internal.telephony.SmsMessageBase.TextEncodingDetails textencodingdetails = SmsMessage.calculateLength(charsequence, flag);
        if(textencodingdetails == null)
        {
            textencodingdetails = new com.android.internal.telephony.SmsMessageBase.TextEncodingDetails();
            int i = 2 * charsequence.length();
            textencodingdetails.codeUnitCount = charsequence.length();
            if(i > 140)
            {
                textencodingdetails.msgCount = (i + 133) / 134;
                textencodingdetails.codeUnitsRemaining = (134 * textencodingdetails.msgCount - i) / 2;
            } else if
            {
                textencodingdetails.msgCount = 1;
                textencodingdetails.codeUnitsRemaining = (140 - i) / 2;
            }
            textencodingdetails.codeUnitSize = 3;
        }
        return textencodingdetails;
    }

    public static IPSmsMessage createFromEfRecord(int i, byte abyte0[])
    {
        IPSmsMessage ipsmsmessage;
        ipsmsmessage = new IPSmsMessage();
        ipsmsmessage.indexOnIcc = i;
        if((1 & abyte0[0]) != 0)
            break MISSING_BLOCK_LABEL_31;
        Log.w("IPSmsMessage", "SMS parsing failed: Trying to parse a free record");
        return null;
        try
        {
            ipsmsmessage.statusOnIcc = 7 & abyte0[0];
            int j = -1 + abyte0.length;
            byte abyte1[] = new byte[j];
            System.arraycopy(abyte0, 1, abyte1, 0, j);
            ipsmsmessage.parsePdu(abyte1);
        }
        catch(RuntimeException runtimeexception)
        {
            Log.e("IPSmsMessage", "SMS PDU parsing failed: ", runtimeexception);
            return null;
        }
        return ipsmsmessage;
    }

    public static IPSmsMessage createFromPdu(byte abyte0[])
    {
        IPSmsMessage ipsmsmessage;
        try
        {
            ipsmsmessage = new IPSmsMessage();
            ipsmsmessage.parsePdu(abyte0);
        }
        catch(RuntimeException runtimeexception)
        {
            Log.e("IPSmsMessage", "SMS PDU parsing failed: ", runtimeexception);
            return null;
        }
        return ipsmsmessage;
    }

    private static byte[] encodeUCS2(String s, byte abyte0[])
        throws UnsupportedEncodingException
    {
        byte abyte1[] = s.getBytes("utf-16be");
        byte abyte2[];
        byte abyte3[];
        if(abyte0 != null)
        {
            abyte2 = new byte[1 + (abyte0.length + abyte1.length)];
            abyte2[0] = (byte)abyte0.length;
            System.arraycopy(abyte0, 0, abyte2, 1, abyte0.length);
            System.arraycopy(abyte1, 0, abyte2, 1 + abyte0.length, abyte1.length);
        } else if
        {
            abyte2 = abyte1;
        }
        abyte3 = new byte[1 + abyte2.length];
        abyte3[0] = (byte)(0xff & abyte2.length);
        System.arraycopy(abyte2, 0, abyte3, 1, abyte2.length);
        return abyte3;
    }

    public static int getPreviousMessageReference()
    {
        return -1 + mMessageReference;
    }

    public static com.android.internal.telephony.gsm.SmsMessage.SubmitPdu getSubmitPdu(String s, String s1, int i, byte abyte0[], boolean flag)
    {
        com.android.internal.telephony.SmsHeader.PortAddrs portaddrs = new com.android.internal.telephony.SmsHeader.PortAddrs();
        portaddrs.destPort = i;
        portaddrs.origPort = 0;
        portaddrs.areEightBits = false;
        SmsHeader smsheader = new SmsHeader();
        smsheader.portAddrs = portaddrs;
        byte abyte1[] = SmsHeader.toByteArray(smsheader);
        if(1 + (abyte0.length + abyte1.length) > 140)
        {
            Log.e("IPSmsMessage", (new StringBuilder()).append("SMS data message may only contain ").append(-1 + (140 - abyte1.length)).append(" bytes").toString());
            return null;
        } else if
        {
            com.android.internal.telephony.gsm.SmsMessage.SubmitPdu submitpdu = new com.android.internal.telephony.gsm.SmsMessage.SubmitPdu();
            ByteArrayOutputStream bytearrayoutputstream = getSubmitPduHead(s, s1, (byte)65, flag, submitpdu);
            bytearrayoutputstream.write(4);
            bytearrayoutputstream.write(1 + (abyte0.length + abyte1.length));
            bytearrayoutputstream.write(abyte1.length);
            bytearrayoutputstream.write(abyte1, 0, abyte1.length);
            bytearrayoutputstream.write(abyte0, 0, abyte0.length);
            submitpdu.encodedMessage = bytearrayoutputstream.toByteArray();
            return submitpdu;
        }
    }

    public static com.android.internal.telephony.gsm.SmsMessage.SubmitPdu getSubmitPdu(String s, String s1, String s2, boolean flag)
    {
        return getSubmitPdu(s, s1, s2, flag, ((byte []) (null)));
    }

    public static com.android.internal.telephony.gsm.SmsMessage.SubmitPdu getSubmitPdu(String s, String s1, String s2, boolean flag, byte abyte0[])
    {
        return getSubmitPdu(s, s1, s2, flag, abyte0, 0, 0, 0);
    }

    public static com.android.internal.telephony.gsm.SmsMessage.SubmitPdu getSubmitPdu(String s, String s1, String s2, boolean flag, byte abyte0[], int i, int j, int k)
    {
        com.android.internal.telephony.gsm.SmsMessage.SubmitPdu submitpdu;
        ByteArrayOutputStream bytearrayoutputstream;
        byte abyte2[];
        if(s2 == null || s1 == null)
            return null;
        byte byte0;
        if(i == 0)
        {
            com.android.internal.telephony.SmsMessageBase.TextEncodingDetails textencodingdetails = calculateLength(s2, false);
            i = textencodingdetails.codeUnitSize;
            j = textencodingdetails.languageTable;
            k = textencodingdetails.languageShiftTable;
            byte abyte4[];
            if(i == 1 && (j != 0 || k != 0))
                if(abyte0 != null)
                {
                    SmsHeader smsheader1 = SmsHeader.fromByteArray(abyte0);
                    if(smsheader1.languageTable != j || smsheader1.languageShiftTable != k)
                    {
                        Log.w("IPSmsMessage", (new StringBuilder()).append("Updating language table in SMS header: ").append(smsheader1.languageTable).append(" -> ").append(j).append(", ").append(smsheader1.languageShiftTable).append(" -> ").append(k).toString());
                        smsheader1.languageTable = j;
                        smsheader1.languageShiftTable = k;
                        abyte0 = SmsHeader.toByteArray(smsheader1);
                    }
                } else if
                {
                    SmsHeader smsheader = new SmsHeader();
                    smsheader.languageTable = j;
                    smsheader.languageShiftTable = k;
                    abyte0 = SmsHeader.toByteArray(smsheader);
                }
        }
        submitpdu = new com.android.internal.telephony.gsm.SmsMessage.SubmitPdu();
        if(abyte0 != null)
            byte0 = 64;
        else if
            byte0 = 0;
        bytearrayoutputstream = getSubmitPduHead(s, s1, (byte)(byte0 | 1), flag, submitpdu);
        if(i != 1) goto _L2; else if goto _L1
_L1:
        abyte4 = GsmAlphabet.stringToGsm7BitPackedWithHeader(s2, abyte0, j, k);
        abyte2 = abyte4;
        break MISSING_BLOCK_LABEL_229;
_L2:
        abyte3 = encodeUCS2(s2, abyte0);
        abyte2 = abyte3;
        continue; /* Loop/switch isn't completed */
        unsupportedencodingexception1;
        Log.e("IPSmsMessage", "Implausible UnsupportedEncodingException ", unsupportedencodingexception1);
        return null;
        encodeexception;
        byte abyte1[];
        try
        {
            abyte1 = encodeUCS2(s2, abyte0);
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            Log.e("IPSmsMessage", "Implausible UnsupportedEncodingException ", unsupportedencodingexception);
            return null;
        }
        abyte2 = abyte1;
        i = 3;
        if(true) goto _L4; else if goto _L3
_L4:
        if(i != 1)
            break; /* Loop/switch isn't completed */
        EncodeException encodeexception;
        UnsupportedEncodingException unsupportedencodingexception1;
        byte abyte3[];
        if((0xff & abyte2[0]) > 160)
        {
            Log.e("IPSmsMessage", (new StringBuilder()).append("Message too long (").append(0xff & abyte2[0]).append(" septets)").toString());
            return null;
        }
        bytearrayoutputstream.write(0);
_L6:
        bytearrayoutputstream.write(abyte2, 0, abyte2.length);
        submitpdu.encodedMessage = bytearrayoutputstream.toByteArray();
        return submitpdu;
_L3:
        if((0xff & abyte2[0]) > 140)
        {
            Log.e("IPSmsMessage", (new StringBuilder()).append("Message too long (").append(0xff & abyte2[0]).append(" bytes)").toString());
            return null;
        }
        bytearrayoutputstream.write(8);
        if(true) goto _L6; else if goto _L5
_L5:
    }

    private static ByteArrayOutputStream getSubmitPduHead(String s, String s1, byte byte0, boolean flag, com.android.internal.telephony.gsm.SmsMessage.SubmitPdu submitpdu)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(180);
        int i;
        byte abyte0[];
        int j;
        int k;
        if(s == null)
            submitpdu.encodedScAddress = null;
        else if
            submitpdu.encodedScAddress = PhoneNumberUtils.networkPortionToCalledPartyBCDWithLength(s);
        if(flag)
            byte0 |= 0x20;
        bytearrayoutputstream.write(byte0);
        i = mMessageReference;
        mMessageReference = i + 1;
        bytearrayoutputstream.write(i % 256);
        abyte0 = PhoneNumberUtils.networkPortionToCalledPartyBCD(s1);
        j = 2 * (-1 + abyte0.length);
        if((0xf0 & abyte0[-1 + abyte0.length]) == 240)
            k = 1;
        else if
            k = 0;
        bytearrayoutputstream.write(j - k);
        bytearrayoutputstream.write(abyte0, 0, abyte0.length);
        bytearrayoutputstream.write(0);
        return bytearrayoutputstream;
    }

    public static int getTPLayerLengthForPDU(String s)
    {
        return -1 + (s.length() / 2 - Integer.parseInt(s.substring(0, 2), 16));
    }

    public static IPSmsMessage newFromCDS(String s)
    {
        IPSmsMessage ipsmsmessage;
        try
        {
            ipsmsmessage = new IPSmsMessage();
            ipsmsmessage.parsePdu(IccUtils.hexStringToBytes(s));
        }
        catch(RuntimeException runtimeexception)
        {
            Log.e("IPSmsMessage", "CDS SMS PDU parsing failed: ", runtimeexception);
            return null;
        }
        return ipsmsmessage;
    }

    public static IPSmsMessage newFromCMT(String as[])
    {
        IPSmsMessage ipsmsmessage;
        try
        {
            ipsmsmessage = new IPSmsMessage();
            ipsmsmessage.parsePdu(IccUtils.hexStringToBytes(as[1]));
        }
        catch(RuntimeException runtimeexception)
        {
            Log.e("IPSmsMessage", "SMS PDU parsing failed: ", runtimeexception);
            return null;
        }
        return ipsmsmessage;
    }

    private void parsePdu(byte abyte0[])
    {
        mPdu = abyte0;
        PduParser pduparser = new PduParser(abyte0);
        scAddress = pduparser.getSCAddress();
        if(scAddress == null);
        int i = pduparser.getByte();
        Log.d("IPSmsMessage", (new StringBuilder()).append("mti: ").append(i).toString());
        mti = i & 3;
        switch(mti)
        {
        case 1: // '\001'
        default:
            throw new RuntimeException("Unsupported message type");

        case 0: // '\0'
        case 3: // '\003'
            parseSmsDeliver(pduparser, i);
            return;

        case 2: // '\002'
            parseSmsStatusReport(pduparser, i);
            break;
        }
    }

    private void parseSmsDeliver(PduParser pduparser, int i)
    {
        boolean flag;
        boolean flag1;
        if((i & 0x80) == 128)
            flag = true;
        else if
            flag = false;
        replyPathPresent = flag;
        originatingAddress = pduparser.getAddress();
        if(originatingAddress == null);
        protocolIdentifier = pduparser.getByte();
        dataCodingScheme = pduparser.getByte();
        Log.v("IPSmsMessage", (new StringBuilder()).append("SMS TP-PID:").append(protocolIdentifier).append(" data coding scheme: ").append(dataCodingScheme).toString());
        scTimeMillis = pduparser.getSCTimestampMillis();
        if((i & 0x40) == 64)
            flag1 = true;
        else if
            flag1 = false;
        parseUserData(pduparser, flag1);
    }

    private void parseSmsStatusReport(PduParser pduparser, int i)
    {
        isStatusReportMessage = true;
        boolean flag;
        if((i & 0x20) == 0)
            flag = true;
        else if
            flag = false;
        forSubmit = flag;
        messageRef = pduparser.getByte();
        recipientAddress = pduparser.getAddress();
        scTimeMillis = pduparser.getSCTimestampMillis();
        dischargeTimeMillis = pduparser.getSCTimestampMillis();
        status = pduparser.getByte();
        if(pduparser.moreDataPresent())
        {
            int j = pduparser.getByte();
            for(int k = j; (k & 0x80) != 0; k = pduparser.getByte());
            if((j & 1) != 0)
                protocolIdentifier = pduparser.getByte();
            if((j & 2) != 0)
                dataCodingScheme = pduparser.getByte();
            if((j & 4) != 0)
            {
                boolean flag1;
                if((i & 0x40) == 64)
                    flag1 = true;
                else if
                    flag1 = false;
                parseUserData(pduparser, flag1);
            }
        }
    }

    private void parseUserData(PduParser pduparser, boolean flag)
    {
        int i;
        i = 0;
        Log.i("IPSmsMessage", "parse userdata");
        if((0x80 & dataCodingScheme) != 0) goto _L2; else if goto _L1
_L1:
        boolean flag2;
        int j;
        boolean flag5;
        boolean flag6;
        if((0x40 & dataCodingScheme) != 0)
            flag5 = true;
        else if
            flag5 = false;
        automaticDeletion = flag5;
        if((0x20 & dataCodingScheme) != 0)
            flag6 = true;
        else if
            flag6 = false;
        if((0x10 & dataCodingScheme) != 0)
            flag2 = true;
        else if
            flag2 = false;
        if(!flag6) goto _L4; else if goto _L3
_L3:
        Log.w("IPSmsMessage", (new StringBuilder()).append("4 - Unsupported SMS data coding scheme (compression) ").append(0xff & dataCodingScheme).toString());
_L13:
        boolean flag1;
        boolean flag3;
        boolean flag4;
        if(i == 1)
            flag3 = true;
        else if
            flag3 = false;
        j = pduparser.constructUserData(flag, flag3);
        userData = pduparser.getUserData();
        userDataHeader = pduparser.getUserDataHeader();
        i;
        JVM INSTR tableswitch 0 4: default 172
    //                   0 649
    //                   1 946
    //                   2 649
    //                   3 1001
    //                   4 1014;
           goto _L5 _L6 _L7 _L6 _L8 _L9
_L5:
        if(messageBody != null)
            parseMessageBody();
        if(!flag2)
        {
            messageClass = android.telephony.SmsMessage.MessageClass.UNKNOWN;
            return;
        }
        break; /* Loop/switch isn't completed */
_L4:
        switch(3 & dataCodingScheme >> 2)
        {
        default:
            i = 0;
            break;

        case 0: // '\0'
            i = 1;
            break;

        case 2: // '\002'
            i = 3;
            break;

        case 1: // '\001'
        case 3: // '\003'
            Log.w("IPSmsMessage", (new StringBuilder()).append("1 - Unsupported SMS data coding scheme ").append(0xff & dataCodingScheme).toString());
            i = 2;
            break;
        }
          goto _L10
_L2:
        if((0xf0 & dataCodingScheme) == 240)
        {
            automaticDeletion = false;
            flag2 = true;
            if((4 & dataCodingScheme) == 0)
                i = 1;
            else if
                i = 2;
        } else if
        if((0xf0 & dataCodingScheme) == 192 || (0xf0 & dataCodingScheme) == 208 || (0xf0 & dataCodingScheme) == 224)
        {
            if((0xf0 & dataCodingScheme) == 224)
                i = 3;
            else if
                i = 1;
            if((8 & dataCodingScheme) == 8)
                flag1 = true;
            else if
                flag1 = false;
            if((3 & dataCodingScheme) == 0)
            {
                isMwi = true;
                mwiSense = flag1;
                if((0xf0 & dataCodingScheme) == 192)
                    flag4 = true;
                else if
                    flag4 = false;
                mwiDontStore = flag4;
                flag2 = false;
            } else if
            {
                isMwi = false;
                Log.w("IPSmsMessage", (new StringBuilder()).append("MWI for fax, email, or other ").append(0xff & dataCodingScheme).toString());
                flag2 = false;
            }
        } else if
        if((0xc0 & dataCodingScheme) == 128)
        {
            if(dataCodingScheme == 132)
            {
                i = 4;
                flag2 = false;
            } else if
            {
                Log.w("IPSmsMessage", (new StringBuilder()).append("5 - Unsupported SMS data coding scheme ").append(0xff & dataCodingScheme).toString());
                i = 0;
                flag2 = false;
            }
        } else if
        {
            Log.w("IPSmsMessage", (new StringBuilder()).append("3 - Unsupported SMS data coding scheme ").append(0xff & dataCodingScheme).toString());
            i = 0;
            flag2 = false;
        }
          goto _L10
_L6:
        messageBody = pduparser.getUserDataUTF8(j);
        if(userDataHeader != null && userDataHeader.portAddrs != null && userDataHeader.portAddrs.destPort == 2948)
        {
            Log.i("IPSmsMessage", "receive WAP PUSH");
        } else if
        {
            byte abyte0[];
            byte abyte1[];
            int i1;
            int j1;
            try
            {
                abyte0 = encodeUCS2(messageBody, null);
                abyte1 = new byte[(mPdu.length + (-1 + abyte0.length)) - messageBody.length()];
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                Log.e("IPSmsMessage", "Implausible UnsupportedEncodingException ", unsupportedencodingexception);
                return;
            }
            Log.i("IPSmsMessage", (new StringBuilder()).append("new Encoded Text: ").append(IccUtils.bytesToHexString(abyte0)).toString());
            System.arraycopy(mPdu, 0, abyte1, 0, -1 + (mPdu.length - messageBody.length()));
            System.arraycopy(abyte0, 0, abyte1, -1 + (mPdu.length - messageBody.length()), abyte0.length);
            i1 = 0 + (2 + mPdu[0]);
            j1 = 2 + (i1 + (1 + (1 + mPdu[i1]) / 2));
            Log.i("IPSmsMessage", (new StringBuilder()).append("change DCS from ").append(abyte1[j1]).append(" to 0x1b").toString());
            abyte1[j1] = 27;
            Log.i("IPSmsMessage", (new StringBuilder()).append("new encoded: ").append(IccUtils.bytesToHexString(abyte1)).toString());
            mPdu = abyte1;
        }
        continue; /* Loop/switch isn't completed */
_L10:
        if(false)
            ;
        continue; /* Loop/switch isn't completed */
_L7:
        int k;
        int l;
        if(flag)
            k = userDataHeader.languageTable;
        else if
            k = 0;
        if(flag)
            l = userDataHeader.languageShiftTable;
        else if
            l = 0;
        messageBody = pduparser.getUserDataGSM7Bit(j, k, l);
        continue; /* Loop/switch isn't completed */
_L8:
        messageBody = pduparser.getUserDataUCS2(j);
        continue; /* Loop/switch isn't completed */
_L9:
        messageBody = pduparser.getUserDataKSC5601(j);
        if(true) goto _L5; else if goto _L11
_L11:
        switch(3 & dataCodingScheme)
        {
        default:
            return;

        case 0: // '\0'
            messageClass = android.telephony.SmsMessage.MessageClass.CLASS_0;
            return;

        case 1: // '\001'
            messageClass = android.telephony.SmsMessage.MessageClass.CLASS_1;
            return;

        case 2: // '\002'
            messageClass = android.telephony.SmsMessage.MessageClass.CLASS_2;
            return;

        case 3: // '\003'
            messageClass = android.telephony.SmsMessage.MessageClass.CLASS_3;
            break;
        }
        return;
        if(true) goto _L13; else if goto _L12
_L12:
    }

    int getDataCodingScheme()
    {
        return dataCodingScheme;
    }

    public android.telephony.SmsMessage.MessageClass getMessageClass()
    {
        return messageClass;
    }

    public int getMessageIdentifier()
    {
        return 0;
    }

    public int getMessagePriority()
    {
        return 0;
    }

    public int getProtocolIdentifier()
    {
        return protocolIdentifier;
    }

    public int getStatus()
    {
        return status;
    }

    public boolean isCphsMwiMessage()
    {
        return ((GsmSmsAddress)originatingAddress).isCphsVoiceMessageClear() || ((GsmSmsAddress)originatingAddress).isCphsVoiceMessageSet();
    }

    public boolean isMWIClearMessage()
    {
        if(isMwi && !mwiSense)
            return true;
        boolean flag;
        if(originatingAddress != null && ((GsmSmsAddress)originatingAddress).isCphsVoiceMessageClear())
            flag = true;
        else if
            flag = false;
        return flag;
    }

    public boolean isMWISetMessage()
    {
        if(isMwi && mwiSense)
            return true;
        boolean flag;
        if(originatingAddress != null && ((GsmSmsAddress)originatingAddress).isCphsVoiceMessageSet())
            flag = true;
        else if
            flag = false;
        return flag;
    }

    public boolean isMwiDontStore()
    {
        if(!isMwi || !mwiDontStore)
            if(isCphsMwiMessage())
            {
                if(" ".equals(getMessageBody()))
                    return true;
            } else if
            {
                return false;
            }
        return true;
    }

    public boolean isReplace()
    {
        return (0xc0 & protocolIdentifier) == 64 && (0x3f & protocolIdentifier) > 0 && (0x3f & protocolIdentifier) < 8;
    }

    public boolean isReplyPathPresent()
    {
        return replyPathPresent;
    }

    public boolean isStatusReportMessage()
    {
        return isStatusReportMessage;
    }

    public boolean isTypeZero()
    {
        return protocolIdentifier == 64;
    }

    boolean isUsimDataDownload()
    {
        return messageClass == android.telephony.SmsMessage.MessageClass.CLASS_2 && (protocolIdentifier == 127 || protocolIdentifier == 124);
    }

    protected void kddiDispatchPdus(byte abyte0[][], SmsMessageBase smsmessagebase)
    {
    }

    public int kddiGetMessageId()
    {
        Log.w("IPSmsMessage", "GetMessageID: is not supported in GSM mode.");
        return 0;
    }

    public int kddiGetServiceCategory()
    {
        Log.w("IPSmsMessage", "GetServiceCategory: is not supported in GSM mode.");
        return 0;
    }

    static final String LOG_TAG = "IPSmsMessage";
    private static int mMessageReference = 0;
    private boolean automaticDeletion;
    private int dataCodingScheme;
    private long dischargeTimeMillis;
    private boolean forSubmit;
    private boolean isStatusReportMessage;
    private android.telephony.SmsMessage.MessageClass messageClass;
    private int mti;
    private int protocolIdentifier;
    private GsmSmsAddress recipientAddress;
    private boolean replyPathPresent;
    private int status;

}
