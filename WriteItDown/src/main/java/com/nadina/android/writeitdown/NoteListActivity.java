package com.nadina.android.writeitdown;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.nadina.android.writeitdown.Note.NoteActivity;
import com.nadina.android.writeitdown.Settings.SettingsActivity;
import com.nadina.android.writeitdown.data.NoteContract;
import com.nadina.android.writeitdown.data.NoteDbHelper;

import java.text.DateFormat;
import java.util.Date;


public class NoteListActivity extends AppCompatActivity implements
        SearchFragment.onSomeEventListener {

    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private LinearLayout linearLayout;
    private SQLiteDatabase mDb;

    private String searchString;
    private boolean isSearch;

    private static final String STR_INDEX = "index";

    private static final String STR_EDIT = "edit";

    Context context;
    Cursor cursor;
    Class destinationActivity;
    SharedPreferences sharedPreferences;

    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list);

        context = NoteListActivity.this;
        destinationActivity = NoteActivity.class;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intentThatStartedThisActivity = getIntent();

        NoteDbHelper noteDbHelper = new NoteDbHelper(this);
        mDb = noteDbHelper.getWritableDatabase();


        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.to_do_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        createCursor();
        loadColorFromPreferences();

        builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                if (sharedPreferences.getBoolean(context.getString(R.string.accept_deleting_key),
                        context.getResources().getBoolean(R.bool.pref_request_deleting))) {
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deletingNoteAction(viewHolder);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            createCursor();
                        }
                    });

                    AlertDialog dialog = NoteListActivity.this.builder.create();
                    dialog.show();
                } else {
                    deletingNoteAction(viewHolder);
                }

            }


        }).attachToRecyclerView(mRecyclerView);

        /**
         * Getting data from intent.
         */
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String textEntered = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            long index = getIntent().getExtras().getLong(STR_INDEX);
            boolean editing = getIntent().getExtras().getBoolean(STR_EDIT);

            if (editing) {
                update_Note(textEntered, index);
            } else {
                add_Note(textEntered, DateFormat.getDateTimeInstance().format(new Date()));
            }
        }


    }

    /**
     * Removing note.
     */
    private void deletingNoteAction(RecyclerView.ViewHolder viewHolder) {
        long id = (long) viewHolder.itemView.getTag();
        remove_Note(id);
        mAdapter.swapCursor(getAllNotes());
    }


    /**
     * Loading colors for background and setting.
     */
    public void loadColorFromPreferences() {
        String str_color = sharedPreferences.getString(getString(R.string.color_key),
                getString(R.string.pref_color_FloralWhite_value));

        String[] arr = str_color.split(",");
        int[] arr2 = new int[3];
        for (int i = 0; i < arr.length; i++) {
            arr2[i] = Integer.valueOf(arr[i]);
        }
        int color = Color.rgb(arr2[0], arr2[1], arr2[2]);

        linearLayout.setBackgroundColor(color);

    }

    private Cursor getAllNotes() {

        String orderBy = "";

        /**
         * Sorting by date and name.
         */
        if (sharedPreferences.getBoolean(context.getString(R.string.check_box_sort_key),
                context.getResources().getBoolean(R.bool.pref_bool_sort_by_date))) {
            orderBy = NoteContract.NoteEntry.DATE + " DESC"; //bug here. Problem with sorting by date.
        } else {
            orderBy = NoteContract.NoteEntry.NOTE + " ASC";
        }
        if (!isSearch) {
            return mDb.query(NoteContract.NoteEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    orderBy);
        } else {
            return getSearchResults();
        }

    }

    /**
     * Search by some letters
     * @return query of results
     */
    public Cursor getSearchResults() {
        return mDb.query(NoteContract.NoteEntry.TABLE_NAME,
                null,
                "note_content LIKE ?",
                new String[]{"%" + searchString + "%"},
                null, null, null, null);
    }


    @Override
    public void someEvent(String searchStringFromFragment) {
        searchString = searchStringFromFragment;
        isSearch = true;
        createCursor();
    }

    /**
     * Opens the selected note and starts another activity for reading or updating.
     */
    public void openNote(long position) {

        Cursor cursor = mDb.query(NoteContract.NoteEntry.TABLE_NAME,
                new String[]{NoteContract.NoteEntry.NOTE},
                "_ID = ?",
                new String[]{String.valueOf(position)},
                null, null, null);

        String text = "";
        if (cursor.moveToFirst()) {
            text = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.NOTE));
        }

        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(STR_INDEX, position);
        startActivity(intent);

    }


    /**
     * Adding new note to the database.
     */
    private long add_Note(String note, String date) {
        ContentValues cv = new ContentValues();
        cv.put(NoteContract.NoteEntry.NOTE, note);
        cv.put(NoteContract.NoteEntry.DATE, date);
        return mDb.insert(NoteContract.NoteEntry.TABLE_NAME, null, cv);
    }

    /**
     * Updating note.
     */
    private long update_Note(String note, long id) {
        String date = DateFormat.getDateTimeInstance().format(new Date());
        ContentValues cv = new ContentValues();
        cv.put(NoteContract.NoteEntry.NOTE, note);
        cv.put(NoteContract.NoteEntry.DATE, date);
        cv.put(NoteContract.NoteEntry._ID, id);
        return mDb.update(NoteContract.NoteEntry.TABLE_NAME, cv, " _ID = ?", new String[]
                {
                        String.valueOf(id)
                });
    }

    /**
     * Removing note from the database
     */
    private boolean remove_Note(long id) {
        return mDb.delete(NoteContract.NoteEntry.TABLE_NAME, NoteContract.NoteEntry._ID + " = " + id, null) > 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                Intent startChildActivityIntent = new Intent(context, destinationActivity);

                startActivity(startChildActivityIntent);

                return true;
            case R.id.menu_item_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.menu_item_search: {
                FragmentManager fMan = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fMan.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                Fragment searchFragment = new SearchFragment();
                fragmentTransaction.add(R.id.frame_for_fragment, searchFragment);
                searchFragment.setRetainInstance(true);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Work with fragment. Remove fragment and search results.
     */
    @Override
    public void onBackPressed() {
        if (isSearch) {
            isSearch = false;
            createCursor();
        }
        FragmentManager fMan = this.getFragmentManager();
        if (fMan.getBackStackEntryCount() > 0) {
            fMan.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Creating cursor.
     */
    private void createCursor() {
        cursor = getAllNotes();
        mAdapter = new NoteAdapter(this, cursor);
        mRecyclerView.setAdapter(mAdapter);
    }

}
