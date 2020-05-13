package com.example.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.myplaces.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    boolean logedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EditMyPlaceActivity.class);
                startActivityForResult(i, NEW_PLACE);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnCreate = (Button) findViewById(R.id.btnCreate);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra("state", LoginActivity.LOG_IN);
                startActivityForResult(i, LoginActivity.LOG_IN);
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra("state", LoginActivity.SING_UP);
                startActivityForResult(i, LoginActivity.SING_UP);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnCreate = (Button) findViewById(R.id.btnCreate);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        TextView info = (TextView) findViewById(R.id.info);
        FloatingActionButton fab = findViewById(R.id.fab);

        if(currentUser == null) {
            btnLogin.setEnabled(true);
            btnLogin.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(true);
            btnCreate.setVisibility(View.VISIBLE);
            btnLogout.setEnabled(false);
            btnLogout.setVisibility(View.INVISIBLE);
            fab.setEnabled(false);
            logedIn = false;
            info.setText("You must LOG IN to use app.");
        } else {
            btnLogin.setEnabled(false);
            btnLogin.setVisibility(View.INVISIBLE);
            btnCreate.setEnabled(false);
            btnCreate.setVisibility(View.INVISIBLE);
            btnLogout.setEnabled(true);
            btnLogout.setVisibility(View.VISIBLE);
            fab.setEnabled(true);
            logedIn = true;
            info.setText("Loged in user: " + currentUser.getEmail());
        }
    }



    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            //mBinding.status.setText(R.string.auth_failed);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    static int NEW_PLACE = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(!logedIn) {
            Toast.makeText(MainActivity.this, "PRIJAVI SE!", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.show_map_item) {
            Intent i = new Intent(this,MyPlacesMapsActivity.class);
            i.putExtra("state", MyPlacesMapsActivity.SHOW_MAP);
            startActivity(i);
        } else if(id == R.id.new_places_item) {
            Intent i = new Intent(this, EditMyPlaceActivity.class);
            startActivityForResult(i, NEW_PLACE);
        } else if(id == R.id.my_places_list_item) {
            Intent i = new Intent(this, MyPlacesList.class);
            startActivity(i);
        } else if(id == R.id.about_item) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Activity.RESULT_OK) {
            ListView myPlacesList = (ListView) findViewById(R.id.my_places_list);
            myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == LoginActivity.LOG_IN) {
            signIn(data.getExtras().getString("email"), data.getExtras().getString("password"));
            Toast.makeText(MainActivity.this, "Uspesno prijavljen.", Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == LoginActivity.SING_UP) {
            createAccount(data.getExtras().getString("email"), data.getExtras().getString("password"));
            Toast.makeText(MainActivity.this, "Uspesno kreiran.", Toast.LENGTH_SHORT).show();
        }
    }
}
