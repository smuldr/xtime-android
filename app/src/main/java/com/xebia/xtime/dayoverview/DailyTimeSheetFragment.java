package com.xebia.xtime.dayoverview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xebia.xtime.shared.model.TimeSheetEntry;

import java.util.ArrayList;
import java.util.List;

public class DailyTimeSheetFragment extends ListFragment {

    private static final String ARG_TIME_SHEETS = "time_sheets";
    private List<TimeSheetEntry> mTimeSheetEntries;
    private Listener mListener;

    public DailyTimeSheetFragment() {
        // Required empty public constructor
    }

    public static DailyTimeSheetFragment getInstance(ArrayList<TimeSheetEntry> timeSheetEntries) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_TIME_SHEETS, timeSheetEntries);
        DailyTimeSheetFragment fragment = new DailyTimeSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Callback to refresh the list view when the list of time sheets has been changed
     */
    public void onDataSetChanged() {
        ((ArrayAdapter) getListView().getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (null != getArguments()) {
            mTimeSheetEntries = getArguments().getParcelableArrayList(ARG_TIME_SHEETS);
        }
        if (null == mTimeSheetEntries) {
            throw new NullPointerException("Missing ARG_TIME_SHEETS argument");
        }

        DailyTimeSheetListAdapter adapter = new DailyTimeSheetListAdapter(getActivity(),
                mTimeSheetEntries);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TimeSheetEntry item = (TimeSheetEntry) l.getItemAtPosition(position);
        mListener.onTimeSheetEntrySelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "DailyTimeSheetFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface for handling clicks on the list of TimeSheetEntries
     */
    public interface Listener {
        public abstract void onTimeSheetEntrySelected(TimeSheetEntry selected);
    }
}