package com.lhl.test.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhl.test.util.HttpCallbackListener;
import com.lhl.test.util.HttpUtil;
import com.lhl.test.util.Utility;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
//
    private Button btn_switch_city;
//
    private Button btn_refresh_weather;
    private LinearLayout weatherInforLayout;
    //
    private TextView cityNameText;
    //
    private TextView publishText;
    //
    private TextView weatherDespText;
    //
    private TextView temp1Text;
    //
    private TextView temp2Text;
    //
    private TextView currentDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
//初始化控件
        init();
    }

    private void init() {
        weatherInforLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        btn_refresh_weather= (Button) findViewById(R.id.refresh_weather);
        btn_refresh_weather.setOnClickListener(this);
        btn_switch_city = (Button) findViewById(R.id.switch_city);
        btn_switch_city.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
//
            publishText.setText("同步中...");
//
            weatherInforLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
//
            queryWeatherCode(countyCode);
        } else {
            showWeather();

        }
    }

    //从SharedPreferences文件中读取存储的天气信息，并显示到界面上
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);

        cityNameText.setText(prefs.getString("city_name", ""));
        Log.d("weatherCode", "prefs.getString --->"+prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInforLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    //查询县级代号对应的天气代号
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromSever(address, "countyCode");
    }

    //根据传入的地址和类型去向服务器查询天气代号或者天气信息
    private void queryFromSever(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("TAG", "onFinish:response= "+response);
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        Log.d("TAG", "array.length : "+array.length);
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            Log.d("TAG", "weatherCode: "+weatherCode);
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Log.d("TAG", "类型为weatherCode ");
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });

    }

    //查询天气代号对应的天气
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromSever(address, "weatherCode");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case  R.id.refresh_weather:
                publishText.setText("同步中");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherCode(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
