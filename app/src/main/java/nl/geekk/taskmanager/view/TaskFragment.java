package nl.geekk.taskmanager.view;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.adapter.TaskAdapter;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.model.ListViewHeader;
import nl.geekk.taskmanager.model.ListViewItem;
import nl.geekk.taskmanager.model.Task;

public class TaskFragment extends Fragment implements AdapterView.OnItemClickListener, TextWatcher {
    private Context context;
    private ListView listView;
    private EditText searchTasksField;
    private TextView noResults;
    private ArrayList<ListViewItem> listViewItems = new ArrayList<ListViewItem>();
    private TaskAdapter taskAdapter;
    private TaskManager taskManager;
    private OnFragmentInteractionListener mListener;

    private static final String API_KEY = "apiKey";

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();

        /*Bundle args = new Bundle();
        args.putString(API_KEY, apiKey);
        fragment.setArguments(args);*/

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments() != null) {
            apiKey = getArguments().getString(API_KEY);
        }*/

        taskManager = ((MainActivity) getActivity()).getTaskManager();//new TaskManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        ((MainActivity) getActivity()).setActionBarTitle("Taken");

        searchTasksField = (EditText) view.findViewById(R.id.search_tasks);
        searchTasksField.addTextChangedListener(this);

        listView = (ListView) view.findViewById(R.id.tasks);
        listView.setOnItemClickListener(this);

        noResults = (TextView) view.findViewById(R.id.no_results);

        taskAdapter = new TaskAdapter(context, getActivity().getLayoutInflater(), listViewItems, taskManager);
        listView.setAdapter(taskAdapter);

        new InitializeListView().execute();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //new InitializeListView().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListViewItem listViewItem = (ListViewItem) parent.getItemAtPosition(position);

        if(listViewItem instanceof Task) {
            Task task = (Task) listViewItem;

            Snackbar.make(view, "Je klikt op: " + task.getTaskTitle(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence sequence, int start, int before, int count) {
        int textLength = sequence.length();
        boolean deadlinePassedHeader = false;
        boolean deadlineFutureHeader = false;

        ArrayList<ListViewItem> tempArrayList = new ArrayList<ListViewItem>();

        for(ListViewItem listViewItem: listViewItems){
            if(listViewItem instanceof Task) {
                Task task = (Task) listViewItem;

                if (textLength <= task.getTaskTitle().length() && task.getTaskTitle().toLowerCase().contains(sequence.toString().toLowerCase())) {
                    if (new Date().after(task.getDeadline()) && !deadlinePassedHeader) {
                        tempArrayList.add(new ListViewHeader("Verstreken deadlines"));

                        deadlinePassedHeader = true;
                    } else if(new Date().before(task.getDeadline()) && !deadlineFutureHeader) {
                        tempArrayList.add(new ListViewHeader("Toekomstige deadlines"));

                        deadlineFutureHeader = true;
                    }

                    tempArrayList.add(listViewItem);
                }
            }
        }

        if(tempArrayList.isEmpty()) {
            listView.setEmptyView(getView().findViewById(R.id.no_results));
        }

        taskAdapter = new TaskAdapter(context, getActivity().getLayoutInflater(), tempArrayList, taskManager);
        listView.setAdapter(taskAdapter);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class InitializeListView extends AsyncTask<Void, Void, ArrayList<Task>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
        }

        @Override
        protected ArrayList<Task> doInBackground(Void... args) {
            ArrayList<Task> tasksArray = taskManager.getTasks();

            return tasksArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Task> tasksArray) {
            boolean deadlinePassedHeader = false;
            boolean deadlineFutureHeader = false;

            listViewItems.clear();

            if(tasksArray.isEmpty()) {
                listView.setEmptyView(noResults);
            } else {
                for(Task t: tasksArray) {
                    if (new Date().after(t.getDeadline()) && !deadlinePassedHeader) {
                        listViewItems.add(new ListViewHeader("Verstreken deadlines"));

                        deadlinePassedHeader = true;
                    } else if(new Date().before(t.getDeadline()) && !deadlineFutureHeader) {
                        listViewItems.add(new ListViewHeader("Toekomstige deadlines"));

                        deadlineFutureHeader = true;
                    }

                    listViewItems.add(t);

                    Log.d("Added task", t.getTaskTitle());
                }
            }

            taskAdapter.notifyDataSetChanged();
        }
    }
}
