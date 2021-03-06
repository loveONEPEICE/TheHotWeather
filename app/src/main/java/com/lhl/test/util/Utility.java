package com.lhl.test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.lhl.test.db.HotWeatherDB;
import com.lhl.test.model.City;
import com.lhl.test.model.County;
import com.lhl.test.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(HotWeatherDB hotWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String str : allProvinces) {
                    String[] array = str.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
//                    将解析出来的数据存储到Province表
                    hotWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(HotWeatherDB hotWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String str : allCities) {
                    String[] array = str.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
//                    将解析出来的数据存储到City表
                    hotWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
  public  static boolean handleCountiesResponse(HotWeatherDB hotWeatherDB,String response,int cityId){
      if(!TextUtils.isEmpty(response)){
          String[] allcounties = response.split(",");
          if (allcounties != null && allcounties.length>0){
              for (String str :allcounties){
                  String[] array = str.split("\\|");
                  County county = new County();
                  county.setCountyCode(array[0]);
                  county.setCountyName(array[1]);
                  county.setCityID(cityId);
//                  将解析出来的数据存储到County表
                  hotWeatherDB.saveCounty(county);
              }
              return true;
          }
      }

      return false;
  }
    /**
     *解析服务器返回的JSON数据，并将解析出来的数据存储到本地
     */

    public static void handleWeatherResponse(Context context,String respons){
        Log.d("TAG", "handleWeatherResponse: ");
        try {
            JSONObject jsonObject = new JSONObject(respons);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            Log.d("TAG", "handleWeatherResponse: weatherInfo="+weatherInfo);

            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中
     *
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));

        editor.commit();

    }


}
