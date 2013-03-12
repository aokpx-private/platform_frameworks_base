// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.*;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.movial.ipphone:
//            IPPhoneSettings, IIPRegistry, IPStateListener

public class IPManager
{

    public IPManager(Context context)
    {
        TAG = "IPManager";
        mConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName componentname, IBinder ibinder)
            {
                mIPRegistry = IIPRegistry.Stub.asInterface(ibinder);
                binded.set(true);
                try
                {
                    cyclicBarrier.await();
                    return;
                }
                catch(Exception exception)
                {
                    Log.e(TAG, exception.toString());
                }
            }

            public void onServiceDisconnected(ComponentName componentname)
            {
                mIPRegistry = null;
                binded.set(false);
            }

            final IPManager this$0;

            
            {
                this$0 = IPManager.this;
                super();
            }
        };
        mContext = context;
    }

    private void bindService(boolean flag)
        throws Exception
    {
        if(IPPhoneSettings.getBoolean(mContext.getContentResolver(), "CELL_ONLY", true))
            throw new Exception("cannot bind to IPRegistry");
        AtomicBoolean atomicboolean = binded;
        atomicboolean;
        JVM INSTR monitorenter ;
        if(binded.get())
            break MISSING_BLOCK_LABEL_96;
        cyclicBarrier.reset();
        Intent intent = new Intent(com/movial/ipphone/IIPRegistry.getName());
        intent.setClassName("com.movial.ipservice", "com.movial.ipservice.IPService");
        if(mContext.bindService(intent, mConnection, 1))
            waitConnectionResponse(flag);
_L1:
        return;
        Exception exception1;
        exception1;
        Log.e(TAG, (new StringBuilder()).append("bindService FAILED. ").append(exception1.toString()).toString());
          goto _L1
        Exception exception;
        exception;
        atomicboolean;
        JVM INSTR monitorexit ;
        throw exception;
    }

    private void unbindService()
    {
        AtomicBoolean atomicboolean = binded;
        atomicboolean;
        JVM INSTR monitorenter ;
        boolean flag = binded.get();
        if(!flag)
            break MISSING_BLOCK_LABEL_38;
        mContext.unbindService(mConnection);
        binded.set(false);
_L1:
        atomicboolean;
        JVM INSTR monitorexit ;
        return;
        Exception exception1;
        exception1;
        Log.e(TAG, (new StringBuilder()).append("unbindService FAILED. ").append(exception1.toString()).toString());
          goto _L1
        Exception exception;
        exception;
        atomicboolean;
        JVM INSTR monitorexit ;
        throw exception;
    }

    private void waitConnectionResponse(boolean flag)
    {
        if(flag)
        {
            try
            {
                cyclicBarrier.await(10000L, TimeUnit.MILLISECONDS);
                return;
            }
            catch(Exception exception)
            {
                Log.e(TAG, (new StringBuilder()).append("waitConnectionResponse FAILED. ").append(exception.toString()).toString());
            }
            break MISSING_BLOCK_LABEL_59;
        }
        cyclicBarrier.await();
        return;
    }

    public int checkAudioMode(int i)
    {
        int j = i;
        try
        {
            bindService(true);
            j = mIPRegistry.checkAudioMode(i);
            unbindService();
        }
        catch(Exception exception)
        {
            Log.e(TAG, (new StringBuilder()).append("checkAudioMode FAILED. ").append(exception.toString()).toString());
            return j;
        }
        return j;
    }

    public void listen(boolean flag, IPStateListener ipstatelistener, int i)
    {
        try
        {
            Log.d(TAG, "bindService");
            bindService(flag);
            mIPRegistry.listen(ipstatelistener.listener, i);
            unbindService();
            Log.d(TAG, "unbindService");
            return;
        }
        catch(Exception exception)
        {
            Log.e(TAG, (new StringBuilder()).append("setListener FAILED. ").append(exception.toString()).toString());
        }
    }

    public void notifyRssiChange(int i)
    {
        try
        {
            bindService(true);
            mIPRegistry.notifyRssiChange(i);
            unbindService();
            return;
        }
        catch(Exception exception)
        {
            Log.e(TAG, (new StringBuilder()).append("notifyRssiChange FAILED. ").append(exception.toString()).toString());
        }
    }

    private static final int DEFAULT_TIMEOUT = 10000;
    private String TAG;
    private final AtomicBoolean binded = new AtomicBoolean(false);
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
    private ServiceConnection mConnection;
    private Context mContext;
    private IIPRegistry mIPRegistry;


/*
    static IIPRegistry access$002(IPManager ipmanager, IIPRegistry iipregistry)
    {
        ipmanager.mIPRegistry = iipregistry;
        return iipregistry;
    }

*/



}
