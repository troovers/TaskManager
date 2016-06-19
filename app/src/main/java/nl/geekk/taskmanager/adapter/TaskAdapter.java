package nl.geekk.taskmanager.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.Date;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.model.ListViewHeader;
import nl.geekk.taskmanager.model.ListViewItem;
import nl.geekk.taskmanager.model.Task;
import nl.geekk.taskmanager.view.MainActivity;

/**
 * Created by Thomas on 4-6-2016.
 */
public class TaskAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    private LayoutInflater inflator;
    private ArrayList<ListViewItem> listViewItems;
    private boolean passedDeadlines = false;
    private boolean futureDeadlines = false;
    private TaskManager taskManager;
    public final static int LIST_VIEW_ROW = 0;
    public final static int LIST_VIEW_HEADER = 1;

    public TaskAdapter(Context context, LayoutInflater layoutInflater, ArrayList<ListViewItem> listViewItems, TaskManager taskManager) {
        this.context = (MainActivity) context;
        this.inflator = layoutInflater;
        this.listViewItems = listViewItems;
        this.taskManager = taskManager;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_listview_row;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = null;

        switch (getItemViewType(position)) {
            case LIST_VIEW_ROW:
                view = inflator.inflate(R.layout.listview_row, null);

                SwipeLayout swipeLayoutRow = (SwipeLayout) view.findViewById(R.id.swipe_listview_row);

                //set show mode.
                swipeLayoutRow.setShowMode(SwipeLayout.ShowMode.LayDown);

                //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
                //swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_wrapper));

                swipeLayoutRow.setBottomViewIds(R.id.bottom_wrapper_delete, R.id.bottom_wrapper_complete, -1, -1);

                swipeLayoutRow.addSwipeListener(new SwipeLayout.SwipeListener() {
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
                break;
            case LIST_VIEW_HEADER:
                view = inflator.inflate(R.layout.listview_header, null);

                SwipeLayout swipeLayoutHeader = (SwipeLayout) view.findViewById(R.id.swipe_listview_row);
                swipeLayoutHeader.setBottomViewIds(-1, -1, -1, -1);

                break;
        }

        return view;
    }

    @Override
    public void fillValues(final int position, final View convertView) {
        switch (getItemViewType(position)) {
            case LIST_VIEW_ROW:
                TextView taskTitle = (TextView) convertView.findViewById(R.id.task_title);
                TextView taskDescription = (TextView) convertView.findViewById(R.id.task_description);
                final TextView deadlineDay = (TextView) convertView.findViewById(R.id.deadline_day);
                TextView deadlineMonth = (TextView) convertView.findViewById(R.id.deadline_month);

                final Task task = (Task) listViewItems.get(position);

                taskTitle.setText(task.getTaskTitle());
                taskDescription.setText(task.getTaskDescription());
                deadlineDay.setText(task.getDeadlineDay());
                deadlineMonth.setText(task.getDeadlineMonth());

                if (new Date().after(task.getDeadline())) {
                    deadlineDay.setTextColor(Color.RED);
                    deadlineMonth.setTextColor(Color.RED);
                } else {
                    deadlineDay.setTextColor(Color.parseColor("#CCCCCC"));
                    deadlineMonth.setTextColor(Color.parseColor("#CCCCCC"));
                }

                convertView.findViewById(R.id.task_delete_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new UpdateTask(position, "delete", task, taskManager, convertView).execute();
                    }
                });

                convertView.findViewById(R.id.task_complete_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new UpdateTask(position, "complete", task, taskManager, convertView).execute();
                    }
                });

                break;
            case LIST_VIEW_HEADER:
                TextView taskHeader = (TextView) convertView.findViewById(R.id.listview_header);
                ImageView taskHeaderIcon = (ImageView) convertView.findViewById(R.id.listview_header_icon);

                ListViewHeader listViewHeader = (ListViewHeader) listViewItems.get(position);

                taskHeader.setText(listViewHeader.getHeaderTitle());

                if(listViewHeader.getIdentifier() == ListViewHeader.PASSED_DEADLINE_HEADER) {
                    taskHeaderIcon.setBackgroundResource(R.drawable.warning);
                    taskHeader.setTextColor(Color.RED);
                } else {
                    taskHeaderIcon.setBackgroundResource(R.drawable.calendar);
                    taskHeader.setTextColor(Color.parseColor("#49ab4c"));
                }

                break;
        }
    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ListViewItem listViewItem = (ListViewItem) getItem(position);

        if(listViewItem instanceof Task) {
            return LIST_VIEW_ROW;
        } else {
            return LIST_VIEW_HEADER;
        }
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int countTasks(int identifier) {
        int count = 0;

        for(ListViewItem listViewItem: listViewItems) {
            if(listViewItem instanceof Task) {
                Task task = (Task) listViewItem;

                if(identifier == ListViewHeader.PASSED_DEADLINE_HEADER) {
                    if (new Date().after(task.getDeadline())) {
                        count += 1;
                    }
                } else if(identifier == ListViewHeader.FUTURE_DEADLINE_HEADER) {
                    if (new Date().before(task.getDeadline())) {
                        count += 1;
                    }
                }
            }
        }

        return count;
    }

    public int getPosition(int identifier) {
        for (ListViewItem listViewItem: listViewItems) {
            if(listViewItem.getIdentifier() == identifier) {
                return listViewItems.indexOf(listViewItem);
            }
        }

        // Header doesn't exist in the listview
        return -1;
    }

    public void checkIfHeadersRequired() {
        if(countTasks(ListViewHeader.PASSED_DEADLINE_HEADER) == 0) {
            int position = getPosition(ListViewHeader.PASSED_DEADLINE_HEADER);

            if(position >= 0) {
                listViewItems.remove(position);
            }
        }

        if(countTasks(ListViewHeader.FUTURE_DEADLINE_HEADER) == 0) {
            int position = getPosition(ListViewHeader.FUTURE_DEADLINE_HEADER);

            if(position >= 0) {
                listViewItems.remove(position);
            }
        }
    }

    private class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        private int position;
        private Task task;
        private String action;
        private TaskManager taskManager;
        private View convertView;
        private ProgressDialog dialog;

        public UpdateTask(int position, String action, Task task, TaskManager taskManager, View convertView) {
            this.position = position;
            this.action = action;
            this.task = task;
            this.taskManager = taskManager;
            this.convertView = convertView;

            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Taak voltooien..");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            if (action.equals("delete")) {
                result = taskManager.deleteTask(task.getTaskId());
            } else if (action.equals("complete")) {
                result = taskManager.completeTask(task);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                listViewItems.remove(position);

                checkIfHeadersRequired();

                TaskAdapter.this.notifyDataSetChanged();

                if (getCount() == 0) {
                    ((ListView) convertView.getParent()).setEmptyView(context.findViewById(R.id.no_results));
                }

                Toast.makeText(context, "Taak voltooid", Toast.LENGTH_LONG).show();

                SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe_listview_row);

                swipeLayout.close();
            }
        }
    }
}
