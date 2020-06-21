package com.thilojaeggi.frooze;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Adaptor.CommentAdapter;
import com.thilojaeggi.frooze.Model.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CommentsActivity extends RoundedBottomSheetDialogFragment {
    EditText addcomment;
    ImageButton finish, post, more;
    String postid;
    String publisherid;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    FirebaseUser firebaseUser;
    private static CommentsActivity instance;

    public static CommentsActivity newInstance() {
        return new CommentsActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comments, container,
                false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = prefs.getString("postid", "none");
        publisherid = prefs.getString("publisher", "none");
        finish = view.findViewById(R.id.backbutton);
        addcomment = view.findViewById(R.id.add_comment);
        post = view.findViewById(R.id.postbutton);
        recyclerView = view.findViewById(R.id.comments_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentList);
        recyclerView.setAdapter(commentAdapter);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.empty), Toast.LENGTH_LONG).show();
                } else {
                    addcomment();
                }
            }
        });
        readComments();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return view;
    }

    private void addcomment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        String key = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("key", key);
        hashMap.put("postid", postid);
        reference.child(key).setValue(hashMap);
        sendNotification();
        addcomment.setText("");
    }

    private void sendNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: " + addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        reference.push().setValue(hashMap);
    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);

                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public Dialog getDialog() {
        return super.getDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Integer stageWidth = display.getWidth();
            Integer stageHeight = display.getHeight();
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior bsb = BottomSheetBehavior
                    .from(bottomSheet);

            bsb.setPeekHeight(stageHeight / 2);

        });

        return dialog;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public static CommentsActivity getInstance() {
        return instance;
    }
}
