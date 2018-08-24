package com.common.testhistorysearch.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.common.testhistorysearch.R;
import com.common.testhistorysearch.model.ResultBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhenhua on 2018/8/24.
 */
public class SearchResultBeanAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private MyCustomFilter mCustomFilter;
    private List<ResultBean> mResultLists;

    private ArrayList<ResultBean> mOriginalValues;

    private final Object mLock = new Object();

    public SearchResultBeanAdapter(Context mContext, List<ResultBean> mResultLists) {
        this.mContext = mContext;
        this.mResultLists = mResultLists;
    }

    @Override
    public int getCount() {
        return mResultLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mResultLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyViewHolder mViewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_item, null);
            mViewHolder = new MyViewHolder();
            mViewHolder.nameTv = view.findViewById(R.id.search_name_tv);
            mViewHolder.phoneTv = view.findViewById(R.id.search_phone_tv);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) view.getTag();
        }
        mViewHolder.nameTv.setText(mResultLists.get(position).getUserName());
        mViewHolder.phoneTv.setText(mResultLists.get(position).getUserPhone());

        return view;
    }

    static class MyViewHolder {
        TextView nameTv;
        TextView phoneTv;
    }

    @Override
    public Filter getFilter() {
        if (mCustomFilter == null) {
            mCustomFilter = new MyCustomFilter();
        }
        return mCustomFilter;
    }

    class MyCustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // 持有过滤操作完成之后的数据。该数据包括过滤操作之后的数据的值以及数量。
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                // 将list的用户集合转换给这个原始数据的ArrayList
                mOriginalValues = new ArrayList<>(mResultLists);
            }

            if (constraint == null || constraint.length() == 0) {
                synchronized (mLock) {
                    ArrayList<ResultBean> list = new ArrayList<>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                // 筛选
                String prefixConstraint = constraint.toString().toLowerCase();

                // 声明一个临时的集合对象将原始数据赋给这个临时变量
                final ArrayList<ResultBean> values = mOriginalValues;

                final int count = values.size();
                // 新的集合对象
                final ArrayList<ResultBean> newLists = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    // 如果姓名的前缀相符或者电话相符就添加到新的集合
                    final ResultBean value = (ResultBean) values.get(i);

                    Log.i("coder", "PinyinUtils.getAlpha(value.getUsername())" + PinyinUtils.getAlpha(value.getUserName()));
                    if (PinyinUtils.getAlpha(value.getUserName()).startsWith(prefixConstraint)
                            || value.getUserPhone().startsWith(prefixConstraint) || value.getUserName().startsWith(prefixConstraint)) {

                        newLists.add(value);
                    }
                }
                // 然后将这个新的集合数据赋给FilterResults对象
                results.values = newLists;
                results.count = newLists.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // 重新将与适配器相关联的List重赋值一下
            mResultLists = (List<ResultBean>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
