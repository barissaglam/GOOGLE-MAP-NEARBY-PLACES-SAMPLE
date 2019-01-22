package saglam.baris.googlenearby.other;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import saglam.baris.googlenearby.R;

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyClusterItem> {

    public MarkerClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {
        // use this to make your change to the marker option
        // for the marker before it gets render on the map
        markerOptions.icon(BitmapDescriptorFactory.
                fromResource(R.drawable.marker_customer));
    }
}