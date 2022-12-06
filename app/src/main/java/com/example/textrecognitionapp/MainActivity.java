package com.example.textrecognitionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MainActivity extends AppCompatActivity {

    private Button captureImageBtn, detectTextBtn;
    private ImageView imageView;
    private TextView textView;
    private Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//                            Bundle extras = data.getExtras();
//                            Bitmap imageBitmap = (Bitmap) extras.get("data");
//                            imageView.setImageBitmap(imageBitmap);
//                        }
//                    }
//                }
//        );

        captureImageBtn = findViewById(R.id.capture_image);
        detectTextBtn = findViewById(R.id.detect_text_image);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);

        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectTextFromImage();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage() {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        InputImage image = InputImage.fromBitmap(imageBitmap, 0);

        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                displayTextFromImage(visionText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
    }

    private void displayTextFromImage(Text visionText) {
        if(visionText.getTextBlocks().isEmpty()){
            Toast.makeText(MainActivity.this, "The image has no text found.", Toast.LENGTH_SHORT).show();
        }

        else{
            String extractedText = visionText.getText();
            ArrayList<String> list = new ArrayList<>(
                    Arrays.asList("hello", "christian", "summit", "coca cola", "johnson")
            );

            String result = FuzzySearch.extractOne(extractedText, list).getString();

            textView.setText(extractedText + result);

        }
    }

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
//            imageActivityResultLauncher.launch(takePictureIntent);
//        }
//    }
}