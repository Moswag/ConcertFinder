package cytex.co.zw.concertfinder.fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.models.ImageUploadInfo;
import cytex.co.zw.concertfinder.utils.Constants;
import cytex.co.zw.concertfinder.utils.MessageToast;
import cytex.co.zw.concertfinder.utils.SHA1;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddConcert extends Fragment {

    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";

    // Root Database Name for Firebase Database.


    // Creating button.
    Button UploadButton, DisplayImageButton;

    // Creating EditText.
    EditText description,locationName;
    Spinner category;

    TextView date, time;

    // Creating ImageView.
    ImageView selectImage;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private FirebaseAuth auth;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog ;

    String email;



    public AddConcert() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_add_concert, container, false);
        getActivity().setTitle("Add Concert");

        FirebaseApp.initializeApp(getActivity());
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.Database_Path);

        //Assign ID'S to button.
        UploadButton = (Button) v.findViewById(R.id.ButtonUploadImage);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            email=auth.getCurrentUser().getEmail();
        }
        else{
            email="private";
        }


        // Assign ID's to EditText.
        category=v.findViewById(R.id.category);
        List<String> list = new ArrayList<String>();
        list.add("Birthday");
        list.add("Graduation Party");
        list.add("Wedding");
        list.add("Ordinary Party");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);


        description = (EditText)v.findViewById(R.id.description);
        locationName=v.findViewById(R.id.location_name);
        date=v.findViewById(R.id.date);
        time=v.findViewById(R.id.time);



        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear, mMonth, mDay, mHour, mMinute;
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear, mHour, mMinute;

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });







        // Assign ID'S to image view.
        selectImage = (ImageView) v.findViewById(R.id.ShowImageView);

        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(getActivity());


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });




        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Concert concert=new Concert();
                String Category=String.valueOf(category.getSelectedItem());
                String Description=description.getText().toString();
                String LocationName=locationName.getText().toString();
                String cDate=date.getText().toString();
                String cTime=time.getText().toString();

                concert.setCategory(Category);
                concert.setDescription(Description);
                concert.setLocatonName(LocationName);
                concert.setDate(cDate);
                concert.setStartTime(cTime);
                concert.setAdderEmail(email);

                // Calling method to upload selected image on Firebase storage.

                if(checkTexts(concert)){
                    //gara wakadaro
                }
                else{
                    uploadConcert(concert);
                }


            }
        });


        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                selectImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.


            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void uploadConcert(final Concert concert) {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Saving Concert...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            final StorageReference storageReference2nd = storageReference.child(
                    Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return storageReference2nd.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                concert.setHasMap(false);
                                concert.setPictureUrl(downloadUri.toString());

                                // Getting Concertid.
                                String concertId = databaseReference.push().getKey();
                                concert.setId(concertId);

                                // Adding image upload id s child element into databaseReference.
                                databaseReference.child(concertId).setValue(concert);


                                clearTexts();

                                // Hiding the progressDialog after done uploading.
                                progressDialog.dismiss();

                                // Showing toast message after done uploading.
                                Toast.makeText(getActivity(), "Concert Successfully Saved", Toast.LENGTH_LONG).show();
                            } else {
                                MessageToast.show(getActivity(), "upload failed: " + task.getException().getMessage());
                                // Hiding the progressDialog after done uploading.
                                progressDialog.dismiss();
                            }
                        }
                    });
        }






        else {

            Toast.makeText(getActivity(), "Please Select Image", Toast.LENGTH_LONG).show();

        }
    }

    private boolean checkTexts(Concert concert){
       if(TextUtils.isEmpty(concert.getDescription())){
            description.setFocusable(true);
            description.setError("Description required");
            return  true;
        }
        else if(TextUtils.isEmpty(concert.getLocatonName())){
            locationName.setFocusable(true);
            locationName.setError("Location Name required");
            return  true;
        }
        else if(TextUtils.isEmpty(concert.getDate())){
            date.setFocusable(true);
            date.setError("Date required");
            return  true;
        }
        else if(TextUtils.isEmpty(concert.getStartTime())){
            time.setFocusable(true);
            time.setError("Category required");
            return  true;
        }
        else if(selectImage.getDrawable() == null){
            MessageToast.show(getActivity(),"Please pick an image");
            return  true;
        }
        else{
            return false;
        }

    }


    void clearTexts(){
        description.setText("");
        locationName.setText("");
        date.setText("");
        time.setText("");
    }


}
