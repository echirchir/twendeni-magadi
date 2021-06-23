package org.etma.main.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.etma.main.R;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.DataFormatException;

public class Util {

    public static final String DECIMAL_FORMAT = "#,###.##";

    public static String getCurrentDateFormatted(){

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if ((month + 1) < 10){
            return year +"-" + "0" + (month+1) +"-" + day;
        }else{
            return year +"-" + (month+1) +"-" + day;
        }

    }

    public static boolean isToday(DateTime time) {
        return LocalDate.now().compareTo(new LocalDate(time)) == 0;
    }

    public static boolean isTomorrow(DateTime time) {
        return LocalDate.now().plusDays(1).compareTo(new LocalDate(time)) == 0;
    }

    public static boolean isYesterday(DateTime time) {
        return LocalDate.now().minusDays(1).compareTo(new LocalDate(time)) == 0;
    }

    public static String getCurrentDate(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return (month+1) +"/"+ day + "/" + year;
    }

    public static String yesterday(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        return dateFormat.format(formatDate());
    }

    private static Date formatDate(){

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static boolean isLastMonth(){

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);

        int thisMonth = c.get(Calendar.MONTH) + 1;

        //Log.d("THISMONTH", "This split date : " + getCurrentDate().split("/")[0]);

        c.add(Calendar.MONTH, -1);

        int lastMonth = c.get(Calendar.MONTH) + 1;

        //Log.d("THISMONTH", "Last month : " + lastMonth);

        return thisMonth != lastMonth;
    }

    public static String removeHtmlTags(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public static boolean isValidUserName(String userName){
        return userName.length() > 1;
    }

    public static void changeTabsFont(TabLayout tabLayout, Typeface font) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(font);
                }
            }
        }
    }

    public static String last30Days(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar calReturn = Calendar.getInstance();
        calReturn.add(Calendar.DATE, -30);

        return dateFormat.format(calReturn.getTime());
    }

    public static int  getVersionFromPackageManager(Context context) {
        PackageManager packageManager = context.getPackageManager();
        FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                // Null feature name means this feature is the open gl es version feature.
                if (featureInfo.name == null) {
                    if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
                        return getMajorVersion(featureInfo.reqGlEsVersion);
                    } else {
                        return 1;
                    }
                }
            }
        }
        return 1;
    }

    public static String formatMoney(String format, double amount){

        DecimalFormat formatter = new DecimalFormat(format);

        return formatter.format(amount);
    }


    /** @see FeatureInfo#getGlEsVersion() */
    private static int getMajorVersion(int glEsVersion) {
        return ((glEsVersion & 0xffff0000) >> 16);
    }


    public static long generateSixDigitNumber(){

        return (long) Math.floor(Math.random() * 900000L) + 100000L;
    }

    public static long generateTenDigitNumber(){

        return  (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
    }

    public static void showSnackBar(Context context, View layout, String resId){
        Snackbar snackbar = Snackbar
                .make(layout, resId, Snackbar.LENGTH_LONG)
                .setAction(context.getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void showSnackBar(Context context, View layout, int resId){
        Snackbar snackbar = Snackbar
                .make(layout, context.getResources().getString(resId), Snackbar.LENGTH_LONG)
                .setAction(context.getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static boolean isDateEarlierThanToday(String previous) throws DataFormatException, ParseException {

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        Date currentDate = format.parse(getCurrentDate());
        Date previousDate = format.parse(previous);

        return previousDate.compareTo(currentDate) <= 0;
    }

    public static void prettyPrintJson(String completeEntity) {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(completeEntity);
        String prettyJsonString = gson.toJson(je);

        //Log.d("PRETTYPRINTJSON", "THE JSON: " + prettyJsonString);
    }

    public static void onceAweekExecute(Context context){
        Calendar cal = Calendar.getInstance();
        int currentWeekOfYear = cal.get(Calendar.WEEK_OF_YEAR);

        SharedPreferences sharedPreferences= context.getSharedPreferences("data", 0);
        int weekOfYear = sharedPreferences.getInt("weekOfYear", 0);

        if(weekOfYear != currentWeekOfYear){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("weekOfYear", currentWeekOfYear);
            editor.apply();
            // Your once a week code here
        }
    }

    public static boolean isValidEmail(CharSequence email) {

        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(CharSequence password){

        return password != null && password.length() > 5;
    }

    public static boolean isValidMobile(String phone)
    {
        return phone.length() > 7;
        //717121909
        //return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static void trimCache(Context context) {
        File dir = context.getCacheDir();
        if(dir!= null && dir.isDirectory()){
            File[] children = dir.listFiles();
            if (children == null) {
                // Either dir does not exist or is not a directory
            } else {
                File temp;
                for (int i = 0; i < children.length; i++) {
                    temp = children[i];
                    temp.delete();
                }
            }

        }

    }

    public static void storeValueInSharedPrefs(Context context, String token){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("BEARER", token);

        editor.apply();
    }

    public static String getValueFromSharedPrefs(String key, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(key, "null");
    }

    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static String encodeImageTobase64(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

}
