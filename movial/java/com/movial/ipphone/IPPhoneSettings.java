// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.movial.ipphone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class IPPhoneSettings
{

    public IPPhoneSettings()
    {
    }

    public static boolean getBoolean(ContentResolver contentresolver, String s, boolean flag)
    {
        boolean flag1 = true;
        int i = getInt(contentresolver, s, -1);
        if(i == -1)
            return flag;
        if(i != flag1)
            flag1 = false;
        return flag1;
    }

    public static int getInt(ContentResolver contentresolver, String s, int i)
    {
        String s1 = getString(contentresolver, s, null);
        if(s1 == null)
            return i;
        else if
            return Integer.parseInt(s1);
    }

    public static String getString(ContentResolver contentresolver, String s, String s1)
    {
        boolean flag;
        Cursor cursor;
        flag = false;
        cursor = null;
        cursor = contentresolver.query(CONTENT_URI, new String[] {
            "value"
        }, "name = ?", new String[] {
            s
        }, null);
        flag = false;
        if(cursor == null)
            break MISSING_BLOCK_LABEL_108;
        boolean flag1 = cursor.moveToNext();
        flag = false;
        if(!flag1)
            break MISSING_BLOCK_LABEL_108;
        s1 = cursor.getString(0);
        flag = true;
        Log.e(TAG, (new StringBuilder()).append("getString(").append(s).append(") = ").append(s1).append("").toString());
        if(cursor != null)
            cursor.close();
_L2:
        if(!flag)
            Log.e(TAG, (new StringBuilder()).append("getString(").append(s).append(") = (default)").append(s1).append("").toString());
        return s1;
        Exception exception1;
        exception1;
        Log.e(TAG, exception1.toString());
        if(cursor != null)
            cursor.close();
        if(true) goto _L2; else if goto _L1
_L1:
        Exception exception;
        exception;
        if(cursor != null)
            cursor.close();
        throw exception;
    }

    public static boolean putBoolean(ContentResolver contentresolver, String s, boolean flag)
    {
        int i;
        if(flag)
            i = 1;
        else if
            i = 0;
        return putInt(contentresolver, s, i);
    }

    public static boolean putInt(ContentResolver contentresolver, String s, int i)
    {
        return putString(contentresolver, s, Integer.toString(i));
    }

    public static boolean putString(ContentResolver contentresolver, String s, String s1)
    {
        Log.e(TAG, (new StringBuilder()).append("putString(").append(s).append(", ").append(s1).append(")").toString());
        try
        {
            ContentValues contentvalues = new ContentValues();
            contentvalues.put("name", s);
            contentvalues.put("value", s1);
            contentresolver.insert(CONTENT_URI, contentvalues);
        }
        catch(Exception exception)
        {
            Log.e(TAG, exception.toString());
            return false;
        }
        return true;
    }

    public static final String CELL_ONLY = "CELL_ONLY";
    public static final String CLIP = "CLIP";
    public static final Uri CONTENT_URI = Uri.parse("content://ipprovider/ipphonesettings");
    public static final String ECM = "ECM";
    public static final String GBA_INIT = "GBA_INIT";
    public static final String PREFERRED_OPTION = "PREFERRED_OPTION";
    public static final String ROVE_IN = "ROVE_IN";
    public static final String ROVE_OUT = "ROVE_OUT";
    public static final String ROVE_THRESHOLD = "ROVE_THRESHOLD";
    private static String TAG = "IPPhoneSettings";
    public static final String WIFI_FIRST_TURNON = "WIFI_FIRST_TURNON";
    public static final String WIFI_SETTINGS_FIRST_LAUNCHED = "WIFI_SETTINGS_FIRST_LAUNCHED";

}
