package com.protheansoftware.gab.model;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author oskar
 * Created by oskar on 09/10/15.
 */
public class BusHandler{
    private final String TAG = "GAB";
    private static BusHandler instance;
    private boolean flag;
    private JdbcDatabaseHandler jdb = JdbcDatabaseHandler.getInstance();

    private BusHandler(){

    }

    public static BusHandler getInstance() {
        if(instance == null){
            instance = new BusHandler();
        }
        return instance;
    }

    public String getBusVIN() {
        long newtime = System.currentTimeMillis();
        long oldtime = newtime - 100000;

        //String tmp = getJSON("https://ece01.ericsson.net:4443/ecity?resourceSpec=Ericsson$Cell_Id_Value&t1=" + oldtime + "&t2=" + newtime);
        String tmp = getXML("http://www.ombord.info/api/xml/system/");
        /*String tmp = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<system>\n" +
                "    <system_id type=\"integer\">2501069301</system_id>\n" +
                "</system>"
                ;*/

        if(tmp == null) {
            flag = true;
            return null;
        }

        ArrayList<String> sa = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=<system_id type=\"integer\">)([^<]*)").matcher(tmp);
        while (m.find()) {sa.add(m.group());}
        if (sa == null) return null;
        Log.e(TAG, "HEJ: "+sa.get(0));
        String result = jdb.getVINFromSystemId(sa.get(0));

        return result;
    }

    private String getXML(String target) {
        try {
            URL url = new URL(target);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();

            int status = connection.getResponseCode();
            Log.d(TAG, "XML GET request status code: " + status);

            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            switch (status) {
                case 200:
                    String line;
                    int numCharsRead;
                    char[] charArray = new char[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((numCharsRead = isr.read(charArray)) > 0) {
                        sb.append(charArray, 0, numCharsRead);
                    }
                    return sb.toString();
            }
        } catch (ConnectException e) {
            Log.d(TAG, "Couldn't connect: "+e);
        } catch(Exception e) {
            Log.e(TAG, "ERROR: "+e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the device wifi
     * @param context The devices context
     * @return SSID of wifi
     */
    public String getMyWifi(Context context) {
        WifiManager mngr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifi = mngr.getConnectionInfo().getSSID();
        return wifi;
    }

    /**
     * Gets from Electricitys API using target URL
     * @param target Target url to get from
     * @return String of response, in JSON form.
     */
    private String getJSON(String target){
        try {
            byte[] authEncBytes = Base64.encode(Secrets.API.getBytes(), Base64.DEFAULT);
            String authEncStr = new String(authEncBytes);

            URL url = new URL (target);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authEncStr);
            connection.setRequestMethod("GET");
            connection.connect();

            int status = connection.getResponseCode();
            Log.d(TAG, "JSON GET request status code: "+status);

            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            switch (status) {
                case 200:
                case 201:
                    String line;
                    int numCharsRead;
                    char[] charArray = new char[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((numCharsRead = isr.read(charArray))>0) {
                        sb.append(charArray, 0, numCharsRead);
                    }
                    return sb.toString();
            }
        } catch(Exception e) {
            Log.e(TAG, "ERROR: "+e);
            e.printStackTrace();
        }
        return null;
    }

    public boolean startSessionIfNeeded(Context context) {
        flag = false;
        int user_id = jdb.getMyId(); //Will be passed to this function as a param
        String bus_vin = getBusVIN();

        Log.e(TAG, "VIN: "+bus_vin);

        if (flag) {
            //We are not on a bus network or cant access local api
            return false;
        }

        Session session = jdb.getSessionVINByUserId(user_id);
        if (session != null) {
            if (session.VIN.equals(bus_vin)) {
                Log.e(TAG, "Session running. Fetch and display matches.");
                //already on this bus network and session is started
                //Display my matches
            } else {
                Log.e(TAG, "Updating session VIN.");
                Log.e(TAG, "User is on a bus but not the same as the session. " +
                        "My VIN: " + bus_vin + ". Session VIN: " + session.VIN);
                //Im on a another bus network, update the session
                jdb.updateSession(bus_vin, user_id);
            }
        } else {
            Log.e(TAG, "Starting a new session...");
            //Im on a bus network and no session is started, start a new session
            jdb.sessionStart(bus_vin);
        }
        return true;
    }

    /**
     * Checks if the doors have been opened on the users bus within deltaTime
     * @param deltaTime Time to check from
     * @return True if the bus have been opened at anytime in the deltaTime, False else.
     */
    public boolean hasDoorsOpened(int deltaTime){
        long newtime = System.currentTimeMillis();
        long oldtime = newtime - deltaTime; //2 * 60 * 1000/10
        String busVin = jdb.getSessionVINByUserId(jdb.getMyId()).VIN;
        String tmp = getJSON("https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$" + busVin + "&resourceSpec=Ericsson$Open_Door_Value&t1=" + oldtime + "&t2=" + newtime);

        Log.d(TAG, tmp);

        //Search for value using regex, gets the string next to value, in this case either true or false.
        ArrayList<String> sa = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=\"value\":\")([^\"]*)").matcher(tmp);

        while (m.find()) {
            if(m.group().equals("true")) return true;
        }

        return false;
    }
}
