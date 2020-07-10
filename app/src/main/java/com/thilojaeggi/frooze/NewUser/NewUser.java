package com.thilojaeggi.frooze.NewUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.MainActivity;
import com.thilojaeggi.frooze.R;

import java.util.HashMap;

public class NewUser extends AppCompatActivity {
    ImageView next;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        next = findViewById(R.id.next);
        Fragment fragment = new UsernameFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.newusercontainer, fragment, "1");
        transaction.commit();
        prefs = getSharedPreferences("PREFS", MODE_PRIVATE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFragment().equals("UsernameFragment")) {
                    String username = prefs.getString("username", "none");
                    if (username.length() >8) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toolonguser), Toast.LENGTH_LONG).show();
                    } else {
                        usernamecheck(username);
                    }
                }
                if (getCurrentFragment().equals("NicknameFragment")) {
                    String username = prefs.getString("username", "none");
                    String nickname = prefs.getString("nickname", "none");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userid = user.getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("premium", "false");
                    hashMap.put("id", userid);
                    hashMap.put("username", username);
                    hashMap.put("fullname", nickname);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/frooze-b2248.appspot.com/o/default.jpg?alt=media&token=303e7fbb-6bd4-440f-b3b3-303b761d40da");
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(NewUser.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void usernamecheck(String username) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if(!dataSnapshot.exists()) {
                    Fragment fragment = new NicknameFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.newusercontainer, fragment, "2");
                    transaction.commit();
                    next.setImageResource(R.drawable.mi_ic_finish);
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.usernameexists), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    String getCurrentFragment() {
        Fragment visiblefragment = getSupportFragmentManager()
                .findFragmentById(R.id.newusercontainer);
        String[] currentFragment = visiblefragment.toString().split("\\{");
        return currentFragment[0];
    }
}