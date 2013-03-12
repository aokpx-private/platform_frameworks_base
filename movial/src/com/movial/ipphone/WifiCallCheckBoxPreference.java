// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.*;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;

// Referenced classes of package com.movial.ipphone:
//            IPManager, IPPhoneSettings, IPStateListener

public class WifiCallCheckBoxPreference extends CheckBoxPreference
{

    public WifiCallCheckBoxPreference(Context context)
    {
        this(context, null);
    }

    public WifiCallCheckBoxPreference(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0x101008f);
    }

    public WifiCallCheckBoxPreference(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mContext = context;
        mIPManager = new IPManager(context);
        mWifiManager = (WifiManager)context.getSystemService("wifi");
    }

    private void registerToIPRegistry(final boolean register)
    {
        (new Thread() {

            public void run()
            {
                if(register)
                {
                    mIPManager.listen(true, mIPStateListener, 2);
                    return;
                }
                try
                {
                    mIPManager.listen(true, mIPStateListener, 0);
                    return;
                }
                catch(Exception exception)
                {
                    Log.d("WifiCallCheckBoxPreference", (new StringBuilder()).append("register IPStateListener failed. ").append(exception.toString()).toString());
                }
                return;
            }

            final WifiCallCheckBoxPreference this$0;
            final boolean val$register;

            
            {
                this$0 = WifiCallCheckBoxPreference.this;
                register = flag;
                super();
            }
        }).start();
    }

    protected void onClick()
    {
        boolean flag = true;
        super.onClick();
        Log.d("WifiCallCheckBoxPreference", (new StringBuilder()).append("onClick. ").append(isChecked()).toString());
        boolean flag1;
        boolean flag2;
        Bundle bundle;
        Message message;
        if(!isChecked())
            flag1 = flag;
        else
            flag1 = false;
        mCellOnly = flag1;
        IPPhoneSettings.putBoolean(mContext.getContentResolver(), "CELL_ONLY", mCellOnly);
        if(mCellOnly)
            flag = false;
        registerToIPRegistry(flag);
        flag2 = true;
        if(mCellOnly)
            flag2 = false;
        bundle = new Bundle();
        bundle.putBoolean("state", flag2);
        message = new Message();
        message.what = 24;
        message.obj = bundle;
        if(mWifiManager.callSECApi(message) == 0)
        {
            Log.d("WifiCallCheckBoxPreference", "mWifiManager.callSECApi(msg) was sucessfull");
            return;
        } else
        {
            Log.d("WifiCallCheckBoxPreference", (new StringBuilder()).append("mWifiManager.callSECApi(msg) failed: enable=").append(flag2).toString());
            return;
        }
    }

    public void pause()
    {
        registerToIPRegistry(false);
    }

    public void resume()
    {
        boolean flag = true;
        mCellOnly = IPPhoneSettings.getBoolean(mContext.getContentResolver(), "CELL_ONLY", flag);
        boolean flag1;
        Preference preference;
        boolean flag2;
        if(!mCellOnly)
            flag1 = flag;
        else
            flag1 = false;
        setChecked(flag1);
        if(mCellOnly)
            setSummary("Disabled");
        preference = mPreference;
        if(!mCellOnly)
            flag2 = flag;
        else
            flag2 = false;
        preference.setEnabled(flag2);
        if(mCellOnly)
            flag = false;
        registerToIPRegistry(flag);
    }

    public void setValues(Preference preference)
    {
        mPreference = preference;
    }

    private static final int EVENT_IMS_WIFI_STATUS = 1;
    private static final String TAG = "WifiCallCheckBoxPreference";
    private boolean mCellOnly;
    private final Context mContext;
    private Handler mHandler = new Handler() {

        public void handleMessage(Message message)
        {
            switch(message.what)
            {
            default:
                return;

            case 1: // '\001'
                setSummary("Disabled");
                break;
            }
            setEnabled(true);
            mPreference.setEnabled(false);
        }

        final WifiCallCheckBoxPreference this$0;

            
            {
                this$0 = WifiCallCheckBoxPreference.this;
                super();
            }
    };
    private IPManager mIPManager;
    private IPStateListener mIPStateListener = new IPStateListener() {

        public void onWifiCallStateChanged(int j, String s)
        {
            Log.d("WifiCallCheckBoxPreference", (new StringBuilder()).append("onWifiCallStateChanged. ").append(j).append(", error: ").append(s).toString());
            setSummary(s);
            mHandler.removeMessages(1);
            switch(j)
            {
            default:
                setEnabled(true);
                mPreference.setEnabled(true);
                return;

            case 2: // '\002'
                setEnabled(false);
                mPreference.setEnabled(false);
                return;

            case 9: // '\t'
                setEnabled(false);
                mPreference.setEnabled(false);
                return;

            case 1: // '\001'
                setEnabled(false);
                mPreference.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(1, 3000L);
                return;
            }
        }

        final WifiCallCheckBoxPreference this$0;

            
            {
                this$0 = WifiCallCheckBoxPreference.this;
                super();
            }
    };
    private Preference mPreference;
    private WifiManager mWifiManager;




}
