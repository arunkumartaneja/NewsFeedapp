package com.abnd.android.newsfeedapp.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.abnd.android.newsfeedapp.model.News;
import com.abnd.android.newsfeedapp.util.QueryUtils;

import java.util.List;

public class NewsFeedLoader extends AsyncTaskLoader<List<News>> {

    /**
     * Query URL
     */
    private String mUrl;

    public NewsFeedLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news.
        return QueryUtils.fetchNewsFeeds(mUrl);
    }
}
