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

//�������ڲ�����Ƶ�����Ž�����������
public class VideoActivity extends Activity {
	//��Ƶ����·��
	public static String videoPath;
	//����Ƶʱ��
	public int videoLength = 0;
	//��Ƶ�ؼ�
	public VideoView vvPlayer;
	//��ֹ�̱߳��
	public boolean working = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//���ر���
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//����ȫ��
		setContentView(R.layout.activity_video);
		
		//�����µ��߳�
		working = true;
		new TimeThread().start();
		
    	//����4�л�ȡ��Ƶ����ʱ������λms
    	MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    	mmr.setDataSource(videoPath);
    	String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    	videoLength = Integer.parseInt(duration);
    	
		//��ʼ����
    	play();
    	
		//������Ƶ���Ž����ļ����¼�
		vvPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() 
		{ 
			@Override
			public void onCompletion(MediaPlayer mp){
				//���Ž�����Ķ���
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
		//��������Ҫ���ŵ�mp4�ļ�
		vvPlayer.setVideoPath(videoPath);
		//�������ÿ���̨��ʽ
		vvPlayer.setMediaController(new MediaController(this));
		//����������ʼ����λ�ã�Ϊ0��ʾ�ӿ�ʼ����
		vvPlayer.seekTo(0);
		//��������mp4�������Ƿ���Ծ۽�
		vvPlayer.requestFocus();
		//��ʼ����
		vvPlayer.start();
		//��ͣ����
		//videoView.pause();
	}

	//�̣߳����ڼ�������������Ӧ
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
	
	//ͨ������ı䴰��
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what == 6)
			{
				//����Ƶ��β�����Զ�����
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
