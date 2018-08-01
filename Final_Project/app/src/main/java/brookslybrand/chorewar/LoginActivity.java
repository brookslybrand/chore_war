package brookslybrand.chorewar;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.database.Cursor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // declare_auth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Starting Login Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // email login button event
        findViewById(R.id.email_login_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailPasswordActivity = new Intent(LoginActivity.this, EmailPasswordActivity.class);
                startActivity(emailPasswordActivity);
            }
        });

        findViewById(R.id.google_login_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailPasswordActivity = new Intent(LoginActivity.this, EmailPasswordActivity.class);
                startActivity(emailPasswordActivity);
            }
        });

        findViewById(R.id.facebook_login_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailPasswordActivity = new Intent(LoginActivity.this, EmailPasswordActivity.class);
                startActivity(emailPasswordActivity);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(homeActivity);
        }
    }
}