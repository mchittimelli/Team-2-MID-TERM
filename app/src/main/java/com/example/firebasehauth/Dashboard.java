package com.example.firebasehauth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dashboard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    TextView txt_name,weatherType,realTemp,minTemp,maxTemp,weekday,date,visibility,humidity;
    Button btn_logot;
    ImageView weatherIcon;
    FirebaseFirestore db;
    FirebaseUser user;
    Controller con;
    JSONArray weatherArray;
    JSONObject todayWeather;
    int index;
    String base_url,tDate;
DayOfWeek dayOfWeek;

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
        btn_logot = view.findViewById(R.id.btn_logout);
        weatherIcon=view.findViewById(R.id.weather_icon);
        weatherType=view.findViewById(R.id.weather_type);
        realTemp=view.findViewById(R.id.real_temp);
        minTemp=view.findViewById(R.id.min_temp);
        maxTemp=view.findViewById(R.id.max_temp);
        weekday=view.findViewById(R.id.weekday);
        visibility=view.findViewById(R.id.visibility);
        humidity=view.findViewById(R.id.humidity);
        date=view.findViewById(R.id.date);
        try {
            weekday.setText(dayOfWeek.toString());
            date.setText(tDate);
            Glide.with(getContext()).asBitmap().load("https://www.metaweather.com/static/img/weather/png/"+weatherArray.getJSONObject(0).getString("weather_state_abbr")+".png").into(weatherIcon);
            weatherType.setText(weatherArray.getJSONObject(0).getString("weather_state_name"));
            realTemp.setText(weatherArray.getJSONObject(0).getString("the_temp").substring(0,5));
            minTemp.setText(weatherArray.getJSONObject(0).getString("min_temp"));
            maxTemp.setText(weatherArray.getJSONObject(0).getString("max_temp"));
            visibility.setText(weatherArray.getJSONObject(0).getString("visibility").substring(0,5));
            humidity.setText(weatherArray.getJSONObject(0).getString("humidity"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btn_logot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    }
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Date now = new Date();
        DateFormat dateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
       tDate= dateFormat.format(now);
        index= Integer.parseInt(new SimpleDateFormat("u").format(new Date()));
        dayOfWeek=DayOfWeek.of(index);
        base_url="https://www.metaweather.com/api/location/3534/";
        String myjson = null;
        try {
            myjson = new syncdata().execute(base_url).get();
          //  Montreal m=new Montreal(myjson);
            JSONObject montreal=new JSONObject(myjson);
            Montreal montrealWeather=new Montreal();

           // montrealWeather.setConsolidatedWeather(getJSONArray("consolidated_weather"));
            weatherArray=montreal.getJSONArray("consolidated_weather");
            System.out.println("JSON object "+montreal.getJSONArray("consolidated_weather"));

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
