package com.example.licor.gourmet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Licor on 2017/6/3.
 */

public class BlogAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<Blog> mBlogList;

    public BlogAdapter(Context context, List<Blog> blogList) {
        mContext = context;
        mBlogList = blogList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mBlogList.size();
    }

    @Override
    public Blog getItem(int i) {
        return mBlogList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setBlogList(List<Blog> blogList) {
        mBlogList = blogList;
        this.notifyDataSetChanged();
        Log.v("in adapter notify", String.valueOf(this.getCount()));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = layoutInflater.inflate(R.layout.blog_item_layout,null);
        ImageView iv = (ImageView) v.findViewById(R.id.image_gridView_item);
        TextView tv = (TextView) v.findViewById(R.id.text_gridView_item);

        String url = mBlogList.get(i).getImageUrl();
        Picasso.with(layoutInflater.getContext()).load(url).fit().error(R.drawable.progress_bar_circle).into(iv);
        tv.setText(mBlogList.get(i).getTitle());
        return v;
    }
}
