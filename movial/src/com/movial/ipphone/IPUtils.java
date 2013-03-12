// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;


public class IPUtils
{
    public static final class EmergencyState extends Enum
    {

        public static EmergencyState valueOf(String s)
        {
            return (EmergencyState)Enum.valueOf(com/movial/ipphone/IPUtils$EmergencyState, s);
        }

        public static EmergencyState[] values()
        {
            return (EmergencyState[])$VALUES.clone();
        }

        private static final EmergencyState $VALUES[];
        public static final EmergencyState CS_CALL_CONNECTED;
        public static final EmergencyState CS_CALL_DIALING;
        public static final EmergencyState CS_CALL_DISCONNECTED;
        public static final EmergencyState CS_CALL_FAILED;
        public static final EmergencyState CS_TURNING_ON_RADIO;
        public static final EmergencyState IDLE;
        public static final EmergencyState IMS_CALL_CONNECTED;
        public static final EmergencyState IMS_CALL_DIALING;
        public static final EmergencyState IMS_CALL_DISCONNECTED;
        public static final EmergencyState IMS_CALL_FAILED;
        public static final EmergencyState NOT_INITIALIZED;

        static 
        {
            NOT_INITIALIZED = new EmergencyState("NOT_INITIALIZED", 0);
            IDLE = new EmergencyState("IDLE", 1);
            CS_TURNING_ON_RADIO = new EmergencyState("CS_TURNING_ON_RADIO", 2);
            CS_CALL_DIALING = new EmergencyState("CS_CALL_DIALING", 3);
            CS_CALL_CONNECTED = new EmergencyState("CS_CALL_CONNECTED", 4);
            CS_CALL_DISCONNECTED = new EmergencyState("CS_CALL_DISCONNECTED", 5);
            CS_CALL_FAILED = new EmergencyState("CS_CALL_FAILED", 6);
            IMS_CALL_DIALING = new EmergencyState("IMS_CALL_DIALING", 7);
            IMS_CALL_CONNECTED = new EmergencyState("IMS_CALL_CONNECTED", 8);
            IMS_CALL_DISCONNECTED = new EmergencyState("IMS_CALL_DISCONNECTED", 9);
            IMS_CALL_FAILED = new EmergencyState("IMS_CALL_FAILED", 10);
            EmergencyState aemergencystate[] = new EmergencyState[11];
            aemergencystate[0] = NOT_INITIALIZED;
            aemergencystate[1] = IDLE;
            aemergencystate[2] = CS_TURNING_ON_RADIO;
            aemergencystate[3] = CS_CALL_DIALING;
            aemergencystate[4] = CS_CALL_CONNECTED;
            aemergencystate[5] = CS_CALL_DISCONNECTED;
            aemergencystate[6] = CS_CALL_FAILED;
            aemergencystate[7] = IMS_CALL_DIALING;
            aemergencystate[8] = IMS_CALL_CONNECTED;
            aemergencystate[9] = IMS_CALL_DISCONNECTED;
            aemergencystate[10] = IMS_CALL_FAILED;
            $VALUES = aemergencystate;
        }

        private EmergencyState(String s, int i)
        {
            super(s, i);
        }
    }


    public IPUtils()
    {
    }

    public static final String ACTION_RADIO_ON = "ACTION_RADIO_ON";
    public static final String ACTION_REREAD_ISIM = "com.movial.reread_isim_records";
    public static final String ACTION_TERMINATE_STACK = "com.movial.terminate_stack";
    public static final int CS_PREFERRED = 0;
    public static final int DIALOG_E911 = 4;
    public static final int DIALOG_ROVEOUT = 2;
    public static final int DIALOG_SIMSWAP = 3;
    public static final String DIALOG_TITLE = "dialog_title";
    public static final String DIALOG_TYPE = "dialog_type";
    public static final int DIALOG_WIFION = 1;
    public static final int EVENT_CLEAR_CONNECTIONS = 44;
    public static final int EVENT_CONFERENCE_FINISHED = 43;
    public static final String EXTRA_NEED_RESTART = "restart_service";
    public static final int IMS_CELL_ONLY = 3;
    public static final int IMS_CELL_PREF = 2;
    public static final int IMS_MOBILE = 2;
    public static final int IMS_PREFERRED = 1;
    public static final String IMS_REGISTRATION = "IMS_REGISTRATION";
    public static final String IMS_REG_STATUS = "IMS_REG_STATUS";
    public static final String IMS_REG_TYPE = "IMS_REG_TYPE";
    public static final int IMS_UNKNOWN = 0;
    public static final int IMS_WIFI = 1;
    public static final String IMS_WIFICALL = "IMS_WIFICALL";
    public static final int IMS_WIFI_ONLY = 1;
    public static final int IMS_WIFI_PREF = 0;
    public static final String IMS_WIFI_STATUS = "IMS_WIFI_STATUS";
    public static final String IMS_WIFI_STATUS_STRING = "IMS_WIFI_STATUS_STRING";
    public static final String INTENT_EXTRA_IMS_CANCELED_BY_USER = "canceled_by_user";
    public static final String INTENT_FORCE_EMERGENCY_CHANGED = "com.movial.force_emergency_changed";
    public static final String INTENT_GBA_INIT = "com.movial.gba_initialized";
    public static final String INTENT_IMS_EMERGENCY_FAIL = "com.movial.ims_emergency_fail";
    public static final String INTENT_IMS_EMERGENCY_STARTED = "com.movial.ims_emergency_start";
    public static final String INTENT_REG_CHECK = "com.movial.reg_check";
    public static final int LISTEN_EVENT_NONE = 0;
    public static final int LISTEN_EVENT_REG_STATUS = 1;
    public static final int LISTEN_EVENT_WIFICALL_STATUS = 2;
    public static final int MAX_SESSION = 7;
    public static final String PROPERTY_CS_EMERGENCY_TIMEOUT = "gsm.ecc.timeout";
    public static final int WIFI_AIRPLANE_MODE_ON = 8;
    public static final int WIFI_CELL_PREFERRED = 5;
    public static final int WIFI_IMS_DEREGISTERED = 10;
    public static final int WIFI_IMS_DISABLED = 1;
    public static final int WIFI_IMS_DISABLING = 9;
    public static final int WIFI_IMS_POOR_SIGNAL = 4;
    public static final int WIFI_IMS_REGISTERED = 6;
    public static final int WIFI_IMS_REGISTERING = 2;
    public static final int WIFI_IMS_REG_FAILED = 3;
    public static final int WIFI_OFF = 7;
    public static final int WIFI_SIM_NOT_READY = 11;
    public static final int WIFI_UNKNOWN;
}
