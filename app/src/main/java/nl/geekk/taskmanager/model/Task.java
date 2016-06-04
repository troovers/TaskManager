package nl.geekk.taskmanager.model;

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
        return String.valueOf(deadline.getDay());
    }

    public String getDeadlineMonth() {
        return String.valueOf(deadline.getMonth());
    }
}
