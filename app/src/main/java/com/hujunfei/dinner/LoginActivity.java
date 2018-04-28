package com.hujunfei.dinner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hujunfei.dinner.R;

import java.io.File;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";
    public static boolean SDCARD_AMOUNT = (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable());
    public static String FILE_ROOT = (SDCARD_AMOUNT ? Environment.getExternalStorageDirectory().toString() : "/sdcard");
    private static String dinnertable_serilize_pathfile = FILE_ROOT + File.separator + "Test" + File.separator + "dinnertable.bin";
    private static String dinnercarte_serilize_pathfile = FILE_ROOT + File.separator + "Test" + File.separator + "dinnercarte.bin";
    private static String dinnercarte_pathfile = FILE_ROOT+ File.separator + "Test" + File.separator + "dinnercarte.dat";

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // 存储tableindex
    private List<Integer> list_tableindex = null;
    // 默认初始化tablesize
    private int wanted_tablesize = 8;

    private List<DinnerTable> list_dinnertable = null;

    private DinnerTableGridView view = null;

    public static DinnerCarte dinnercarte = null;

    //persmission method.
    public static void VerifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VerifyStoragePermissions(this);
        try {
            SerializeObject object = new SerializeObject(dinnercarte_serilize_pathfile);
            dinnercarte = (DinnerCarte)object.ReadObject();
            object.Destory();
        } catch (Exception e) {
            Log.d(TAG, "Load from serilize error!");
        }
        if (dinnercarte == null && FILE_ROOT != null && dinnercarte_pathfile != null) {
            dinnercarte = new DinnerCarte(dinnercarte_pathfile);
        }
        if (dinnercarte == null || dinnercarte.GetDinnerFoodsTypes().size() == 0) {
            // dinnercarte = new DinnerCarte(dinnercarte_pathfile);
            dinnercarte = new DinnerCarte(this.getResources().openRawResource(R.raw.dinnercarte));
        }
        try {
            SerializeObject object = new SerializeObject(dinnertable_serilize_pathfile);
            list_dinnertable = (List<DinnerTable>)object.ReadObject();
            view = new DinnerTableGridView(this,dinnercarte, wanted_tablesize,list_dinnertable);
            object.Destory();
            setContentView(view.GetView());
        } catch (Exception e) {
            view = new DinnerTableGridView(this,dinnercarte, list_tableindex,wanted_tablesize);
            setContentView(view.GetView());
        }

    }

    /**
     * Save all appropriate fragment state.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SerializeObject object = new SerializeObject(dinnertable_serilize_pathfile);
        object.WriteObject(view.GetDinnerTableList());
        object.Destory();
        object = new SerializeObject(dinnercarte_serilize_pathfile);
        object.WriteObject(dinnercarte);
        object.Destory();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        SerializeObject object = new SerializeObject(dinnertable_serilize_pathfile);
        object.WriteObject(view.GetDinnerTableList());
        object.Destory();
        object = new SerializeObject(dinnercarte_serilize_pathfile);
        object.WriteObject(dinnercarte);
        object.Destory();
        super.onDestroy();
    }
}
