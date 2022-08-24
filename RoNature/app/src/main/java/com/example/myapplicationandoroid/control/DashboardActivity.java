package com.example.myapplicationandoroid.control;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplicationandoroid.R;
import com.example.myapplicationandoroid.home.HomeFragment;
import com.example.myapplicationandoroid.chat.ChatListFragment;
import com.example.myapplicationandoroid.posts.AddPhotoFragment;
import com.example.myapplicationandoroid.profil.ProfileFragment;
import com.example.myapplicationandoroid.users.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    ActionBar actionBar;
    private final NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    HomeFragment fragmentHome = new HomeFragment();
                    FragmentTransaction fragmentTransactionHome = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionHome.replace(R.id.content, fragmentHome, "");
                    fragmentTransactionHome.commit();
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment fragmentProfile = new ProfileFragment();
                    FragmentTransaction fragmentTransactionProfile = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionProfile.replace(R.id.content, fragmentProfile);
                    fragmentTransactionProfile.commit();
                    return true;

                case R.id.nav_addphotos:
                    actionBar.setTitle("Add Photos");
                    AddPhotoFragment fragmentAddPhotot = new AddPhotoFragment();
                    FragmentTransaction fragmentTransactionAddPhotot = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionAddPhotot.replace(R.id.content, fragmentAddPhotot, "");
                    fragmentTransactionAddPhotot.commit();
                    return true;

                case R.id.nav_users:
                    actionBar.setTitle("People");
                    UserFragment fragmentUsers = new UserFragment();
                    FragmentTransaction fragmentTransactionUsers = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionUsers.replace(R.id.content, fragmentUsers, "");
                    fragmentTransactionUsers.commit();
                    return true;

//                case R.id.nav_chat:
//                    actionBar.setTitle("Messages");
//                    ChatListFragment listFragment=new ChatListFragment();
//                    FragmentTransaction fragmentTransactionChat=getSupportFragmentManager().beginTransaction();
//                    fragmentTransactionChat.replace(R.id.content,listFragment,"");
//                    fragmentTransactionChat.commit();
//                    return true;

            }
            return false;
        }
    };
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Profile Activity");

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");

        // When we open the application first time  Home the fragment should be shown to the user
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }
}
