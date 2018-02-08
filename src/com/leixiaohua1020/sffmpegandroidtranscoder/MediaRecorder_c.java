package com.leixiaohua1020.sffmpegandroidtranscoder;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.leixiaohua1020.sffmpegandroidtranscoder.MainActivity.TimeThread;

public class MediaRecorder_c extends Activity implements SurfaceHolder.Callback {
	//serial com3=new serial();
    private static final String TAG = "MediaRecorder";
    private SurfaceView mSurfaceview;
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    // private Camera camera;
    private String path;
    private TextView textView;
    private int text = 0;
    private TextView moshiText;
    private TextView timeText;
    // private TextView zengyiText;
    private TextView textDianliang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.media_recorder);
        //打开串口
        Log.d(TAG, "MediaRecorder oncreate");
        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        textView = (TextView)findViewById(R.id.mc_text);
        
        timeText=(TextView)findViewById(R.id.mc_date);
        //设置surface相关
        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
    	holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.d("串口","onCreate");
    }
    
    private android.os.Handler handler = new android.os.Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            text++;
            int hour=text/3600;
            int min=text/60%60;
            int second=text%60;
            if(second<10){
            	textView.setText(hour+":"+min+":0"+second);
            }else{
            	textView.setText(hour+":"+min+":"+second);
            }
            handler.postDelayed(this,1000);
            if(Parameter.uartInt==6)
            {
                stopRecorder();
            }
        }
    };

    // 获取系统时间
    public static String getDate() {
    	return new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
    }
    //开始录制
    private void startRecorder()
    {
        Log.d("串口","new mRecorder");
        //开始录制视频了
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        Log.d("串口","open camera");
        //打开相机
        while(Parameter.camera==null){
            Log.d("串口","camera is null，try open");
            Parameter.camera = Camera.open();
        }
        Log.d("串口","set camera");
        //设置相机
        if (Parameter.camera != null) {
            Parameter.camera.unlock();
            mRecorder.setCamera(Parameter.camera);
        }
        Log.d("串口","set Format");
        //设置视频的格式及路径、很奇怪设置音频时在自己1块板子上会出错
        try {
            // 这两项需要放在setOutputFormat之前
            //mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // Set output file format
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            // 这两项需要放在setOutputFormat之后
            //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mRecorder.setVideoSize(1280,720);	//(640,480)、(720,576)
            mRecorder.setVideoFrameRate(30);
            // // mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
            // // mRecorder.setOrientationHint(0);
            // // 设置记录会话的最大持续时间（毫秒）
            // // mRecorder.setMaxDuration(60 * 1000);
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            //存储位置
            if(Parameter.addr==1){
                path = "/mnt/udisk1";
            }
            else{
                path = "/mnt/sdcard2";
            }
            File dir = new File(path + "/video");
            if (!dir.exists()) {
                dir.mkdir();
            }
            //文件夹存在才开始
            if (dir.exists()) {
                path = dir + "/" + getDate() + ".mp4";
                Parameter.videoName=getDate() + ".mp4";
                mRecorder.setOutputFile(path);
                mRecorder.prepare();
                mRecorder.start();
                Log.d("串口","start ok ");
            }
            handler.postDelayed(runnable,1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停止录制
    private void stopRecorder()
    {
        try {
            handler.removeCallbacks(runnable);
            Log.d("串口","判断mRecorder之前");
            if(mRecorder!=null)
            {
                Log.d("串口","stop in ");
                mRecorder.setOnErrorListener(null);
                mRecorder.setOnInfoListener(null);
                mRecorder.setPreviewDisplay(null);
                Log.d("串口","stop begin ");
                mRecorder.stop();
                Log.d("串口","stop ok ");
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
            Log.d("串口","判断mRecorder之后");
            if (Parameter.camera != null) {
                Parameter.camera.setPreviewCallback(null);
                Parameter.camera.stopPreview();
                Parameter.camera.release();
                Parameter.camera = null;
            }
            Log.d("串口","开始切换相机");
            Parameter.wrInfo();
            android.os.Process.killProcess(android.os.Process.myPid());
            //  Intent intent = new Intent(MediaRecorder_c.this,MainActivity.class);
            //  startActivity(intent);
            //  finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Parameter.inPhoto=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("串口","onResume");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
		
		if(	Parameter.camera == null)
		{
			Parameter.camera = Camera.open(0);
            Log.d("串口","create camera");
		}
		if (Parameter.camera == null) {
			Parameter.camera = CameraCheck.getCameraInstance(this);
           //camera = Camera.open(0);
		}
		try {
			if(Parameter.camera!=null){
				Parameter.camera.setPreviewDisplay(surfaceHolder); 
				Parameter.camera.startPreview();
            }
            else
            {
                Log.d("串口","create camera failed");
            }
		} catch (IOException e) {
            Log.d("串口","surfaceCreated异常");
            finish();
			e.printStackTrace();
		}
        Log.d("串口","surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
        Log.d("串口","surfaceChanged");
        startRecorder();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        Parameter.inPhoto=true;
        handler.removeCallbacks(runnable);
        Log.d("ffmright","MR destroy");
        if (mRecorder != null) {
            mRecorder.release(); 
            mRecorder = null;
            Log.d(TAG, "surfaceDestroyed release mRecorder");
        }
        if (Parameter.camera != null) {
            Parameter.camera.setPreviewCallback(null);
            Parameter.camera.stopPreview();
            Parameter.camera.release();
            Parameter.camera = null;
        }
        surfaceHolder.removeCallback(this);
        mSurfaceview = null;
        mSurfaceHolder = null;
        Log.d("串口","surfaceDestroyed");
    }
}