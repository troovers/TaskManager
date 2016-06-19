package nl.geekk.taskmanager.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.geekk.taskmanager.model.SPKeys;
import nl.geekk.taskmanager.model.ServiceHandler;
import nl.geekk.taskmanager.notifications.NotificationEventReceiver;

public class StartActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT) ;
    private SPKeys spKeys = new SPKeys(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("main_login_preferences", MODE_PRIVATE);

        new PrefetchData().execute();

        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }

    private class PrefetchData extends AsyncTask<Void, Void, StartParams> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
        }

        @Override
        protected StartParams doInBackground(Void... arg0) {
            StartParams startParams = new StartParams();

            startParams.first_login = sharedPreferences.getBoolean(spKeys.getFirstLoginString(), true);
            startParams.remember_login = sharedPreferences.getBoolean(spKeys.getRememberLoginString(), false);
            startParams.api_key = sharedPreferences.getString(spKeys.getApiKeyString(), "");
            startParams.date_edited = sharedPreferences.getString(spKeys.getDateEditedString(), "");

            if(!startParams.first_login && startParams.remember_login) {
                int user_id = sharedPreferences.getInt(spKeys.getUserIdString(), 0);

                // Check if the account exists
                boolean exists = checkExistence(user_id);

                if(!exists) {
                    startParams.message = "Uw account bestaat niet meer";
                    startParams.remember_login = false;
                }

                // Check for changes made to the last edited date
                boolean changed = checkForChanges(user_id, startParams.date_edited);

                if(changed) {
                    startParams.message = "Uw gegevens zijn online gewijzigd";
                    startParams.remember_login = false;
                }
            }

            return startParams;
        }

        @Override
        protected void onPostExecute(StartParams startParams) {
            if(startParams.first_login || !startParams.remember_login) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);

                if(startParams.message != "") {
                    intent.putExtra("MESSAGE", startParams.message);
                }

                startActivity(intent);
            } else {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }

            finish();
        }

    }

    public class StartParams {
        public boolean first_login, remember_login;
        public String message;
        public String date_edited;
        public String api_key;
    }

    public boolean checkExistence(int userId) {
        ServiceHandler serviceHandler = new ServiceHandler();

        JSONObject jsonObj = serviceHandler.getJSONByGET("http://smash.nl/task_manager/v1/accountExists/"+userId, "", "");

        if (jsonObj != null) {
            try {
                boolean exists = jsonObj.getBoolean("exists");

                return exists;
            } catch (JSONException e) {
                Log.e("JSON", e.getMessage());
                return false;
            }
        } else {
            Log.e("SERVICEHANDLER", "Couldn't get any data from the url");
            return false;
        }
    }

    public boolean checkForChanges(int userId, String dateEdited) {
        Date app_edited = null;
        Date database_edited = null;

        try {
            database_edited = datetimeFormat.parse(dateEdited);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ServiceHandler serviceHandler = new ServiceHandler();

        // Making a request to url and getting response
        JSONObject jsonObj = serviceHandler.getJSONByGET("http://smash.nl/task_manager/v1/accountChanged/"+userId, "", "");

        if (jsonObj != null) {
            try {
                boolean error = jsonObj.getBoolean("error");

                if(!error) {
                    try {
                        app_edited = datetimeFormat.parse(jsonObj.getString("edited"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return app_edited.getTime() > database_edited.getTime();
                } else {
                    return false;
                }
            } catch (JSONException e) {
                Log.e("JSON", e.getMessage());
                return false;
            }
        } else {
            Log.e("SERVICEHANDLER", "Couldn't get any data from the url");
            return false;
        }
    }
}
