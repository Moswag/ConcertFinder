package cytex.co.zw.concertfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cytex.co.zw.concertfinder.ChatActivity;
import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.maps.ConcertLocation;
import cytex.co.zw.concertfinder.models.Comment;
import cytex.co.zw.concertfinder.models.Concert;


public class UserConcertsAdapter extends RecyclerView.Adapter<UserConcertsAdapter.ViewHolder> {

    Context context;
    List<Concert> concertsList;
    List<Comment> commentsList;

    public UserConcertsAdapter(Context context, List<Concert> concertsList, List<Comment> commentsList) {

        this.concertsList = concertsList;
        this.commentsList=commentsList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_concerts, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

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

        holder.category.setText(concert.getCategory());
        holder.description.setText(concert.getDescription());
        holder.location.setText("Location: "+concert.getLocatonName());
        holder.date.setText("Date: "+concert.getDate());
        holder.time.setText("Time: "+ concert.getStartTime());
        holder.button_comment.setText(String.valueOf(i)+" Comments");


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
                Intent intent=new Intent(context,ChatActivity.class);
                intent.putExtra("concert_id",concert.getId());
                intent.putExtra("category",concert.getCategory());
                intent.putExtra("description",concert.getDescription());
                intent.putExtra("image_url",concert.getPictureUrl());
                intent.putExtra("location",concert.getLocatonName());
                intent.putExtra("date",concert.getDate()+" "+concert.getStartTime());
                context.startActivity(intent);
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
        public AppCompatTextView view_location,button_comment;
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
        }
    }
}
