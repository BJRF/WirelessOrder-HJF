package com.hujunfei.dinner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hujunfei.dinner.R;
import com.hujunfei.library.DinnerCarteMenu;

import java.util.ArrayList;
import java.util.List;

public class DinnerFoodListActivity extends AppCompatActivity {

    private static final String TAG = "DINNERFOODLISTACTIVITY";
    private DinnerCarte dinnercarte = null;
    private int dinnertable_id = 0;
    private DinnerCarteMenu dinner_carte_menu = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dinner_carte_layout);
        Intent intent = getIntent();
        dinnertable_id = intent.getIntExtra(DinnerCarte.DINNERTABLE_ID_KEY,0);
        dinnercarte = LoginActivity.dinnercarte;
        dinner_carte_menu = (DinnerCarteMenu)findViewById(R.id.dinner_carte_menu);
        InitView(dinnercarte, dinner_carte_menu, dinnertable_id);
    }

    private void InitView(DinnerCarte dinnercarte, DinnerCarteMenu dinnercartemenu,int dinnertableid) {
        List<String> settypes = dinnercarte.GetDinnerFoodsTypes();
        List<String> listtypes = new ArrayList<>();
        List<View> containerviews = new ArrayList<View>();
        for (String str : settypes) {
            listtypes.add(str);
            containerviews.add(new DinnerFoodListView(this,dinnercarte,dinnertableid,str).GetView());
            Log.d(TAG,"Type: " + str);
        }
        dinnercartemenu.SetDinnerTableMenu(listtypes,containerviews);
    }
}
