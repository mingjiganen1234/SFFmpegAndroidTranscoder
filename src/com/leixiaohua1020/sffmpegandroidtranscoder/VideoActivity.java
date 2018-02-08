package com.leixiaohua1020.sffmpegandroidtranscoder;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

//该类用于播放视频，播放结束立即返回
public class VideoActivity extends Activity {
	//视频所在路径
	public static String videoPath;
	//该视频时长
	public int videoLength = 0;
	//视频控件
	public VideoView vvPlayer;
	//终止线程标记
	public boolean working = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
		setContentView(R.layout.activity_video);
		
		//启动新的线程
		working = true;
		new TimeThread().start();
		
    	//以下4行获取视频播放时长，单位ms
    	MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    	mmr.setDataSource(videoPath);
    	String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    	videoLength = Integer.parseInt(duration);
    	
		//开始播放
    	play();
    	
		//设置视频播放结束的监听事件
		vvPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() 
		{ 
			@Override
			public void onCompletion(MediaPlayer mp){
				//播放结束后的动作
				onBackPressed();
				activityCtrl.cntActivity--;
				Log.d("activity","VideoActivity --");
				working = false;
				VideoActivity.this.finish();
			}
		});
	}
	
	private void play()
	{
		vvPlayer = (VideoView)findViewById(R.id.videoPlayer);
		//用来设置要播放的mp4文件
		vvPlayer.setVideoPath(videoPath);
		//用来设置控制台样式
		vvPlayer.setMediaController(new MediaController(this));
		//用来设置起始播放位置，为0表示从开始播放
		vvPlayer.seekTo(0);
		//用来设置mp4播放器是否可以聚焦
		vvPlayer.requestFocus();
		//开始播放
		vvPlayer.start();
		//暂停播放
		//videoView.pause();
	}

	//线程：用于监听按键进行响应
	private class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(10);
					if(Parameter.uartInt == 6)
					{
						Message msg = new Message();
						msg.what = Parameter.uartInt;
						handler.sendMessage(msg);
						Parameter.uartInt = 0;
					}
				} catch (Exception e) {
					android.os.Process.killProcess(android.os.Process.myPid());  
				}
			} while (working);
		}
	}
	
	//通过句柄改变窗体
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what == 6)
			{
				//到视频结尾让他自动结束
				vvPlayer.seekTo(videoLength);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
