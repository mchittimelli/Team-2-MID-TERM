package com.example.firebasehauth;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHandler {
    public HttpHandler() {
    }
    public String makeService(String aurl)
    {
        String response = null;



        try {
            URL url = new URL(aurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type","application/json");

            InputStream in = new BufferedInputStream(con.getInputStream());

            response = convertStream(in);

        }catch (MalformedURLException e)
        {
            Log.e("In HttpHandler Class :",e.getMessage());
        }catch (IOException e)
        {
            Log.e("In HttpHandler Class :",e.getMessage());
        }


        return response;

    }
    public String convertStream(InputStream is){

        BufferedReader read = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        String line;


        try
        {
            while ((line = read.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        }catch (IOException e)
        {
            Log.e("At ConvertStream :",e.getMessage());
        }finally {

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return sb.toString();

    }
}
