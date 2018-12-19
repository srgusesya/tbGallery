package com.example.sitirahzanagusesya.tbgallery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sitirahzanagusesya.tbgallery.R;
import com.example.sitirahzanagusesya.tbgallery.model.GalleryItem;

import java.util.ArrayList;
import java.util.List;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.GalleryHolder> {

    ArrayList<GalleryItem> dataGallery;
    OnGalleryItemClicked clickHandler;
    Context context;

    public void setDataGallery(ArrayList<GalleryItem> gallerys) {
        this.dataGallery = gallerys;
        notifyDataSetChanged();
    }

    public GalleryListAdapter(Context context) {
        this.context = context;
    }

    public void setDataGallery(List<GalleryItem> gallerys) {
        dataGallery = new ArrayList<>(gallerys);
        notifyDataSetChanged();
    }

    public void setClickHandler(OnGalleryItemClicked clickHandler) {
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.gallery_row, parent, false);
        GalleryHolder holder = new GalleryHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, final int position) {
        GalleryItem gallery = dataGallery.get(position);
        holder.textNama.setText(gallery.getName());
        holder.textLokasi.setText(gallery.getLocation());

        String url = "http://10.44.7.145:8000/image/" + gallery.getPhoto();
        Glide.with(holder.itemView)
                .load(url)
                .into(holder.imagePhoto);

    }

    @Override
    public int getItemCount() {
        if (dataGallery != null) {
            return dataGallery.size();
        }
        return 0;
    }

    public class GalleryHolder extends RecyclerView.ViewHolder {
        ImageView imagePhoto;
        TextView textNama;
        TextView textLokasi;

        public GalleryHolder(View itemView) {
            super(itemView);
            imagePhoto = itemView.findViewById(R.id.imgPhoto);
            textNama = itemView.findViewById(R.id.textNama);
            textLokasi = itemView.findViewById(R.id.textLokasi);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryItem galleryItem = dataGallery.get(getAdapterPosition());
                    clickHandler.galleryItemClicked(galleryItem);
                }
            });
        }
    }

    public interface OnGalleryItemClicked {
        void galleryItemClicked(GalleryItem galleryItem);

    }
}
