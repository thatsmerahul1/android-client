package com.ecarezone.android.patient.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.UploadImageResponse;
import com.ecarezone.android.patient.service.EcareZoneWebService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by L&T Technology Services on 3/3/2016.
 */
public class ImageUtil {

    private static String TAG = ImageUtil.class.getSimpleName();

    private static int TARGET_IMAGE_WIDTH = 600;
    private static int TARGET_IMAGE_HEIGHT = 600;
    private static int IMAGE_QUALITY = 40;

    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private static String imageFileName = "profilepic";

    /* Upload the profile image to server */
    public static UploadImageResponse uploadProfilePicture(String imagePath) {
        if (imagePath == null) {
            return null;
        }
        // Scale the image
        Bitmap scaledBitmap = createScaledBitmap(imagePath, TARGET_IMAGE_WIDTH, TARGET_IMAGE_HEIGHT);
        File file = new File(imagePath);
        try {
            saveBitmapToFile(scaledBitmap, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TypedFile typedFile = new TypedFile("multipart/form-data", file);
        // upload the image to server
        UploadImageResponse response = null;
        try {
            response = EcareZoneWebService.api.upload(typedFile, LoginInfo.userId);
        }catch (Exception e){
            Log.i(TAG,"Exception during image upload. Image upload failed.");
            e.printStackTrace();
        }
        return response;
    }

    /* Saves the image to a file and returns the image path. */
    public static String saveUriPhotoToFile(Uri uri, FragmentActivity activity) {
        InputStream inputStream = null;
        File image = null;
        try {
            inputStream = activity.getContentResolver().openInputStream(uri);
            //create an empty file to save the image into
            image = ImageUtil.createImageFile();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        if (image != null && inputStream != null) {
            byte[] buffer = new byte[8 * 1024];
            OutputStream targetStream;
            int bytesRead;
            try {
                targetStream = new FileOutputStream(image);
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    targetStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                targetStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return null;
            }
            return image.getAbsolutePath();
        }
        return null;
    }

    /* Creates a bitmap by scaling the source image to the size of the displaying image view in UI layout*/
    public static Bitmap createScaledBitmap(String srcImagePath, int targetW, int targetH) {
        // get the dimensions of the image
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcImagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(srcImagePath, bmOptions);
        return bitmap;
    }

    /* creates an image in external storage and returns the File */
    public static File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    /* creates an image in external storage and returns the File */
    public static File createChatImageFile( String recipient) throws IOException {
        File storageDir = new File(Environment.getExternalStorageDirectory()
                +  "/eCareZone"+ "/" + recipient + "/outgoing");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss");
        String todayDate = dateFormat.format(new Date());

        File image = File.createTempFile(
                "IMG-"+ todayDate,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    /* Utility to save the passed bitmap image to the file */
    public static boolean saveBitmapToFile(Bitmap bitmap, File file) throws IOException {
        boolean success = false;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        success = bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bytes);

        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());

        fo.close();
        return success;
    }

    /* fires an intent to open camera to take a photo.
    *  Passes the empty file through bundle to place the captured photo into it
    * */
    public static String dispatchTakePictureIntent(Activity context, boolean fromChat, String recipient) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imagePath = null;
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                if(fromChat){
                    photoFile = ImageUtil.createChatImageFile(recipient);
                } else {
                    photoFile = ImageUtil.createImageFile();
                }
                imagePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "Unable to create the empty image file");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(context, context.getResources().getText(R.string.imageutil_camera_not_found), Toast.LENGTH_LONG).show();
        }
        return imagePath;
    }

    public static UploadImageResponse uploadChatImage(File file) {
        if (file == null) {
            return null;
        }
        TypedFile typedFile = new TypedFile("multipart/form-data", file);
        UploadImageResponse response = EcareZoneWebService.api.upload(typedFile, LoginInfo.userId);
        return response;
    }

    public static File uploadChatImageToDisc(String imagePath) {
        // Scale the image
        Bitmap scaledBitmap = createScaledBitmap(imagePath, TARGET_IMAGE_WIDTH, TARGET_IMAGE_HEIGHT);
        File file = new File(imagePath);
        try {
            saveBitmapToFile(scaledBitmap, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}