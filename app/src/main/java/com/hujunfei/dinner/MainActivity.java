package com.hujunfei.dinner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hujunfei.dinner.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText name=(EditText) this.findViewById(R.id.editText);
        EditText password=(EditText) this.findViewById(R.id.editText2);
        Button bu=(Button) this.findViewById(R.id.button);
        Button bu2=(Button) this.findViewById(R.id.button2);
        ClickListen cll=new ClickListen(name, password,this);
        bu.setOnClickListener(cll);
        bu2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
