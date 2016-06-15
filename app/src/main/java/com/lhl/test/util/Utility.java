package com.lhl.test.util;

import android.text.TextUtils;
import android.util.Log;

import com.lhl.test.db.HotWeatherDB;
import com.lhl.test.model.City;
import com.lhl.test.model.County;
import com.lhl.test.model.Province;

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
                    Log.d("TAG", "handleProvincesResponse: ");
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
                    Log.d("TAG", "handleCitiesResponse: ");
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
                  Log.d("TAG", "handleCountiesResponse: ");
              }
              return true;
          }
      }

      return false;
  }
}
