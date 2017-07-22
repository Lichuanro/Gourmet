package com.example.licor.gourmet;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Licor on 2017/5/30.
 */

public class getUrlResponse extends AsyncTask<String, Void, Information> {
    private final String LOG_TAG = getUrlResponse.class.getSimpleName();

    public AsyncResponse delegate = null;

    private final Context mContext;
    public getUrlResponse(Context context) {
        this.mContext = context;
    }

    public String getImageName(String id) {

        String imageName = null;

        HttpURLConnection urlConnection = null;
        String rawJsonResponse = null;
        BufferedReader reader = null;


        try {
            final String WIKIDATA_BASE_URL = "https://www.wikidata.org/w/api.php";
            final String ACTION_PARAM = "action";
            final String ENTITY_PARAM = "entity";
            final String FORMAT_PARAM = "format";
            final String PROPERTY_PARAM = "property";

            Uri builtUri = Uri.parse(WIKIDATA_BASE_URL).buildUpon()
                    .appendQueryParameter(ACTION_PARAM, "wbgetclaims")
                    .appendQueryParameter(ENTITY_PARAM, id)
                    .appendQueryParameter(FORMAT_PARAM, "json")
                    .appendQueryParameter(PROPERTY_PARAM, "P18")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to wikidata, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            rawJsonResponse = buffer.toString();

            Log.v("imageNameRawJson", rawJsonResponse);
            // get the image page
            final String JSON_CLAIM = "claims";
            final String JSON_PROPERTY = "P18";
            final String JSON_MAIN = "mainsnak";
            final String JSON_DATA_VALUE = "datavalue";
            final String JSON_IMAGE_NAME = "value";

            JSONObject jsonObject = new JSONObject(rawJsonResponse);
            JSONObject claimObject = jsonObject.getJSONObject(JSON_CLAIM);
            JSONArray propertyArray = claimObject.getJSONArray(JSON_PROPERTY);
            JSONObject firstObject = propertyArray.getJSONObject(0);
            JSONObject mainObject = firstObject.getJSONObject(JSON_MAIN);
            JSONObject dataValue = mainObject.getJSONObject(JSON_DATA_VALUE);
            imageName = dataValue.getString(JSON_IMAGE_NAME);
            imageName = imageName.replace(" ", "_");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return imageName;

    }

    public String getImageUrl(String imageName) {
        HttpURLConnection urlConnection = null;
        String rawJsonResponse = null;
        BufferedReader reader = null;
        String imageUrl = null;

        if (imageName == null) {
            return null;
        }
        try {
            String baseImageUrl = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:";
            baseImageUrl += imageName;
            URL url = new URL(baseImageUrl.toString());

            // Create the request to wikidata, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            rawJsonResponse = buffer.toString();

            Log.v("imageRawString", rawJsonResponse);
            // get the image page
            final String JSON_QUERY = "query";
            final String JSON_PAGE = "pages";
            final String JSON_IMAGEINFO = "imageinfo";
            final String JSON_URL = "url";

            JSONObject jsonObject = new JSONObject(rawJsonResponse);
            JSONObject queryObject = jsonObject.getJSONObject(JSON_QUERY);
            JSONObject pageObject = queryObject.getJSONObject(JSON_PAGE);
            JSONObject firstObject = pageObject.getJSONObject(pageObject.names().getString(0));
            JSONArray imageInfoList = firstObject.getJSONArray(JSON_IMAGEINFO);
            JSONObject firstImageInfo = imageInfoList.getJSONObject(0);
            imageUrl = firstImageInfo.getString(JSON_URL);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    public Information loadInformation(String jsonResponse) throws JSONException {
        Information information;
        String name = null;
        String photoUrl = null;
        String description = null;
        String id = null;

        final String JSON_LIST = "search";
        final String JSON_ID = "id";
        final String JSON_LABEL = "label";
        final String JSON_DESCRIPTION = "description";

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray searchJson = jsonObject.getJSONArray(JSON_LIST);
            JSONObject firstMatch = searchJson.getJSONObject(0);

            name = firstMatch.getString(JSON_LABEL);
            description = firstMatch.getString(JSON_DESCRIPTION);
            id = firstMatch.getString(JSON_ID);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        String imageName = getImageName(id);
        Log.v("imageName", imageName);
        photoUrl = getImageUrl(imageName);
        Log.v("photoUrl", photoUrl);

        return new Information(name, photoUrl, description);
    }

    @Override
    protected Information doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String query = params[0];

        HttpURLConnection urlConnection = null;
        String rawJsonResponse = null;
        BufferedReader reader = null;

        try {
            final String WIKIDATA_BASE_URL = "https://www.wikidata.org/w/api.php";
            final String ACTION_PARAM = "action";
            final String SEARCH_PARAM = "search";
            final String FORMAT_PARAM = "format";
            final String LANGUAGE_PARAM = "language";

            Uri builtUri = Uri.parse(WIKIDATA_BASE_URL).buildUpon()
                    .appendQueryParameter(ACTION_PARAM, "wbsearchentities")
                    .appendQueryParameter(SEARCH_PARAM, query)
                    .appendQueryParameter(FORMAT_PARAM, "json")
                    .appendQueryParameter(LANGUAGE_PARAM, "en")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to wikidata, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            rawJsonResponse = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Information information = null;
        try {
            information = loadInformation(rawJsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return information;
    }

    @Override
    protected void onPostExecute(Information information) {
        delegate.processFinish(information);
    }
}
