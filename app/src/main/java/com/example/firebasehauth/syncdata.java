package com.example.firebasehauth;

import android.os.AsyncTask;

public class syncdata extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        System.out.println(s);

    }
    @Override
    protected String doInBackground(String... strings) {
        String jurl = strings[0];
        HttpHandler hp = new HttpHandler();

        String json = hp.makeService(jurl);

        System.out.println("This is from Syncdata :"+json);

        return json;

    }
}
