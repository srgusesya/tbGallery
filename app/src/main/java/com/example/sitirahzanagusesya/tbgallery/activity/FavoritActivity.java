package com.example.sitirahzanagusesya.tbgallery.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.sitirahzanagusesya.tbgallery.R;
import com.example.sitirahzanagusesya.tbgallery.model.GalleryItem;

public class FavoritActivity extends AppCompatActivity {

    ImageView favPhoto;
    TextView favNama;
    TextView favLokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorit);

        favPhoto = findViewById(R.id.fav_photo);

    }


}
