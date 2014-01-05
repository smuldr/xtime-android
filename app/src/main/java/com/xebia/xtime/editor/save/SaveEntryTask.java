package com.xebia.xtime.editor.save;

import android.os.AsyncTask;

import com.xebia.xtime.shared.model.TimeSheetEntry;

/**
 * Asynchronous task to save the changes to a time sheet entry
 */
public class SaveEntryTask extends AsyncTask<TimeSheetEntry, Void, Boolean> {

    private final Listener mListener;

    public SaveEntryTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected Boolean doInBackground(TimeSheetEntry... params) {
        if (null == params || params.length < 1) {
            throw new NullPointerException("Missing TimeSheetEntry parameter");
        }
        return new SaveTimeSheetRequest(params[0]).submit();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mListener.onSaveComplete(result);
    }

    /**
     * Interface for listening for results from the SaveEntryTask
     */
    public interface Listener {
        public abstract void onSaveComplete(Boolean result);
    }
}