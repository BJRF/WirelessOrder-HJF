package com.hujunfei.dinner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.RecognizerListener;
import com.hujunfei.dinner.R;
import com.hujunfei.speech.setting.IatSettings;
import com.hujunfei.speech.util.ApkInstaller;
import com.hujunfei.speech.util.FuncUtil;
import com.hujunfei.speech.util.JsonParser;
import com.hujunfei.util.UtilTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by lianfei1314 on 2018/4/20.
 */
public class DinnerSpeechActivity extends AppCompatActivity{
    private final static String TAG = DinnerSpeechActivity.class.getSimpleName();

    private DinnerCarte dinnercarte = null;
    private int dinnertable_id = 0;
    // 设置桌号
    private TextView item_table_index = null;
    // 语音设置
    private ImageButton imagebutton_iat_setting = null;
    // 已点餐展示
    private ListView listview_foods = null;
    // 录音点餐按钮
    private Button iat_dinner = null;
    // 菜单点菜按钮
    private Button carte_dinner = null;

    private SimpleAdapter adapter = null;

    // 语音点菜控件
    // 语音听写对象
    private SpeechRecognizer iat = null;

    // 语音听写UI
    private RecognizerDialog iat_dialog = null;

    // 用HashMap存储听写结果
    private HashMap<String,String> iat_results = new LinkedHashMap<>();

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_LOCAL;

    // 语记安装助手
    private ApkInstaller installer = null;

    private Toast toast = null;
    private SharedPreferences sharedpreference = null;
    private int ret = 0;

