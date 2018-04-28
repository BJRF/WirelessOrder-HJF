package com.hujunfei.speech.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.iflytek.cloud.SpeechUtility;


public class ApkInstaller {
    private final static String TAG = ApkInstaller.class.getSimpleName();

    private Context mContext;

    public ApkInstaller(Context context) {
        mContext = context;
    }

    public void install() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("检测到您未安装语记！\n是否前往下载语记？")
                .setTitle("下载提示")
                .setPositiveButton("确认前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String url = SpeechUtility.getUtility().getComponentUrl();
                        String assetsApk = "SpeechService.apk";
                        processInstall(mContext,url,assetsApk);
                    }
                })
                .setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * 如果服务组件没有安装打开语音服务组件下载页面，进行下载安装
     */
    private boolean installFromURL(Context context, String url) {
        // 直接下载
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW,uri);
        context.startActivity(it);
        return true;
    }

    private boolean installFromAssets(Context context,String assertApk) {
        return false;
    }

    private boolean processInstall(Context context ,String url,String assetsApk) {
        // 优先从本地，失败后从网络安装
        if (!installFromAssets(context,assetsApk)) {
            return installFromURL(context,url);
        }
        return true;
    }
}
