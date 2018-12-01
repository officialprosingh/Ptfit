package online.propaans.ptfit.ptfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignActivity extends AppCompatActivity {


       private EditText username_sign_up , password_sign_up ;
        private Button login_signup_button , signup;
        private ProgressBar signup_progress ;

        private FirebaseAuth mAuth;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign);

            mAuth = FirebaseAuth.getInstance();



            username_sign_up = (EditText) findViewById(R.id.username_sign_up);
            password_sign_up = (EditText) findViewById(R.id.password_sign_up);
            signup_progress = (ProgressBar) findViewById(R.id.sign_up_progress);

            login_signup_button = (Button) findViewById(R.id.login_sign_up);
            login_signup_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignActivity.this, LoginActivity.class));
                }
            });

            signup = (Button) findViewById(R.id.sign_up);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = username_sign_up.getText().toString().trim();
                    String password = password_sign_up.getText().toString().trim();



                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        username_sign_up.setError("Please Enter a Valid Email");
                        username_sign_up.requestFocus();
                        return;
                    }

                    if (email.isEmpty()){
                        username_sign_up.setError("Email is Required");
                        username_sign_up.requestFocus();
                        return;
                    }
                    if (password.isEmpty()){
                        password_sign_up.setError("Password is Required");
                        password_sign_up.requestFocus();
                        return;
                    }

                    if (password.length()<6){
                        password_sign_up.setError("Maximum Length of Password Should be 6");
                        password_sign_up.requestFocus();
                        return;
                    }

                    signup_progress.setVisibility(View.VISIBLE);


                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            signup_progress.setVisibility(View.GONE);
                            if (task.isSuccessful()){
                                Intent intent = new Intent(SignActivity.this, WelcomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(getApplicationContext(), "Email is Already Registered ", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                }
            });


        }
    }
