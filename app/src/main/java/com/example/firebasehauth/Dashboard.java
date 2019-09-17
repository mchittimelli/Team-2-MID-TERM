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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dashboard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    TextView txt_name;
    Button btn_logot;
    FirebaseFirestore db;
    FirebaseUser user;
    Controller con;
    ArrayList weatherArray;

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
        // Inflate the layout for this fragment
//weatherArray=new ArrayList();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

Call<List<ConsolidatedWeather>> weatherListCall=service.getList();
weatherListCall.enqueue(new Callback<List<ConsolidatedWeather>>() {
    @Override
    public void onResponse(Call<List<ConsolidatedWeather>> call, Response<List<ConsolidatedWeather>> response) {
        final List<ConsolidatedWeather> mweather =response.body();
   weatherArray=new ArrayList(mweather);
        System.out.println("size"+weatherArray.size());

    }

    @Override
    public void onFailure(Call<List<ConsolidatedWeather>> call, Throwable t) {
        System.out.println("Failed this one");
    }
});


            Call<ConsolidatedWeather> mycall=service.montrealWeather();

        mycall.enqueue(new Callback<ConsolidatedWeather>() {
            @Override
            public void onResponse(Call<ConsolidatedWeather> call, Response<ConsolidatedWeather> response) {
                final ConsolidatedWeather weather=response.body();

                //weather.getId();
              Log.d("my weather", weather.toString());

            }

            @Override
            public void onFailure(Call<ConsolidatedWeather> call, Throwable t) {
                Log.d("Failed","in 2nd method");
            }
        });
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


}
