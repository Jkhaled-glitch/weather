package com.example.weather;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import android.widget.RemoteViews;



/**
 * Implementation of App Widget functionality.
 */
public abstract class NewAppWidget extends AppWidgetProvider {

    public static final String API_KEY="cff42b35ce9e1ad989fc956cf2010514";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetTexttemp = "20 °C";
        CharSequence widgetTextcity= "London, GB";

        // Construct the RemoteViews object
         final   RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.tvTemp_widget, widgetTexttemp);
        views.setTextViewText(R.id.tvCity_widget, widgetTextcity);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
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


