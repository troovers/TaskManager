package nl.geekk.taskmanager.model;

import java.text.DateFormatSymbols;
import java.util.Date;

/**
 * Created by Thomas on 4-6-2016.
 */
public class Task {
    private int taskId;
    private String taskTitle;
    private String taskDescription;
    private int status;
    private Date createdAt;
    private Date deadline;
    private String[] months = {"JAN", "FEB", "APR", "MEI", "JUN", "JUL", "AUG", "SEP", "OKT", "NOV", "DEC"};

    public Task(int taskId, String taskTitle, String taskDescription, int status, Date createdAt, Date deadline) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = deadline;
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

    public String getMonthString(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}
