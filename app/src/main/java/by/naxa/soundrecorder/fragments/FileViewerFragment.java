package by.naxa.soundrecorder.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import by.naxa.soundrecorder.DBHelper;
import by.naxa.soundrecorder.R;
import by.naxa.soundrecorder.RecordingItem;
import by.naxa.soundrecorder.adapters.FileViewerAdapter;
import by.naxa.soundrecorder.listeners.OnDatabaseChangedListener;
import by.naxa.soundrecorder.listeners.OnStartDragListener;
import by.naxa.soundrecorder.listeners.SwapItemTouchHelperCallback;
import by.naxa.soundrecorder.util.Paths;

/**
 * Created by Daniel on 12/23/2014.
 */
public class FileViewerFragment extends Fragment implements OnDatabaseChangedListener, OnStartDragListener {
    private static final String LOG_TAG = "FileViewerFragment";

    private FileViewerAdapter mFileViewerAdapter;

    private ArrayList<RecordingItem> mRecordingItems;
    private DBHelper mDatabase;
    private LinearLayoutManager llm;
    private ItemTouchHelper mItemTouchHelper;

    public static FileViewerFragment newInstance() {
        FileViewerFragment f = new FileViewerFragment();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observer.startWatching();
    }

    @Override
    public void onDestroy() {
        observer.stopWatching();
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
        mDatabase = new DBHelper(getActivity());
        // instanciate new List for store recordingItems;
        if (mRecordingItems == null) {
            mRecordingItems = new ArrayList<>();
        }
        DBHelper.setOnDatabaseChangedListener(this);
        fillRecordingItemList();
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        //newest to oldest order (database stores from oldest to newest)
        llm = new LinearLayoutManager(
                getActivity(), RecyclerView.VERTICAL, true);
        llm.setStackFromEnd(true);

        mFileViewerAdapter = new FileViewerAdapter(getActivity(), llm, mRecordingItems, this);
        ItemTouchHelper.Callback callback = new SwapItemTouchHelperCallback(mFileViewerAdapter);
        // Set Custome CallBack to ItemtoucheHelper
        mItemTouchHelper = new ItemTouchHelper(callback);
        // Link ItemTouchAdapter to Recyclerview
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mFileViewerAdapter);

        return v;
    }

    private void fillRecordingItemList() {
        mDatabase.getCount();
        if (mRecordingItems != null) {
            mRecordingItems.clear();
        }
        for (int i = 0; i < mDatabase.getCount(); i++) {
            mRecordingItems.add(mDatabase.getItemAt(i));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFileViewerAdapter = null;
    }

    private final FileObserver observer =
            new FileObserver(Paths.combine(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                    Paths.SOUND_RECORDER_FOLDER)) {
                // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if (event == FileObserver.DELETE) {
                        // user deletes a recording file out of the app

                        final String filePath = Paths.combine(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                                Paths.SOUND_RECORDER_FOLDER, file);
                        Log.d(LOG_TAG, "File deleted [" + filePath + "]");

                        // remove file from database and recyclerview
                        mFileViewerAdapter.removeOutOfApp(filePath);
                    }
                }
            };

    @Override
    public void onNewDatabaseEntryAdded() {
        fillRecordingItemList();
        mFileViewerAdapter.notifyItemInserted(mRecordingItems.size() - 1);
        llm.scrollToPositionWithOffset(mRecordingItems.size() - 1, 0);
    }

    @Override
    //TODO
    public void onDatabaseEntryRenamed(int position) {
        fillRecordingItemList();
        mFileViewerAdapter.notifyItemChanged(position);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}




