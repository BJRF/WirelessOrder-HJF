package com.hujunfei.dinner;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializeObject {

    private static final String TAG = "SERIALIZEOBJECT";

    private FileOutputStream fileoutputstream = null;
    private ObjectOutputStream objectoutputstream = null;
    private FileInputStream fileinputstream = null;
    private ObjectInputStream objectinputstream = null;
    private File file = null;

    public SerializeObject(String filepath) {
        try {
            if (filepath != null && !filepath.equals("")) {
                file = new File(filepath.toString());
                boolean mkdirok = true;
                if (!file.getParentFile().exists()) {
                    mkdirok = file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
            }
            Log.d(TAG, "open " + filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void WriteObject(Object object) {
        try {
            fileoutputstream = new FileOutputStream(file.toString());
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(object);
            Log.d(TAG, "write object to file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object ReadObject() {
        Object object = null;
        try {
            fileinputstream = new FileInputStream(file.toString());
            objectinputstream = new ObjectInputStream(fileinputstream);
            object = objectinputstream.readObject();
            Log.d(TAG, "read object from file");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public void Destory() {
        if (fileoutputstream != null) {
            try {
                fileoutputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (objectoutputstream != null) {
            try {
                objectoutputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileinputstream != null) {
            try {
                fileinputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (objectinputstream != null) {
            try {
                objectinputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
