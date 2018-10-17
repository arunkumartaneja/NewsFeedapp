package com.abnd.android.newsfeedapp.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.abnd.android.newsfeedapp.R;
import com.abnd.android.newsfeedapp.adapter.NewsFeedAdapter;
import com.abnd.android.newsfeedapp.loader.NewsFeedLoader;
import com.abnd.android.newsfeedapp.model.News;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsFeedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int NEWS_FEED_LOADER_ID = 1;

    private static final String NEWS_FEED_URL = "https://content.guardianapis.com/search?api-key=ba7a5ca7-2605-46a0-8578-3ebc1536ba01";

    private NewsFeedAdapter mAdapter;
    private Integer pageSize;
    private boolean loadingMore = false;

    @BindView(R.id.loading_more_indicator) View loadingMoreIndicator;
    @BindView(R.id.loading_indicator) View loadingIndicator;
    @BindView(R.id.list) ListView newsFeedListView;
    @BindView(R.id.empty_view) TextView mEmptyStateTextView;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        ButterKnife.bind(this);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(sharedPrefs);

        pageSize = Integer.valueOf(sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default)));

        newsFeedListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsFeedAdapter(this, new ArrayList<News>());
        newsFeedListView.setAdapter(mAdapter);

        newsFeedListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!loadingMore && firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    pageSize += 5;
                    loadingMore = true;
                    loadingMoreIndicator.setVisibility(View.VISIBLE);
                    refreshNewsFeed(true);
                }
            }
        });

        refreshNewsFeed(false);

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        refreshNewsFeed(true);
                    }
                }
        );

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News news = mAdapter.getItem(position);
                if (news != null) {
                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri newsUrl = Uri.parse(news.getWebUrl());
                    // Create a new intent to view the News URL
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUrl);
                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            }
        });

    }

    private void setTheme(SharedPreferences sharedPrefs) {
        boolean isNightModeOn = sharedPrefs.getBoolean(
                getString(R.string.setting_night_mode_key),
                Boolean.valueOf(getString(R.string.setting_night_mode_default_value)));

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            boolean isAutoNightModeOn = sharedPrefs.getBoolean(
                    getString(R.string.setting_auto_night_mode_key),
                    Boolean.valueOf(getString(R.string.setting_auto_night_mode_default_value)));
            if (isAutoNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    private void refreshNewsFeed(boolean isRefreshEvent) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            // Get details on the currently active default data network
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            if (isRefreshEvent) {
                loaderManager.restartLoader(NEWS_FEED_LOADER_ID, null, this);
            } else {
                loaderManager.initLoader(NEWS_FEED_LOADER_ID, null, this);
            }
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                swipeRefreshLayout.setRefreshing(true);
                refreshNewsFeed(true);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        pageSize = Integer.valueOf(sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default)));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        boolean showImage = sharedPrefs.getBoolean(
                getString(R.string.settings_show_image_key),
                Boolean.valueOf(getString(R.string.settings_show_image_default)));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(NEWS_FEED_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("page-size", pageSize.toString());
        uriBuilder.appendQueryParameter("order-by", orderBy);
        if (showImage) {
            uriBuilder.appendQueryParameter("show-fields", "thumbnail,byline");
        } else {
            uriBuilder.appendQueryParameter("show-fields", "byline");
        }

        return new NewsFeedLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Hide loading indicator because the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No News feeds."
        mEmptyStateTextView.setText(R.string.no_news_feed);

        swipeRefreshLayout.setRefreshing(false);

        if (loadingMore) {
            loadingMore = false;
            loadingMoreIndicator.setVisibility(View.GONE);
            if  (data.size() >= 5) {
                mAdapter.addAll(data.subList(data.size() - 5, data.size() - 1));
            }

        } else {
            // Clear the adapter of previous news feed
            mAdapter.clear();
            // If there is a valid list of {@link News}, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

}
