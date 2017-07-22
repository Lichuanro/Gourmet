package com.example.licor.gourmet;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Licor on 2017/6/3.
 */

public class getBlog extends AsyncTask<Void,Void, List<Blog>> {

    public AsyncResponseForBlog delegate = null;

    public List<Blog> getConnection() {
        List<Blog> blogList = new ArrayList<>();


        String url = "http://iamafoodblog.com/";
        Connection conn = Jsoup.connect(url);

        conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
        Document doc = null;

        try {
            doc = conn.get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements foodMenu = doc.select("div.thumbnail");



        for (Element element : foodMenu) {

            String fUrl = element.select("a").attr("href");
            String fName = element.select("h4").text();
            Connection connection = Jsoup.connect(fUrl);
            connection.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document document = null;
            try {
                document = connection.get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fPic = document.select("div.article-image").select("img").attr("src");
            Elements media = document.select("[src]");
            Elements steps = document.select("div.the-post.container").select("div.post-container.recipe-body");

            String context = new String();
            String detailPics = new String();
            for(Element step:steps){
                detailPics = step.select("img").attr("src");
            }
            context = document.select("div.post-container.recipe-body").select("p:nth-child(4)").text()
                    +"\n"+document.select("div.post-container.recipe-body").select("p:nth-child(5)").text()
                    +"\n"+document.select("div.post-container.recipe-body").select("p:nth-child(6)").text();
            blogList.add(new Blog(fName, context, fPic,detailPics,fUrl));

        }
        return blogList;
    }

    @Override
    protected List<Blog> doInBackground(Void... voids) {
        return getConnection();
    }

    @Override
    protected void onPostExecute(List<Blog> blogs) {
        delegate.processFinish(blogs);
    }
}

