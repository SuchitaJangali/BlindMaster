package com.example.myapplication;



import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.model.Relative;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class MainActivity extends CameraActivity{
    ResourceBundle sharedPreferences;
    private boolean isFaceDetected = false;


    int touch=0;
    CameraBridgeViewBase cameraBridgeViewBase;
    File caseFile;
    CascadeClassifier mCascadeClassifier;
    Bitmap bitmap;
    private Mat mRgba,mGrey;
    private boolean mFaceDetected = false;

    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mDatabaseReference;
    TextToSpeech textToSpeech;
    float x1,x2,y1,y2;





    ImageView mCapturedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(),"ready",Toast.LENGTH_LONG).show();
        mCapturedImage = findViewById(R.id.imageView);

        cameraBridgeViewBase=findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        if(!OpenCVLoader.initDebug())
        {
            cameraBridgeViewBase.enableView();
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,  this,baseCallback);
        }else
        {
            try {
                baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            } catch (IOException e) {

                e.printStackTrace();            }
        }
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {

            @Override
            public void onCameraViewStarted(int width, int height) {
                mRgba = new Mat(height, width, CvType.CV_8UC4);
            }

            @Override
            public void onCameraViewStopped() {
                mRgba.release();
                mGrey.release();
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mRgba = inputFrame.rgba();
                mGrey = inputFrame.gray();
                //detect Face
                MatOfRect facedetections = new MatOfRect();
                mCascadeClassifier.detectMultiScale(mRgba,facedetections);//detected face store in mca

                for(Rect react: facedetections.toArray()){

                    Imgproc.rectangle(mRgba, new Point(react.x,react.y),
                            new Point(react.x + react.width, react.y + react.height),
                            new Scalar(255,0,0));
                }
               Rect nearestFace= findNearestFace(facedetections.toArray(), mRgba.width(), mRgba.height());
                if (nearestFace != null) {
                    // Crop the detected face from the frame
                    Mat croppedFace = new Mat(mRgba, nearestFace);

                    // Convert Mat to Bitmap for uploading to Firebase Storage
                    Bitmap bitmap = Bitmap.createBitmap(croppedFace.cols(), croppedFace.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(croppedFace, bitmap);

                    // Upload the bitmap to Firebase Storage
                   uploadFaceToFirebase(bitmap);
                    textToSpeech.speak("faces detected",TextToSpeech.QUEUE_FLUSH,null);

                }
                return mRgba;

            }

        });
        cameraBridgeViewBase.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Capture the image on touch event (ACTION_DOWN)
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Action when finger touches the screen
                        x1=event.getX();
                        y1=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // Action when finger is lifted from the screen
                        x2=event.getX();
                        y2=event.getY();
                        if(x1==x2 ) {
                            touch+=1;

                            captureImage();
                            Intent intent = new Intent(MainActivity.this,Login.class);

                            intent.putExtra("image",mCapturedImage.toString());
                            startActivity(intent);

                        }
                        if(x1<x2){
                            //helping page
                            Intent intent = new Intent( MainActivity.this ,FirstPage.class);
                            startActivity(intent);
                        }
                        if(x1>x2){
                            //Voice command
                            // Intent intent1 = new Intent(MainActivity.this, VoiceCommands.class);

                            //startActivity(intent1);
                        }
                        break;

                }

                return true;
            }
        });


    //voice

        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("touch to click photo", TextToSpeech.QUEUE_FLUSH, null);

            }
        });

    }

    private void captureImage() {
        if (mRgba != null) {
            // Capture the current frame
            // Process and display the captured image
            // You can save the Mat mRgba as an image file here
            // For example: Highgui.imwrite("/sdcard/opencv_image.jpg", mRgba);
            Bitmap bitmap = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
            org.opencv.android.Utils.matToBitmap(mRgba, bitmap);
            mCapturedImage.setImageBitmap(bitmap);
            Toast.makeText(MainActivity.this, "Image Captured", Toast.LENGTH_SHORT).show();
        }
    }//to click a picture


    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }
    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    InputStream is  = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File cascadeDir = getDir("cascade" , Context.MODE_PRIVATE);
                    caseFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");

                    FileOutputStream fos = new FileOutputStream(caseFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while((bytesRead = is.read(buffer)) !=-1 ){
                        fos.write(buffer,0,bytesRead);
                    }
                    is.close();
                    fos.close();

                    mCascadeClassifier = new CascadeClassifier(caseFile.getAbsolutePath());
                    if(mCascadeClassifier.empty()){
                        mCascadeClassifier = null;
                    }
                    else{
                        cascadeDir.delete();
                    }
                    cameraBridgeViewBase.enableView();
                }
                break;

                default:
                    super.onManagerConnected(status);
            }
        }
    };
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    //Find neraes faces  only one
    public Rect findNearestFace(Rect[] faces, int cameraWidth, int cameraHeight) {
        if (faces.length == 0) {
            return null; // No faces detected
        }

        // Calculate the distance of each face from the camera (using the center of each face)
        double minDistance = Double.MAX_VALUE;
        Rect nearestFace = null;
        Point cameraCenter = new Point((double) cameraWidth / 2, (double) cameraHeight / 2);

        for (Rect face : faces) {
            // Calculate the center of the face
            Point faceCenter = new Point(face.x + (double) face.width / 2, face.y + (double) face.height / 2);

            // Calculate the distance between the face center and the camera center
            double distance = Math.sqrt(Math.pow(faceCenter.x - cameraCenter.x, 2) + Math.pow(faceCenter.y - cameraCenter.y, 2));

            // Update nearest face if this face is closer
            if (distance < minDistance) {
                minDistance = distance;
                nearestFace = face;
            }
        }

        return nearestFace;
    }
    private void uploadFaceToFirebase(Bitmap faceBitmap) {
        // Upload the face image to Firebase Storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mFirebaseStorage.getReference(getusername()).child("jhon" );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        faceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storageRef.putBytes(data).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();

                // Store face information in Firebase Realtime Database
                storeFaceInfo(downloadUri.toString());
            }
        });
    }
    private void storeFaceInfo(String imageUrl) {
        // Store additional information about the detected face in Firebase Realtime Database
        // You can store metadata like name, timestamp, etc.

        String faceId = mDatabaseReference.push().getKey();
        Relative faceInfo = new Relative("spnam","2243",imageUrl);
        mDatabaseReference.child(faceId).setValue(faceInfo);

        // Set isFaceDetected flag to true to prevent detecting multiple faces
        isFaceDetected = true;
    }

    private  String getusername(){
        // Retrieve the login status from SharedPreferences
        return sharedPreferences.getString("username");
    }
}
