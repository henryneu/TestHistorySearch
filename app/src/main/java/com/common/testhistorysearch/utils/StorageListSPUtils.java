package com.common.testhistorysearch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhenhua on 2018/5/24.
 * SharedPreferences 存取 List<T> 工具类
 */

public class StorageListSPUtils {

    private static SharedPreferences mSharedPreferences;

    public StorageListSPUtils(Context context, String preferenceName) {
        mSharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    /**
     * 保存 List
     * @param tag
     * @param datalist
     */
    public <T> void saveDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        // 转换成 Json 数据，再保存
        String strJson = gson.toJson(datalist);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.putString(tag, strJson);
        mEditor.apply();
    }

    /**
     * 获取 List
     * @param tag
     * @return
     */
    public <T> List<T> loadDataList(String tag) {
        List<T> dataList = new ArrayList<>();
        // 获取存储的 Json 数据
        String strJson = mSharedPreferences.getString(tag, null);
        if (null == strJson) {
            return dataList;
        }

        Gson gson = new Gson();
        dataList = gson.fromJson(strJson, new TypeToken<List<T>>() {}.getType());
        return dataList;
    }

    /**
     * 移除 List
     * @param tag
     */
    public <T> void removeDateList(String tag) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.remove(tag);
        mEditor.apply();
    }
}
