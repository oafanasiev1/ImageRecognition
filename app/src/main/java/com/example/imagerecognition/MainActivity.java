package com.example.imagerecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Tag";
    private TextView txtView, txtView2;
    private Button snapBtn, detectBtn;
    private ImageView imageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        txtView = findViewById(R.id.textView1);
        txtView2 = findViewById(R.id.textView2);
        snapBtn = findViewById(R.id.btnSnap);
        detectBtn = findViewById(R.id.btnDetect);
        imageView = findViewById(R.id.imageView);
//        snapBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                dispatchTakePictureIntent();
//            }
//        });

//        detectBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                detectImage();
//            }
//        });

    }


    public void takeImage(View view){
        dispatchTakePictureIntent();
    }


    public void recognizeImage(View view){
        detectImage();
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    public void detectImage(){

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {

            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                float maxConfidence;

                txtView.setTextSize(20);
                txtView2.setTextSize(20);
                txtView.setText(labels.get(0).getText());
                txtView2.setText(labels.get(1).getText());



//                //for (FirebaseVisionImageLabel label: labels){
//                for (int i=0;i<3;i++) {
//                    label = labels.get(i);
//                    Log.d(TAG, label.getText());
//                    Log.d(TAG, label.getConfidence() + "");
//
//
//                    txtView.setTextSize(20);
//
//                    txtView.setText(label.getText());
//
//                //   Toast.makeText(this, label.getText(), Toast.LENGTH_SHORT).show();
//
//                    maxConfidence = label.getConfidence();
//
//
//                }


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        txtView.setText("Cannot recognize");
                        Log.d("error:", "Cannot recognize");
                    }
                });

    }



}
