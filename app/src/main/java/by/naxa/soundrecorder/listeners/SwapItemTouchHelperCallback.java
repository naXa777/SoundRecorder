package by.naxa.soundrecorder.listeners;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import by.naxa.soundrecorder.adapters.FileViewerAdapter;

public class SwapItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final FileViewerAdapter mFileViewerAdapter;

    public SwapItemTouchHelperCallback(FileViewerAdapter mFileViewerAdapter) {
        this.mFileViewerAdapter = mFileViewerAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mFileViewerAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mFileViewerAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
