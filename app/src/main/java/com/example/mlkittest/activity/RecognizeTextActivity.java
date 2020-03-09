package com.example.mlkittest.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.mlkittest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class RecognizeTextActivity extends AppCompatActivity {

    @BindView(R.id.pickImage)
    Button pickImage;
    @BindView(R.id.pickedImage)
    ImageView pickedImage;
    @BindView(R.id.result)
    TextView result;

    private Context _context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_text);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.pickImage)
    public void onViewClicked() {
        ImagePicker.create(this).single()
                .start();
    }

    @OnLongClick(R.id.result)
    public void onViewLongClick() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("recognized text", result.getText());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "copied to clipboard", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            File file = new File(image.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            runTextRecognizition(bitmap);
            pickedImage.setImageBitmap(bitmap);
            pickedImage.setBackgroundColor(Color.BLACK);
        }
    }

    private void runTextRecognizition(Bitmap bitmap) {
        pickImage.setEnabled(false);
        result.setText("");
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        preocessText(firebaseVisionText);
                        pickImage.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(_context, "failed to recognize text", Toast.LENGTH_SHORT).show();
                        pickImage.setEnabled(true);
                    }
                });

    }

    private void preocessText(FirebaseVisionText firebaseVisionText) {
        String text = firebaseVisionText.getText();
        result.setText(text);
    }
}
