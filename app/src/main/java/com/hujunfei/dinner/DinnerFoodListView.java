package com.hujunfei.dinner;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

/**
 * Created by lijunyuan on 2018/4/20.
 */
public class DinnerFoodListView {
    private static final String TAG = "DINNERFOODLISTVIEW";
    private Context context = null;
    private ListView listview = null;
    private DinnerFoodListAdapter adapter = null;
    private DinnerCarte dinnercarte = null;
    private int tableid = -1;

    public DinnerFoodListView(Context context, DinnerCarte dinnercarte, int dinnertable, String type) {
        this.context = context;
        this.dinnercarte = dinnercarte;
        this.tableid = dinnertable;
        listview = new ListView(context);
        listview.setDividerHeight(0);
        adapter = new DinnerFoodListAdapter(context,dinnercarte,dinnertable,type);
        listview.setAdapter(adapter);
        SetEventListener();
    }

    private void SetEventListener() {

    }

    public View GetView() {
        return listview;
    }
}
