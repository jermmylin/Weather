package com.apress.gerber.weather.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apress.gerber.weather.R;
import com.apress.gerber.weather.db.WeatherDB;
import com.apress.gerber.weather.model.City;
import com.apress.gerber.weather.model.County;
import com.apress.gerber.weather.model.Province;
import com.apress.gerber.weather.util.HttpCallbackListener;
import com.apress.gerber.weather.util.HttpUtil;
import com.apress.gerber.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private WeatherDB weatherDB;
    private List<String>dataList = new ArrayList<String>();

    /**
     *省列表
     * 市列表
     * 县列表
     */
    private List<Province>provinceList;
    private List<City>cityList;
    private List<County>countyList;
    private Province selectedProvince;
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    Log.d(TAG, "onItemClick: "+provinceList.get(i).getProvinceName());
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }
    /**
     * 查询全国所有的省，优先从数据库查询，没有再去服务器上查询
     */
    private void queryProvinces(){
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
                Log.d(TAG, "queryProvinces: "+province.getProvinceName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText("中国");
                currentLevel = LEVEL_PROVINCE;
            }
        }else {
            Log.d(TAG, "queryProvinces: from server");
            queryFromServer(null, "province");
        }
    }
    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再从服务器上查询
     */
    private void queryCities(){
        cityList = weatherDB.loadCities(selectedProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }
    /**
     * 查询选中的市内所有的县，优先从数据库查询，如果没有查询再到服务器上查询
     */
    private void queryCounties(){
        countyList = weatherDB.loadCounties(selectedCity.getId());
        if (countyList.size()>0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(), "country");
        }
    }
    /**
     * 根据传入的代号和类型从服务器上查询省市县数据
     */
    private void queryFromServer(final String code, final String type){
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
                    Log.d(TAG, "onFinish: province"+response);
                    result =  Utility.handleProvincesResponse(weatherDB, response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(weatherDB, response,selectedProvince.getId());
                }else if ("country".equals(type)){
                    result = Utility.handleCountiesResponse(weatherDB, response,selectedCity.getId());
                }
                Log.d(TAG, "the type is "+type);
                Log.d(TAG, "the result: "+ result);
                if (result){
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Log.d(TAG, "close the progressDialog");
                            if ("province".equals(type)){
                                queryProvinces();
                                Log.d(TAG, "queryProvince ");
                            }else if ("city".equals(type)){
                                queryCities();
                                Log.d(TAG, "queryCities");
                            }else if ("country".equals(type)){
                                queryCounties();
                                Log.d(TAG, "queryCounties");
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
            //通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}