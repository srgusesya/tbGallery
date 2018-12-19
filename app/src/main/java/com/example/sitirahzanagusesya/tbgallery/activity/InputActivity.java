package com.example.sitirahzanagusesya.tbgallery.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sitirahzanagusesya.tbgallery.GalleryClient;
import com.example.sitirahzanagusesya.tbgallery.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InputActivity extends AppCompatActivity implements LocationListener, EasyPermissions.PermissionCallbacks {

    ImageView inputImg;
    EditText inputNama;
    EditText inputLokasi;
    EditText inputLat;
    EditText inputLong;
    EditText inputDesc;
    Button buttonGetLoc, save;
    RequestBody filename, nama, lokasi, lat, longi, desc;
    MultipartBody.Part fileToUpload;
    LocationManager locationManager;

    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;
    private String userChoosenTask;
    private static final String STATE_COUNTER = "counter";
    private int mCounter;
    private static final String SERVER_PATH = "Path_to_your_server";
    private Uri uri;
private  static final int READ_REQUEST_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        inputImg = findViewById(R.id.inputImage);
        inputNama = findViewById(R.id.inputNama);
        inputLokasi = findViewById(R.id.inputLokasi);
        inputLat = findViewById(R.id.inputLat);
        inputLong = findViewById(R.id.inputLong);
        inputDesc = findViewById(R.id.inputDesc);
        buttonGetLoc = findViewById(R.id.buttonGetLoc);
        save = findViewById(R.id.btn_input);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 101);

        }

        buttonGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    if (checkData()) {
                        saveData();
                    }
                } else {
                    Toast.makeText(InputActivity.this, "Please Turn On Your Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        inputLat.setText("" + latitude);
        inputLong.setText("" + longitude);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(InputActivity.this, "Make sure you have an Internet Connection and GPS is enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_input, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COUNTER, mCounter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_kamera) {
            selectImage();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onResume (){
//        super.onResume();
//        getLocation();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        locationManager.removeUpdates(this);
//    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(InputActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(InputActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(uri != null){
            String filePath = getRealPathFromURIPath(uri, InputActivity.this);
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        } super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, InputActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                try {
                    onSelectFromGalleryResult(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                try {
                    onCaptureImageResult(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) throws IOException {
        Bitmap select = null;
        if (data != null) {
            select = decodeBitmap(data.getData());
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                uri = data.getData();
                String filePath = getRealPathFromURIPath(uri, InputActivity.this);
                File file = new File(filePath);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            } else {
                EasyPermissions.requestPermissions(this, "hi", READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        inputImg.setImageBitmap(select);
    }

    private void onCaptureImageResult(Intent data) throws IOException {
//        Bitmap capture = (Bitmap) data.getExtras().get("data");
        Bitmap capture = decodeBitmap(data.getData());
        inputImg.setImageBitmap(capture);
    }

    public Bitmap decodeBitmap(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public void saveData() {
        String API_BASE_URL = "http://10.44.7.145:8000/api/";

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nama = RequestBody.create(okhttp3.MultipartBody.FORM, inputNama.getText().toString());
        lokasi = RequestBody.create(okhttp3.MultipartBody.FORM, inputLokasi.getText().toString());
        lat = RequestBody.create(okhttp3.MultipartBody.FORM, inputLat.getText().toString());
        longi = RequestBody.create(okhttp3.MultipartBody.FORM, inputLong.getText().toString());
        desc = RequestBody.create(okhttp3.MultipartBody.FORM, inputDesc.getText().toString());


        //Creating object for interface
        GalleryClient api = adapter.create(GalleryClient.class);

        api.saveData(
                nama,
                lokasi,
                lat,
                longi,
                desc,
                fileToUpload,
                filename
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InputActivity.this, "Save Data Success!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(InputActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    Log.i("Yes", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Ops", "Unable submit post to API.");
            }

        });
    }

    private String getRealPathFromURIPath(Uri uri, Activity inputActivity) {
        Cursor cursor = inputActivity.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public boolean checkData() {
        if (TextUtils.isEmpty(inputNama.getText().toString().trim())) {
            inputNama.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(inputLokasi.getText().toString().trim())) {
            inputLokasi.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(inputLat.getText().toString().trim())) {
            inputLat.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(inputLong.getText().toString().trim())) {
            inputLong.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(inputDesc.getText().toString().trim())) {
            inputDesc.setError("This field is required");
            return false;
        }
        if (inputImg.getDrawable() == null) {
            Toast.makeText(this, "Please Take Picture!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}


