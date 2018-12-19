package com.example.sitirahzanagusesya.tbgallery.activity;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sitirahzanagusesya.tbgallery.GalleryClient;
import com.example.sitirahzanagusesya.tbgallery.R;
import com.example.sitirahzanagusesya.tbgallery.adapter.GalleryListAdapter;
import com.example.sitirahzanagusesya.tbgallery.db.AppDatabase;
import com.example.sitirahzanagusesya.tbgallery.db.Gallery;
import com.example.sitirahzanagusesya.tbgallery.model.GalleryItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements GalleryListAdapter.OnGalleryItemClicked {

    private static final String TAG = "MainActivity";
    List<GalleryItem> dataGallery = new ArrayList<>();
    List<GalleryItem> mItems = new ArrayList<>();
    RecyclerView recyclerView;
    GalleryListAdapter adapter;

    AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, String.valueOf(dataGallery.size()));

        mDb = Room.databaseBuilder(this, AppDatabase.class, "tb_gallery.db")
                .allowMainThreadQueries()
                .build();

        adapter = new GalleryListAdapter(this);
        adapter.setDataGallery(dataGallery);
        adapter.setClickHandler(this);

        recyclerView = findViewById(R.id.rv_gallery_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();

        //jika berganti orientasi pada device (portrait/landscape)
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        if (savedInstanceState != null) {
            mItems = savedInstanceState.getParcelableArrayList("gallery2");
            if (mItems != null) {
                adapter.setDataGallery(new ArrayList<GalleryItem>(mItems));
                dataGallery = mItems;
            }

        } else {
            getData();
        }
        recyclerView.setAdapter(adapter);
    }

    public void getData() {

        if (isConnected()) {
            String API_BASE_URL = "http://10.44.7.145:8000/api/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GalleryClient client = retrofit.create(GalleryClient.class);

            Call<List<GalleryItem>> call = client.getData();
            call.enqueue(new Callback<List<GalleryItem>>() {
                @Override
                public void onResponse(Call<List<GalleryItem>> call, Response<List<GalleryItem>> response) {
                    //GalleryList galleryList = response.body();
                    List<GalleryItem> galleryList = response.body();
                    dataGallery = galleryList;

                    saveGalleryToDb(galleryList);

                    adapter.setDataGallery((ArrayList<GalleryItem>) galleryList);

                }

                @Override
                public void onFailure(Call<List<GalleryItem>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //ambil data ke db
            List<Gallery> gallery2s = mDb.galleryDao().getAllGallery();
            ArrayList<GalleryItem> gallery = new ArrayList<>();
            for (Gallery gal : gallery2s) {
                GalleryItem m = new GalleryItem(
                        gal.id,
                        gal.name,
                        gal.location,
                        gal.latitude,
                        gal.longitude,
                        gal.photo,
                        gal.description
                );
                gal.add(m);
            }
            adapter.setDataGallery(gallery);
        }
    }

    //    cek internet
    public Boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("gallery2", (ArrayList<? extends Parcelable>) dataGallery);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Toast.makeText(this, "Refresh Data", Toast.LENGTH_SHORT).show();
                getData();
                break;

            case R.id.menu_input:
                Intent inputGalleryIntent = new Intent(this, InputActivity.class);
                startActivity(inputGalleryIntent);
                break;

            case R.id.menu_favorite:
                Intent favoriteGalleryIntent = new Intent(this, FavoritActivity.class);
                startActivity(favoriteGalleryIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveGalleryToDb(final List<GalleryItem> galleryList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (GalleryItem m : galleryList) {
                    Gallery gallery = new Gallery();
                    gallery.id = m.getId();
                    gallery.name = m.getName();
                    gallery.location = m.getLocation();
                    gallery.latitude = m.getLatitude();
                    gallery.longitude = m.getLongitude();
                    gallery.photo = m.getPhoto();
                    gallery.description = m.getDescription();

                    mDb.galleryDao().insertGallery(gallery);
                }
            }
        }).start();
    }

    @Override
    public void galleryItemClicked(GalleryItem galleryItem) {

        Toast.makeText(this,
                "Clicked Item is : " + galleryItem.getName(),
                Toast.LENGTH_SHORT).show();

        Intent detailGalleryIntent = new Intent(this, DetailActivity.class);
        detailGalleryIntent.putExtra("key_movie_parcelable", galleryItem);
        startActivity(detailGalleryIntent);
    }

}
