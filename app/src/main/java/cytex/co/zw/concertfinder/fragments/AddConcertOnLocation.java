package cytex.co.zw.concertfinder.fragments;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.utils.Constants;
import cytex.co.zw.concertfinder.utils.GPSTracker;
import cytex.co.zw.concertfinder.utils.MessageToast;
import cytex.co.zw.concertfinder.utils.SHA1;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddConcertOnLocation extends Fragment {

    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";

    // Root Database Name for Firebase Database.




    // Creating EditText.
    EditText description;
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

    Button saveConcert;

    String email;



    private  static final  int REQUEST_CODE_PERMISSION=2;
    String mPermission= Manifest.permission.ACCESS_FINE_LOCATION;

    GPSTracker gps;

    public AddConcertOnLocation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_add_concert_on_location, container, false);
        getActivity().setTitle("Add Concert On Location");

        FirebaseApp.initializeApp(getActivity());
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.Database_Path);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            email=auth.getCurrentUser().getEmail();
        }
        else{
            email="private";
        }



        gps=new GPSTracker(getActivity());

        // Assign ID's to EditText.
        category=v.findViewById(R.id.category);
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



        description = v.findViewById(R.id.description);
        date=v.findViewById(R.id.date);
        time=v.findViewById(R.id.time);

        saveConcert=v.findViewById(R.id.saveConcert);





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




        saveConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gps.canGetLocation()) {
                    Concert concert=new Concert();
                    String Category=String.valueOf(category.getSelectedItem());
                    String Description=description.getText().toString();
                    String cDate=date.getText().toString();
                    String cTime=time.getText().toString();

                    concert.setCategory(Category);
                    concert.setDescription(Description);
                    concert.setLatitude(gps.getLatitude());
                    concert.setLongitude(gps.getLongitude());
                    concert.setLocatonName(getAddressName(gps.getLatitude(),gps.getLongitude()));
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
                else {
                    gps.showSettingsAlert();
                }

            }
        });




        return v;
    }

    private String getAddressName(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String address="";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            String  add = obj.getAddressLine(0);
            // add = add + "\n" + obj.getCountryName();
            //   add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            // add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();

            Log.e("Location", "Address" + add);
            address=add;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
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
                                concert.setHasMap(true);
                                concert.setPictureUrl(downloadUri.toString());


                                // Getting image upload ID.
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
        else if(TextUtils.isEmpty(concert.getDate())){
            date.setFocusable(true);
            date.setError("Date required");
            return  true;
        }
        else if(TextUtils.isEmpty(concert.getStartTime())){
            time.setFocusable(true);
            time.setError("Start Time required");
            return  true;
        }
        else{
            return false;
        }

    }


    void clearTexts(){
        description.setText("");
        date.setText("");
        time.setText("");
    }


}
