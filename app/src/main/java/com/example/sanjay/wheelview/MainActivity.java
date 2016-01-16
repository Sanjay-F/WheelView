package com.example.sanjay.wheelview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sanjay.wheelviewlib.RecycleWheelView;
import com.example.sanjay.wheelviewlib.WheelAdapter;
import com.example.sanjay.wheelviewlib.picker.DatePicker;
import com.example.sanjay.wheelviewlib.picker.NumberPicker;
import com.example.sanjay.wheelviewlib.picker.OptionPicker;

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

    public void onDateClick(View view) {
        final NumberPicker picker = new NumberPicker(this);
        picker.setTopLineVisible(true);
        picker.setTextColor(R.color.mainBlue);
        picker.setLineColor(R.color.mainBlue);
        picker.setTopLineColor(0XFFFFFF);

        picker.setOptLineVisible(true);
        picker.setCancelVisible(false);



        picker.setOffset(2);
        picker.setRange(145, 200);
        picker.setSelectedItem(172);
        picker.setLabel("厘米");
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                Log.e("this", " value=" + option);
                Log.e("sele", "value= " + picker.getSelectedValue());
            }
        });
        picker.show();
    }

    public void onYearClick(View view) {
        DatePicker picker = new DatePicker(this);
        picker.setRange(2000, 2016);
        picker.setTitle("生日");
        picker.setSelectedItem(2015, 10, 10);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                Log.e("this", year + "-" + month + "-" + day);
            }
        });
        picker.show();

    }
}
