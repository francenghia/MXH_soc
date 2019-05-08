package com.example.franc.mxh.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.franc.mxh.Model.Messages;
import com.example.franc.mxh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdpater extends RecyclerView.Adapter<MessageAdpater.ViewHolder>{
    private List<Messages> messagesList;
    private FirebaseAuth auth;
    private DatabaseReference userDatabaseRef;

    public MessageAdpater(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_chat,parent,false);
       auth=FirebaseAuth.getInstance();

       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String messageSendId =auth.getCurrentUser().getUid();
        Messages messages = messagesList.get(position);

        String fromUserId = messages.getFrom();
        String fromMessageType= messages.getType();
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String image = dataSnapshot.child("image").getValue().toString();
                    Picasso.with(holder.circleImageView.getContext()).load(image).placeholder(R.drawable.image).into(holder.circleImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(fromMessageType.equals("text")){
            holder.txtReceiveMessageText.setVisibility(View.INVISIBLE);
            holder.circleImageView.setVisibility(View.INVISIBLE);

            if(fromUserId.equals(messageSendId)){
                holder.txtSendMessageText.setGravity(Gravity.LEFT);
                holder.txtSendMessageText.setText(messages.getMessage());
            }else {
                holder.txtSendMessageText.setVisibility(View.INVISIBLE);
                holder.txtReceiveMessageText.setVisibility(View.VISIBLE);
                holder.circleImageView.setVisibility(View.VISIBLE);

                holder.txtReceiveMessageText.setGravity(Gravity.LEFT);
                holder.txtReceiveMessageText.setText(messages.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSendMessageText , txtReceiveMessageText;
        public CircleImageView circleImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSendMessageText=itemView.findViewById(R.id.sender_message_text);
            txtReceiveMessageText=itemView.findViewById(R.id.reciver_message_text);
            circleImageView = itemView.findViewById(R.id.circleDefaulChat);

        }
    }
}