    // 初始化监听器
    private InitListener init_listener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误：" + code);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // 听写UI监听器
    private RecognizerDialogListener recog_dialog = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            recognizeResult(getResult(recognizerResult));
            // recognizeResult("西红柿，羊肉串10个");
        }

        @Override
        public void onError(SpeechError speechError) {
            showTip(speechError.getPlainDescription(true));
        }
    };

    // 听写监听器
    private RecognizerListener recog = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            // showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+ data.length);
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            // showTip("开始点菜");
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            /// showTip("结束点菜");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            Log.d(TAG, recognizerResult.getResultString());
            recognizeResult(getResult(recognizerResult));
            // recognizeResult("西红柿，羊肉串10个");
            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(speechError.getPlainDescription(true));
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dinner_speech_layout);

        Intent intent = getIntent();
        dinnertable_id = intent.getIntExtra(DinnerCarte.DINNERTABLE_ID_KEY,0);
        dinnercarte = LoginActivity.dinnercarte;

        // 初始化状态
        initView();
    }

    private void initView() {
        // 获取控件
        item_table_index = (TextView)findViewById(R.id.item_table_index);
        imagebutton_iat_setting = (ImageButton)findViewById(R.id.image_iat_set);
        listview_foods = (ListView)findViewById(R.id.iat_listview);
        iat_dinner = (Button)findViewById(R.id.iat_recognize);
        carte_dinner = (Button)findViewById(R.id.dinner_carte);

        // 设置tableid
        item_table_index.setText(String.valueOf(dinnertable_id));

        // 初始化语音
        initSpeech();

        // 设置事件监听
        SetEventListener();
    }

    private void initSpeech() {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面
        iat = SpeechRecognizer.createRecognizer(DinnerSpeechActivity.this,init_listener);

        // 初始化听写Dialog,如果只使用有UI听写功能，无需创建SpeecghRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        iat_dialog = new RecognizerDialog(DinnerSpeechActivity.this,init_listener);

        sharedpreference = getSharedPreferences(IatSettings.PREFER_NAME,MODE_PRIVATE);
        toast = Toast.makeText(DinnerSpeechActivity.this,"",Toast.LENGTH_SHORT);
        installer = new ApkInstaller(DinnerSpeechActivity.this);
        checkService();
    }

    private void SetEventListener() {
        // 设置imagebutton的设置界面
        imagebutton_iat_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动新的activity，进入设计界面
                startActivity(new Intent(DinnerSpeechActivity.this, IatSettings.class));
            }
        });
        // 设置listview展示已经点的菜
        adapter = new SimpleAdapter();
        listview_foods.setAdapter(adapter);
        listview_foods.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int index = dinnercarte.GetCurrentFoodIndexs(dinnertable_id).get(position);
                final String name = dinnercarte.GetFoodNameByIndex(dinnertable_id,index);
                AlertDialog.Builder builder = new AlertDialog.Builder(DinnerSpeechActivity.this);
                Dialog dialog = builder.setTitle("确定删除")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("确定删除\""+name+"\"吗？")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "\"" + name  + "\"被删除了!");
                                dinnercarte.DinnerOrder(dinnertable_id,index,0);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dialog.show();
                return false;
            }
        });
        // 长按点菜
        iat_dinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // 开始监听
                        // 清空数据
                        iat_results.clear();
                        // 设置参数
                        setParam();
                        // 是否显示UI
                        boolean isShowDialog = sharedpreference.getBoolean("iat_show",true);
                        if (isShowDialog) {
                            // 显示听写对话框
                            iat_dialog.setListener(recog_dialog);
                            iat_dialog.show();
                            showTip("开始点餐");
                        } else {
                            // 不显示听写对话框
                            ret = iat.startListening(recog);
                            if (ret != ErrorCode.SUCCESS) {
                                showTip("听写失败，错误码：" + ret);
                            } else {
                                showTip("开始点餐");
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // 结束监听
                        iat.stopListening();
                        // showTip("停止");
                        break;
                }
                return false;
            }
        });
        // 菜单点菜
        carte_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 预定一桌，指定index
                dinnercarte.DinnerStart(dinnertable_id);
                // 启动一个新的activity
                Intent intent = new Intent(DinnerSpeechActivity.this,DinnerFoodListActivity.class);
                // DinnerCarte id
                intent.putExtra(DinnerCarte.DINNERTABLE_ID_KEY,dinnertable_id);
                startActivity(intent);
                Log.d(TAG, dinnertable_id + "号台点餐啦！");
            }
        });
    }

    private void recognizeResult(String str_result) {
        final HashMap<String,Integer> map_name2number = UtilTools.splitRecog(str_result);
        Set<String> set_names = map_name2number.keySet();
        for (final String name : map_name2number.keySet()) {
            final List<Integer> list_relative = dinnercarte.GetRelativeFoodIndex(name);
            if (list_relative.size() > 0) {
                View view = LayoutInflater.from(DinnerSpeechActivity.this).inflate(R.layout.recog_dialog_layout,null);
                RecogAdapter rec_adapter = new RecogAdapter(list_relative);
                final AlertDialog.Builder builder = new AlertDialog.Builder(DinnerSpeechActivity.this);
                // 启动一个dialog
                final AlertDialog recog_dialog  = builder.setView(view)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("请选择")
                        .create();

                ListView listview = (ListView)view.findViewById(R.id.recog_dialog_listview);
                listview.setAdapter(rec_adapter);
                recog_dialog.show();
                // 点击选项时
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final int foodindex = list_relative.get(position);
                        int number = map_name2number.get(name).intValue();
                        if (number == 0) {
                            number = dinnercarte.GetFoodNumberByIndex(dinnertable_id,foodindex);
                            if (number == 0) {
                                number = 1;
                            }
                        }
                        String foodname = dinnercarte.GetFoodNameByIndex(dinnertable_id,foodindex);
                        // 构建一个新的Dialog获取item number
                        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(DinnerSpeechActivity.this);
                        // 获取自定义布局
                        View item_number_input_layout = LayoutInflater.from(DinnerSpeechActivity.this).inflate(R.layout.item_number_input_layout,null);
                        final EditText input_edittext = (EditText)item_number_input_layout.findViewById(R.id.item_number_input_edittext);
                        input_edittext.setText(String.valueOf(number));
                        input_edittext.setSelection(input_edittext.getText().length());
                        input_edittext.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                        dialog_builder.setIcon(R.mipmap.w0)
                                .setTitle("请输入\""+ foodname + "\"数量:")
                                .setView(item_number_input_layout)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String str = input_edittext.getText().toString();
                                        int nitems = 0;
                                        if (!str.equals("")) {
                                            nitems = Integer.valueOf(str).intValue();
                                        }
                                        dinnercarte.DinnerOrder(dinnertable_id,foodindex,nitems);
                                        recog_dialog.dismiss();
                                        if (adapter != null) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .setCancelable(true)
                                .create()
                                .show();
                    }
                });
            }
        }
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.setText(str);
                toast.show();
            }
        });
    }

    private void checkService() {
        /**
         * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
         */
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            installer.install();
        } else {
            String result = FuncUtil.checkLocalResource();
            if (!TextUtils.isEmpty(result)) {
                showTip(result);
            }
        }
    }

    private String getResult(RecognizerResult result) {
        String text = JsonParser.parseIatResult(result.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(result.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        iat_results.put(sn,text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key: iat_results.keySet()) {
            resultBuffer.append(iat_results.get(key));
        }
        return UtilTools.convertStringWithDights(resultBuffer.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        iat.cancel();
        iat.destroy();
    }

    private void setParam() {
        // 清空参数
        iat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        iat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        // 设置返回结果格式
        iat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = sharedpreference.getString("iat_language_preference","mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            iat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            iat.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
            // 设置语言区域
            iat.setParameter(SpeechConstant.ACCENT,lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        iat.setParameter(SpeechConstant.VAD_BOS, sharedpreference.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        iat.setParameter(SpeechConstant.VAD_EOS, sharedpreference.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        iat.setParameter(SpeechConstant.ASR_PTT, sharedpreference.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，
        iat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        iat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    private class SimpleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dinnercarte.GetCurrentFoodIndexs(dinnertable_id).size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView != null) {
                viewHolder = (ViewHolder)convertView.getTag();
            } else {
                convertView = LayoutInflater.from(DinnerSpeechActivity.this).inflate(R.layout.item_speech_goods_layout,null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            FillValue(position,viewHolder);
            return convertView;
        }

        private void FillValue(final int position, ViewHolder viewHolder) {
            int index = dinnercarte.GetCurrentFoodIndexs(dinnertable_id).get(position);
            String name = dinnercarte.GetFoodNameByIndex(dinnertable_id,index);
            int number = dinnercarte.GetFoodNumberByIndex(dinnertable_id,index);
            viewHolder.textview_name.setText("菜名： " + name + "  数量： ");
            viewHolder.textview_number.setText(String.valueOf(number));
            // 设置Listener
            SetEventListener(position,index,viewHolder);
        }

        private void SetEventListener(final int position, final int index, final ViewHolder viewHolder) {
            viewHolder.textview_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 构建一个新的Dialog获取item number
                    AlertDialog.Builder dialog_builder = new AlertDialog.Builder(DinnerSpeechActivity.this);
                    // 获取自定义布局
                    View item_number_input_layout = LayoutInflater.from(DinnerSpeechActivity.this).inflate(R.layout.item_number_input_layout,null);
                    final EditText input_edittext = (EditText)item_number_input_layout.findViewById(R.id.item_number_input_edittext);
                    input_edittext.setText(viewHolder.textview_number.getText().toString());
                    input_edittext.setSelection(viewHolder.textview_number.getText().length());
                    input_edittext.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    dialog_builder.setIcon(R.mipmap.w0)
                            .setTitle("请输入数量:")
                            .setView(item_number_input_layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String str = input_edittext.getText().toString();
                                    int nitems = 0;
                                    if (!str.equals("")) {
                                        nitems = Integer.valueOf(str).intValue();
                                    }
                                    dinnercarte.DinnerOrder(dinnertable_id,index,nitems);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消",null)
                            .setCancelable(true)
                            .create()
                            .show();
                }
            });
        }
    }

    static class ViewHolder {
        TextView textview_name = null;
        TextView textview_number = null;

        ViewHolder(View view) {
            textview_name = (TextView)view.findViewById(R.id.item_speech_food);
            textview_number = (TextView)view.findViewById(R.id.item_speech_food_number);
        }
    }

    private class RecogAdapter extends BaseAdapter {
        List<Integer> list_foodindex = null;

        public RecogAdapter(List<Integer> list_index) {
            list_foodindex = list_index;
        }

        @Override
        public int getCount() {
            return list_foodindex.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(DinnerSpeechActivity.this).inflate(R.layout.item_recog_result,null);
            }
            // 设置菜名
            ((TextView)convertView.findViewById(R.id.item_recog_result)).setText(dinnercarte.GetFoodNameByIndex(dinnertable_id,list_foodindex.get(position)));
            return convertView;
        }
    }
}
