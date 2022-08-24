package com.example.myapplicationandoroid.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplicationandoroid.R;
import com.example.myapplicationandoroid.chat.ChatActivity;
import com.example.myapplicationandoroid.users.UsersProfilActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {


    String ptime, myuid, myname, myemail, mydp, uimage, postId, plike, hisdp, hisname;
    ImageView pictureIV, imageIV;
    TextView nameTxt, timeTxt, titleTxt, descriptionTxt, likeTxt, commentTxt;
    ImageButton moreIBtn;
    Button likeBtn, shareBtn;
    LinearLayout profileLyt;
    EditText commentExt;
    ImageButton sendIBtn;
    RecyclerView recyclerView;
    List<ModelComment> commentList;
    AdapterComment adapterComment;
    ImageView commenterIV;
    boolean mlike = false;
    ActionBar actionBar;
    ProgressDialog progressDialog;
    boolean count = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Post Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        postId = getIntent().getStringExtra("pid");
        recyclerView = findViewById(R.id.recyclecomment);
        pictureIV = findViewById(R.id.pictureco);
        imageIV = findViewById(R.id.pimageIV);
        nameTxt = findViewById(R.id.pNameTxt);
        timeTxt = findViewById(R.id.pTimeTxt);
        moreIBtn = findViewById(R.id.moreBtn);
        titleTxt = findViewById(R.id.pTitleTxt);
        myemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        descriptionTxt = findViewById(R.id.pdescriptionTxt);
        commentTxt = findViewById(R.id.pcommentTxt);
        likeTxt = findViewById(R.id.plikeTxt);
        likeBtn = findViewById(R.id.likeBtn);
        commentExt = findViewById(R.id.typecommetTxt);
        sendIBtn = findViewById(R.id.sendcommentBtn);
        commenterIV = findViewById(R.id.commenterIV);
        shareBtn = findViewById(R.id.shareBtn);
        profileLyt = findViewById(R.id.profileLyt);
        progressDialog = new ProgressDialog(this);

        loadPostInfo();
        loadUserInfo();
        setLikes();
//        actionBar.setSubtitle("SignedInAs:" + myemail);
        loadComments();
        sendIBtn.setOnClickListener(v -> postComment());
        likeBtn.setOnClickListener(v -> likePost());
        likeTxt.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailsActivity.this, PostLikedByActivity.class);
            intent.putExtra("pid", postId);
            startActivity(intent);
        });
        //redirect tu users profile activity
        pictureIV.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailsActivity.this, UsersProfilActivity.class);

            // putting uid of user in extras
            intent.putExtra("uid", myuid);
            PostDetailsActivity.this.startActivity(intent);

        });

    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelComment modelComment = dataSnapshot1.getValue(ModelComment.class);
                    commentList.add(modelComment);
                    adapterComment = new AdapterComment(getApplicationContext(), commentList, myuid, postId);
                    recyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikes() {
        final DatabaseReference liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postId).hasChild(myuid)) {
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hearted, 0, 0, 0);
                    likeBtn.setText("Liked");
                } else {
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart, 0, 0, 0);
                    likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        mlike = true;
        final DatabaseReference liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mlike) {
                    if (dataSnapshot.child(postId).hasChild(myuid)) {
                        postref.child(postId).child("plike").setValue("" + (Integer.parseInt(plike) - 1));
                        liekeref.child(postId).child(myuid).removeValue();
                        mlike = false;

                    } else {
                        postref.child(postId).child("plike").setValue("" + (Integer.parseInt(plike) + 1));
                        liekeref.child(postId).child(myuid).setValue("Liked");
                        mlike = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        progressDialog.setMessage("Adding Comment");

        final String commentss = commentExt.getText().toString().trim();
        if (TextUtils.isEmpty(commentss)) {
            Toast.makeText(PostDetailsActivity.this, "Empty comment", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.show();
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference datarf = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cId", timestamp);
        hashMap.put("comment", commentss);
        hashMap.put("ptime", timestamp);
        hashMap.put("uid", myuid);
        hashMap.put("uemail", myemail);
        hashMap.put("udp", mydp);
        hashMap.put("uname", myname);
        datarf.child(timestamp).setValue(hashMap).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            Toast.makeText(PostDetailsActivity.this, "Added", Toast.LENGTH_LONG).show();
            commentTxt.setText("");
            updateCommetCount();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(PostDetailsActivity.this, "Failed", Toast.LENGTH_LONG).show();
        });
    }

    private void updateCommetCount() {
        count = true;
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (count) {
                    String comments = "" + dataSnapshot.child("pcomments").getValue();
                    int newcomment = Integer.parseInt(comments) + 1;
                    reference.child("pcomments").setValue("" + newcomment);
                    count = false;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {

        Query myref = FirebaseDatabase.getInstance().getReference("Users");
        myref.orderByChild("uid").equalTo(myuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    myname = dataSnapshot1.child("name").getValue().toString();
                    mydp = dataSnapshot1.child("image").getValue().toString();
                    try {
                        Glide.with(PostDetailsActivity.this).load(mydp).into(commenterIV);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadPostInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference.orderByChild("ptime").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String ptitle = dataSnapshot1.child("title").getValue().toString();
                    String descriptions = dataSnapshot1.child("description").getValue().toString();
                    uimage = dataSnapshot1.child("uimage").getValue().toString();
                    hisdp = dataSnapshot1.child("udp").getValue().toString();
                    // hisuid = dataSnapshot1.child("uid").getValue().toString();
                    String uemail = dataSnapshot1.child("uemail").getValue().toString();
                    hisname = dataSnapshot1.child("uname").getValue().toString();
                    ptime = dataSnapshot1.child("ptime").getValue().toString();
                    plike = dataSnapshot1.child("plike").getValue().toString();
                    String commentcount = dataSnapshot1.child("pcomments").getValue().toString();
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(ptime));
                    String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                    nameTxt.setText(hisname);
                    titleTxt.setText(ptitle);
                    descriptionTxt.setText(descriptions);
                    likeTxt.setText(plike + " Likes");
                    timeTxt.setText(timedate);
                    commentTxt.setText(commentcount + " Comments");
                    if (uimage.equals("noImage")) {
                        imageIV.setVisibility(View.GONE);
                    } else {
                        imageIV.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(PostDetailsActivity.this).load(uimage).into(imageIV);
                        } catch (Exception e) {

                        }
                    }
                    try {
                        Glide.with(PostDetailsActivity.this).load(hisdp).into(pictureIV);
                    } catch (Exception e) {

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
