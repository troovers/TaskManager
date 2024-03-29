package nl.geekk.taskmanager.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.model.MyApplication;
import nl.geekk.taskmanager.model.SPKeys;
import nl.geekk.taskmanager.receivers.ConnectionReceiver;

public class MainActivity extends AppCompatActivity implements TaskFragment.OnFragmentInteractionListener, NotesFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ConnectionReceiver.ConnectivityReceiverListener {
    private SharedPreferences mainAccountPreferences, userDefinedPreferences;
    private SPKeys spKeys = new SPKeys(this);
    private String apiKey;
    private TaskManager taskManager;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mainAccountPreferences = getSharedPreferences("main_login_preferences", MODE_PRIVATE);
        userDefinedPreferences = getSharedPreferences("user_defined_preferences", MODE_PRIVATE);

        apiKey = mainAccountPreferences.getString(spKeys.getApiKeyString(), "");

        taskManager = new TaskManager(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.root_layout, TaskFragment.newInstance(), "Home").commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public TaskManager getTaskManager() {
        if (taskManager == null) {
            return new TaskManager(this);
        } else {
            return taskManager;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_task_button) {
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tasks) {
            getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, TaskFragment.newInstance(), "Taken").addToBackStack(null).commit();
        } else if (id == R.id.nav_notes) {
            getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, NotesFragment.newInstance(), "Notities").addToBackStack(null).commit();
        } else if (id == R.id.nav_manage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, PreferencesFragment.newInstance(), "Instellingen").addToBackStack(null).commit();
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("main_login_preferences", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(spKeys.getRememberLoginString(), false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setActionBarTitle(String title) {
        //getSupportActionBar().setTitle(title);
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();

        showConnectivityError();

        return isConnected;
    }

    // Showing the status in Snackbar
    private void showConnectivityError() {
        Snackbar snackbar = Snackbar.make(drawerLayout, "Geen internetverbinding", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        snackbar.setActionTextColor(Color.RED);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            showConnectivityError();
        }
    }
}
