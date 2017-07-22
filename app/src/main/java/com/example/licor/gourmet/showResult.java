package com.example.licor.gourmet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.tag;

public class showResult extends AppCompatActivity implements AsyncResponse {

    public TextView resultTextView1;
    public TextView resultTextView2;
    public TextView resultTextView3;
    public TextView resultTextView4;
    public TextView resultTextView5;
    public TextView resultDescriptionTextView;
    public ImageView resultImageView;
    public ProgressBar progressBar;
    public Button backButton;

    List<Result> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Intent showDetailIntent = getIntent();
        resultList = (List<Result>) showDetailIntent.getSerializableExtra("Result");

        resultTextView1 = (TextView) findViewById(R.id.resultTitleTextView1);
        resultTextView2 = (TextView) findViewById(R.id.resultTitleTextView2);
        resultTextView3 = (TextView) findViewById(R.id.resultTitleTextView3);
        resultTextView4 = (TextView) findViewById(R.id.resultTitleTextView4);
        resultTextView5 = (TextView) findViewById(R.id.resultTitleTextView5);
        resultDescriptionTextView = (TextView) findViewById(R.id.resultDetailTextView);
        resultImageView = (ImageView) findViewById(R.id.resultImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setVisibility(View.INVISIBLE);

        List<TextView> resultTextViewList = new ArrayList<TextView>();
        resultTextViewList.add(resultTextView1);
        resultTextViewList.add(resultTextView2);
        resultTextViewList.add(resultTextView3);
        resultTextViewList.add(resultTextView4);
        resultTextViewList.add(resultTextView5);

        for (int i = 0; i < 5; i++) {
            resultTextViewList.get(i).setText(resultList.get(i).getName() + "   " + resultList.get(i).getValue());
        }

        // load the first result automatically
        String text = resultList.get(0).getName();
        getUrlResponse getUrlResponseTask = new getUrlResponse(this);
        getUrlResponseTask.delegate = this;
        getUrlResponseTask.execute(text);
    }

    public void textPressed(View view) {
        resultImageView.setImageResource(android.R.color.transparent);
        progressBar.setVisibility(View.VISIBLE);
        int tag = Integer.parseInt(view.getTag().toString());
        String text = resultList.get(tag).getName();
        getUrlResponse getUrlResponseTask = new getUrlResponse(this);
        getUrlResponseTask.delegate = this;
        getUrlResponseTask.execute(text);
    }

    @Override
    public void processFinish(Information output) {
        resultDescriptionTextView.setText(output.getDescription());
        Picasso.with(this).load(output.getPhotoUrl()).fit().into(resultImageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                backButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
