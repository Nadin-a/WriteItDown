package com.nadina.android.writeitdown.Note;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import com.nadina.android.writeitdown.NoteListActivity;
import com.nadina.android.writeitdown.R;
import com.nadina.android.writeitdown.databinding.ActivityNoteBinding;

public class NoteActivity extends AppCompatActivity {

    ActivityNoteBinding mBinding;

    SharedPreferences sharedPreferences;

    Context context;
    Class destinationActivity;

    private static final String STR_INDEX = "index";

    private static final String STR_EDIT = "edit";

    long position;
    boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_note);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        init_elements();

        get_intent();

        context = NoteActivity.this;
        destinationActivity = NoteListActivity.class;

        mBinding.sendToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startChildActivityIntent = new Intent(context, destinationActivity);

                if (!mBinding.note.getText().toString().isEmpty()) {
                    startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, mBinding.note.getText().toString());
                    startChildActivityIntent.putExtra(STR_INDEX, position);
                    if (editing) {
                        startChildActivityIntent.putExtra(STR_EDIT, editing);
                    }

                    startActivity(startChildActivityIntent);
                }
            }
        });

        mBinding.sendToNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mimeType = "text/plain";


                ShareCompat.IntentBuilder
                        .from(NoteActivity.this)
                        .setType(mimeType)
                        .setText(mBinding.note.getText().toString())
                        .startChooser();
            }
        });


    }

    /**
     * Getting data from the intent.
     */
    private void get_intent() {
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String textReceived = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            position = getIntent().getExtras().getLong(STR_INDEX);
            editing = true;
            mBinding.note.setText(textReceived);
        }
    }

    private void init_elements() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadColorFromPreferences();
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


        mBinding.note.setBackgroundColor(color);
        mBinding.activityNoteLayout.setBackgroundColor(color);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
