package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Read extends CameraActivity {
    CameraBridgeViewBase cameraBridgeViewBase;
    TextToSpeech textToSpeech;
    Bitmap bitmap;
    private Mat mRgba,mGrey;

    TextRecognizer recognizer;
    String reconizedText;
    float x1,x2,y1,y2;
    TextView read;
    String[] ss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        read=findViewById(R.id.readText);
        cameraBridgeViewBase=findViewById(R.id.CameraView);
        //load text Reconization model

        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        if(!OpenCVLoader.initDebug())
        {
            cameraBridgeViewBase.enableView();
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,  this,baseCallback);
        }else
        {
            Toast.makeText(getApplicationContext(),"opencv open",Toast.LENGTH_LONG).show();

        }
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                mRgba=new Mat(
                        height,width, CvType.CV_8UC4);
            }

            @Override
            public void onCameraViewStopped() {
                mRgba.release();

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mRgba=inputFrame.rgba();

                return mRgba;
            }
        });
        //llioad reconzer

        recognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());

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
                            capturedImage();
                            textToSpeech.speak(" ",TextToSpeech.QUEUE_FLUSH,null);
                            reconizedText=recognizeText();
                            ss=reconizedText.split(" ");

                        }
                        if(x1<x2){
                            //home page
                            Intent intent = new Intent( Read.this ,FirstPage.class);
                            startActivity(intent);
                        }
                        if(x1>x2){
                            //Voice command
                            Intent intent1 = new Intent(Read.this, CommandPage.class);

                            startActivity(intent1);
                        }
                        break;

                }

                return true;
            }
        });
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(0.5f);//speed
                textToSpeech.speak(" you are in Reading mode ", TextToSpeech.QUEUE_FLUSH, null);
                textToSpeech.speak("touch to start reading",TextToSpeech.QUEUE_FLUSH,null);
            }
        });

    }
    private String recognizeText() {
        InputImage image=InputImage.fromBitmap(bitmap,0);
        //reconize text
        Task<Text> result =
                recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {

                    @Override
                    public void onSuccess(Text text) {
        StringBuilder result=new StringBuilder();
        for(Text.TextBlock block:text.getTextBlocks())
        {
            String blockText =block.getText();
            Point[]  blockCornerPoint= block.getCornerPoints();
            Rect blockFrame=block.getBoundingBox();
            for (Text.Line line:block.getLines()){
                String linetext=line.getText();
                Point[] lineCornerpoint=line.getCornerPoints();
                Rect lineReact =line.getBoundingBox();
                for (Text.Element element :line.getElements()){
                    String elmentText=element.getText();
                    result.append(elmentText+" ");
                    textToSpeech.speak(elmentText,TextToSpeech.QUEUE_ADD,null);

                    //result.append(" ");
                }
                textToSpeech.setLanguage(new Locale("en","IN"));
                textToSpeech.setSpeechRate(0.5f);//speed

                read.setText(result);

                String[] s=result.toString().split(" ");
                for(int i=0;i<s.length;i++){

                }
//                for (String ss:s){
//                    Handler handler=new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            textToSpeech.speak(ss,TextToSpeech.QUEUE_FLUSH,null);
//
//                        }
//                    },2000);
//                }

            }
        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("recon",e.getMessage());

                    }
                });

        return (String) read.getText();
    }
    private void capturedImage() {
        if (mRgba != null) {
            // Capture the current frame
            // Process and display the captured image
            // You can save the Mat mRgba as an image file here
            // For example: Highgui.imwrite("/sdcard/opencv_image.jpg", mRgba);
            bitmap = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
            org.opencv.android.Utils.matToBitmap(mRgba, bitmap);
            }

    }//to click a picture
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
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }
    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    cameraBridgeViewBase.enableView();
                }
                break;

                default:
                    super.onManagerConnected(status);
            }
        }
    };

}

