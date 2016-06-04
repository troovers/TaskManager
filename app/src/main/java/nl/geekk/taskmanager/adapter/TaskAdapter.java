package nl.geekk.taskmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.model.Task;
import nl.geekk.taskmanager.view.MainActivity;

/**
 * Created by Thomas on 4-6-2016.
 */
public class TaskAdapter extends BaseAdapter {
    private MainActivity context;
    private LayoutInflater inflator;
    private ArrayList<Task> tasks;

    public TaskAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Task> tasks)
    {
        this.context = (MainActivity) context;
        this.inflator = layoutInflater;
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // Create new of gebruik een al bestaande (recycled by Android)
        if(convertView == null) {
            convertView = inflator.inflate(R.layout.listview_row, null);

            viewHolder = new ViewHolder();
            viewHolder.taskTitle = (TextView) convertView.findViewById(R.id.task_title);
            viewHolder.taskDescription = (TextView) convertView.findViewById(R.id.task_description);
            viewHolder.deadlineDay = (TextView) convertView.findViewById(R.id.deadline_day);
            viewHolder.deadlineMonth = (TextView) convertView.findViewById(R.id.deadline_month);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Task task = (Task) tasks.get(position);

        viewHolder.taskTitle.setText(task.getTaskTitle());
        viewHolder.taskDescription.setText(task.getTaskDescription());
        viewHolder.deadlineDay.setText(task.getDeadlineDay());
        viewHolder.deadlineMonth.setText(task.getDeadlineMonth());

        return convertView;
    }

    private static class ViewHolder {
        public TextView taskTitle, taskDescription, deadlineDay, deadlineMonth;
    }
}
