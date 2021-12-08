package com.kariaki.choice.UI.Camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Size;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.common.util.concurrent.ListenableFuture;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChatPage;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Engine;
import com.otaliastudios.cameraview.controls.PictureFormat;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public interface CameraHelper{

        void submitCapturedImageUrl(String imagePath);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RelativeLayout rootLayout;
    String homePath;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_camera, container, false);

        viewByID(view);
        context=getContext();

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rootLayout = view.findViewById(R.id.CameraRootlayout);

        homePath = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/choice/CameraImages";


        File homfile = new File(homePath);
        if (!homfile.exists()) {
            homfile.mkdirs();

        }
        cameraView.setLifecycleOwner(getViewLifecycleOwner());
        cameraView.setEngine(Engine.CAMERA1);

        cameraConfigs();

        capture.setOnClickListener(v->{
            cameraView.takePicture();
        });


        return  view;
    }

    CameraView cameraView;
    ImageView focusView;
    ImageView capture;
    private void viewByID(View view){
        cameraView=view.findViewById(R.id.cameraView);
        capture=view.findViewById(R.id.capture);
        focusView=view.findViewById(R.id.focusView);
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private void cameraConfigs() {
        WindowManager manager = getActivity().getWindowManager();
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        cameraView.setFrameProcessingMaxHeight(size.y);
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
        cameraView.setPictureFormat(PictureFormat.JPEG);

        cameraView.setSoundEffectsEnabled(false);
        cameraView.setFrameProcessingMaxWidth(720);
        cameraView.setFrameProcessingMaxHeight(1920);
        cameraView.setPreviewFrameRate(30f);
        cameraView.setPlaySounds(false);

        cameraView.setFrameProcessingMaxWidth(size.x);


        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
                super.onExposureCorrectionChanged(newValue, bounds, fingers);
            }

            @Override
            public void onAutoFocusStart(@NonNull PointF point) {
                super.onAutoFocusStart(point);
                focusView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Pulse).duration(500).playOn(focusView);
                focusView.setX(point.x);
                focusView.setY(point.y);
            }

            @Override
            public void onPictureTaken(@NonNull PictureResult result) {

              byte[]fileByte=  result.getData();



            }


            @Override
            public void onAutoFocusEnd(boolean successful, @NonNull PointF point) {
                super.onAutoFocusEnd(successful, point);
                focusView.setVisibility(View.INVISIBLE);
            }
        });


    }
    private File getOutputMediaFile(int type) throws IOException {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir =
                new File(Environment.
                        getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "ChoiceImages");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }
        File file = new File(mediaStorageDir.getAbsolutePath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

}