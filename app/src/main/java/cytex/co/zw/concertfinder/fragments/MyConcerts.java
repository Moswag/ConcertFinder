package cytex.co.zw.concertfinder.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.adapter.UserConcertsAdapter;
import cytex.co.zw.concertfinder.adapter.ViewConcertsAdapter;
import cytex.co.zw.concertfinder.models.Comment;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyConcerts extends Fragment {
    DatabaseReference databaseReference,commentsDatabaseReference;

    private FirebaseAuth auth;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<Concert> list;
    List<Concert> filteredConcerts;
    List<Comment> listComments;

    String email;

    public MyConcerts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_my_concerts, container, false);
        getActivity().setTitle("My Concerts");

        list = new ArrayList<>();
        listComments = new ArrayList<>();
        filteredConcerts=new ArrayList<>();


        FirebaseApp.initializeApp(getActivity());

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            email=auth.getCurrentUser().getEmail();
        }
        else{
            email="private";
        }



        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getActivity());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Your Concerts.");

        // Showing progress dialog.
        progressDialog.show();

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.

        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.Database_Path);
        commentsDatabaseReference=FirebaseDatabase.getInstance().getReference(Constants.TABLE_COMMENT);

        // Adding Add Value Event Listener to databaseReference.
        commentsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Comment comment = postSnapshot.getValue(Comment.class);
                    listComments.add(comment);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.


            }
        });




        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Concert concert = postSnapshot.getValue(Concert.class);

                    list.add(concert);
                }

                for(Concert concert:list){
                    if(concert.getAdderEmail().equals(email)){
                        filteredConcerts.add(concert);
                    }
                }

                adapter = new UserConcertsAdapter(getActivity(), filteredConcerts,listComments);

                recyclerView.setAdapter(adapter);

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });


        return v;

    }

}
