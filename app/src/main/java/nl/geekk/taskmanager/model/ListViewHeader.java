package nl.geekk.taskmanager.model;

import nl.geekk.taskmanager.adapter.TaskAdapter;

/**
 * Created by Thomas on 15-6-2016.
 */
public class ListViewHeader implements ListViewItem {
    private String headerTitle;
    private int identifier;
    public final static int PASSED_DEADLINE_HEADER = 0;
    public final static int FUTURE_DEADLINE_HEADER = 1;

    public ListViewHeader(String headerTitle, int identifier) {
        this.headerTitle = headerTitle;
        this.identifier = identifier;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }
}
