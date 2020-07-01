package com.example.bltcamera.preview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.bltcamera.R;

public class PreviewActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_LOCATION = "EXTRA_IMAGE_LOCATION";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";
    private ImageView ivPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ivPreview = findViewById(R.id.ivPreview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra(EXTRA_IMAGE_LOCATION), options);
        ivPreview.setImageBitmap(bitmap);
    }
}