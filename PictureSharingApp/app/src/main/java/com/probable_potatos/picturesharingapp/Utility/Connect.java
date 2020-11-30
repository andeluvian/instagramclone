package com.probable_potatos.picturesharingapp.Utility;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


// Connect to API with volley

public class Connect {

    Context context;

    public Connect(Context context) {
        this.context = context;
    }


    /* EXAMPLE POST from activity:

        // ip of your computer if running on device (192.168.1.7) something similar
        String URL = "http://192.168.1.7:3000/groups/create";

        Connect connection = new Connect(getApplicationContext());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grpName", "groupname");
        parameters.put("email","test@test.com");
        parameters.put("expirationDate", "123456");

        connection.volleyPOST(URL,parameters);
        // this then Toasts response {"message":"successfully created group."}

     */
    public void volleyPOST(String url, Map<String, String> parameters, String token)

    {
        final String idToken = token;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(parameters),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("idToken", idToken);
                return headers;
            }
        };
        queue.add(req);
    }

    public void volleyPostNoToast(String url, Map<String, String> parameters, String token)
    {
        final String idToken = token;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(parameters),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("idToken", idToken);
                return headers;
            }
        };
        queue.add(req);
    }

    /* EXAMPLE GET from activity: (This might be useless as it only toasts the response)

        String URL = "http://192.168.1.7:3000/groups/";

        Connect connection = new Connect(getApplicationContext());

        connection.volleyGET(URL);
        // this then Toasts response {"PYLLY"}

     */
    public void volleyGET(String url)
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        queue.add(stringRequest);
    }

}
