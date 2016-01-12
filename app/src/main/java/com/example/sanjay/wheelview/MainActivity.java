package com.example.sanjay.wheelview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sanjay.wheelviewlib.RecycleWheelView;
import com.example.sanjay.wheelviewlib.TextWheelAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecycleWheelView dateWheel;
    private TextWheelAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateWheel = (RecycleWheelView) findViewById(R.id.dateWheelView);
        dateWheel.setOnSelectListener(new RecycleWheelView.OnSelectItemListener() {
            @Override
            public void onSelectChanged(int position) {
                mAdapter.setSelectedIndex(position);
//                Toast.makeText(MainActivity.this, String.format("index : %d", position), Toast.LENGTH_SHORT).show();
            }
        });
        dateWheel.setLableTextColor(getResources().getColor(R.color.colorAccent));
        dateWheel.setLineColor(getResources().getColor(R.color.colorAccent));
        dateWheel.setVisibleItem(5);
        dateWheel.setCurve(true);
        dateWheel.setLable("cm");
        mAdapter = new TextWheelAdapter<>(this);
        List<String> dataList = new ArrayList<>();
        for (int i = 60; i < 240; i++) {
            dataList.add(String.format("%d", i));
        }
        mAdapter.setData(dataList);
        dateWheel.setAdapter(mAdapter);
        dateWheel.setSelectedItem(50);

    }
}
