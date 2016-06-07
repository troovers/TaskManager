package nl.geekk.taskmanager.view;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Date;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.adapter.TaskAdapter;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.model.Task;

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener, TextWatcher {
    private Context context;
    private ListView listView;
    private EditText searchTasksField;
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private TaskAdapter taskAdapter;
    private TaskManager taskManager;
    private SharedPreferences sharedPreferences;
    private OnFragmentInteractionListener mListener;

    private static final String API_KEY = "apiKey";
    private String apiKey;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String apiKey) {
        MainFragment fragment = new MainFragment();

        Bundle args = new Bundle();
        args.putString(API_KEY, apiKey);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            apiKey = getArguments().getString(API_KEY);
        }

        taskManager = MainActivity.getTaskManager();//new TaskManager(apiKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((MainActivity) getActivity()).setActionBarTitle("Taken");

        searchTasksField = (EditText) view.findViewById(R.id.search_tasks);
        searchTasksField.addTextChangedListener(this);

        listView = (ListView) view.findViewById(R.id.tasks);
        listView.setOnItemClickListener(this);
        listView.destroyDrawingCache();

        taskAdapter = new TaskAdapter(context, getActivity().getLayoutInflater(), tasks);
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
        new InitializeListView().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = (Task) parent.getItemAtPosition(position);

        Snackbar.make(view, "Je klikt op: "+task.getTaskTitle(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence sequence, int start, int before, int count) {
        int textLength = sequence.length();
        ArrayList<Task> tempArrayList = new ArrayList<Task>();

        for(Task t: tasks){
            if (textLength <= t.getTaskTitle().length()) {
                if (t.getTaskTitle().toLowerCase().contains(sequence.toString().toLowerCase())) {
                    tempArrayList.add(t);
                }
            }
        }

        if(tempArrayList.isEmpty()) {
            listView.setEmptyView(getView().findViewById(R.id.no_results));
        }

        taskAdapter = new TaskAdapter(context, getActivity().getLayoutInflater(), tempArrayList);
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
            tasks.clear();

            for(Task t: tasksArray) {
                tasks.add(t);
            }

            taskAdapter.notifyDataSetChanged();
        }
    }
}
