package com.example.myapplicationandoroid.home;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplicationandoroid.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyHolder> {

    Context context;
    List<ModelComment> list;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference hisuid;
    String myuid;
    String postid;
    public AdapterComment(Context context, List<ModelComment> list, String myuid, String postid) {
        this.context = context;
        this.list = list;
        this.myuid = myuid;
        this.postid = postid;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String uid = list.get(position).getUid();
        String name = list.get(position).getUname();
        String email = list.get(position).getUemail();
        String image = list.get(position).getUdp();
        final String cid = list.get(position).getcId();
        String comment = list.get(position).getComment();
        String timestamp = list.get(position).getPtime();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));

        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

//        // getting uid of another user using intent
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        hisuid= firebaseDatabase.getReference("Users");
//        Query userquery = hisuid.orderByChild("uid").equalTo(uid);
//
//        //go to users profil activity
//        holder.imagea.setOnClickListener(v -> {
//            Intent intent = new Intent(context, UsersProfilActivity.class);
//
//            // putting uid of user in extras
//            intent.putExtra("uid", hisuid.child("uid"));
//            context.startActivity(intent);
//        });
        holder.name.setText(name);
        holder.time.setText(timedate);
        holder.comment.setText(comment);
        try {
            Glide.with(context).load(image).into(holder.imagea);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView imagea;
        TextView name, comment, time;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imagea = itemView.findViewById(R.id.commenterIV);
            name = itemView.findViewById(R.id.commenterTxt);
            comment = itemView.findViewById(R.id.commentTxt);
            time = itemView.findViewById(R.id.commentTimeTxt);
        }
    }
}
