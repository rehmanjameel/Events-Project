package pk.cust.events.utils;

import android.app.Application;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pk.cust.events.services.DeleteExpiredEventsWorker;

public class App extends Application {

    public static Context context;
    private static final String KEY_LOGGED_IN = "logged_in";
    public static boolean IS_PROFILE = false;
    public static boolean IS_ROOM_SPACE = false;
    public static boolean IS_CHAT_FROM_HOME = false;
    public static boolean IS_ACCEPTED_ROOM = false;
    public static final String USER_NAME = "USER_NAME";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String ADDRESS = "ADDRESS";
    public static final String TOKEN = "TOKEN";
    public static final String EMAIL = "EMAIL";

    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    public static final String JSON_DATA_KEY = "json_data";
    private static final String KEY_COURSES_LIST = "coursesList";

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
//        createNotificationChannnel();

        // Schedule the worker to run every 15 minutes
        PeriodicWorkRequest deleteExpiredPostsWorkRequest =
                new PeriodicWorkRequest.Builder(DeleteExpiredEventsWorker.class, 15, TimeUnit.SECONDS)
                        .build();

        WorkManager.getInstance(this).enqueue(deleteExpiredPostsWorkRequest);
    }

    public static Context getContext() {
        return context;
    }

    public static SharedPreferences getPreferenceManager() {
        return getContext().getSharedPreferences("shared_prefs", MODE_PRIVATE);
    }

    public static void saveString(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }

    public static void saveInt(String key, int value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getInt(key, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    public static void saveFloat(String key, float value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public static double getFloat(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getFloat(key, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    public static void saveLogin(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }
    public static void saveBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }

    public static boolean getBoolean(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(key, false);
    }

    public static boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public static void logout() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().clear().apply();
    }

    public static void removeKey(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().remove(key).apply();
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveFileInDocuments(Context context, URL url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(String.valueOf(url)));
        request.setTitle(fileName);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Set destination directory
        File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "GVMFiles/" + fileName);

        if (!destinationFile.exists()) {
            // File does not exist, enqueue the download
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "GVMFiles/" + fileName);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
        } else {
            // File already exists, show a toast
            Toast.makeText(context, "File already exists", Toast.LENGTH_SHORT).show();
        }

    }

    public String convertDateFormat(String inputDateString) {
        try {
            // Define the input date format
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy");

            // Parse the input date string into a Date object
            Date date = inputDateFormat.parse(inputDateString);

            // Define the output date format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Format the Date object into the desired output string
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long convertDateTimeToMilliseconds(String dateString, String timeString) {
        try {
            // Combine date and time strings
            String dateTimeString = dateString + " " + timeString;
            Date currentDate = new Date();
            SimpleDateFormat curDate = new SimpleDateFormat("yyyy-MM-dd");

            String dateTimeString1 = curDate.format(currentDate) + " " + timeString;

            // Define the format of your date and time string
            SimpleDateFormat dateFormat = null;
            Date date;
            if (timeString.isEmpty()) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                // Parse the date and time string into a Date object
                date = dateFormat.parse(dateString);
            } else if (dateString.isEmpty()) {

                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                date = dateFormat.parse(dateTimeString1);

            } else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                date = dateFormat.parse(dateTimeString);

            }


            // Get the time in milliseconds
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String convertMillisToDateTime(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    // Check if location (GPS or network) is enabled
//    public static boolean isLocationEnabled(Context context) {
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//    // Show a dialog if GPS is not enabled
//    public static void showGPSNotEnabledDialog(Context context) {
//        new AlertDialog.Builder(context)
//                .setTitle(context.getString(com.google.android.gms.base.R.string.common_google_play_services_enable_title))
//                .setMessage(context.getString(R.string.app_name))
//                .setCancelable(false)
//                .setPositiveButton(context.getString(com.google.android.gms.base.R.string.common_google_play_services_enable_button), (dialog, which) -> {
//                    context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                })
//                .show();
//    }

}
