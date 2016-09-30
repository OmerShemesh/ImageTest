package com.example.omer.imagetest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.provider.FacebookProvider;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    public static final String CITIES = "cities";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    public static final int RC_SIGN_IN = 0;
    private FirebaseAuth.AuthStateListener authStateListener;

    public static class CityViewHolder extends RecyclerView.ViewHolder{

        public TextView cityName;
        public ImageView cityImage;
        public RelativeLayout cityLayout;

        public CityViewHolder(View itemView) {
            super(itemView);

            cityName = (TextView)itemView.findViewById(R.id.city_name);
            cityImage = (ImageView)itemView.findViewById(R.id.city_image);
            cityLayout = (RelativeLayout)itemView.findViewById(R.id.city_layout);

        }


    }

    private FirebaseRecyclerAdapter<City, CityViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.city_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        ref = FirebaseDatabase.getInstance().getReference();





        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
                    Picasso.with(getApplicationContext())
                            .load(auth.getCurrentUser().getPhotoUrl())
                            .resize(100, 100)
                            .into(profileImage);

                    profileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            auth.signOut();
                        }
                    });
                    TextView profileName = (TextView) findViewById(R.id.profile_name);
                    profileName.setText("Welcome " + auth.getCurrentUser().getDisplayName() + "!");
                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<City, CityViewHolder>(
                            City.class,
                            R.layout.city_template,
                            CityViewHolder.class,
                            ref.child(CITIES)
                    ) {
                        @Override
                        protected void populateViewHolder(CityViewHolder viewHolder, City model, int position) {

                            viewHolder.cityName.setText(model.getName());
                            Picasso.with(getApplicationContext())
                                    .load(model.getUrl())
                                    .fit()
                                    .into(viewHolder.cityImage);
                        }
                    };

                    linearLayoutManager.setStackFromEnd(true);
                    linearLayoutManager.setReverseLayout(true);

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(firebaseRecyclerAdapter);
                } else {
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setProviders(
                            AuthUI.EMAIL_PROVIDER,
                            AuthUI.FACEBOOK_PROVIDER
                    ).setIsSmartLockEnabled(false).build(), RC_SIGN_IN);

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_add_photo:
                Intent i = new Intent(this,AddImageActivity.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);


    }


}
