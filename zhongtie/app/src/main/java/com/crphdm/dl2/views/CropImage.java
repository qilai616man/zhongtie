package com.crphdm.dl2.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.goyourfly.gdownloader.utils.Ln;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by samsung on 14-11-19.
 */
public class CropImage {
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;

    private Activity mActivity;
    private File mFileTemp;
    private boolean mIsUploadFile;
    private OnCropImageListener mListener;

    public interface OnCropImageListener {
        public void onCropOk(File file);
        public void onCancel();
    }

    public CropImage(Activity activity) {
        mActivity = activity;



    }

    public void setCropListener(OnCropImageListener cropImageListener){
        mListener = cropImageListener;
    }

    private void initFile(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), getFileName());
        } else {
            mFileTemp = new File(mActivity.getFilesDir(), getFileName());
        }
    }

    private String getFileName(){
        return "ICON_" + System.currentTimeMillis() + ".jpg";
    }

    public void startCropImage() {
        Intent intent = new Intent(mActivity, eu.janmuller.android.simplecropimage.CropImage.class);
        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.SCALE, true);

        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.ASPECT_X, 1);
        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.ASPECT_Y, 1);

        mActivity.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }
    public void startCropImage(Fragment fragment) {
        Intent intent = new Intent(mActivity, eu.janmuller.android.simplecropimage.CropImage.class);
        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.SCALE, true);

        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.ASPECT_X, 1);
        intent.putExtra(eu.janmuller.android.simplecropimage.CropImage.ASPECT_Y, 1);

        fragment.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void openGallery() {
        initFile();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mActivity.startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    public void openGallery(Fragment fragment) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        fragment.startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    public void takePicture(Uri takePicUri) {
        initFile();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
                mImageCaptureUri = takePicUri;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            mActivity.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            Ln.d("cannot take picture");
        }
    }

    public void takePicture(Fragment fragment,Uri takePicUri) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
                mImageCaptureUri = takePicUri;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            fragment.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            Ln.d("cannot take picture");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != mActivity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    startCropImage();
                } catch (Exception e) {
                    Ln.e("Error while creating temp file");
                }
                mIsUploadFile = true;
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                mIsUploadFile = true;
                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:
                String path = data.getStringExtra(eu.janmuller.android.simplecropimage.CropImage.IMAGE_PATH);
                if (path == null) {
                    mIsUploadFile = false;
                    return;
                }

                if (mIsUploadFile) {
                    mListener.onCropOk(mFileTemp);
                } else {
                    mListener.onCancel();
                }
                break;
        }
    }

    public void onActivityResult(Fragment fragment,int requestCode, int resultCode, Intent data) {
        if (resultCode != mActivity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();

                    startCropImage(fragment);

                } catch (Exception e) {

                    Ln.e("Error while creating temp file");
                }
                mIsUploadFile = true;
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                mIsUploadFile = true;
                startCropImage(fragment);
                break;
            case REQUEST_CODE_CROP_IMAGE:
                String path = data.getStringExtra(eu.janmuller.android.simplecropimage.CropImage.IMAGE_PATH);
                if (path == null) {
                    mIsUploadFile = false;
                    return;
                }

                if (mIsUploadFile) {
                    mListener.onCropOk(mFileTemp);
                } else {
                    mListener.onCancel();
                }
                break;
        }
    }
}

