package nl.geekk.taskmanager.model;

import android.util.Log;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import nl.geekk.taskmanager.adapter.TaskAdapter;

/**
 * Created by Thomas on 4-6-2016.
 */
public class Task implements ListViewItem {
    private int taskId;
    private String taskTitle;
    private String taskDescription;
    private int status;
    private Date createdAt;
    private Date deadline;
    private int identifier;
    private String[] months = {"JAN", "FEB", "APR", "MEI", "JUN", "JUL", "AUG", "SEP", "OKT", "NOV", "DEC"};

    public final static int TASK = 0;

    public Task(int taskId, String taskTitle, String taskDescription, int status, Date createdAt, Date deadline) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.identifier = TASK;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getDeadlineDay() {
        return (String) android.text.format.DateFormat.format("dd", deadline);
    }

    public String getDeadlineMonth() {
        int month = deadline.getMonth();

        return months[month-1];
    }

    public int getWeekNumber() {
        Calendar now = Calendar.getInstance();

        now.set(Calendar.YEAR, deadline.getYear());
        now.set(Calendar.MONTH, deadline.getMonth());
        now.set(Calendar.DATE, deadline.getDay());

        return now.get(Calendar.WEEK_OF_YEAR);
    }

    public String getMonthString(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }
}
