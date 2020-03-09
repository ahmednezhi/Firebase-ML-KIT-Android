package com.example.mlkittest.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.mlkittest.FaceContourGraphic;
import com.example.mlkittest.GraphicOverlay;
import com.example.mlkittest.R;
import com.example.mlkittest.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetectFaceActivity extends AppCompatActivity {

    @BindView(R.id.pickedImage)
    ImageView pickedImage;
    @BindView(R.id.graphic_overlay)
    GraphicOverlay graphicOverlay;
    @BindView(R.id.pickImage)
    Button pickImage;

    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_face);
        _context = this;
        ButterKnife.bind(this);


    }

    @OnClick(R.id.pickImage)
    public void onViewClicked() {
        ImagePicker.create(this).single()
                .start();
    }

    private void runFaceDetector(Bitmap bitmap) {

        pickImage.setEnabled(false);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST) //ACCURATE
                        //.setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        //.setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        //.setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(highAccuracyOpts);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        Toast.makeText(_context, firebaseVisionFaces.size() + " faces detected", Toast.LENGTH_SHORT).show();
                        processFaceResult(firebaseVisionFaces);
                        pickImage.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(_context, "failed to detect faces", Toast.LENGTH_SHORT).show();
                        pickImage.setEnabled(true);
                    }
                });


    }


    private void processFaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        for (int i = 0; i < firebaseVisionFaces.size(); i++) {

            FirebaseVisionFace face = firebaseVisionFaces.get(i);
            FaceContourGraphic faceGraphic = new FaceContourGraphic(graphicOverlay);
            graphicOverlay.add(faceGraphic);
            faceGraphic.updateFace(face);

            /*Rect bounds = firebaseVisionFaces.get(i).getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, bounds);
            graphicOverlay.add(rectOverlay);*/
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            graphicOverlay.clear();
            Image image = ImagePicker.getFirstImageOrNull(data);
            File file = new File(image.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            runFaceDetector(bitmap);
            pickedImage.setImageBitmap(bitmap);
        }
    }
}
