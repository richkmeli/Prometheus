package informatica.unipr.it.Prometheus.fragment;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.settings.LocationSettingsActivity;
import informatica.unipr.it.Prometheus.settings.SettingsActivity;
import informatica.unipr.it.Prometheus.tutorial.TutorialActivity;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private SharedPreferences mapSharedPreferences;
    private GoogleMap maps;
    private AsyncTask threadSearch;
    private ProgressBar mapsProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        threadSearch = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }
        };


        final View rootView = inflater.inflate(R.layout.activity_maps, container, false);
        mapSharedPreferences = getActivity().getSharedPreferences("Map", Context.MODE_PRIVATE);
        SharedPreferences tutorialSharedPreferences = getActivity().getSharedPreferences("Tutorial", Context.MODE_PRIVATE);
        if (!tutorialSharedPreferences.getBoolean("mapHomeShowed", false)) {
            tutorialSharedPreferences.edit().putBoolean("mapHomeShowed", true).apply();
            Intent tutorial = new Intent(getContext(), TutorialActivity.class);
            tutorial.putExtra("which", "maps");
            startActivity(tutorial);
        }

        setHasOptionsMenu(true);

        mapsProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingMaps);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.maps));


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);
        SharedPreferences locationSharedPreferences = getActivity().getSharedPreferences("Location", Context.MODE_PRIVATE);

        if (!locationSharedPreferences.getBoolean("isLocationActive", true)) {
            Toast.makeText(getActivity(), getString(R.string.silence_place_toast), Toast.LENGTH_LONG).show();
        }
        return rootView;

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final Handler handler = new Handler();
        maps = googleMap;
        maps.setMyLocationEnabled(true);

        Thread openThread = new Thread(new Runnable() {
            @Override
            public void run() {

                double latitude;
                double longitude;

                LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

                String locationProvider = LocationManager.NETWORK_PROVIDER;


                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getActivity().onBackPressed();
                }
                Location myLocation = locationManager.getLastKnownLocation(locationProvider);
                LatLng latLng = null;
                if (mapSharedPreferences.getBoolean("thereIsMarker", false)) {
                    latitude = Double.parseDouble(mapSharedPreferences.getString("latitude", "0"));
                    longitude = Double.parseDouble(mapSharedPreferences.getString("longitude", "0"));

                    latLng = new LatLng(latitude, longitude);
                    final LatLng finalLatLng1 = latLng;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            maps.addMarker(new MarkerOptions().position(finalLatLng1).snippet(getString(R.string.marker_subtitle)).title((getString(R.string.marker_title))));
                            maps.moveCamera(CameraUpdateFactory.newLatLng(finalLatLng1));
                            maps.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    });

                } else {
                    if (myLocation == null) {
                        //Universita'
                        latitude = 44.76516282282244;
                        longitude = 10.311720371246338;
                    } else {
                        latitude = myLocation.getLatitude();
                        longitude = myLocation.getLongitude();
                    }
                    latLng = new LatLng(latitude, longitude);
                    final LatLng finalLatLng = latLng;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            maps.moveCamera(CameraUpdateFactory.newLatLng(finalLatLng));
                            maps.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    });
                }
            }
        });
        openThread.start();
        maps.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                maps.clear();
                mapSharedPreferences.edit().putBoolean("thereIsMarker", false).apply();
            }
        });

        maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                maps.clear();
                final MarkerOptions myMarker = new MarkerOptions().position(point).snippet(getString(R.string.marker_subtitle)).title((getString(R.string.marker_title)));
                maps.addMarker(myMarker);
                mapSharedPreferences.edit().putString("latitude", Double.valueOf(point.latitude).toString()).apply();
                mapSharedPreferences.edit().putString("longitude", Double.valueOf(point.longitude).toString()).apply();
                mapSharedPreferences.edit().putBoolean("thereIsMarker", true).apply();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // Serve per soluzione alternativa
        Context context = getActivity();
        SharedPreferences avatarSP = context.getSharedPreferences("Avatar", Context.MODE_PRIVATE);
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint(getString(R.string.search_hint_map));


        if (!avatarSP.getString("avatarTheme", "PromTheme").equals("PigTheme")) {
            int searchImgId = android.support.v7.appcompat.R.id.search_button;
            ImageView iconSearch = (ImageView) searchView.findViewById(searchImgId);
            iconSearch.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);
            int xButton = android.support.v7.appcompat.R.id.search_close_btn;
            ImageView xButtonIcon = (ImageView) searchView.findViewById(xButton);
            xButtonIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        } else {
            int searchImgId = android.support.v7.appcompat.R.id.search_button;
            ImageView iconSearch = (ImageView) searchView.findViewById(searchImgId);
            iconSearch.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            int xButton = android.support.v7.appcompat.R.id.search_close_btn;
            ImageView xButtonIcon = (ImageView) searchView.findViewById(xButton);
            xButtonIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }


        final SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                mapsProgressBar.setVisibility(View.VISIBLE);

                threadSearch = new AsyncTask() {
                    LatLng searchPos;
                    int searchResult;

                    @Override
                    protected Object doInBackground(Object[] params) {
                        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses =
                                    geoCoder.getFromLocationName(query, 5);
                            if (addresses.size() > 0) {
                                searchPos = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                                searchResult = 1;

                            } else {
                                searchResult = 2;

                            }
                        } catch (IOException e) {
                            searchResult = 3;

                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        mapsProgressBar.setVisibility(View.GONE);
                        switch (searchResult) {
                            case 1:
                                maps.moveCamera(CameraUpdateFactory.newLatLng(searchPos));
                                maps.animateCamera(CameraUpdateFactory.zoomTo(15));
                                break;
                            case 2:
                                Toast.makeText(getActivity(), getString(R.string.toast_more_information), Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                Toast.makeText(getActivity(), getString(R.string.toast_impossible), Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), getString(R.string.toast_impossible), Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                threadSearch.execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) search.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            if (!avatarSP.getString("avatarTheme", "PromTheme").equals("PigTheme")) {
                mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
            } else {
                mCursorDrawableRes.set(searchTextView, R.drawable.cursor_black);
            }
        } catch (Exception e) {
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onPause() {
        threadSearch.cancel(true);
        mapsProgressBar.setVisibility(View.GONE);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(getContext(), LocationSettingsActivity.class);
            startActivity(settings);
            getActivity().finish();


        } else if (id == R.id.action_tutorial) {
            Intent tutorial = new Intent(getContext(), TutorialActivity.class);
            tutorial.putExtra("which", "maps");
            startActivity(tutorial);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}
