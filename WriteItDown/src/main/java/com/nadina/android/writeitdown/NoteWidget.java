package com.nadina.android.writeitdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import com.nadina.android.writeitdown.data.NoteContract;
import com.nadina.android.writeitdown.data.NoteDbHelper;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        NoteDbHelper noteDbHelper = new NoteDbHelper(context);
        SQLiteDatabase mDb = noteDbHelper.getReadableDatabase();

        Cursor mCount = mDb.rawQuery("select count(*) from " + NoteContract.NoteEntry.TABLE_NAME, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();

        String widget_note = "";

        if (count <= 0) {
            widget_note = context.getResources().getString(R.string.no_notes);
        } else if (count > 0 && count <= 5) {
            widget_note = context.getResources().getString(R.string.some_notes);
        } else {
            widget_note = context.getResources().getString(R.string.many_notes);
        }

        Intent intent = new Intent(context, NoteListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        views.setTextViewText(R.id.appwidget_text, widget_note);
        views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

