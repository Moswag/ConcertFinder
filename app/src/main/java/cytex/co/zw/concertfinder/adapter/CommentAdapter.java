package cytex.co.zw.concertfinder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.models.Comment;
import cytex.co.zw.concertfinder.utils.Constants;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Holderview> {
    private List<Comment> commentlist;
    private Context context;
    RequestOptions option;
    String email;

    public CommentAdapter(List<Comment> commentlist, Context context,String email) {
        this.commentlist = commentlist;
        this.context = context;
        this.email=email;


    }

    @NonNull
    @Override
    public Holderview onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat,viewGroup,false);

        return new Holderview(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull Holderview holderview, final int position) {

        if(commentlist.get(position).getEmail().equals(email)){
            holderview.v_name.setText(Constants.YOU);
        }
        else{
            holderview.v_name.setText(commentlist.get(position).getEmail());
        }
        holderview.v_content.setText(commentlist.get(position).getComment());
        holderview.v_datetime.setText(commentlist.get(position).getTime());


    }

    @Override
    public int getItemCount() {
        return commentlist.size();
    }

    public void setfilter(List<Comment> listcomment){
        commentlist=new ArrayList<>();
        commentlist.addAll(listcomment);
        notifyDataSetChanged();
    }

    class Holderview extends RecyclerView.ViewHolder{

        TextView v_name;
        TextView v_content;
        TextView v_datetime;

        Holderview(View itemview){
            super(itemview);

            v_name=(TextView) itemview.findViewById(R.id.name);
            v_content=(TextView) itemview.findViewById(R.id.content);
            v_datetime=(TextView) itemview.findViewById(R.id.datetime);
        }

    }
}
