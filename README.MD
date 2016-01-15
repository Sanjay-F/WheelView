# WheelView
基于RecycleView写的一个WheelView

![](art/demo.png)
## show you the code --demo1

        dateWheel = (RecycleWheelView) findViewById(R.id.dateWheelView);
         
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
        
        dateWheel.setAdapter(mAdapter);
        dateWheel.setSelectedItem(50);
        dateWheel.setOnSelectListener(new RecycleWheelView.OnSelectItemListener() {
                @Override
                public void onSelectChanged(int position) {
                    mAdapter.setSelectedIndex(position);
                    Toast.makeText(MainActivity.this, "index=" + position, Toast.LENGTH_SHORT).show();
                }
            });



![demo2](art/demo2.png)

## DatePicker---demo2

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
 
# 目前支持的特性有

1. 加标签，定制颜色，大小等效果。例如图片显示的年
2. 设置是否渐显
3. 设置是否缩小`(curve)`
4. 显示的item数目
5. 中间选中的颜色和没选中的原色不一样功能(用丑陋的回调方式解决了)

# 存在的问题
1. 目前还不能自适应大小
   就是当设置只显示3个时候，不能动态调节ChildView的大小,使其自动填充整个界面
2. 在设置为curve的时候，显示有些小问题,暂时不建议使用  
 
