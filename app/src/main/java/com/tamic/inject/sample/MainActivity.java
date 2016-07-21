package com.tamic.inject.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tamic.tamic_retrofit.core.ICallback;
import com.tamic.tamic_retrofit.core.Tamic;
import com.tamic.inject.R;

public class MainActivity extends AppCompatActivity {

    private ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.bt_test);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                perform();
            }


        });

    }

    private void perform() {

        Tamic tamic = new Tamic.Builder(MainActivity.this)
                .baseUrl("http://ip.taobao.com/")
                .connectTimeout(5)
                .addLog(true)
                .build();

        ApiService service = tamic.create(ApiService.class);

        service.getData("21.22.11.33", new ICallback<IpResult>() {
            @Override
            public void success(IpResult ipResult) {
                Toast.makeText(MainActivity.this, "success" + ipResult.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failed(Throwable e) {
                Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
