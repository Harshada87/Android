package harshada.iss.com.demo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import harshada.iss.com.demo.LoadingTask;
import harshada.iss.com.demo.R;
import harshada.iss.com.demo.Utility.Appcontroller;
import harshada.iss.com.demo.Utility.CheckNetworkConnection;

public class SplashActivity extends AppCompatActivity implements LoadingTask.LoadingTaskFinishedListener {
    private static final String TAG = SplashActivity.class.getSimpleName();
    Toast toast;
     ProgressDialog pDialog;
     boolean isDataLoaded = false;
    public static final String PREFS_NAME = "ISS_Pass_Current";
    String TimeStamp, Lat,Long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CheckNetworkConnection(this, new CheckNetworkConnection.OnConnectionCallback() {

            @Override
            public void onConnectionSuccess() {
                init();
                Toast.makeText(SplashActivity.this, "onSuccess()", toast.LENGTH_SHORT).show();
                init();
            }

            @Override
            public void onConnectionFail(String msg) {
                Toast.makeText(SplashActivity.this, "onFail() Internate connection", toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    private void init() {
        setContentView(R.layout.activity_splash);

        // Find the progress bar
     //   ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        pDialog = new ProgressDialog(this);
        // Start your loading
        getISSCurrentLocation();

    }

    private void getISSCurrentLocation() {
        try {
            String tag_string_req = "ISS_Current";

            pDialog.setMessage("Connecting ...");
            showDialog();

            String ISS_URL = "http://api.open-notify.org/iss-now.json";
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    ISS_URL, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {
                    hideDialog();
                    Log.d(TAG, "Server Response: " + response.toString());
isDataLoaded = true;

                    try {
                        JSONObject jObj = new JSONObject(response);
                        //  boolean error = jObj.getBoolean("error");
                        String serverData = jObj.getString("message");
                        TimeStamp = String.valueOf(jObj.optInt("timestamp"));
                        if (serverData.equals("success")) {


                           JSONObject objPass = jObj.getJSONObject("iss_position");
                           for(int i= 0; i<=objPass.length(); i++)
                           {
                              /* String Lat = objPass.optString("latitude");
                              */
                               Lat = objPass.optString("latitude");
                                Long = objPass.optString("longitude");
                               Log.i("ISS Present ", Lat+"==="+Long+"@@"+TimeStamp);

System.out.print("Data"+ TimeStamp);

                           }
    }


                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
completeSplash();

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("tag", "cordinate");
                    params.put("", "");
                    params.put("", "");

                    return params;
                }

            };

            // Adding request to request queue
            Appcontroller.getInstance().addToRequestQueue(strReq, tag_string_req);


        }
        catch (Exception e)
        {
            Log.e("Ex", e.toString());
        }
    }

     private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();

    }
    @Override
    public void onTaskFinished() {
if(isDataLoaded)
{
    completeSplash();
}

    }

    private void completeSplash() {
        startApp();
        finish();
    }
    private void startApp() {
        Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
        intent.putExtra("TS", TimeStamp);
        intent.putExtra("Lat", Lat);
        intent.putExtra("Long",Long);

        startActivity(intent);
    }
}

