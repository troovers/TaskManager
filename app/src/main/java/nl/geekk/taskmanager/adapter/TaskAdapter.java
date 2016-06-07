package nl.geekk.taskmanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.model.Task;
import nl.geekk.taskmanager.view.MainActivity;
import nl.geekk.taskmanager.view.MainFragment;

/**
 * Created by Thomas on 4-6-2016.
 */
/*public class TaskAdapter extends BaseAdapter {
    private MainActivity context;
    private LayoutInflater inflator;
    private ArrayList<Task> tasks;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");

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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;

        // Create new of gebruik een al bestaande (recycled by Android)
        if(convertView == null) {
            convertView = inflator.inflate(R.layout.listview_row, null);

            viewHolder = new ViewHolder();
            viewHolder.taskChecked = (CheckBox) convertView.findViewById(R.id.check_task);
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

        if(new Date().after(task.getDeadline())) {
            viewHolder.deadlineDay.setTextColor(Color.RED);
            viewHolder.deadlineMonth.setTextColor(Color.RED);
        }

        final View finalConvertView = convertView;
        viewHolder.taskChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = viewHolder.taskChecked.isChecked();

                if(isChecked) {
                    Animation fadeOut = new AlphaAnimation(1, 0);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(500);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            tasks.remove(position);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            viewHolder.taskChecked.setChecked(false);
                            //TaskAdapter.this.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    finalConvertView.startAnimation(fadeOut);
                    //TaskAdapter.this.notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public TextView taskTitle, taskDescription, deadlineDay, deadlineMonth;
        public CheckBox taskChecked;
    }
}*/

public class TaskAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    private LayoutInflater inflator;
    private ArrayList<Task> tasks;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TaskAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Task> tasks) {
        this.context = (MainActivity) context;
        this.inflator = layoutInflater;
        this.tasks = tasks;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_listview_row;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = inflator.inflate(R.layout.listview_row, null);

        SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe_listview_row);

        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        //swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_wrapper));

        swipeLayout.setBottomViewIds(R.id.bottom_wrapper_delete, R.id.bottom_wrapper_complete, -1, -1);

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        return view;
    }

    @Override
    public void fillValues(final int position, final View convertView) {
        TextView taskTitle = (TextView) convertView.findViewById(R.id.task_title);
        TextView taskDescription = (TextView) convertView.findViewById(R.id.task_description);
        final TextView deadlineDay = (TextView) convertView.findViewById(R.id.deadline_day);
        TextView deadlineMonth = (TextView) convertView.findViewById(R.id.deadline_month);

        Task task = (Task) tasks.get(position);

        taskTitle.setText(task.getTaskTitle());
        taskDescription.setText(task.getTaskDescription());
        deadlineDay.setText(task.getDeadlineDay());
        deadlineMonth.setText(task.getDeadlineMonth());

        if(new Date().after(task.getDeadline())) {
            deadlineDay.setTextColor(Color.RED);
            deadlineMonth.setTextColor(Color.RED);
        }

        convertView.findViewById(R.id.task_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasks.remove(position);
                TaskAdapter.this.notifyDataSetChanged();
                Toast.makeText(context, "Taak verwijderd", Toast.LENGTH_SHORT).show();

                ViewGroup viewGroup = (ViewGroup) convertView.getParent();

                SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe_listview_row);

                swipeLayout.close();

                repaintViews(viewGroup);
            }
        });
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

    public void repaintViews(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if(i < tasks.size()) {
                View child = viewGroup.getChildAt(i);

                ((TextView) child.findViewById(R.id.deadline_day)).setTextColor(Color.parseColor("#CCCCCC"));
                ((TextView) child.findViewById(R.id.deadline_month)).setTextColor(Color.parseColor("#CCCCCC"));

                if (new Date().after(tasks.get(i).getDeadline())) {
                    ((TextView) child.findViewById(R.id.deadline_day)).setTextColor(Color.RED);
                    ((TextView) child.findViewById(R.id.deadline_month)).setTextColor(Color.RED);
                }
            }
        }
    }
}
