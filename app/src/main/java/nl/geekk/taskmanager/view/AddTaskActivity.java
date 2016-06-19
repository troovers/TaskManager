package nl.geekk.taskmanager.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.model.Task;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText taskTitle, taskDescription;
    private TextView taskDeadline;
    private ImageButton pickDeadline;
    private Button addTask;
    private TaskManager taskManager;
    private int year, month, day;
    static final int DATE_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskTitle = (EditText) findViewById(R.id.new_task_title);
        taskDescription = (EditText) findViewById(R.id.new_task_description);
        taskDeadline = (TextView) findViewById(R.id.deadline);
        pickDeadline = (ImageButton) findViewById(R.id.pick_deadline);
        addTask = (Button) findViewById(R.id.add_task_button);

        taskManager = new TaskManager(this);

        /** Listener for click event of the buttons */
        taskDeadline.setClickable(true);
        taskDeadline.setOnClickListener(this);
        pickDeadline.setOnClickListener(this);
        addTask.setOnClickListener(this);

        /** Get the current date */
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        /** Display the current date in the TextView */
        updateDisplay();
    }

    @Override
    public void onBackPressed() {
        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }

    private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int pYear, int pMonth, int pDay) {
            year = pYear;
            month = pMonth;
            day = pDay;

            updateDisplay();
        }
    };

    private void updateDisplay() {
        taskDeadline.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, pDateSetListener, year, month, day);
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMinDate(System.currentTimeMillis() - 1000);

                return datePickerDialog;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_task_button:
                String title = taskTitle.getText().toString();
                String description = taskDescription.getText().toString();
                String[] deadline = taskDeadline.getText().toString().split("/");

                String year = deadline[2];
                String month = String.format("%02d", Integer.valueOf(deadline[1]));
                String day = String.format("%02d", Integer.valueOf(deadline[0]));

                if (!title.equals("") && !description.equals("") && !deadline.equals("")) {
                    new AddTask(title, description, year + "-" + month + "-" + day).execute();
                } else {
                    Snackbar.make(v, "Niet alle velden zijn gevuld", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                break;
            case R.id.deadline:
                showDialog(DATE_DIALOG_ID);
                break;
            case R.id.pick_deadline:
                showDialog(DATE_DIALOG_ID);
                break;
        }
    }

    private class AddTask extends AsyncTask<Void, Void, Boolean> {

        private String title, description, deadline;
        private ProgressDialog dialog;

        public AddTask(String title, String description, String deadline) {
            this.title = title;
            this.description = description;
            this.deadline = deadline;
            this.dialog = new ProgressDialog(AddTaskActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.dialog.setMessage("Loading..");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            taskManager.addTask(title, description, deadline);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            finish();
        }
    }
}
