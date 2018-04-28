package com.hujunfei.dinner;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hujunfei.dinner.R;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;


/**
 * Created by LIJUNYUAN on 2018/4/20.
 */
public class DinnerAccountActivity extends AppCompatActivity {
    private static String dinneraccout_pathfile = LoginActivity.FILE_ROOT + File.separator + "Test" + File.separator + "dinneraccount.dat";
    private static final String TAG = "DINNERACCOUNTACTIVITY";
    private DinnerCarte dinnercarte = null;
    private int dinnertable_id = 0;
    private TextView dinner_account_info = null;
    private Button dinner_account_back = null;
    private Button dinner_account_account = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        dinnertable_id = intent.getIntExtra(DinnerCarte.DINNERTABLE_ID_KEY,0);
        dinnercarte = LoginActivity.dinnercarte;
        if (dinnercarte.DinnerHaveStart(dinnertable_id) == 0) {
            Toast.makeText(getApplicationContext(),"桌号： " + dinnertable_id + " 还没开桌!",Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.dinner_account_layout);
        InitView();
    }

    private void InitView() {
        dinner_account_info = (TextView)findViewById(R.id.dinner_account_info);
        dinner_account_back = (Button)findViewById(R.id.dinner_account_back);
        dinner_account_account = (Button)findViewById(R.id.dinner_account_account);

        dinner_account_info.setText(dinnercarte.GetDinnerInfo(dinnertable_id));
        dinner_account_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DinnerAccountActivity.this.finish();
            }
        });
        dinner_account_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final View view = LayoutInflater.from(DinnerAccountActivity.this).inflate(R.layout.dinner_account_summary_layout, null);
                final EditText et_shishou = (EditText)view.findViewById(R.id.dinner_account_edittext_shishou);
                final EditText et_yingshou = (EditText)view.findViewById(R.id.dinner_account_edittext_yingshou);
                final EditText et_zhaoling = (EditText)view.findViewById(R.id.dinner_account_edittext_zhaoling);
                et_yingshou.setText(String.valueOf(dinnercarte.DinnerAccount(dinnertable_id)));
                et_yingshou.setFocusable(false);
                et_zhaoling.setText(String.valueOf(0));
                et_zhaoling.setFocusable(false);
                et_shishou.setSelection(et_shishou.getText().length());
                et_shishou.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int zhaoling = 0;
                        int yingshou = 0;
                        int shishou = 0;
                        if (!et_yingshou.getText().equals("")) {
                            yingshou = Integer.valueOf(et_yingshou.getText().toString()).intValue();
                        }
                        if (!et_shishou.getText().equals("")) {
                            shishou = Integer.valueOf(et_shishou.getText().toString()).intValue();
                        }
                        zhaoling = shishou - yingshou;
                        if (zhaoling < 0) {
                            zhaoling = 0;
                        }
                        et_zhaoling.setText(String.valueOf(zhaoling));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(DinnerAccountActivity.this);
                Dialog dialog = builder.setTitle("结账")
                        .setIcon(R.mipmap.w0)
                        .setView(view)
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!et_shishou.getText().toString().equals("")) {
                                    WriteAccountLog(dinnertable_id);
                                    dinnercarte.DinnerEnd(dinnertable_id);
                                    DinnerAccountActivity.this.finish();
                                    Log.d(TAG,dinnertable_id + "已买单");
                                } else {
                                    Toast.makeText(getApplicationContext(),"实收金额不能为空",Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();
            }
        });
    }

    private void WriteAccountLog(int tableid) {
        Calendar calendar = Calendar.getInstance();
        String str = dinnercarte.GetDinnerInfo(tableid);
        String info = "\nTable id " + tableid +
                calendar.get(Calendar.YEAR) + "/" +
                (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH) + " " +
                calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE)
                + "\n";
        try {
            FileWriter writer = new FileWriter(dinneraccout_pathfile, true);
            writer.write(info);
            writer.write(str);
            writer.write("\n\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
