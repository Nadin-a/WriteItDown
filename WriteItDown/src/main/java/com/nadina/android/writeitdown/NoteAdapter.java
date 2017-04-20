package com.nadina.android.writeitdown;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nadina.android.writeitdown.data.NoteContract;

/**
 * Created by Nadina on 01.03.2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ToDoViewHolder> {


    private Context mContext;
    private Cursor mCursor;

    NoteAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String note = mCursor.getString(mCursor.getColumnIndex(NoteContract.NoteEntry.NOTE));
        String date = mCursor.getString(mCursor.getColumnIndex(NoteContract.NoteEntry.DATE));

        long id = mCursor.getLong(mCursor.getColumnIndex(NoteContract.NoteEntry._ID));

        holder.tv_text.setText(note);
        holder.tv_data.setText(date);

        holder.itemView.setTag(id);
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.my_item, parent, false);
        return  new ToDoViewHolder(v);
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (mCursor != null) {
            this.notifyDataSetChanged();
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class ToDoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView tv_text;
        TextView tv_data;
        SharedPreferences sharedPreferences;

        public ToDoViewHolder(View itemView) {
            super(itemView);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            tv_text = (TextView) itemView.findViewById(R.id.tv_myText);
            tv_data = (TextView) itemView.findViewById(R.id.tv_date);
            if (sharedPreferences.getBoolean(itemView.getContext().getString(R.string.check_box_date_key),
                    itemView.getContext().getResources().getBoolean(R.bool.pref_show_date_default))) {
                tv_data.setVisibility(View.VISIBLE);
            } else {
                tv_data.setVisibility(View.INVISIBLE);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            ((NoteListActivity) view.getContext()).openNote((long)itemView.getTag());
        }
    }

}
