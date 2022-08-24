package com.example.myapplicationandoroid.profil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.myapplicationandoroid.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class EditProfilePageActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    public String[] cameraPermission;
    public String[] storagePermission;
    public Uri imageuri;
    public String profileOrCoverPhoto;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_image/";
    String uId;
    ImageView setIV;
    TextView profilePicTV, editNameTV, editPasswordTV;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //here we will handle the result of our intent
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //image picked
                    //get uri of image
                    Intent data = result.getData();
                    Uri imageUri = data.getData();
                    setIV.setImageURI(imageUri);
                    uploadProfileCoverPhoto(imageUri);

                } else {
                    //cancelled
                    Toast.makeText(EditProfilePageActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);

        profilePicTV = findViewById(R.id.profilepic);
        editNameTV = findViewById(R.id.editname);
        setIV = findViewById(R.id.setting_profile_image);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        editPasswordTV = findViewById(R.id.changepassword);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = firebaseDatabase.getReference("Users");
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String image = "" + dataSnapshot1.child("image").getValue();

                    try {
                        Glide.with(EditProfilePageActivity.this).load(image).into(setIV);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editPasswordTV.setOnClickListener(v -> {
            progressDialog.setMessage("Changing Password");
            showPasswordChangeDailog();
        });

        profilePicTV.setOnClickListener(v -> {
            progressDialog.setMessage("Updating Profile Picture");
            profileOrCoverPhoto = "image";
            showImagePicDialog();
//            if(checkAndRequestPermissions(EditProfilePageActivity.this)){
//                chooseImage(EditProfilePageActivity.this);
//            }
        });

        editNameTV.setOnClickListener(v -> {
            progressDialog.setMessage("Updating Name");
            showNamePhoneUpdate("name");
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String image = "" + dataSnapshot1.child("image").getValue();

                    try {
                        Glide.with(EditProfilePageActivity.this).load(image).into(setIV);
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editPasswordTV.setOnClickListener(v -> {
            progressDialog.setMessage("Changing Password");
            showPasswordChangeDailog();
        });

        profilePicTV.setOnClickListener(v -> {
            progressDialog.setMessage("Updating Profile Picture");
            profileOrCoverPhoto = "image";
            showImagePicDialog();
//            if(checkAndRequestPermissions(EditProfilePageActivity.this)){
//                chooseImage(EditProfilePageActivity.this);
//            }
        });

        editNameTV.setOnClickListener(v -> {
            progressDialog.setMessage("Updating Name");
            showNamePhoneUpdate("name");
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String image = "" + dataSnapshot1.child("image").getValue();

                    try {
                        Glide.with(EditProfilePageActivity.this).load(image).into(setIV);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        editPasswordTV.setOnClickListener(v -> {
            progressDialog.setMessage("Changing Password");
            showPasswordChangeDailog();
        });

        profilePicTV.setOnClickListener(v -> {
            progressDialog.setMessage("Updating Profile Picture");
            profileOrCoverPhoto = "image";
            showImagePicDialog();
//            if(checkAndRequestPermissions(EditProfilePageActivity.this)){
//                chooseImage(EditProfilePageActivity.this);
//            }
        });

        editNameTV.setOnClickListener(v -> {
            progressDialog.setMessage("Updating Name");
            showNamePhoneUpdate("name");
        });
    }

//    // checking storage permission ,if given then we can add something in our storage
//    private Boolean checkStoragePermission() {
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//    }

    // requesting for storage permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

//
//    // checking camera permission ,if given then we can click image using our camera
//    private Boolean checkCameraPermission() {
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
//        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//        return result && result1;
//    }

    // requesting for camera permission if not given
    private void requestCameraPermission() {

        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // We will show an alert box where we will write our old and new password
    private void showPasswordChangeDailog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null);
        final EditText oldpass = view.findViewById(R.id.oldpasslog);
        final EditText newpass = view.findViewById(R.id.newpasslog);

        Button editpass = view.findViewById(R.id.updatepass);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        editpass.setOnClickListener(v -> {
            String oldp = oldpass.getText().toString().trim();
            String newp = newpass.getText().toString().trim();
            if (TextUtils.isEmpty(oldp)) {
                Toast.makeText(EditProfilePageActivity.this, "Current Password cant be empty", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(newp)) {
                Toast.makeText(EditProfilePageActivity.this, "New Password cant be empty", Toast.LENGTH_LONG).show();
                return;
            }
            dialog.dismiss();
            updatePassword(oldp, newp);
        });
    }

    // Now we will check that if old password was authenticated correctly then we will update the new password
    private void updatePassword(String oldp, final String newp) {
        progressDialog.show();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldp);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> user.updatePassword(newp)
                        .addOnSuccessListener(aVoid1 -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfilePageActivity.this, "Changed Password",
                                    Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfilePageActivity.this, "Failed password update", Toast.LENGTH_LONG).show();
                        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(EditProfilePageActivity.this, "Failed password update", Toast.LENGTH_LONG).show();
        });
    }

    // Updating name
    private void showNamePhoneUpdate(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update " + key);

        // creating a layout to write the new name
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        final EditText editText = new EditText(this);
        editText.setHint("Enter " + key);
        layout.addView(editText);
        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            final String value = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(value)) {
                progressDialog.show();

                // Here we are updating the new name
                HashMap<String, Object> result = new HashMap<>();
                result.put(key, value);
                databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();

                    // after updated we will show updated
                    Toast.makeText(EditProfilePageActivity.this, "Updated name", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfilePageActivity.this, "Unable to update", Toast.LENGTH_LONG).show();
                });

                if (key.equals("name")) {
                    final DatabaseReference databaser = FirebaseDatabase.getInstance().getReference("Posts");
                    Query query = databaser.orderByChild("uId").equalTo(uId);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String child = databaser.getKey();
                                dataSnapshot1.getRef().child("uname").setValue(value);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                Toast.makeText(EditProfilePageActivity.this, "Unable to update", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> progressDialog.dismiss());
        builder.create().show();
    }

    // Here we are showing image pic dialog where we will select and image either from camera or gallery
    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            // if access is not given then we will request for permission
            requestCameraPermission();
            requestStoragePermission();
            if (which == 0) {
                pickFromCamera();
            } else if (which == 1) {
                pickFromGallery();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageuri = data.getData();
                uploadProfileCoverPhoto(imageuri);
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
                uploadProfileCoverPhoto(imageuri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will click a photo and then go to start activity for result for updating data
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        imageuri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent camerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        galleryActivityResultLauncher.launch(camerIntent);
    }

    // We will select an image from gallery
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryActivityResultLauncher.launch(galleryIntent);
    }

    // We will upload the image from here.
    private void uploadProfileCoverPhoto(final Uri uri) {
        progressDialog.show();

        // We are taking the filepath as storagepath + firebaseauth.getUid()+".png"
        String filepathname = storagePath + "" + profileOrCoverPhoto + "_" + firebaseUser.getUid();
        StorageReference storageReference1 = storageReference.child(filepathname);
        storageReference1.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;

            // We will get the url of our image using uritask
            final Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()) {

                // updating our image url into the realtime database
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(profileOrCoverPhoto, downloadUri.toString());
                databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfilePageActivity.this, "Updated", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfilePageActivity.this, "Error Updating, permission denied ", Toast.LENGTH_LONG).show();
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(EditProfilePageActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(EditProfilePageActivity.this, "Error", Toast.LENGTH_LONG).show();
        });
    }
}
