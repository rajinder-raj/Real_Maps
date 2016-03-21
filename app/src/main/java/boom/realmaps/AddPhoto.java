package boom.realmaps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;

public class AddPhoto extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 1;
    private Firebase fbdb;
    private GeoFire geofbdb;
    ImageView imageToUpload;
    Button bUploadImage;
    EditText uploadImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        // Firebase initial setup
        Firebase.setAndroidContext(this);
        fbdb = new Firebase("https://boomerango.firebaseio.com/imagesV2");
        geofbdb = new GeoFire(fbdb);

        // set buttons and listeners
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        bUploadImage = (Button) findViewById(R.id.bUploadImage);

        uploadImageName = (EditText) findViewById(R.id.etUploadName);

        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // start the gallery picker
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE); // display the image over the gallery button
                break;
            case R.id.bUploadImage:
                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap(); // get image from imageToUpload button
                if (image != null) {
                    String uploader = "raj"; // todo retrieve current user here
                    double latitude = 0; // todo get coordinates lat
                    double longitutde = 0; // todo get coordinates long
                    addImage(new Image(uploader, image), latitude, longitutde);
                }
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

    public void addImage(Image img, double latitude, double longitude) {
        Firebase fbdb_post = fbdb.child("images");
        Firebase fbdb_newpost = fbdb_post.push();

        fbdb_newpost.setValue(img);

        String key = fbdb_newpost.getKey();
        GeoLocation imgGeo = new GeoLocation(latitude, longitude);
        geofbdb.setLocation(key, imgGeo);
        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
    }
}