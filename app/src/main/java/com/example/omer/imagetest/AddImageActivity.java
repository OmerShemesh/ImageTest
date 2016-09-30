package com.example.omer.imagetest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddImageActivity extends AppCompatActivity {


    public static final int GALLERY = 0;
    private ImageView addPhoto;
    private EditText name;
    private Button btnAddPhoto;
    private Uri imageUri;
    private DatabaseReference ref;
    private StorageReference storageRef;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Adding Image...");

        addPhoto = (ImageView) findViewById(R.id.add_photo);
        name = (EditText)findViewById(R.id.name);
        btnAddPhoto = (Button)findViewById(R.id.btn_add_photo);


        ref = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.equals(name.getText(),"")){
                    name.setError("Please Enter a Name!");
                }
                else{

                    dialog.show();
                    storageRef.child("images").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            ref.child(MainActivity.CITIES).push().setValue(new City(name.getText().toString(),downloadUrl.toString()));
                            dialog.dismiss();
                            finish();
                        }
                    });

                }

            }
        });


    }

    private void openGallery() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(i,"Select Picture"),GALLERY);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY && resultCode == RESULT_OK && data.getData() != null){
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Picasso.with(this)
                        .load(imageUri)
                        .fit()
                        .into(addPhoto);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
