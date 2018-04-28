package com.hujunfei.dinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunyuan on 2018/4/20.
 */
public class DinnerTable implements Serializable {

    private static final String TAG = "DINNERTABLE";

    private int table_id = -1;

    public void SetDinnerTableId(int id) {
        this.table_id = id;
    }

    public int GetDinnerTableId() {
        return this.table_id;
    }

    public DinnerTable() {
        super();
        table_id = -1;
    }

    public DinnerTable(int id) {
        super();
        table_id = id;
    }

    public static List<DinnerTable> CreateDinnerTableList(List<Integer> list_tableindex) {
        List<DinnerTable> list_dinnertables = new ArrayList<>();
        for (int index = 0; index < list_tableindex.size(); ++index) {
            list_dinnertables.add(new DinnerTable(list_tableindex.get(index)));
        }
        return list_dinnertables;
    }
}
