package nl.geekk.taskmanager.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServiceHandler {
    static String response = null;

    public ServiceHandler() {

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public JSONObject getJSONByPOST(String hostName, String params, String apiKey) {
        try {
            byte[] postData = params.getBytes(StandardCharsets.UTF_8);

            URL url = new URL(hostName);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput( true );
            connection.setInstanceFollowRedirects( false );
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", apiKey);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            connection.setUseCaches(false);

            try(DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                dataOutputStream.write( postData );
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String jsonString = new String();

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line+"\n");
            }

            bufferedReader.close();

            jsonString = stringBuilder.toString();

            Log.d("JSON", jsonString);

            return new JSONObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJSONByGET(String hostName, String params, String apiKey) {
        try {
            HttpURLConnection connection = null;

            URL link = new URL(hostName+params);

            connection = (HttpURLConnection) link.openConnection();

            connection.setRequestProperty("Authorization", apiKey);
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String jsonString = new String();

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line+"\n");
            }

            bufferedReader.close();

            jsonString = stringBuilder.toString();

            return new JSONObject(jsonString);
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}