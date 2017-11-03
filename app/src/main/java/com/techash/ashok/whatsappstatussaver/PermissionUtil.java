package com.techash.ashok.whatsappstatussaver;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ashok on 11/3/2017.
 */

public class PermissionUtil {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    PermissionUtil(Context context)
    {
        this.context=context;
        sharedPreferences=context.getSharedPreferences(context.getString(R.string.permission_preference),Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void updatePermissionPreference(String permission)
    {
        switch(permission)
        {
            case "read_storage":
                editor.putBoolean(context.getString(R.string.permission_read_storage),true);
                editor.commit();
                break;
            case "write_storage":
                editor.putBoolean(context.getString(R.string.permission_write_storage),true);
                editor.commit();
                break;
        }

    }
    public boolean checkPermissionPreference(String permission)
    {
        boolean isShown=false;
        switch (permission)
        {
            case "read_storage":
                isShown=sharedPreferences.getBoolean(context.getString(R.string.permission_read_storage),false);
                break;
            case "write_storage":
                isShown=sharedPreferences.getBoolean(context.getString(R.string.permission_write_storage),false);
                break;
        }
        return isShown;
    }

}
