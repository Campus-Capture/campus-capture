package com.github.campus_capture.bootcamp.map;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.campus_capture.bootcamp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class LabelInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;

    public LabelInfoWindowAdapter(Context context)
    {
        this.context = context;
    }
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        View layout = LayoutInflater.from(context).inflate(R.layout.zone_info_window, null);
        TextView titleView = layout.findViewById(R.id.info_window_title);
        titleView.setText(marker.getTitle());
        if(marker.getSnippet() != null)
        {
            TextView contentView = layout.findViewById(R.id.info_window_content);
            contentView.setText(Html.fromHtml(marker.getSnippet(), Html.FROM_HTML_MODE_LEGACY));
        }
        return layout;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
