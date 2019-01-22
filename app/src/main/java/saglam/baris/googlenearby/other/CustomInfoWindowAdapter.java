package saglam.baris.googlenearby.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import saglam.baris.googlenearby.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;
    private final View mWindow;

    public CustomInfoWindowAdapter(Context ctx) {
        context = ctx;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_map_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView top = (TextView) mWindow.findViewById(R.id.tvTop);
        TextView bottom = (TextView) mWindow.findViewById(R.id.tvBottom);

        top.setText(marker.getTitle());
        bottom.setText(marker.getSnippet());
        return mWindow;
    }
}
