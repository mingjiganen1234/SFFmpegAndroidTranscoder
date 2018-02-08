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
        //�򿪴���
        Log.d(TAG, "MediaRecorder oncreate");
        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        textView = (TextView)findViewById(R.id.mc_text);
        
        timeText=(TextView)findViewById(R.id.mc_date);
        //����surface���
        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        // setType�������ã�Ҫ������.
    	holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.d("����","onCreate");
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

    // ��ȡϵͳʱ��
    public static String getDate() {
    	return new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
    }
    //��ʼ¼��
    private void startRecorder()
    {
        Log.d("����","new mRecorder");
        //��ʼ¼����Ƶ��
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        Log.d("����","open camera");
        //�����
        while(Parameter.camera==null){
            Log.d("����","camera is null��try open");
            Parameter.camera = Camera.open();
        }
        Log.d("����","set camera");
        //�������
        if (Parameter.camera != null) {
            Parameter.camera.unlock();
            mRecorder.setCamera(Parameter.camera);
        }
        Log.d("����","set Format");
        //������Ƶ�ĸ�ʽ��·���������������Ƶʱ���Լ�1������ϻ����
        try {
            // ��������Ҫ����setOutputFormat֮ǰ
            //mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // Set output file format
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            // ��������Ҫ����setOutputFormat֮��
            //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mRecorder.setVideoSize(1280,720);	//(640,480)��(720,576)
            mRecorder.setVideoFrameRate(30);
            // // mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
            // // mRecorder.setOrientationHint(0);
            // // ���ü�¼�Ự��������ʱ�䣨���룩
            // // mRecorder.setMaxDuration(60 * 1000);
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            //�洢λ��
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
            //�ļ��д��ڲſ�ʼ
            if (dir.exists()) {
                path = dir + "/" + getDate() + ".mp4";
                Parameter.videoName=getDate() + ".mp4";
                mRecorder.setOutputFile(path);
                mRecorder.prepare();
                mRecorder.start();
                Log.d("����","start ok ");
            }
            handler.postDelayed(runnable,1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //ֹͣ¼��
    private void stopRecorder()
    {
        try {
            handler.removeCallbacks(runnable);
            Log.d("����","�ж�mRecorder֮ǰ");
            if(mRecorder!=null)
            {
                Log.d("����","stop in ");
                mRecorder.setOnErrorListener(null);
                mRecorder.setOnInfoListener(null);
                mRecorder.setPreviewDisplay(null);
                Log.d("����","stop begin ");
                mRecorder.stop();
                Log.d("����","stop ok ");
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
            Log.d("����","�ж�mRecorder֮��");
            if (Parameter.camera != null) {
                Parameter.camera.setPreviewCallback(null);
                Parameter.camera.stopPreview();
                Parameter.camera.release();
                Parameter.camera = null;
            }
            Log.d("����","��ʼ�л����");
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
        Log.d("����","onResume");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
		
		if(	Parameter.camera == null)
		{
			Parameter.camera = Camera.open(0);
            Log.d("����","create camera");
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
                Log.d("����","create camera failed");
            }
		} catch (IOException e) {
            Log.d("����","surfaceCreated�쳣");
            finish();
			e.printStackTrace();
		}
        Log.d("����","surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // ��holder�����holderΪ��ʼ��onCreate����ȡ�õ�holder����������mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
        Log.d("����","surfaceChanged");
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
        Log.d("����","surfaceDestroyed");
    }
}