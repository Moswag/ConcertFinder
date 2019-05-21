package cytex.co.zw.concertfinder.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

import cytex.co.zw.concertfinder.ChatActivity;
import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.maps.ConcertLocation;
import cytex.co.zw.concertfinder.models.Comment;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.models.Like;
import cytex.co.zw.concertfinder.utils.Constants;
import cytex.co.zw.concertfinder.utils.GPSTracker;
import cytex.co.zw.concertfinder.utils.MessageToast;
import cytex.co.zw.concertfinder.utils.SHA1;


public class ViewConcertsAdapter extends RecyclerView.Adapter<ViewConcertsAdapter.ViewHolder> {

    Context context;
    List<Concert> concertsList;
    List<Comment> commentsList;
    List<Like> likeList;
    DatabaseReference databaseReference;
    private FirebaseAuth auth;
    String email;

    private  static final  int REQUEST_CODE_PERMISSION=2;
    String mPermission= Manifest.permission.ACCESS_FINE_LOCATION;

    GPSTracker gps;

    private static final String TAG="ViewConcertsAdapter";



    public ViewConcertsAdapter(Context context, List<Concert> concertsList, List<Comment> commentsList,List<Like> likeList) {

        this.concertsList = concertsList;
        this.commentsList=commentsList;
        this.likeList=likeList;
        this.context = context;

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            email=auth.getCurrentUser().getEmail();
        }
        else{
            email="private";
        }


        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TABLE_LIKES);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);


        ViewHolder viewHolder = new ViewHolder(view);

        gps=new GPSTracker(context);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Concert concert = concertsList.get(position);
        int i=0;
        for(Comment cm:commentsList){
            if(cm.getConcertId().equals(concert.getId())){
                i++;
            }
        }


        int j=0;
        for(Like lk:likeList){
            if(lk.getConcertId().equals(concert.getId())){
                j++;
            }
        }

        holder.category.setText(concert.getCategory());
        holder.description.setText(concert.getDescription());
        holder.location.setText("Location: "+concert.getLocatonName());
        holder.date.setText("Date: "+concert.getDate());
        holder.time.setText("Time: "+ concert.getStartTime());
        holder.button_comment.setText(String.valueOf(i)+" Comments");
        holder.button_like.setText(String.valueOf(j)+" Likes");


        if(concert.getHasMap()){
            holder.view_location.setVisibility(View.VISIBLE);
            holder.loc.setVisibility(View.VISIBLE);
        }



        //Loading image from Glide library.
        Glide.with(context).load(concert.getPictureUrl()).into(holder.imageView);


        holder.view_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ConcertLocation.class);
                intent.putExtra("latitude",String.valueOf(concert.getLatitude()));
                intent.putExtra("longitude",String.valueOf(concert.getLongitude()));
                intent.putExtra("description",concert.getDescription());
                context.startActivity(intent);
            }
        });

        holder.button_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(concert.getHasMap()){
                    if (gps.canGetLocation()) {
                        float distance=getDis(concert.getLatitude(),concert.getLongitude(),gps.getLatitude(),gps.getLongitude());
                        Log.d(TAG,"The distance is "+distance);
                        if(distance<1000){
                            Intent intent=new Intent(context,ChatActivity.class);
                            intent.putExtra("concert_id",concert.getId());
                            intent.putExtra("category",concert.getCategory());
                            intent.putExtra("description",concert.getDescription());
                            intent.putExtra("image_url",concert.getPictureUrl());
                            intent.putExtra("location",concert.getLocatonName());
                            intent.putExtra("date",concert.getDate()+" "+concert.getStartTime());
                            context.startActivity(intent);
                        }
                        else{
                            Log.d(TAG,"The distance is longer with "+distance);
                            MessageToast.show(context,"You need to be 1KM within the Party zone inorder for you to chat");
                        }
                    }
                    else{
                        gps.showSettingsAlert();
                    }
                }
                else{
                    Intent intent=new Intent(context,ChatActivity.class);
                    intent.putExtra("concert_id",concert.getId());
                    intent.putExtra("category",concert.getCategory());
                    intent.putExtra("description",concert.getDescription());
                    intent.putExtra("image_url",concert.getPictureUrl());
                    intent.putExtra("location",concert.getLocatonName());
                    intent.putExtra("date",concert.getDate()+" "+concert.getStartTime());
                    context.startActivity(intent);
                }

            }
        });


        holder.button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Like like=new Like();
                like.setEmail(email);
                like.setConcertId(concert.getId());
                like.setConcert(concert.getDescription());
                like.setHost(concert.getAdderEmail());
                //it will create a unique id and we will use it as the Primary Key for our User
                String id = databaseReference.push().getKey();
                like.setId(SHA1.sha(email));
                databaseReference.child(SHA1.sha(email)).setValue(like);
            }
        });
    }

    @Override
    public int getItemCount() {

        return concertsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView category,description,location,date,time;
        public AppCompatTextView view_location,button_comment,button_like;
        public  ImageView loc;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);

            category=itemView.findViewById(R.id.category);
            description =  itemView.findViewById(R.id.description);
            location=itemView.findViewById(R.id.location);
            date=itemView.findViewById(R.id.date);
            time=itemView.findViewById(R.id.time);
            view_location=itemView.findViewById(R.id.view_location);
            loc=itemView.findViewById(R.id.loc);
            button_comment=itemView.findViewById(R.id.button_comment);
            button_like=itemView.findViewById(R.id.button_like);
        }
    }


    private float getDis(double lat1, double lon1, double lat2, double lon2){
        float[] results = new float[1];
        Location.distanceBetween(
                lat1,lon1,
                lat2,lon2, results);

        Float dis=results[0];
        String web=String.valueOf(dis);
        Toast.makeText(context,web+" M to the place",Toast.LENGTH_LONG).show();
        return dis;


    }
}
