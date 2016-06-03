package nl.geekk.taskmanager.model;

import android.content.Context;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.view.StartActivity;

/**
 * Created by Thomas on 3-6-2016.
 */
public class SPKeys {
    public Context context;

    public SPKeys(Context context) {
        this.context = context;
    }

    public String getFirstLoginString() {
        return context.getResources().getString(R.string.sp_first_login);
    }

    public String getRememberLoginString() {
        return context.getResources().getString(R.string.sp_remember_login);
    }

    public String getUserIdString() {
        return context.getResources().getString(R.string.sp_user_id);
    }

    public String getFirstNameString() {
        return context.getResources().getString(R.string.sp_first_name);
    }

    public String getLastNameString() {
        return context.getResources().getString(R.string.sp_last_name);
    }

    public String getApiKeyString() {
        return context.getResources().getString(R.string.sp_api_key);
    }

    public String getDateEditedString() {
        return context.getResources().getString(R.string.sp_date_edited);
    }
}
