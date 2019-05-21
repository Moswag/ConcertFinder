package cytex.co.zw.concertfinder;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytex.co.zw.concertfinder.adapter.CommentAdapter;
import cytex.co.zw.concertfinder.adapter.ViewConcertsAdapter;
import cytex.co.zw.concertfinder.models.Comment;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.utils.Constants;
import cytex.co.zw.concertfinder.utils.MessageToast;

public class ChatActivity extends AppCompatActivity {

    LinearLayout mContainer;
    EditText mEditText;
    FloatingActionButton send;

    private List<Comment> lstComments;
    private List<Comment> filteredComments;
    private RecyclerView recyclerView;

    DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private static String TAG="ChatActivity";


    String email;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        filteredComments=new ArrayList<>();







        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            email=auth.getCurrentUser().getEmail();
        }
        else{
            email="private";
        }


        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TABLE_COMMENT);

        // Request option for glide
        RequestOptions requestOptions=new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);

        final  String concert_id=getIntent().getExtras().getString("concert_id");
        String category=getIntent().getExtras().getString("category");
        final String description=getIntent().getExtras().getString("description");
        String image_url=getIntent().getExtras().getString("image_url");
        String location=getIntent().getExtras().getString("location");
        String date=getIntent().getExtras().getString("date");





        ImageView img=(ImageView)findViewById(R.id.thumbnail);
        TextView concert_category=(TextView)findViewById(R.id.category);
        TextView description_content=(TextView)findViewById(R.id.post_desc);
        TextView concert_location=(TextView)findViewById(R.id.location);
        TextView concert_date=(TextView)findViewById(R.id.post_time);

        //fro previous fragment
        concert_category.setText(category);
        description_content.setText(description);
        concert_location.setText(location);
        concert_date.setText(date);
        //glide

       // Toast.makeText(getApplicationContext(),image_url,Toast.LENGTH_LONG).show();

        Glide.with(this).load(image_url).into(img);

        lstComments=new ArrayList<>();


        //lstComments=dbHelper.getPostsComments(post_id);

        mContainer = (LinearLayout) findViewById(R.id.container);
        mEditText = (EditText) findViewById(R.id.whatsapp_edit_view);
        recyclerView=(RecyclerView)findViewById(R.id.comment_recycler);
        send=(FloatingActionButton) findViewById(R.id.send_button);
        //  mEditText.setOnEditorActionListener(new DoneOnEditorActionListener());





        // jsonrequest("",post_id);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.clearFocus();
                if(mEditText.getText().toString()!=""){
                    lstComments.clear();
                    Comment comment=new Comment();
                    comment.setComment(mEditText.getText().toString());
                    comment.setEmail(email);
                    comment.setConcertId(concert_id);
                    comment.setConcert(description);
                    comment.setTime(String.valueOf(new Date()));
                    //it will create a unique id and we will use it as the Primary Key for our User
                    String id = databaseReference.push().getKey();
                    comment.setId(id);
                    databaseReference.child(id).setValue(comment);

                    //jsonrequest(mEditText.getText().toString(),post_id);
                    mEditText.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please fill in the the comment to send",Toast.LENGTH_LONG).show();
                }

            }
        });



        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Comment comment = postSnapshot.getValue(Comment.class);

                    lstComments.add(comment);
                }

                for(Comment cmd:lstComments){
                    if(cmd.getConcertId().equals(concert_id))
                    filteredComments.add(cmd);
                }


                Log.d(TAG,filteredComments.toString());
                setuprecyclerview(filteredComments);


                // Hiding the progress dialog.

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
               // progressDialog.dismiss();

            }
        });
    }




    private void setuprecyclerview(List<Comment> lstComment) {
        CommentAdapter myAdapter=new CommentAdapter(lstComment, getApplicationContext(),email);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
       // Toast.makeText(getApplicationContext(),"Onresume executed",Toast.LENGTH_LONG).show();
    }

}

