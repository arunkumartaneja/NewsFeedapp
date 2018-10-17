package com.abnd.android.newsfeedapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abnd.android.newsfeedapp.R;
import com.abnd.android.newsfeedapp.model.News;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NewsFeedAdapter extends ArrayAdapter<News> {

    private static final String API_Date_Pattern = "yyyy-MM-dd'T'kk:mm:ss'Z'";
    private static final String APP_Date_Pattern = "dd MMM yyyy hh:mm a";
    private static final String UTC = "UTC";

    public NewsFeedAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
    }

    /**
     * Returns a list item view that displays information about the news feed at the given position
     * in the list of news.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @Nullable ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        News news = getItem(position);
        if (news != null) {
            holder.titleView.setText(news.getTitle());
            holder.dateView.setText(formatDate(news.getDate()));
            holder.sectionView.setText(news.getSection());
            holder.pillarView.setText(news.getPillar());
            if (news.getThumbnail() != null) {
                holder.thumbnailView.setImageBitmap(news.getThumbnail());
            } else {
                holder.thumbnailView.setImageResource(R.drawable.no_image);
            }
            holder.authorView.setText("Author: " + news.getAuthor());
        }

        return convertView;
    }

    private String formatDate(String dateString) {
        Date date = null;
        SimpleDateFormat simpleDateFormat;
        try {
//            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'", Locale.ENGLISH);
            simpleDateFormat = new SimpleDateFormat(API_Date_Pattern, Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UTC));
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        simpleDateFormat = new SimpleDateFormat(APP_Date_Pattern);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }


    /**
     * ViewHolder to improve list view
     * https://dzone.com/articles/optimizing-your-listview
     */
    private class ViewHolder {
        private TextView titleView;
        private TextView dateView;
        private TextView pillarView;
        private TextView sectionView;
        private ImageView thumbnailView;
        private TextView authorView;

        ViewHolder(View view) {
            titleView = view.findViewById(R.id.news_title);
            dateView = view.findViewById(R.id.news_publish_date);
            pillarView = view.findViewById(R.id.news_pillar);
            sectionView = view.findViewById(R.id.news_section);
            thumbnailView = view.findViewById(R.id.news_thumbnail);
            authorView = view.findViewById(R.id.news_author);
        }
    }
}
