package com.gomorrah.motherfather.server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.gomorrah.motherfather.Info.DeviceInfo;
import com.gomorrah.motherfather.sms.Sms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebServer extends NanoHTTPD
{
    private Context context;

	public WebServer(int port, Context context) throws IOException {
        super(port);
        this.context = context;
	}

    @Override public Response serve(IHTTPSession session) {
        // Request options
        Method method = session.getMethod();
        String uri = session.getUri();

//        System.out.println(method + " '" + uri + "' ");

        // Update log
//        ((TextView)((Activity)context).findViewById(R.id.log))
//            .setText(String.format("%s '%s'", method, uri));
//        listener.onUpdate(String.format("%s '%s'", method, uri));

        // Server response
        String response = "";

        // Show device info
        if(uri.equals("/")) { response = (new DeviceInfo(context)).toJson(); }

        // Get SMS list
        if(uri.equals("/sms") && method.equals(Method.GET)) {
            Cursor c = context.getContentResolver().query(
                Uri.parse("content://sms/inbox"), null, null, null, null
            );

            // Read the sms data and store it in the list
            List<JSONObject> smsList = new ArrayList<JSONObject>();
            if(c.moveToFirst()) {
                for(int i=0; i < c.getCount(); i++) {
                    JSONObject sms = new JSONObject();
                    try {
                        sms.put("body", c.getString(c.getColumnIndexOrThrow("body")));
                        sms.put("number", c.getString(c.getColumnIndexOrThrow("address")));
                        sms.put("date", convertSmsDate(c.getLong(c.getColumnIndexOrThrow("date"))));
                    } catch (JSONException e) {
                        // TODO: logcat!
                        e.printStackTrace();
                    }
                    smsList.add(sms);

                    c.moveToNext();
                }
            }
            c.close();

            JSONArray smsJson = new JSONArray(smsList);
            response = smsJson.toString();
        }

        // Send new SMS
        if(uri.equals("/sms") && method.equals(Method.POST)) {

            // Process POST request
            Map<String, String> files = new HashMap<String, String>();

            try { session.parseBody(files); }
            catch (IOException e) {
                return new Response(
                    Response.Status.INTERNAL_ERROR,
                    MIME_PLAINTEXT,
                    "SERVER INTERNAL ERROR: IOException: " + e.getMessage()
                );
            } catch (ResponseException e) {
                return new Response(e.getStatus(), MIME_PLAINTEXT, e.getMessage());
            }

            // Process POST params
            Map<String, String> params = session.getParms();

            // Not enough data provided
            if(params.get("phone") == null && params.get("message") == null) {
                response = jsonResponse(
                    "failure",
                    "Should provide phone and message as POST params."
                );
            // Send SMS
            } else {
                Sms sms = new Sms(params.get("phone"), params.get("message"));
                try {
                    sms.send();
                    response = jsonResponse(
                        "success",
                        "SMS successfully send."
                    );
                }
                catch(IllegalArgumentException e) {
                    response = jsonResponse(
                        "failure",
                        e.getMessage()
                    );
                }
            }
        }

        // Return server response
        return new Response(response);
    }

    /* Additional tools */

    /**
     * Prepare JSON response
     */
    protected String jsonResponse(String status, String message) {
        JSONObject response = new JSONObject();
        try {
            response.put("status", status);
            response.put("message", message);
        }
        catch (JSONException e) { e.printStackTrace(); }
        return response.toString();
    }

    /**
     * Convert SMS date to string
     */
    protected String convertSmsDate(Long date) {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatter.format(calendar.getTime());
    }


}
