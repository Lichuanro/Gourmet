package com.example.licor.gourmet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BlogDetailFragment extends Fragment {

    private ImageView mainImage;
    private ImageView detailImage;
    private TextView title;
    private TextView content;
    private String url;

    public Button gotoBroswerButton;

    public BlogDetailFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_blog_detail,null);

        mainImage = (ImageView) v.findViewById(R.id.main_image);
        detailImage = (ImageView) v.findViewById(R.id.detail_image);
        title = (TextView) v.findViewById(R.id.food_title);
        content = (TextView) v.findViewById(R.id.textView);
        gotoBroswerButton = (Button) v.findViewById(R.id.gotoBroswerButton);

        if (getArguments() != null) {
            title.setText(getArguments().getString("title"));
            content.setText(getArguments().getString("content"));
            url = getArguments().getString("url");

            gotoBroswerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    getActivity().startActivity(intent);
                }
            });
            Picasso.with(this.getActivity()).load(getArguments().getString("main_img")).fit().error(R.drawable.progress_bar_dot).into(mainImage);
            Picasso.with(this.getActivity()).load(getArguments().getString("detail_img")).fit().error(R.drawable.progress_bar_dot).into(detailImage);
        }

        return v;
    }

    public static BlogDetailFragment newInstance(String img1,String img2,String title,String content, String url){

        BlogDetailFragment detailFragment = new BlogDetailFragment();
        Bundle args = new Bundle();
        args.putString("main_img", img1 );
        args.putString("detail_img",img2);
        args.putString("title",title);
        args.putString("content",content);
        args.putString("url", url);
        detailFragment.setArguments(args);

        return detailFragment;
    }

}
