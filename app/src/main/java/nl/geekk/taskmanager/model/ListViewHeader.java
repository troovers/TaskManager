package nl.geekk.taskmanager.model;

import nl.geekk.taskmanager.adapter.TaskAdapter;

/**
 * Created by Thomas on 15-6-2016.
 */
public class ListViewHeader implements ListViewItem {
    private String headerTitle;
    public final static int LIST_VIEW_ROW = 1;
    public final static int LIST_VIEW_HEADER = 2;

    public ListViewHeader(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    @Override
    public int getViewType() {
        return LIST_VIEW_HEADER;
    }
}
