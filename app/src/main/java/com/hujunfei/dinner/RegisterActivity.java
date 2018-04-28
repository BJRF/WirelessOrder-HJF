package com.hujunfei.dinner;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;

import com.hujunfei.dinner.R;

public class RegisterActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EditText name=(EditText) this.findViewById(R.id.editText1);
        EditText password=(EditText) this.findViewById(R.id.editText2);
        Button bu=(Button) this.findViewById(R.id.button1);
        ClickListener2 cll2=new ClickListener2(name, password,this);
        bu.setOnClickListener(cll2);
    }

}
