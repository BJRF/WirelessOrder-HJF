package com.hujunfei.dinner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.hujunfei.dinner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunyuan on 2018/4/20.
 */
public class DinnerTableGridView {

    private static final String TAG = "DINNERTABLEGRIDVIEW";
    private GridView gridview = null;
    private ImageView imageview = null;
    private List<DinnerTable> list_dinnertable = null;
    private DinnerTableGridAdapter adapter = null;
    private View view = null;
    private DinnerCarte dinnercarte = null;

    public View GetView() {return view;}
    public List<DinnerTable> GetDinnerTableList() {return list_dinnertable;}

    public DinnerTableGridView(Context context, DinnerCarte dinnercarte, List<Integer> dinnertableids, int initsize) {
        this.dinnercarte = dinnercarte;
        if (dinnertableids != null && dinnertableids.size() > 0) {
            // 创建dinnertable list
            list_dinnertable = DinnerTable.CreateDinnerTableList(dinnertableids);
        } else {
            List<Integer> list_initids = new ArrayList<>();
            for (int index = 0; index < initsize; ++index) {
                list_initids.add(index + 1);
            }
            list_dinnertable = DinnerTable.CreateDinnerTableList(list_initids);
        }
        view = LayoutInflater.from(context).inflate(R.layout.grid_table_layout,null);
        gridview = (GridView)view.findViewById(R.id.grid_tables);
        imageview = (ImageView)view.findViewById(R.id.imageview_additem);
        // 根据list dinnertable创建adapter
        adapter = new DinnerTableGridAdapter(context,list_dinnertable,dinnercarte);
        // 设置adapter
        gridview.setAdapter(adapter);
        // 设置事件监听
        SetEventListener(context, initsize);
    }

    public DinnerTableGridView(Context context, DinnerCarte dinnercarte, int initsize, @NonNull List<DinnerTable> dinnertable) {
        this.dinnercarte = dinnercarte;
        list_dinnertable = dinnertable;
        view = LayoutInflater.from(context).inflate(R.layout.grid_table_layout,null);
        gridview = (GridView)view.findViewById(R.id.grid_tables);
        imageview = (ImageView)view.findViewById(R.id.imageview_additem);
        // 根据list dinnertable创建adapter
        adapter = new DinnerTableGridAdapter(context,list_dinnertable, dinnercarte);
        // 设置adapter
        gridview.setAdapter(adapter);
        // 设置事件监听
        SetEventListener(context, initsize);
    }

    private void SetEventListener(final Context context, final int initsize) {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, (adapter.GetDinnerTableId(position))  + "号台点击了!");
                adapter.NotifyUpdate(position);
            }
        });

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position >= initsize) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    Dialog dialog = builder.setTitle("确定删除")
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage("确定删除"+(adapter.GetDinnerTableId(position))+"号台吗?")
                            .setCancelable(true)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, (adapter.GetDinnerTableId(position))  + "号台被删除了!");
                                    list_dinnertable.remove(position);
                                    adapter.NotifyUpdate(-1);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                }

                return false;
            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(context);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                Dialog dialog = builder.setTitle("添加桌台")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(et)
                        .setMessage("输入桌号")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!et.getText().toString().equals("")) {
                                    int tableindex = Integer.valueOf (et.getText().toString()).intValue();
                                    boolean found = false;
                                    for (int index = 0; index < list_dinnertable.size(); ++index) {
                                        if (list_dinnertable.get(index).GetDinnerTableId() == tableindex) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        DinnerTable table = new DinnerTable(tableindex);
                                        list_dinnertable.add(table);
                                        adapter.NotifyUpdate(-1);
                                    } else {
                                        Toast.makeText(context, "桌号已经存在", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }
}
