package nl.geekk.taskmanager.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nl.geekk.taskmanager.model.ServiceHandler;
import nl.geekk.taskmanager.model.Task;

/**
 * Created by Thomas on 4-6-2016.
 */
public class TaskManager {
    private String apiKey;
    private ServiceHandler serviceHandler;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT) ;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT) ;

    public TaskManager(String apiKey) {
        this.apiKey = apiKey;
        this.serviceHandler = new ServiceHandler();
    }

    public boolean addTask(String title, String description, String deadline) {
        Log.d("DEADLINE", "date: "+deadline);
        boolean result = false;

        JSONObject jsonObject = serviceHandler.getJSONByPOST("http://smash.nl/task_manager/v1/tasks", "task="+title+"&description="+description+"&deadline="+deadline, apiKey);

        if (jsonObject != null) {
            try {
                result = jsonObject.getBoolean("error");

                if(result) {
                    Log.e("JSON", "error");
                }
            } catch (JSONException e) {
                Log.e("JSON", e.getMessage());
            }
        } else {
            Log.e("SERVICEHANDLER", "Couldn't get any data from the url");
        }

        return result;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();

        JSONObject jsonObject = serviceHandler.getJSONByGET("http://smash.nl/task_manager/v1/tasks", "", apiKey);

        if (jsonObject != null) {
            try {
                boolean error = jsonObject.getBoolean("error");
                JSONArray tasksArray = jsonObject.getJSONArray("tasks");

                if(!error) {
                    tasks = getTasksFromArray(tasksArray);
                } else {
                    Log.e("JSON", "error");
                }
            } catch (JSONException e) {
                Log.e("JSON", e.getMessage());
            }
        } else {
            Log.e("SERVICEHANDLER", "Couldn't get any data from the url");
        }

        return tasks;
    }

    public ArrayList<Task> getTasksFromArray(JSONArray tasksArray) {
        ArrayList<Task> tasks = new ArrayList<Task>();

        try {
            if (tasksArray.length() > 0) {
                for (int i = 0; i < tasksArray.length(); i++) {
                    JSONObject taskObject = tasksArray.getJSONObject(i);
                    int taskId = taskObject.getInt("task_id");
                    String task = taskObject.getString("task");
                    String description = taskObject.getString("description");
                    int status = taskObject.getInt("status");

                    Date createdAt = null;
                    Date deadline = null;

                    try {
                        createdAt = datetimeFormat.parse(taskObject.getString("createdAt"));
                        deadline = dateFormat.parse(taskObject.getString("deadline"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    tasks.add(new Task(taskId, task, description, status, createdAt, deadline));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tasks;
    }
}
