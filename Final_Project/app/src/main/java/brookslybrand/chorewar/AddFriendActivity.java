package brookslybrand.chorewar;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "AddFriendActivity";
    private static final String REQUIRED = "Required";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBodyField = findViewById(R.id.field_body);
        mSubmitButton = findViewById(R.id.fab_submit_post);
    }

    @Override
    public void onStart() {
        super.onStart();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query usersQuery = mDatabase.child("users");

                usersQuery.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Attempts to add the friend if they exist
                                attemptAddFriend((Map<String,Object>) dataSnapshot.getValue());

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
            }
        });
    }

    /* Check if the user's email exists in the database */
    private void attemptAddFriend(Map<String,Object> users) {
        // get the email of the friend the user is searching for
        String friendEmail = mBodyField.getText().toString();
        String currentUsersEmail = mAuth.getCurrentUser().getEmail();

        String userID = mAuth.getUid();
        Map friends = (Map) users.get(userID);

        // check if already friends
        if(friends.containsKey("friends")) {
            friends = (Map) friends.get("friends");
            if(friends.values().contains(friendEmail)) {
                resultToast("You are already friends with " + friendEmail);
                return;
            }
        }

        if (friendEmail.equals(currentUsersEmail)) {
            resultToast("You can't be friends with yourself...");
            return;
        }

        if (!friendEmail.isEmpty()) {
            for (Map.Entry<String, Object> user : users.entrySet()) {
                //Get user map
                Map singleUser = (Map) user.getValue();
                //Get phone field and append to list
                if (friendEmail.equals(singleUser.get("email"))) {
                    String userId = mAuth.getCurrentUser().getUid();
                    // add new friend to current user's list of friends
                    mDatabase.child("users").child(userId).child("friends")
                            .push().setValue(friendEmail);

                    // add current user to friend's list of friends
                    mDatabase.child("users").child(user.getKey()).child("friends")
                            .push().setValue(currentUsersEmail);

                    resultToast("Added " + friendEmail + " to your friends.");

                    // the friend was added successfully, so finish the activity
                    finish();
                    return;
                }
            }

            // if no user was found
            resultToast(getString(R.string.user_not_found));
        } else {
            resultToast("Please type in an email");
        }
    }

    private void resultToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
