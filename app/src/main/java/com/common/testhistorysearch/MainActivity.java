package com.common.testhistorysearch;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.testhistorysearch.utils.KeyBoardUtil;
import com.common.testhistorysearch.utils.StorageListSPUtils;
import com.common.testhistorysearch.widget.FakeBoldTextView;
import com.common.testhistorysearch.widget.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // 回退
    @BindView(R.id.header_back_image)
    ImageView mHeaderBackImage;

    // EditText 中的搜索图标
    @BindView(R.id.search_header_image)
    ImageView mSearchHeaderImage;

    // 搜索输入文本框
    @BindView(R.id.search_header_tv)
    EditText mSearchHeaderTv;

    // 取消
    @BindView(R.id.header_cancel_tv)
    FakeBoldTextView mHeaderCancelTv;

    // 删除搜索历史
    @BindView(R.id.search_delete_history)
    ImageView mSearchDeleteHistory;

    // 展示搜索历史记录的布局
    @BindView(R.id.search_history_layout)
    LinearLayout mSearchListLayout;

    // 流式布局展示搜索历史
    @BindView(R.id.search_history_fl)
    FlowLayout mSearchHistoryFl;

    // SharedPreferences 存取 搜索历史 的标签
    private static final String TAG_SEARCH_HISTORY = "tagSearchHistory";

    // 默认最多展示的搜索历史数
    // 这里只展示10个搜索历史，根据需要修改为你自己想要的数值
    private static final int DEFAULT_SEARCH_HISTORY_COUNT = 10;

    // 搜索历史
    private List<String> mSearchHistoryLists;

    // 存储 搜索历史集合 的工具类
    private StorageListSPUtils mStorageListSPUtils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 初始化
        init();
        mSearchHeaderTv.setOnEditorActionListener(this);
        mHeaderBackImage.setOnClickListener(this);
        mSearchHeaderImage.setOnClickListener(this);
        mHeaderCancelTv.setOnClickListener(this);
        mSearchDeleteHistory.setOnClickListener(this);
        // 初始化搜索历史布局
        initView();
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化搜索历史
        mSearchHistoryLists = new ArrayList<>();
        // 初始化存储 搜索历史集合 的工具类
        mStorageListSPUtils = new StorageListSPUtils(this, TAG);
    }

    /**
     * 初始化搜索历史布局
     */
    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        // 获取 SharedPreferences 中已存储的 搜索历史
        mSearchHistoryLists = mStorageListSPUtils.loadDataList(TAG_SEARCH_HISTORY);
        if (mSearchHistoryLists.size() != 0) {
            mSearchListLayout.setVisibility(View.VISIBLE);
            for (int i = mSearchHistoryLists.size() - 1; i >= 0; i--) {
                TextView textView = (TextView) layoutInflater.inflate(R.layout.search_history_tv, mSearchHistoryFl, false);
                final String historyStr = mSearchHistoryLists.get(i);
                textView.setText(historyStr);
                // 设置搜索历史的回显
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSearchHeaderTv.setText(historyStr);
                        mSearchHeaderTv.setSelection(historyStr.length());
                    }
                });
                // FlowLayout 中添加 搜索历史
                mSearchHistoryFl.addView(textView);
            }
        }
    }

    /**
     * EditText 编辑完成点击软键盘搜索后的监听事件
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.search_header_tv) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 存取 SharedPreferences 中存储的搜索历史并做相应的处理
                processAction();
                // 点击软件盘搜索后，隐藏软键盘
                if (null != getCurrentFocus()) {
                    KeyBoardUtil.hideKeyboard(getCurrentFocus(), MainActivity.this);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 存取 SharedPreferences 中存储的搜索历史并做相应的处理
     */
    private void processAction() {
        // 获取 EditText 输入内容
        String searchInput = mSearchHeaderTv.getText().toString().trim();
        if (TextUtils.isEmpty(searchInput)) {
            Toast.makeText(this, getResources().getString(R.string.app_search_input_empty), Toast.LENGTH_SHORT).show();
        } else {
            // 先获取之前已经存储的搜索历史
            List<String> previousLists = mStorageListSPUtils.loadDataList(TAG_SEARCH_HISTORY);
            if (previousLists.size() != 0) {
                // 如果之前有搜索历史，则添加
                mSearchHistoryLists.clear();
                mSearchHistoryLists.addAll(previousLists);
            }
            // 去除重复，如果搜索历史中已经存在则remove，然后添加到后面
            if (!mSearchHistoryLists.contains(searchInput)) {
                // 如果搜索历史超过设定的默认个数，去掉最先添加的，并把新的添加到最后
                // 这里只展示10个搜索历史，根据需要修改为你自己想要的数值
                if (mSearchHistoryLists.size() >= DEFAULT_SEARCH_HISTORY_COUNT) {
                    mSearchHistoryLists.remove(0);
                    mSearchHistoryLists.add(mSearchHistoryLists.size(), searchInput);
                } else {
                    mSearchHistoryLists.add(searchInput);
                }
            } else {
                // 如果搜索历史已存在，找到其所在的下标值
                int inputIndex = -1;
                for (int i = 0; i< mSearchHistoryLists.size(); i++) {
                    if (searchInput.equals(mSearchHistoryLists.get(i))) {
                        inputIndex = i;
                    }
                }
                // 如果搜索历史已存在，先从 List 集合中移除再添加到集合的最后
                mSearchHistoryLists.remove(inputIndex);
                mSearchHistoryLists.add(mSearchHistoryLists.size(), searchInput);
            }
            // 存储新的搜索历史到 SharedPreferences
            mStorageListSPUtils.saveDataList(TAG_SEARCH_HISTORY, mSearchHistoryLists);
            Toast.makeText(this, getResources().getString(R.string.app_search_input) + searchInput, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理点击事件
     */
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.search_header_image:
                // 存取 SharedPreferences 中存储的搜索历史并做相应的处理
                processAction();
                break;
            case R.id.search_delete_history:
                // 删除搜索历史
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.app_commit_delete_history))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.app_delete_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 清楚 SharedPreferences 中存储的搜索历史
                                mStorageListSPUtils.removeDateList(TAG_SEARCH_HISTORY);
                                mSearchHistoryLists.clear();
                                mSearchHistoryFl.removeAllViews();
                                // 删除之后，搜索历史布局隐藏
                                mSearchListLayout.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.app_delete_cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            case R.id.header_cancel_tv:
                // 取消
                this.finish();
                break;
            case R.id.header_back_image:
                // 回退
                this.finish();
                break;
        }
    }
}
