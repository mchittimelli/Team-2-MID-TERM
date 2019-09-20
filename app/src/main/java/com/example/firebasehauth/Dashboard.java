package com.example.firebasehauth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.concurrent.ExecutionException;


import static java.util.Calendar.DAY_OF_WEEK;


public class Dashboard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    TextView last_login,txt_name,weatherType,realTemp,minTemp,maxTemp,weekday, humidity, predictability, txt_tomorrow, txt_dayafter, txt_dayafterafter, tmin, tmax, damin, damax, daamin, daamax;
    Button btn_logot;
    ImageView weatherIcon, tomorrow, dayafter, dayafterafter;
    FirebaseFirestore db;
    FirebaseUser user;
    Controller con;
    JSONArray weatherArray;
    String base_url,today, tom, dayaftertom, dayafteraftertom,cLogin;

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
        db = FirebaseFirestore.getInstance();



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readFireStore();
        txt_name = view.findViewById(R.id.txt_dashname);
        last_login=view.findViewById(R.id.last_login);
        btn_logot = view.findViewById(R.id.btn_logout);
        weatherIcon=view.findViewById(R.id.weather_icon);
        weatherType=view.findViewById(R.id.weather_type);
        realTemp=view.findViewById(R.id.real_temp);
        minTemp=view.findViewById(R.id.min_temp);
        maxTemp=view.findViewById(R.id.max_temp);
        weekday=view.findViewById(R.id.weekday);
        humidity = view.findViewById(R.id.humidity);
        predictability = view.findViewById(R.id.predictability);

        tomorrow = view.findViewById(R.id.tomorrow);
        dayafter = view.findViewById(R.id.dayafter);
        dayafterafter = view.findViewById(R.id.dayafterafter);

        txt_tomorrow = view.findViewById(R.id.tom);
        txt_dayafter = view.findViewById(R.id.dayaftertom);
        txt_dayafterafter = view.findViewById(R.id.dayafteraftertom);

        tmin = view.findViewById(R.id.tmin_temp);
        tmax = view.findViewById(R.id.tmax_temp);

        damin = view.findViewById(R.id.damin_temp);
        damax = view.findViewById(R.id.damax_temp);

        daamin = view.findViewById(R.id.daamin_temp);
        daamax = view.findViewById(R.id.daamax_temp);

        try {
            weekday.setText("Today: " + today);
            Glide.with(getContext()).asBitmap().load("https://www.metaweather.com/static/img/weather/png/"+weatherArray.getJSONObject(0).getString("weather_state_abbr")+".png").into(weatherIcon);
            weatherType.setText(weatherArray.getJSONObject(0).getString("weather_state_name"));
            realTemp.setText(weatherArray.getJSONObject(0).getString("the_temp").substring(0,4) + "°C");
            minTemp.setText(weatherArray.getJSONObject(0).getString("min_temp").substring(0,5) + "°C");
            maxTemp.setText(weatherArray.getJSONObject(0).getString("max_temp").substring(0,5) + "°C");
            humidity.setText(weatherArray.getJSONObject(0).getString("humidity") + "%");
            predictability.setText(weatherArray.getJSONObject(0).getString("predictability") + "%");

            Glide.with(getContext()).asBitmap().load("https://www.metaweather.com/static/img/weather/png/"+weatherArray.getJSONObject(1).getString("weather_state_abbr")+".png").into(tomorrow);
            txt_tomorrow.setText(tom);
            tmin.setText(weatherArray.getJSONObject(1).getString("min_temp").substring(0,5) + "°C");
            tmax.setText(weatherArray.getJSONObject(1).getString("max_temp").substring(0,5) + "°C");
            Glide.with(getContext()).asBitmap().load("https://www.metaweather.com/static/img/weather/png/"+weatherArray.getJSONObject(2).getString("weather_state_abbr")+".png").into(dayafter);
            txt_dayafter.setText(dayaftertom);
            damin.setText(weatherArray.getJSONObject(2).getString("min_temp").substring(0,5) + "°C");
            damax.setText(weatherArray.getJSONObject(2).getString("max_temp").substring(0,5) + "°C");
            Glide.with(getContext()).asBitmap().load("https://www.metaweather.com/static/img/weather/png/"+weatherArray.getJSONObject(3).getString("weather_state_abbr")+".png").into(dayafterafter);
            txt_dayafterafter.setText(dayafteraftertom);
            daamin.setText(weatherArray.getJSONObject(3).getString("min_temp").substring(0,5) + "°C");
            daamax.setText(weatherArray.getJSONObject(3).getString("max_temp").substring(0,5) + "°C");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        btn_logot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference docref = db.collection("users").document(user.getUid());
                docref.update("LastLogin",cLogin);
                FirebaseAuth.getInstance().signOut();
                con = new Controller();
                con.navigateToFragmnet(R.id.login,getActivity(),null);
            }
        });

    }
    public void readFireStore()
    {
        DocumentReference docref = db.collection("users").document(user.getUid());
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snap = task.getResult();
                    if (snap.exists())
                    {
                        Log.d("Snap Data",snap.getData().toString());

                        txt_name.setText("Welcome "+snap.get("Name")+"!");
                        last_login.setText("Last Login at "+snap.get("LastLogin"));
                        cLogin=snap.get("currentLogin").toString();
                    }
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        calendar.add(DAY_OF_WEEK,1);


        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        today = simpleDateformat.format(now);
        tom = calendar.getTime().toString().substring(0,3) + ", " + calendar.getTime().toString().substring(4,10);
        calendar.add(DAY_OF_WEEK,1);

        dayaftertom = calendar.getTime().toString().substring(0,3) + ", " + calendar.getTime().toString().substring(4,10);
        calendar.add(DAY_OF_WEEK,1);

        dayafteraftertom = calendar.getTime().toString().substring(0,3) + ", " + calendar.getTime().toString().substring(4,10);

        System.out.println("Tomorrow: " + calendar.getTime().toString().substring(0,3) + ", " + calendar.getTime().toString().substring(4,10));

        base_url = "https://www.metaweather.com/api/location/3534/";
        String myjson = null;
        try {
            myjson = new syncdata().execute(base_url).get();
            //  Montreal m=new Montreal(myjson);
            JSONObject montreal = new JSONObject(myjson);
            Montreal montrealWeather = new Montreal();

            // montrealWeather.setConsolidatedWeather(getJSONArray("consolidated_weather"));
            weatherArray = montreal.getJSONArray("consolidated_weather");
            System.out.println("JSON object " + montreal.getJSONArray("consolidated_weather"));

        } catch (ExecutionException | JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


}
