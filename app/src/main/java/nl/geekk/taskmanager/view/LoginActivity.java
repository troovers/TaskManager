package nl.geekk.taskmanager.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.model.SPKeys;
import nl.geekk.taskmanager.model.ServiceHandler;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SPKeys spKeys = new SPKeys(this);
    private UserLoginTask mAuthTask = null;
    private EditText usernameField;
    private EditText passwordField;
    private CheckBox rememberField;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences("main_login_preferences", MODE_PRIVATE);

        // Set up the login form.
        usernameField = (EditText) findViewById(R.id.username);

        passwordField = (EditText) findViewById(R.id.password);
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }

                return false;
            }
        });

        rememberField = (CheckBox) findViewById(R.id.remember_login);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        usernameField.setError(null);
        passwordField.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        boolean remember = rememberField.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.error_incorrect_password));
            focusView = passwordField;

            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameField.setError(getString(R.string.error_field_required));
            focusView = usernameField;

            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuthTask = new UserLoginTask(username, password, remember);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String username;
        private final String password;
        private final boolean remember;
        private int userId = 0;
        private String apiKey = "";
        private String userName, firstName, lastName, edited;

        UserLoginTask(String username, String password, boolean remember) {
            this.username = username;
            this.password = password;
            this.remember = remember;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();

            // Making a request to url and getting response
            JSONObject jsonObj = serviceHandler.getJSONByPOST("http://smash.nl/task_manager/v1/login", "username="+username+"&password="+password, apiKey);

            if (jsonObj != null) {
                try {
                    boolean error = jsonObj.getBoolean("error");

                    if(!error) {
                        userId = jsonObj.getInt("userId");
                        userName = jsonObj.getString("username");
                        firstName = jsonObj.getString("firstName");
                        lastName = jsonObj.getString("lastName");
                        edited = jsonObj.getString("edited");
                        apiKey = jsonObj.getString("apiKey");
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

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean(spKeys.getFirstLoginString(), false);
                editor.putBoolean(spKeys.getRememberLoginString(), remember);
                editor.putInt(spKeys.getUserIdString(), userId);
                editor.putString(spKeys.getFirstNameString(), firstName);
                editor.putString(spKeys.getLastNameString(), lastName);
                editor.putString(spKeys.getDateEditedString(), edited);
                editor.putString(spKeys.getApiKeyString(), apiKey);

                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            } else {
                passwordField.setError(getString(R.string.error_incorrect_password));
                passwordField.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

