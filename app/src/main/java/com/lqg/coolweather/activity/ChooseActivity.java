package com.lqg.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lgq.coolweather.Util.HttpHandlerUtil;
import com.lgq.coolweather.Util.HttpListener;
import com.lgq.coolweather.Util.HttpRequestUtil;
import com.lgq.coolweather.db.City;
import com.lgq.coolweather.db.County;
import com.lgq.coolweather.db.Province;
import com.lgq.coolweather.db.weatherDB;
import com.lqg.coolweather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL1 on 2016/7/30.
 */
public class ChooseActivity extends Activity implements AdapterView.OnItemClickListener {

    //选中等级
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    //各级列表
    private List<Province> ProvinceList;
    private List<City> CityList;
    private List<County> countyList;

    //ListView数据列表
    private List<String> dataLists = new ArrayList<>();
    private ArrayAdapter<String> adapter ;
    private TextView tvTitle;
    private ListView listview;
    private static weatherDB weatherDB;
    private ProgressDialog progressDialog;

    private Province select_Province;
    private City select_City;
    private County select_County;
    //选中级别
    private  int CURRENTLEVEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.list_layout);
        initView();
    }

    private void initView() {
        weatherDB =weatherDB.getInstance(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        listview = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataLists);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        queryProvinces();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (CURRENTLEVEL == LEVEL_PROVINCE) {
            select_Province = ProvinceList.get(position);
            queryCities();
        }else if (CURRENTLEVEL == LEVEL_CITY){
            select_City = CityList.get(position);
            queryCounties();
        }
    }

    private void queryProvinces() {
        ProvinceList = weatherDB.loadProvince();
        if (ProvinceList.size() >0){
            for (Province province :ProvinceList){
                dataLists.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            CURRENTLEVEL = LEVEL_PROVINCE;
            tvTitle.setText("中国");
        }else{
            queryFrmoService(null,"province");
        }
    }

    private void queryCounties() {
        CityList = weatherDB.loadCity(select_Province.getId());
        if (CityList.size() >0){
            for (City city : CityList){
                dataLists.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            CURRENTLEVEL = LEVEL_CITY;
            tvTitle.setText(select_Province.getProvinceName());
        }else{
            queryFrmoService(select_Province.getProvinceCode(),"city");
        }
    }

    private void queryCities() {
        countyList = weatherDB.loadCounty(select_City.getId());
        if (countyList.size() >0){
            for (County county : countyList){
                dataLists.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            CURRENTLEVEL = LEVEL_COUNTY;
            tvTitle.setText(select_City.getCityName());
        }else{
            queryFrmoService(select_City.getCityCode(),"county");
        }
    }
    private void queryFrmoService(final String code , final String type){
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpRequestUtil.sendHttpRequest(address, new HttpListener() {
            @Override
            public void onSucceed(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result =  HttpHandlerUtil.handlerProvince(weatherDB,response);
                }else if ("city".equals(type)){
                    result = HttpHandlerUtil.handlerCity(weatherDB, response, select_Province.getId());
                }else if ("county".equals(type)){
                    result =  HttpHandlerUtil.handlerCounty(weatherDB,response, select_City.getId());
                }
                /**
                 * 结果返回UI线程
                 */
                if (result){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseActivity.this,"查询失败",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    private void closeProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(ChooseActivity.this);
            progressDialog.setMessage("查询中");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }
    private void showProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 捕捉Back键
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (CURRENTLEVEL == LEVEL_COUNTY) {
            queryCities();
        } else if (CURRENTLEVEL == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }

    
}
