package com.lhl.test.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhl.test.db.HotWeatherDB;
import com.lhl.test.model.City;
import com.lhl.test.model.County;
import com.lhl.test.model.Province;
import com.lhl.test.util.HttpCallbackListener;
import com.lhl.test.util.HttpUtil;
import com.lhl.test.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {
    private ProgressDialog pregressDialog;
    private ListView listView;
    private TextView tv_choose_title;
    private ArrayAdapter adapter;
    private List<String> datalist;
    private HotWeatherDB hotWeatherDB;
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    //当前选中的级别
    private int currentLevel;
    //    选中的省份
    private Province selectedProvince;
    //    选中的城市
    private City selectedCity;
    //    省列表
    private List<Province> provincesList;
    //    县列表
    private List<County> countyList;
    //    市列表
    private List<City> cityList;
    /**
     * 是否从WeatherActivity中跳转过来
     */
    private boolean isFromWeatherActivity ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到WeatherActivity
        if (prefs.getBoolean("city_selected",false ) && !isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        setContentView(R.layout.choose_area);
        if (datalist == null) {
            datalist = new ArrayList<>();
        }
        listView = (ListView) findViewById(R.id.lv_choose);
        tv_choose_title = (TextView) findViewById(R.id.tv_choose_title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);

        hotWeatherDB = HotWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String countyCode   = countyList.get(index).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询选中市内所有的县，优先从数据库中查询，如果没有查询到再去服务器上查询
     */

    private void queryCounties() {
        countyList = hotWeatherDB.loadCounties(selectedCity.get_id());
        if (countyList.size() > 0) {
            datalist.clear();
            for (County c : countyList) {
                datalist.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tv_choose_title.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }


    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到就到再去服务器上查询
     */

    private void queryProvinces() {
        provincesList = hotWeatherDB.loadProvinces();
        if (provincesList.size() > 0) {
            datalist.clear();
            for (Province p : provincesList) {
                datalist.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tv_choose_title.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

/**
 * 查询选中市内所有的城市，优先从数据库查询，如果没有查询到就到服务器上查询
 */

    private void queryCities() {
        cityList = hotWeatherDB.loadCities(selectedProvince.get_id());
        if (cityList.size()>0){
            datalist.clear();
            for (City c:cityList){
                datalist.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tv_choose_title.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }

    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据
     */
    private void queryFromServer(final String code,final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(hotWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(hotWeatherDB,response,selectedProvince.get_id());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(hotWeatherDB,response,selectedCity.get_id());
                }
                if (result){
//                    通过runOnUIThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closedProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
//        通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closedProgressDialog();
//                Looper.prepare();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
/**
 * 显示进度对话框
 */

    private void showProgressDialog() {
        if (pregressDialog == null){
            pregressDialog = new ProgressDialog(this);
            pregressDialog.setMessage("正在加载");
            pregressDialog.setCanceledOnTouchOutside(false);
        }
        pregressDialog.show();
    }
/**
 * 关闭进度对话框
 */
    private void closedProgressDialog(){
        if ( pregressDialog != null){
            pregressDialog.dismiss();
        }
    }
    /**
     *捕获Back键，根据当前的级别来判断，此时应该返回时列表、省列表、还是直接退出
     */
    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
    if (currentLevel == LEVEL_COUNTY){
        queryCities();
    }else if (currentLevel ==LEVEL_CITY){
        queryProvinces();
    }else {
        if (isFromWeatherActivity){
            Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
            startActivity(intent);
        }
        finish();
    }
    }

}
