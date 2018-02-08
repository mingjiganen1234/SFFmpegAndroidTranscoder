package com.leixiaohua1020.sffmpegandroidtranscoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.List; 

/**
 * sufaceView 的预览类，其中SurfaceHolder.CallBack用来监听Surface的变化，
 * 当Surface发生改变的时候自动调用该回调方法
 * 通过调用方SurfaceHolder.addCallBack来绑定该方法
 * @author zw.yan
 *
 */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	private String TAG = "CameraPreview";
	/**
	 * Surface的控制器，用来控制预览等操作
	 */
	private SurfaceHolder mHolder;
	/**
	 * 相机实例
	 */
	
	private Camera mCamera = null;
	/**
	 * 图片处理
	 */
	public static final int MEDIA_TYPE_IMAGE = 1;
	/**
	 * 获取当前的context
	 */
	private Context mContext;
	/**
	 * 当前传感器的方向，当方向发生改变的时候能够自动从传感器管理类接受通知的辅助类
	 */
	MyOrientationDetector cameraOrientation;
	/**
	 * 设置最适合当前手机的图片宽度
	 */
	int setFixPictureWidth = 0;
	/**
	 * 设置当前最适合的图片高度
	 */
	int setFixPictureHeight = 0;
	
	
	
	//@SuppressWarnings("deprecation")
	public CameraPreview(Context context) {
		super(context);
		this.mContext = context;
		//isSupportAutoFocus = context.getPackageManager().hasSystemFeature(
		//		PackageManager.FEATURE_CAMERA_AUTOFOCUS);
		mHolder = getHolder();
		mHolder.addCallback(this);//绑定当前的回调方法	
		
	}
	public void getSize(){
		List<Camera.Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();  
        List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();  
  
        for (int i=0; i<pictureSizes.size(); i++) {  
            Camera.Size pSize = pictureSizes.get(i);  
            Log.i("size"+"-initCamera", "-PictureSize.width = "+pSize.width+"--PictureSize.height = "+pSize.height);  
        }  
  
        for (int i=0; i<previewSizes.size(); i++) {  
            Camera.Size pSize = previewSizes.get(i);  
            Log.i("size"+"-initCamera", "-previewSize.width = "+pSize.width+"--previewSize.height = "+pSize.height);  
        } 
	}
	/**
	 * 创建的时候自动调用该方法
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		 
		if (mCamera == null) {
			mCamera = CameraCheck.getCameraInstance(mContext);
		}
		try {
			if(mCamera!=null){
				mCamera.setPreviewDisplay(holder); 
				mCamera.startPreview();
				List<Camera.Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();  
		        List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();  
		  
		        for (int i=0; i<pictureSizes.size(); i++) {  
		            Camera.Size pSize = pictureSizes.get(i);  
		            Log.i("size"+"-initCamera", "-PictureSize.width = "+pSize.width+"--PictureSize.height = "+pSize.height);  
		        }  
		  
		        for (int i=0; i<previewSizes.size(); i++) {  
		            Camera.Size pSize = previewSizes.get(i);  
		            Log.i("size"+"-initCamera", "-previewSize.width = "+pSize.width+"--previewSize.height = "+pSize.height);  
		        } 
		        Camera.Parameters mParameters = mCamera.getParameters();
		        mParameters.setPictureSize(720, 576);
		        mCamera.setParameters(mParameters);
			}
		} catch (IOException e) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
			}
			e.printStackTrace();
		}

	}
	/**
	 * 当surface的大小发生改变的时候自动调用的
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mCamera!=null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	public void camera_release(){
		if(mCamera!=null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	/**
	 * 调整照相的方向，设置拍照相片的方向
	 */
	//private void takePhoto() {
	public void takePhoto() {
			mCamera.takePicture(shutterCallback, pictureCallback, mPicture);
	}
	
	//private void takePhoto() {
	public void i2c_set(int value) {
		I2CFPGA i2c_test=new I2CFPGA();
		i2c_test.Ioctl(0,0);
		}
	
	private ShutterCallback shutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
		}
	};

	private PictureCallback pictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub

		}
	};
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePictureTask().execute(data);
			mCamera.startPreview();//重新开始预览
		}
	};

	public class SavePictureTask extends AsyncTask<byte[], String, String> {
		@SuppressLint("SimpleDateFormat")
		@Override
		protected String doInBackground(byte[]... params) {
			File pictureFile = FileUtil.getOutputMediaFile(MEDIA_TYPE_IMAGE,
					mContext);
			if (pictureFile == null) {
				Toast.makeText(mContext, "请插入存储卡！", Toast.LENGTH_SHORT).show();
				return null;
			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(params[0]);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
			
			return null;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	//	reAutoFocus();
		return false;
	}
}