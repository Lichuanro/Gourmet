package com.example.licor.gourmet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


public class BlogFragment extends Fragment implements AsyncResponseForBlog {
    private BlogAdapter blogAdapter;

    public BlogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        blogAdapter = new BlogAdapter(this.getContext(), new ArrayList<Blog>());

        getBlog getBlogTask = new getBlog();
        getBlogTask.delegate = this;
        getBlogTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        GridView gridview = (GridView)view.findViewById(R.id.blogGridView);

        gridview.setAdapter(blogAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                fragmentTransfer(v,blogAdapter.getItem(position));
            }
        });
        return view;
    }

    public void fragmentTransfer(View view, Blog blog){
        BlogDetailFragment detailFragment = BlogDetailFragment.newInstance(
                blog.getImageUrl(),blog.getDetailImg(),blog.getTitle(),blog.getContent(), blog.getUrl());

        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)  //将当前fragment加入到返回栈中
                .replace(R.id.fragment_content, detailFragment).commit();
    }

    @Override
    public void processFinish(List<Blog> output) {
        blogAdapter.setBlogList(output);
    }
}
