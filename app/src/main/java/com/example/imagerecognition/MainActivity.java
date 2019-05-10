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
import android.widget.Toast;

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
    private TextView txtView, txtView2, txtViewError;
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
        txtViewError = findViewById(R.id.textView4);
        snapBtn = findViewById(R.id.btnSnap);
        detectBtn = findViewById(R.id.btnDetect);
        imageView = findViewById(R.id.imageView);


    }



/*
This method goes around the private method. Makes thee methods in this class encapsulated.
 */
    public void takeImage(View view){ dispatchTakePictureIntent(); }

    public void recognizeImage(View view){
        detectImage();
    }




    /*
    This method activates the camera intent
     */
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

/*
This method is activated when the picture is taken. It saves it as a Bitmap and displays it to the user
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }



/*
This method detectsts the image. The app downloaded the Free Neural Network from the Firebase.
It sends the image to the Neural Network and returns a list of the answers
 */
    public void detectImage() {

        if (imageBitmap == null) {
            txtViewError.setTextSize(40);
            txtViewError.setText("Take a Picture first");
        } else {
            txtViewError.setText("");
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

            labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {

                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                    float maxConfidence;
                    // I chose to display only top 2 recognitions because the rest if not accurate at all.
                    txtView.setTextSize(30);
                    txtView2.setTextSize(30);
                    txtView.setText(labels.get(0).getText());
                    txtView2.setText(labels.get(1).getText());
                    //The toast displays the highest confidence.
                    Toast.makeText(MainActivity.this, labels.get(0).getText() +
                            " has a highest confidence of " +
                            labels.get(0).getConfidence(), Toast.LENGTH_LONG).show();
                }
            })//In case something fails and it cannot recognize the image. 
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            txtView.setText("Cannot recognize");
                            Log.d("error:", "Cannot recognize");
                        }
                    });

        }

    }

}
