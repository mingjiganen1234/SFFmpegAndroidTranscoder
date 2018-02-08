package com.leixiaohua1020.sffmpegandroidtranscoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FilesActivity extends Activity {
	//�б�Ķ���
	ListView listView;
	//�����������Ķ���
	FilesAdapter fileAdapter;
	//��ֹ�̱߳��
	public boolean working = true;
	//ͼƬ�鿴��
	Intent intentImage;
	private String[] data = { "Apple", "Banana", "Orange", "Watermelon", "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango" };
    File[] filesData;
	
	//���ú���
	@Override
	protected void onResume()
	{
		 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 }
		 super.onResume();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//���ر���
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//����ȫ��
		setContentView(R.layout.activity_files);
		
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(FilesActivity.this, R.id.tv1, data);
//		ListView listView = (ListView) findViewById(R.id.lvFiles);
//		listView.setAdapter(adapter);

//   	 	//��ʼ��һ��Adapter
//		Teacher t = new Teacher();
//		ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, t.getAllTeachers());
//		//ͨ��ID��ȡlistView
//		ListView listView = (ListView) findViewById(R.id.lvFiles);
//		//����listView��Adapter
//		listView.setAdapter(testAdapter);

		//�����µ��߳�
		working = true;
		new TimeThread().start();
		//��ʼ���ļ������Ӵ�
		createView();
		//��Ӵ����¼�
		listView.setOnItemClickListener(new OnItemClickListener(){
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	//������Ҫ�����ݣ���תҳ�����ʾ��ϸ��Ϣ
	        	dispose(parent, view, position, id);
	        }
	    });
	}
	
	//��ʼ������
	public void createView()
	{
        filesData = new File[2];
        filesData[0] = new File("/mnt/sdcard2/video");
        filesData[1] = new File("/mnt/sdcard2/photo");
        fileAdapter = new FilesAdapter(this, filesData);
        //���б�ָ��ؼ���������
		listView = (ListView) findViewById(R.id.lvFiles);
		fileAdapter.setSelect(fileAdapter.itemSelect);
        listView.setAdapter(fileAdapter);
    }
	
	//ȷ�Ϻ���Ҫ���������
	public void dispose(AdapterView<?> parent, View view, int position, long id)
	{        
        String path = filesData[position].getPath();
        String post = path.substring(path.length()-3);
        File file = new File(path);
        //�ж����ļ��вż�����
        if(file.isDirectory())
        {
            filesData = file.listFiles();
            fileAdapter = new FilesAdapter(FilesActivity.this, filesData);
    		ListView listView = (ListView) findViewById(R.id.lvFiles);
            listView.setAdapter(fileAdapter);
        }
        else if(post.equals("jpg"))
        {
        	intentImage = new Intent(Intent.ACTION_VIEW);
            intentImage.addCategory(Intent.CATEGORY_DEFAULT);
            intentImage.setDataAndType(Uri.fromFile(file), "image/*");
            startActivity(intentImage);
            
        }
        else if(post.equals("mp4"))
        {   
        	VideoActivity.videoPath = path;
        	//����3�л�ȡ��Ƶ����ʱ������λms
        	MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        	mmr.setDataSource(VideoActivity.videoPath);
        	String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    		Intent intent = new Intent(FilesActivity.this,VideoActivity.class);
    		startActivity(intent);
        }
	}

	//�̣߳����ڼ�������������Ӧ
	private class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(10);
					if(Parameter.uartInt >0)
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
			
			switch(msg.what)
			{
				case 6://����
					switch(activityCtrl.cntActivity)
					{
						case 2://��Ŀ¼����
							activityCtrl.cntActivity--;
							Log.d("activity","DirectoryActivity --");
							working = false;
							onBackPressed();	//����
							break;
						case 3://���ļ����ڴ���
							activityCtrl.cntActivity--;
							Log.d("activity","FolderActivity --");
							createView();		//����Ŀ¼
							break;
						default:
							break;
					}
					break;
				case 7://ȷ��
					if(activityCtrl.cntActivity<4)
					{
						Log.d("activity","FilesActivity ++");
						activityCtrl.cntActivity++;
						listView.performItemClick(null, fileAdapter.itemSelect,0);
					}
					break;
				case 3://��
					if(fileAdapter.itemSelect>0)
					{
						fileAdapter.itemSelect--;
						fileAdapter.setSelect(fileAdapter.itemSelect);
					}
					break;
				case 11://��
					if(fileAdapter.itemSelect+1<fileAdapter.itemMax)
					{
						fileAdapter.itemSelect++;
						fileAdapter.setSelect(fileAdapter.itemSelect);
					}
					break;
				default:
					break;
			}
		}
	};
	
	public boolean isForeground(Context context,String className){
		if(context == null || TextUtils.isEmpty(className))
			return false;
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
		if(list!=null && list.size()>0)
		{
			ComponentName cpn = list.get(0).topActivity;
			if(className.equals(cpn.getClassName()))
				return true;
		}
		return false;
	}
    
    public class Teacher {
        public List<String> getAllTeachers() {
            List<String> teachers = new ArrayList<String>();
            teachers.add("�ź�ϼ");
            teachers.add("�½�");
            teachers.add("Ҷε");

            return teachers;
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.files, menu);
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
