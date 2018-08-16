package brookslybrand.chorewar;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class NewWarActivity extends AppCompatActivity {

    private static final String TAG = "NewWarActivity";

    private EditText mWarNameField;
    private EditText mRewardField;
    private EditText mConsequenceField;

    private PopupWindow pw;
    private boolean expanded;        //to  store information whether the selected values are displayed completely or in shortened representatn
    public static boolean[] checkSelectedWarriors;    // store select/unselect information about the values in the list
    public static boolean[] checkSelectedChores;    // store select/unselect information about the values in the list
    public static ArrayList<String> mChores;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_war);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Views
        mWarNameField = findViewById(R.id.war_name);
        mRewardField = findViewById(R.id.reward);
        mConsequenceField = findViewById(R.id.consequence);

        // Once the user is done creating the war, log the data and return to the home activity
        Button startWarButton = (Button) findViewById(R.id.start_war);
        startWarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWar();
            }
        });
    }

    /* Initialize the data in the dropdown menues */
    @Override
    public void onStart() {
        super.onStart();

        initializeWarriors();
        initializeChores();

        // set the selected values to be empty strings;
        DropDownListAdapterWarriors.resetSelectedCount();
        DropDownListAdapterChores.resetSelectedCount();
    }

    /*
     * Function to set up initial warriors: queries the list of warriors and populates a dropdown menu
     * */
    private void initializeWarriors() {

        Query usersQuery = mDatabase.child("users");
        String userID = mAuth.getUid();

        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //data source for drop-down list
                final ArrayList<String> items = getFriends((Map<String, Object>) dataSnapshot.getValue());

                checkSelectedWarriors = new boolean[items.size()];
                //initialize all values of list to 'unselected' initially
                for (int i = 0; i < checkSelectedWarriors.length; i++) {
                    checkSelectedWarriors[i] = false;
                }

                /*SelectBox is the TextView where the selected values will be displayed in the form of "Item 1 & 'n' more".
                 * When this selectBox is clicked it will display all the selected values
                 * and when clicked again it will display in shortened representation as before.
                 * */
                final TextView tv = (TextView) findViewById(R.id.selectBoxWarriors);
                tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (!expanded) {
                            //display all selected values
                            String selected = "";
                            int flag = 0;
                            for (int i = 0; i < items.size(); i++) {
                                if (checkSelectedWarriors[i] == true) {
                                    selected += items.get(i);
                                    selected += ", ";
                                    flag = 1;
                                }
                            }
                            if (flag == 1)
                                tv.setText(selected);
                            expanded = true;
                        } else {
                            //display shortened representation of selected values
                            tv.setText(DropDownListAdapterWarriors.getSelected());
                            expanded = false;
                        }
                    }
                });

                //onClickListener to initiate the dropDown list
                Button createButton = (Button) findViewById(R.id.openDropdownWarriors);
                createButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (items.size() == 0) {
                            Context context = getApplicationContext();
                            String text = "You don't have any friends";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            initiatePopUpWarriors(items, tv);
                        }

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*
     * Function to set up initial chores: queries the list of chores and populates a dropdown menu
     * */
    private void initializeChores() {
        // TODO: populate with data from the database

        //data source for drop-down list
        mChores = new ArrayList<>();

        mChores.add("Wash Dishes");
        mChores.add("Mow Lawn");
        mChores.add("Clean Bathroom");
        mChores.add("Mop and Vacuum");
        mChores.add("Dust");

        checkSelectedChores = new boolean[mChores.size()];
        //initialize all values of list to 'unselected' initially
        for (int i = 0; i < checkSelectedChores.length; i++) {
            checkSelectedChores[i] = false;
        }

        /*SelectBox is the TextView where the selected values will be displayed in the form of "Item 1 & 'n' more".
         * When this selectBox is clicked it will display all the selected values
         * and when clicked again it will display in shortened representation as before.
         * */
        final TextView tv = (TextView) findViewById(R.id.selectBoxChores);
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!expanded) {
                    //display all selected values
                    String selected = "";
                    int flag = 0;
                    for (int i = 0; i < mChores.size(); i++) {
                        if (checkSelectedChores[i] == true) {
                            selected += mChores.get(i);
                            selected += ", ";
                            flag = 1;
                        }
                    }
                    if (flag == 1)
                        tv.setText(selected);
                    expanded = true;
                } else {
                    //display shortened representation of selected values
                    tv.setText(DropDownListAdapterChores.getSelected());
                    expanded = false;
                }
            }
        });

        //onClickListener to initiate the dropDown list
        Button createButton = (Button) findViewById(R.id.openDropdownChores);
        createButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                initiatePopUpChores(mChores, tv);
            }
        });
    }

    /*
     * Function to set up the pop-up window which acts warriors drop-down list
     * */
    private void initiatePopUpWarriors(ArrayList<String> items, TextView tv) {
        LayoutInflater inflater = (LayoutInflater) NewWarActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get the pop-up window i.e.  drop-down layout
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.pop_up_window, (ViewGroup) findViewById(R.id.popUpView));

        //get the view to which drop-down layout is to be anchored
        RelativeLayout warriorsLayout = findViewById(R.id.selectWarriorsLayout);
        pw = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);

        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        pw.setOutsideTouchable(true);
        pw.setHeight(LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        pw.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        //provide the source layout for drop-down
        pw.setContentView(layout);

        //anchor the drop-down to bottom-left corner of 'warriorsLayout'
        pw.showAsDropDown(warriorsLayout);

        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.dropDownList);
        DropDownListAdapterWarriors adapter = new DropDownListAdapterWarriors(this, items, tv);
        list.setAdapter((ListAdapter) adapter);
    }

    /*
     * Function to set up the pop-up window which acts chores drop-down list
     * */
    private void initiatePopUpChores(ArrayList<String> items, TextView tv) {
        LayoutInflater inflater = (LayoutInflater) NewWarActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get the pop-up window i.e.  drop-down layout
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.pop_up_window, (ViewGroup) findViewById(R.id.popUpView));

        //get the view to which drop-down layout is to be anchored
        RelativeLayout choresLayout = findViewById(R.id.selectChoresLayout);
        pw = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);

        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        pw.setOutsideTouchable(true);
        pw.setHeight(LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        pw.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        //provide the source layout for drop-down
        pw.setContentView(layout);

        //anchor the drop-down to bottom-left corner of 'warriorsLayout'
        pw.showAsDropDown(choresLayout);

        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.dropDownList);
        DropDownListAdapterChores adapter = new DropDownListAdapterChores(this, items, tv);
        list.setAdapter((ListAdapter) adapter);
    }

    /* Get all of the users friend's emails */
    private ArrayList<String> getFriends(Map<String, Object> users) {

        ArrayList<String> friendsList = new ArrayList<String>();

        String userID = mAuth.getUid();
        Map user = (Map) users.get(userID);

        // check if already friends
        if (user.containsKey("friends")) {
            Map friends = (Map) user.get("friends");

            String[] result = (String[]) friends.values().toArray(new String[0]);
            friendsList = new ArrayList(Arrays.asList(result));

            return friendsList;
        }

        return friendsList;

    }

    private void createWar() {
        if (!validateForm()) {
            return;
        }

        Context context = getApplicationContext();
        String war_name = mWarNameField.getText().toString();
        String text = war_name + " has begun!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        // add all the chores to the users
        FirebaseUser user = mAuth.getCurrentUser();
        ArrayList<String> selectedChores = new ArrayList();
        for (int i = 0; i < checkSelectedChores.length; i++) {
            // get all checked chores
            if (checkSelectedChores[i]) {
                selectedChores.add(mChores.get(i));
            }
        }

        // TODO: Currently only assigns a chore to the person who set the war... needs to assign to all wariors
        String randomChore = selectedChores.get(new Random().nextInt(selectedChores.size()));
        writeNewChore(user.getUid(), randomChore);

        Intent homeActivity = new Intent(NewWarActivity.this, HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String war_name = mWarNameField.getText().toString();
        if (TextUtils.isEmpty(war_name)) {
            mWarNameField.setError("Required.");
            valid = false;
        } else {
            mWarNameField.setError(null);
        }

        String reward = mRewardField.getText().toString();
        if (TextUtils.isEmpty(reward)) {
            mRewardField.setError("Required.");
            valid = false;
        } else {
            mRewardField.setError(null);
        }

        String consequence = mConsequenceField.getText().toString();
        if (TextUtils.isEmpty(consequence)) {
            mConsequenceField.setError("Required.");
            valid = false;
        } else {
            mConsequenceField.setError(null);
        }

        // if valid up to this point, check the dropdown menus
        if (valid) {

            // get the counts of warriors and chores selected
            // the user counts as a warrior
            int warriorsCount = 1;
            for (int i = 0; i < checkSelectedWarriors.length; i++) if (checkSelectedWarriors[i]) warriorsCount++;
            int choresCount = 0;
            for (int i = 0; i < checkSelectedChores.length; i++) if (checkSelectedChores[i]) choresCount++;

            // if there are no selected warriors
            if (warriorsCount <= 1) {
                Context context = getApplicationContext();
                String text = "You must select at least one other warrior";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                valid = false;
            }  else if (choresCount != warriorsCount) {
            // if there are no selected chores
                Context context = getApplicationContext();
                String text = "The number of chores must match the number of warriors (including yourself)";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                valid = false;
            }
        }

        return valid;
    }

    // add the current chore to the users list of chore
    private void writeNewChore(String userId, String chore) {
        mDatabase.child("users").child(userId).child("currentChores").push().setValue(chore);
    }
}

