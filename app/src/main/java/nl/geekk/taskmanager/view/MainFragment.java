package nl.geekk.taskmanager.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.adapter.TaskAdapter;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.model.Task;

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Context context;
    private ListView listView;
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

        taskManager = new TaskManager(apiKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) view.findViewById(R.id.tasks);
        listView.setOnItemClickListener(this);

        //tasks.add(new Task(2, "Titel", "omschrijving", 1, new Date("2016-03-02 14:33:33"), new Date("2016-03-05")));

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
