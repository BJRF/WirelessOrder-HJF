package com.hujunfei.speech.util;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONObject;


public class FuncUtil {
    /**
     * 获取语记是否包含离线听写资源，如未包含跳转至资源下载页面
     * 1.PLUS_LOCAL_ALL: 本地所有资源
     * 2.PLUS_LOCAL_ASR: 本地识别资源
     * 3.PLUS_LOCAL_TTS: 本地合成资源
     */
    public static String checkLocalResource() {
        String resource = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_ASR);
        try {
            JSONObject result = new JSONObject(resource);
            int ret = result.getInt(SpeechUtility.TAG_RESOURCE_RET);
            switch (ret) {
                case ErrorCode.SUCCESS:
                    JSONArray asrArray = result.getJSONObject("result").optJSONArray("asr");
                    if (asrArray != null) {
                        int i = 0;
                        // 查询否包含离线听写资源
                        for (; i < asrArray.length(); i++) {
                            if("iat".equals(asrArray.getJSONObject(i).get(SpeechConstant.DOMAIN))){
                                //asrArray中包含语言、方言字段，后续会增加支持方言的本地听写。
                                //如："accent": "mandarin","language": "zh_cn"
                                break;
                            }
                        }
                        if (i >= asrArray.length()) {

                            SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                            return "没有听写资源，跳转至资源下载页面";
                        }
                    }else {
                        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                        return "没有听写资源，跳转至资源下载页面";
                    }
                    break;
                case ErrorCode.ERROR_VERSION_LOWER:
                    return "语记版本过低，请更新后使用本地功能";
                case ErrorCode.ERROR_INVALID_RESULT:
                    SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                    return "获取结果出错，跳转至资源下载页面";
                case ErrorCode.ERROR_SYSTEM_PREINSTALL:
                    //语记为厂商预置版本。
                default:
                    break;
            }
        } catch (Exception e) {
            SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
            return "获取结果出错，跳转至资源下载页面";
        }
        return "";
    }

}
