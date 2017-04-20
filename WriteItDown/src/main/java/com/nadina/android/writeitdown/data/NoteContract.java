package com.nadina.android.writeitdown.data;

import android.provider.BaseColumns;

/**
 * Created by Nadina on 15.03.2017.
 */

public class NoteContract {

    public static final class NoteEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "note_table";
        public static final String NOTE = "note_content";
        public static final String DATE = "date_of_note";

    }

}
