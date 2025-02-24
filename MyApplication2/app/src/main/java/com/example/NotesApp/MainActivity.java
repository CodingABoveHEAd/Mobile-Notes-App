package com.example.NotesApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText mloginemail, mloginpassword;
    private RelativeLayout mlogin, mgotosignup;
    private TextView mgotoforgotpassword, mscroll, mwelcome,mjsonparsing;
    ProgressBar mprogressbar;
    Animation mbounce;


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mwelcome = findViewById(R.id.welcome);
        mscroll = findViewById(R.id.scroll);
        mloginemail = findViewById(R.id.loginemail);
        mloginpassword = findViewById(R.id.loginpassword);
        mlogin = findViewById(R.id.login);
        mgotoforgotpassword = findViewById(R.id.gotoforgotpassword);
        mgotosignup = findViewById(R.id.gotosignup);
        firebaseAuth = FirebaseAuth.getInstance();
        mjsonparsing=findViewById(R.id.jsonParseTextView);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        mprogressbar = findViewById(R.id.progressbarofmainactivity);

        mscroll.setSelected(true);

        mbounce = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);
        mwelcome.startAnimation(mbounce);

        mwelcome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mwelcome.startAnimation(mbounce);
            }
        });

        if (firebaseUser != null) {
            finish();
            startActivity(new Intent(MainActivity.this, NOtesActivity.class));
        }
        mjsonparsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),About_the_app.class));
                Toast.makeText(getApplicationContext(),"Showing About The App",Toast.LENGTH_SHORT).show();
            }
        });

        mgotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,signup.class));
            }
        });

        mgotoforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,forgetpassword.class));
            }
        });

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mloginemail.getText().toString().trim();
                String password = mloginpassword.getText().toString().trim();

                if (mail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All Fields Are Required", Toast.LENGTH_SHORT).show();
                } else {
                    mprogressbar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(mail, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        checkmailverification();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Account Doesn't Exist", Toast.LENGTH_SHORT).show();
                                        mprogressbar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void checkmailverification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.isEmailVerified()) {
            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, NOtesActivity.class));
        } else {
            mprogressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Verify your mail first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
