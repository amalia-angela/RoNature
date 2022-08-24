package com.example.myapplicationandoroid.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.myapplicationandoroid.R;
import com.example.myapplicationandoroid.users.UsersProfilActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {


    Context context;
    String myuid;
    boolean mprocesslike = false;
    List<ModelPost> modelPosts;
    private DatabaseReference liekeref, postref;

    public AdapterPosts(Context context, List<ModelPost> modelPosts) {
        this.context = context;
        this.modelPosts = modelPosts;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
//        position= holder.getAbsoluteAdapterPosition();
        final String uid = modelPosts.get(position).getUid();
        String nameh = modelPosts.get(position).getUname();
        final String titlee = modelPosts.get(position).getTitle();
        final String descri = modelPosts.get(position).getDescription();
        final String ptime = modelPosts.get(position).getPtime();
        String dp = modelPosts.get(position).getUdp();
        String plike = modelPosts.get(position).getPlike();
        final String image = modelPosts.get(position).getUimage();
        String email = modelPosts.get(position).getUemail();
        String comm = modelPosts.get(position).getPcomments();
        final String pid = modelPosts.get(position).getPtime();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(ptime));
        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.name.setText(nameh);
        holder.title.setText(titlee);
        holder.description.setText(descri);
        holder.time.setText(timedate);
        holder.like.setText(plike + " Likes");
        holder.comments.setText(comm + " Comments");
        setLikes(holder, ptime);
        try {
            Glide.with(context).load(dp).into(holder.picture);
        } catch (Exception e) {

        }
       //go to users profil activity
        holder.picture.setOnClickListener(v -> {
            Intent intent = new Intent(context, UsersProfilActivity.class);

            // putting uid of user in extras
            intent.putExtra("uid", uid);
            context.startActivity(intent);
        });

        holder.image.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(image).into(holder.image);
        } catch (Exception e) {

        }
        holder.like.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PostLikedByActivity.class);
            intent.putExtra("pid", pid);
            holder.itemView.getContext().startActivity(intent);
        });

        holder.likebtn.setOnClickListener(v -> {
            final int plike1 = Integer.parseInt(modelPosts.get(position).getPlike());
            mprocesslike = true;
            final String postid = modelPosts.get(position).getPtime();
            liekeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (mprocesslike) {
                        if (dataSnapshot.child(postid).hasChild(myuid)) {
                            postref.child(postid).child("plike").setValue("" + (plike1 - 1));
                            liekeref.child(postid).child(myuid).removeValue();
                            mprocesslike = false;
                        } else {
                            postref.child(postid).child("plike").setValue("" + (plike1 + 1));
                            liekeref.child(postid).child(myuid).setValue("Liked");
                            mprocesslike = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
        holder.more.setOnClickListener(v -> showMoreOptions(holder.more, uid, myuid, ptime, image));
        holder.comment.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("pid", ptime);
            context.startActivity(intent);
        });
    }

    private void showMoreOptions(ImageButton more, String uid, String myuid, final String pid, final String image) {

        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);
        if (uid.equals(myuid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "DELETE");
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "EDIT");
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0) {
                deltewithImage(pid, image);
            }
//            if(item.getItemId()==1){
//                editImage(pid,image);
//            }

            return false;
        });
        popupMenu.show();
    }

    private void deltewithImage(final String pid, String image) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting");
        StorageReference picref = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        picref.delete().addOnSuccessListener(aVoid -> {
            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("ptime").equalTo(pid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        dataSnapshot1.getRef().removeValue();
                    }

                    pd.dismiss();
                    Toast.makeText(context, "Deleted Sucessfully", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

    private void setLikes(final MyHolder holder, final String pid) {
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(pid).hasChild(myuid)) {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hearted, 0, 0, 0);
                    holder.likebtn.setText("Liked");
                } else {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart, 0, 0, 0);
                    holder.likebtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView picture, image;
        TextView name, time, title, description, like, comments;
        ImageButton more;
        Button likebtn, comment;
        LinearLayout profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.accountIV);
            image = itemView.findViewById(R.id.pictureIV);
            name = itemView.findViewById(R.id.accountNameTxt);
            time = itemView.findViewById(R.id.timeTxt);
            more = itemView.findViewById(R.id.moreBtn);
            title = itemView.findViewById(R.id.locationTxt);
            description = itemView.findViewById(R.id.descripTxt);
            like = itemView.findViewById(R.id.countLikeTxt);
            comments = itemView.findViewById(R.id.countCommentTxt);
            likebtn = itemView.findViewById(R.id.likeBtn);
            comment = itemView.findViewById(R.id.commentBtn);
            profile = itemView.findViewById(R.id.profileLyt);
        }
    }
}
