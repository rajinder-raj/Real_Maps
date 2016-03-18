package boom.realmaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


/**
 * Created by jparsee(john) on 17/02/16.
 */
public class Image {
    private String img_id = null;
    private String uploader = null;
    private String dateUploaded = null;
    private String image_serialized = null;
    // private ArrayList<LikeDislike> likesDislikes = new ArrayList<LikeDislike>();
    // unused, to add later!

    public Image(String username, Bitmap bitmap) {
        img_id =
        image_serialized = serializeBitmap(bitmap);
        this.uploader = username;
    }

    // For Firebase database; do not remove!
    public Image() {    }

    public String getUploader() {
        return uploader;
    }

    public String getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getImage_serialized() {
        return image_serialized;
    }

    /**
     * Take a bitmap image, compress and serializes it
     * @param bitmap - bitmap image
     * @return - serialized bitmap, uncompressed
     */
    public String serializeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // TODO - comment here
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); // reduce the size of the image to a jpeg
       return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT); // encodes it in a string
    }

    /**
     * Get the image back as a bitmap
     * TODO - test if encoding is correct
     * @return - normal bitmap, compressed
     */
    public Bitmap convertBitmap() {
        byte[] bitmapByteArray = Base64.decode(image_serialized, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length);
        return bitmap;
    }
}
