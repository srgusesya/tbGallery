package com.example.sitirahzanagusesya.tbgallery.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.sitirahzanagusesya.tbgallery.R;
import com.example.sitirahzanagusesya.tbgallery.model.GalleryItem;

public class DetailActivity extends AppCompatActivity {

    ImageView detailPhoto;
    TextView detailNama;
    TextView detailLokasi;
    TextView detailLat;
    TextView detailLong;
    TextView detailDeskripsi;
    GalleryItem galleryItem;
    GalleryItem gal = new GalleryItem();
    GalleryItem mItems = new GalleryItem();
    private static final String STATE_COUNTER = "counter";
    private int mCounter;
    ToggleButton toggle_fav_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailPhoto = findViewById(R.id.detail_photo);
        detailNama = findViewById(R.id.detail_nama);
        detailLokasi = findViewById(R.id.detail_lokasi);
        detailLat = findViewById(R.id.detail_lat);
        detailLong = findViewById(R.id.detail_long);
        detailDeskripsi = findViewById(R.id.detail_deskripsi);

        Intent detailIntent = getIntent();
        if (null != detailIntent) {
            galleryItem = detailIntent.getParcelableExtra("key_movie_parcelable");
        }

        if (galleryItem != null) {
            detailNama.setText(galleryItem.getName());
            detailLokasi.setText(galleryItem.getLocation());
            detailLat.setText(String.valueOf(galleryItem.getLatitude()));
            detailLong.setText(String.valueOf(galleryItem.getLongitude()));
            detailDeskripsi.setText(galleryItem.getDescription());

        String url = "http://10.44.7.145:8000/image/" + galleryItem.getPhoto();
        Glide.with(this)
                .load(url)
                .into(detailPhoto);

        }

        toggle_fav_btn = findViewById(R.id.toggle_btn_id);
        toggle_fav_btn.setChecked(false);
//        toggle_fav_btn.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_favorite_border_black_24dp));
        toggle_fav_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(DetailActivity.this, "Add To Favorite", Toast.LENGTH_SHORT).show();
                    toggle_fav_btn.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_favorite_black_24dp));
                }else{
                    Toast.makeText(DetailActivity.this, "Remove From Favorite", Toast.LENGTH_SHORT).show();
                    toggle_fav_btn.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_favorite_border_black_24dp));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_share) {
            String data = detailDeskripsi.getText().toString();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, data);
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(shareIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COUNTER, mCounter);
    }
}
