package com.example.notes.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Data.CardSource;
import com.example.notes.Data.Note;
import com.example.notes.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private final static String TAG = "NotesAdapter";
    private CardSource notes;
    private final Fragment fragment;
    private OnItemClickListener itemClickListener;
    private int menuPosition;
    private float touchPositionX;
    private float touchPositionY;

    public NotesAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setCardSource(CardSource notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_note_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder viewHolder, int position) {
        viewHolder.setData(notes.getNoteData(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public int getMenuPosition() {
        return menuPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCardTitle;
        private TextView tvCardShortContents;
        private TextView tvCardCreationDate;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvCardTitle = itemView.findViewById(R.id.card_title);
            tvCardShortContents = itemView.findViewById(R.id.card_short_content);
            tvCardCreationDate = itemView.findViewById(R.id.card_creation_date);

            registerContextMenu(itemView);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(v, getAdapterPosition());
            });

            itemView.setOnTouchListener((view, motionEvent) -> {
                touchPositionX = motionEvent.getX();
                touchPositionY = motionEvent.getY();
                return false;
            });

            itemView.setOnLongClickListener(v -> {
                menuPosition = getLayoutPosition();
                itemView.showContextMenu(touchPositionX, touchPositionY);
                return true;
            });
        }

        private void registerContextMenu(@NonNull View itemView) {
            if (fragment != null){
                itemView.setOnLongClickListener(view -> {
                    menuPosition = getLayoutPosition();
                    return false;
                });

                fragment.registerForContextMenu(itemView);
            }
        }

        public void setData(Note noteData){
            tvCardTitle.setText(noteData.getTitle());
            tvCardTitle.setBackgroundColor(Color.parseColor(noteData.getHeaderColor()));

            String noteContents = noteData.getContents();
            noteContents = noteContents.length() > 100 ?
                    noteContents.substring(0, 97) + "..." : noteContents;
            tvCardShortContents.setText(noteContents);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault());
            tvCardCreationDate.setText(simpleDateFormat.format(noteData.
                    getCreationDate().getTime()));
        }
    }
}
