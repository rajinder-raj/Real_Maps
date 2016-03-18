package boom.realmaps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Upload extends AppCompatActivity implements View.OnClickListener{
    private static final int RESULT_LOAD_IMAGE = 1;
    private Firebase fbdb;
    ImageView imageToUpload, downloadImage; // holds the bitmap image
    Button bUploadImage, bDownloadImage;
    EditText uploadImageName, downloadImageName;
    @JsonIgnoreProperties(ignoreUnknown=true)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Firebase initial setup
        Firebase.setAndroidContext(this);
        fbdb = new Firebase("https://boomerango.firebaseio.com/imagesV2");

        // set buttons and listeners
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        downloadImage = (ImageView) findViewById(R.id.downloadImage);

        bUploadImage = (Button) findViewById(R.id.bUploadImage);
        bDownloadImage = (Button) findViewById(R.id.bDownloadImage);

        uploadImageName = (EditText) findViewById(R.id.etUploadName);
        downloadImageName = (EditText) findViewById(R.id.etDownloadName);

        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);
        bDownloadImage.setOnClickListener(this);
    }

    // Determines what to activate when something is clicked
    @Override
    public void onClick(View v) {
        // determines which action to
        switch(v.getId()) {
            case R.id.imageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // start the gallery picker
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE); // display the image over the gallery button
                break;
            case R.id.bUploadImage:
                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap(); // get image from imageToUpload button
                UploadImage upload = new UploadImage(image, uploadImageName.getText().toString()); // create the UploadImage object to upload
                upload.execute(); // run the upload in the background
                break;
            case R.id.bDownloadImage:
                new DownloadImage(downloadImageName.getText().toString()).execute();
                break;
        }

    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

    /**
     * Private class for handling the retrival of the image from the firebase server.
     * Inherits the AsyncTask to enable it to work in the background as it uploads.
     * Bitmap parameter in the AsyncTask allows us to extract an image from the task
     *
     */
    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        Image retrievedImg = null;
        String img_id = null;

        /**
         * Constructor takes in the name of the image to find
         * @param img_id - image to find
         */
        public DownloadImage(String img_id) {
            this.img_id = img_id;
        }

        /**
         * Handles the serialization and uploading of the image.
         * Avoids the app itself hanging when retrieving the image
         * @param params - nothing
         * @return - nothing
         */
        @Override
        protected Bitmap doInBackground(Void... params) {
            // Firebase listener required to retrieve values from database
            fbdb.addValueEventListener(new ValueEventListener() {
                /**
                 * Search through the Image database for a match to the ID
                 * @param dataSnapshot - a reference to a firebase location containing data
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        retrievedImg = postSnapShot.getValue(Image.class);
                        // TODO - if you use a different value to compare, update the get method here
                        if (retrievedImg.getUploader().equals(img_id)) {
                            break;
                        }
                        else
                            retrievedImg = null;
                    }
                }

                /**
                 * Should the retrieval fail, call this method
                 * @param firebaseError - error messaged related
                 */
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
        // TODO - AysncTask's onPostExecute is not being found on super
        /**
         *
         */
        /*
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Image Download Successful", Toast.LENGTH_SHORT).show();
        }
        */
    }

    /**
     * Private class for handling the uploading of image from the firebase server.
     */
    private class UploadImage extends AsyncTask<Void, Void, Void> {
        Bitmap image = null;
        String name = null;

        /**
         * Constructor for the Upload entity
         * @param image - the user selected image to be uploaded
         * @param name - the title of the image
         */
        public UploadImage(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        /**
         * Handles the serialization and uploading of the image.
         * Avoids the app itself hanging as the image is serialized and
         * is uploaded to the Firebase database
         * @param params
         * @return - nothing
         * TODO - pass additional metadata to the Image entity
         */
        @Override
        protected Void doInBackground(Void... params) {
            Image newImg = new Image(name, image);
            fbdb.push().setValue(newImg); // add the the metadata and serialized image to the database
            return null;
        }

        /**
         * Notification of the image that is successfully uploaded after uploading.
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

        }
    }

}
