package com.example.sanjay.wheelview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.sanjay.wheelviewlib.RecycleWheelView;
import com.example.sanjay.wheelviewlib.WheelAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecycleWheelView dateWheel;
    private WheelAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateWheel = (RecycleWheelView) findViewById(R.id.dateWheelView);
        dateWheel.setOnSelectListener(new RecycleWheelView.OnSelectItemListener() {
            @Override
            public void onSelectChanged(int position) {
                mAdapter.setSelectedIndex(position);
                Toast.makeText(MainActivity.this, "index=" + position, Toast.LENGTH_SHORT).show();
            }
        });

        dateWheel.setLabelTextColor(getResources().getColor(R.color.colorAccent));
        dateWheel.setLineColor(getResources().getColor(R.color.colorAccent));
        dateWheel.setVisibleItem(5);
        dateWheel.setLabel("cm");
        mAdapter = new WheelAdapter<>(this);
        List<String> dataList = new ArrayList<>();
        for (int i = 60; i < 240; i++) {
            dataList.add(String.format("%d", i));
        }
        mAdapter.setData(dataList);

        //also support
//        mAdapter.setTextColor();
//        mAdapter.setTextSize();
//        mAdapter.setTextPadding();
        dateWheel.setAdapter(mAdapter);
        dateWheel.setSelectedItem(50);

    }
}
