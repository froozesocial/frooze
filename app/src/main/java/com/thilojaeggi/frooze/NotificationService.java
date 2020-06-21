package com.thilojaeggi.frooze;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Adaptor.NotificationAdapter;
import com.thilojaeggi.frooze.Model.Notifications;
import com.thilojaeggi.frooze.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotificationService extends Service {
    private NotificationManager mNotificationManager;
    private NotificationAdapter notificationAdapter;
    private List<Notifications> notificationList;
    String notificationtext;
    String username, commented;
    Context mContext;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        startNotificationListener();
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getApplicationContext(), notificationList);
     //
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void startNotificationListener() {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
            reference.limitToFirst(1);
            reference.addChildEventListener(new ChildEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    notificationList.clear();
                        Notifications notification = dataSnapshot.getValue(Notifications.class);
                        notificationList.add(notification);
                        getUserInfo(notification.getUserid());
                    if (notification.getText().equals("followingyou")){
                        String followingyouxml = getString(R.string.followingyou);
                        notificationtext = followingyouxml;
                    } else if (notification.getText().equals("likedyourpost")){
                        String likedyourpostxml = getString(R.string.likedyourpost);
                        notificationtext = likedyourpostxml;
                    } else {
                        commented = notification.getText().replace("commented:",getString(R.string.commented));
                        notificationtext = commented;
                    }
                     if (username != null && !notification.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        showNotification(getApplicationContext(), username, notificationtext);
                    }

                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
    private void getUserInfo(String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username = user.getUsername();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "All";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

     /*   TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
      //  stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
       mBuilder.setContentIntent(resultPendingIntent);*/
        notificationManager.notify(notificationId, mBuilder.build());
    }
}