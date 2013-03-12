// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.os.Handler;
import android.os.Message;

// Referenced classes of package com.movial.ipphone:
//            IIPStateListener

public class IPStateListener
{

    public IPStateListener()
    {
        listener = new IIPStateListener.Stub() {

            public void onRegisteredStateChanged(boolean flag, int i)
            {
                Handler handler = mHandler;
                int j;
                if(flag)
                    j = 1;
                else
                    j = 0;
                Message.obtain(handler, 1, j, i, null).sendToTarget();
            }

            public void onWifiCallStateChanged(int i, String s)
            {
                Message.obtain(mHandler, 2, i, 0, s).sendToTarget();
            }

            final IPStateListener this$0;

            
            {
                this$0 = IPStateListener.this;
                super();
            }
        };
        mHandler = new Handler() {

            public void handleMessage(Message message)
            {
                boolean flag = true;
                switch(message.what)
                {
                default:
                    return;

                case 1: // '\001'
                    IPStateListener ipstatelistener = IPStateListener.this;
                    if(message.arg1 != flag)
                        flag = false;
                    ipstatelistener.onRegisteredStateChanged(flag, message.arg2);
                    return;

                case 2: // '\002'
                    onWifiCallStateChanged(message.arg1, (String)message.obj);
                    return;
                }
            }

            final IPStateListener this$0;

            
            {
                this$0 = IPStateListener.this;
                super();
            }
        };
    }

    public void onRegisteredStateChanged(boolean flag, int i)
    {
    }

    public void onWifiCallStateChanged(int i, String s)
    {
    }

    IIPStateListener listener;
    private Handler mHandler;

}
