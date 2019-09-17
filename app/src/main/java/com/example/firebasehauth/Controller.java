package com.example.firebasehauth;

import android.app.Activity;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class Controller {
    public void navigateToFragmnet(int fragId, Activity curAct, Bundle b)
    {
        NavController navController = Navigation.findNavController(curAct,R.id.host_frag);
        navController.navigate(fragId,b);
    }
}
