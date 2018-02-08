package com.leixiaohua1020.sffmpegandroidtranscoder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Environment;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.view.Window;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.os.Message;
import android.os.Handler;
import android.text.format.DateFormat;
import android.content.BroadcastReceiver;
import android.content.Context;  
import android.content.IntentFilter;
import android.hardware.Camera;

public class MainActivity extends Activity implements OnClickListener {
	private CameraPreview mPreview;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private FrameLayout preview;
	private TextView timeText;
	private TextView dianliang=null;
	private TextView saveaddr; 
	private TextView textMoshi;
	private TextView textZengyi;
	private TextView textTiaoJiao;
	private TextView textViewMenuState;
	private TextView textViewGuangZi;
	private ImageView imageMenu;
	Dialog dialog;

	//popupWindow
	ImageView btnMouse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_preview);
		
		//���ڼ���+1
		activityCtrl.cntActivity++;
		btnMouse=(ImageView)findViewById(R.id.btnMouse);
		Parameter.imageYFloat=1234;
		Parameter.inPhoto=true;
		/*һ��ʼ��ʱ��������������ʧ��*/
		ObjectAnimator y = ObjectAnimator.ofFloat(btnMouse, "y", btnMouse.getY(), Parameter.imageYFloat);
		ObjectAnimator x = ObjectAnimator.ofFloat(btnMouse, "x", btnMouse.getX(), Parameter.imageXFloat);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(x, y);
		animatorSet.start();
		
		Parameter.clickThread=true;
		
		// ��IntentFilter��ѡ����Ҫ��������Ϊ    
		/*IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);// sd�������룬���Ѿ�����    
		intentFilter.setPriority(1000);// ����������ȼ�    
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// sd�����ڣ�����û�й���    
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);// sd�����Ƴ�    
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);// sd����Ϊ USB�������洢���������ر����    
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);// sd���Ѿ���sd����۰γ������ǹ��ص㻹û���    
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);// ��ʼɨ��    
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);// ɨ�����    
		intentFilter.addDataScheme("file");    
		registerReceiver(broadcastRec, intentFilter);// ע���������   
		Log.d("MainActivity", "MainActivity oncreate");*/

		// �쳣��������Ҫ����ʱע�͵������伴�ɣ�   
		//CrashHandler crashHandler = CrashHandler.getInstance();    
		// ע��crashHandler    
		//crashHandler.init(getApplicationContext());    
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mPreview = new CameraPreview(this);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		timeText = (TextView) findViewById(R.id.mc_date);
		dianliang=(TextView)findViewById(R.id.mc_dianliang);
		saveaddr = (TextView)findViewById(R.id.saveaddr);
		textMoshi =(TextView)findViewById(R.id.mc_moshi);
		textZengyi =(TextView)findViewById(R.id.mc_zengyi);
		textTiaoJiao=(TextView)findViewById(R.id.textViewValueOfTiaoJiao);
		textViewMenuState=(TextView)findViewById(R.id.textViewMenuState);
		textViewGuangZi=(TextView)findViewById(R.id.textViewGuangZi);
		textViewGuangZi.setText("");
		imageMenu=(ImageView)findViewById(R.id.imageMenu);
		
		/*�˵����ݵĳ�ʼ��*/
		Parameter.boolMenuState=false;
		textViewMenuState.setText("");
		Log.d("ffmright","MainActivity com3 open");

		//�ж�U���Ƿ����
		if(Parameter.judgeExist("/mnt/udisk1/photo"))
		{
			Parameter.addr = 1;
		}
		else
		{
			Parameter.addr = 2;
		}
		
		//�����µ��߳�
		new TimeThread().start();
		if(Parameter.clickThread){
			new ClickThread().start();
			Parameter.clickThread=false;
		}
		Parameter.rdInfo();
	}

	/*private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {    
		@Override    
		public void onReceive(Context context, Intent intent) {    
			try{
				String action = intent.getAction();  
				if (action.equals("android.intent.action.MEDIA_MOUNTED")){    					//����
					Parameter.addr=1;
					Parameter.udiskExist=true;
					Log.d("MainActivity", "detect sd is in"+action);
				}else if(action.equals("android.intent.action.MEDIA_BAD_REMOVAL")){		//�γ�
					Parameter.addr=2;
					Parameter.udiskExist=false;
					Log.d("MainActivity", "detect sd is out"+action);
				}else if(action.equals("android.intent.action.MEDIA_REMOVAL")){				//�γ�
					Parameter.addr=2;
					Parameter.udiskExist=true;
					Log.d("MainActivity", "MainActivity detect udisk is out"+action);
				}
			}catch(Exception e){
				Log.e("MainActivity","MainActivity detect error");
				android.os.Process.killProcess(android.os.Process.myPid()); 
			}
				
				
		}    
	};   */

	class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(200);
					//�ж�U���Ƿ����
					if(Parameter.addr == 1)
					{
						if(Parameter.judgeExist("/mnt/udisk1/photo"))
						{
							Parameter.addr = 1;
						}
						else
						{
							Parameter.addr = 2;
						}
					}
					//����ˢ�½���
					Message msg = new Message();
					msg.what = 1; 						//��Ϣ(һ������ֵ)
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					android.os.Process.killProcess(android.os.Process.myPid());  
				}
			} while (true);
		}
	}
	
	/*Uart�����¼�*/
	class ClickThread extends Thread {
		@Override
		public void run() {
			int[] RX;
			serial com3=new serial();
			com3.Open(3, 9600);
			do {
				try {
					Thread.sleep(1);
					RX = com3.Read();
					
					 /*������
					  * i++;
					 if(i==10000){
						 RX = new int[1];
						 RX[0] = 9;
						 Log.d("����","09");
					 }
					 else if(i==20000)
					 {
						 RX = new int[1];
						 RX[0] = 6;
					 }
					 else
					 {
						 RX = null;
					 }*/
					if(RX!=null){
						//�����յ���ֵת��Ϊ��ֵ
						Parameter.uartInt=0;
						for(int j=0;j<RX.length;j++){
							Parameter.uartInt+=RX[j]*Math.pow(256, j);
						}
						//ͨ����ֵ��Χ�ж�ָ������
						if(Parameter.uartInt>99 && Parameter.uartInt<201){
								Parameter.intDianliang=Parameter.uartInt-100;
						}
						else if(Parameter.uartInt>=10000){
							Parameter.intGuangZi=Parameter.uartInt-10000;
						}
						else if(Parameter.inPhoto && Parameter.uartInt<99){
							Message msg = new Message();
							msg.what = 3;									//ˢ�´�����Ϣ(һ������ֵ)
							mHandler.sendMessage(msg);
						}
						Log.d("����", String.valueOf(Parameter.uartInt));
					}
					if(Parameter.boolComSend){
						com3.Write(Parameter.intComSend, 3);
						Parameter.boolComSend=false;
					}
				} 
				catch (Exception e) {
					android.os.Process.killProcess(android.os.Process.myPid());  
				}
			} while (true);
		}
	}
	
	//�����߳����洦����Ϣ������UI����
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(activityCtrl.cntActivity > 1)
			{
				return;
			}
			switch (msg.what) {
			case 1:
			{
				//����ʱ��
				long sysTime = System.currentTimeMillis();
				CharSequence sysTimeStr = DateFormat.format("yyyy/MM/dd-kk:mm:ss", sysTime);
				timeText.setText(sysTimeStr);
				dianliang.setText(Parameter.intDianliang+"%");
				textTiaoJiao.setText(String.valueOf(Parameter.intTiaoJiao));
				textZengyi.setText(String.valueOf(Parameter.zengyi));
				//�˵���ʾ����ʧ
				if(Parameter.boolMenuState)
					imageMenu.setBackgroundResource(R.drawable.menu_white);
				else
					imageMenu.setBackgroundResource(R.drawable.menu_transparent);
				//�޸Ĵ洢��ַ����ʾ
				if(Parameter.addr == 1)
				{	
					saveaddr.setText("U��");
				}
				else
				{
					saveaddr.setText("SD");
				}
				//�޸�ģʽ����ʾ
				switch(Parameter.moshi){
					case 0:
						textMoshi.setText("�ں�");
						break;
					case 1:
						textMoshi.setText("�ɼ�");
						break;
					case 2:
						textMoshi.setText("����");
						break;
					default: break;
				}
				//���¹��Ӽ���ֵ
				if(Parameter.boolShowGuangZi){
					String str="���Ӽ���"+":"+Parameter.intGuangZi;
					textViewGuangZi.setText(str);
				}else{
					textViewGuangZi.setText("");
				}
				if(!Parameter.inPhoto)
					break;
					
				ObjectAnimator y = ObjectAnimator.ofFloat(btnMouse, "y", btnMouse.getY(), Parameter.imageYFloat);
				ObjectAnimator x = ObjectAnimator.ofFloat(btnMouse, "x", btnMouse.getX(), Parameter.imageXFloat);
				AnimatorSet animatorSet = new AnimatorSet();
				animatorSet.playTogether(x, y);
				animatorSet.setDuration(0);
				animatorSet.start();
				break;
			}
			case 3:
				/*����������*/
				switch (Parameter.uartInt) {
				case 0://����
					resetMenu();
					textViewMenuState.setText("����");
					Parameter.buttonSelect=2;
					Parameter.boolMenuControlState=true;
					break;
				case 1://����
					// resetMenu();
					// Parameter.buttonSelect=5;
					try
					{
						if(Parameter.addr==1)
						{
							if(Parameter.judgeExist("/mnt/udisk1/photo"))
							{
								mPreview.takePhoto();
								textViewMenuState.setText("���ճɹ�");
								mHandler.post(controlStateRunnable);
							}
							else
							{
								textViewMenuState.setText("δ��⵽U��");
								mHandler.post(controlStateRunnable);
							}
						}
						else
						{
							if(Parameter.judgeExist("/mnt/sdcard2/photo"))
							{
								mPreview.takePhoto();
								textViewMenuState.setText("���ճɹ�");
								mHandler.post(controlStateRunnable);
								// mHandler.postDelayed(controlStateRunnable, 1000);
								Log.d("takePhoto",Parameter.strViewMenuState);
							}
							else
							{
								textViewMenuState.setText("������룺0x01");		//���洢SD����������ʱ���򱨳��������0x01
								mHandler.post(controlStateRunnable);
							}
						}
						Parameter.boolMenuControlState=false;
					}
					catch(Exception e)
					{
						android.os.Process.killProcess(android.os.Process.myPid());  
					}
					break;
				case 2://�ػ����ݲ�����
					break;
				case 3://��
					if(Parameter.boolMenuState){
						if(Parameter.imageYFloat>Parameter.menuTop){
							Parameter.buttonSelect=Parameter.buttonSelect-1;
							Parameter.imageYFloat=Parameter.imageYFloat-Parameter.moveDistance;
						}
					}else if(Parameter.boolMenuControlState){
						switch(Parameter.buttonSelect){
							case 1://����
								if(Parameter.zengyi<16){
									Parameter.zengyi=Parameter.zengyi+1;
									com3Send(1, Parameter.zengyi);
									textZengyi.setText(String.valueOf(Parameter.zengyi));
								}else{
									Parameter.zengyi=16;
									com3Send(1, Parameter.zengyi);
									textZengyi.setText(String.valueOf(Parameter.zengyi));
								}
								break;
							case 2://����
								if(Parameter.intTiaoJiao<6){
									Parameter.intTiaoJiao=Parameter.intTiaoJiao+1;
									textTiaoJiao.setText(String.valueOf(Parameter.intTiaoJiao));
									com3Send(2, Parameter.intTiaoJiao);
								}else{
									Parameter.intTiaoJiao=6;
									com3Send(2, Parameter.intTiaoJiao);
									textTiaoJiao.setText(String.valueOf(Parameter.intTiaoJiao));
								}
								break;
							case 3://ģʽ
								if(Parameter.moshi<2)Parameter.moshi=Parameter.moshi+1;
								else Parameter.moshi=0;
								com3Send(0, Parameter.moshi);
								break;
							// case 4://���Ӽ���
							// 	if(Parameter.intLevelOfGuangZi<3)Parameter.intLevelOfGuangZi=Parameter.intLevelOfGuangZi+1;
							// 	else Parameter.intLevelOfGuangZi=0;
							// 	com3Send(3, Parameter.intLevelOfGuangZi);
							// 	break;
							default:	break;
						}
					}
					break;
				case 4://�����˵�
					if(Parameter.boolMenuControlState) {
						break;
					}
					if(!Parameter.boolMenuState){
						imageMenu.bringToFront();					//��ͼƬ�������ϲ�
						Parameter.imageYFloat=Parameter.menuTop;	//��ѡ�п�ѡ�е�һ��
						Parameter.buttonSelect=1;					//��ѡ��������Ϊ��һ��
						Parameter.boolMenuState=true;				//���Ƿ��ǲ˵�״̬����Ϊ���ǡ�
					}
					break;
				case 5://ģʽ
					resetMenu();
					textViewMenuState.setText("�޸�ģʽ");
					Parameter.buttonSelect=3;
					Parameter.boolMenuControlState=true;
					break;
				case 6://����
					resetMenu();
					break;
				case 7://ȷ��
					if(!Parameter.boolMenuState) break;//�����ǰ�����ڲ˵��Ļ���������
					Parameter.boolMenuState=false;
					switch(Parameter.buttonSelect){
					case 1://����
						textViewMenuState.setText("��������");
						Parameter.boolMenuControlState=true;
						break;
					case 2://����
						textViewMenuState.setText("����");
						Parameter.boolMenuControlState=true;
						break;
					case 3://ģʽ
						textViewMenuState.setText("�޸�ģʽ");
						Parameter.boolMenuControlState=true;
						break;
					case 4://���Ӽ���
						//ͨ������ѭ��0��1��2��3
						if(Parameter.intLevelOfGuangZi<3)
						{
							Parameter.intLevelOfGuangZi=Parameter.intLevelOfGuangZi+1;
							Parameter.boolShowGuangZi=true;
						}
						else
						{
							Parameter.intLevelOfGuangZi=0;
							Parameter.boolShowGuangZi=false;
						}
						com3Send(3, Parameter.intLevelOfGuangZi);
						break;
					case 5://����
						try
						{
							if(Parameter.addr==1)
							{
								if(Parameter.judgeExist("/mnt/udisk1/photo"))
								{
									mPreview.takePhoto();
									textViewMenuState.setText("���ճɹ�");
									mHandler.post(controlStateRunnable);
								}
								else
								{
									textViewMenuState.setText("δ��⵽U��");
									mHandler.post(controlStateRunnable);
								}
							}
							else
							{
								if(Parameter.judgeExist("/mnt/sdcard2/photo"))
								{
									mPreview.takePhoto();
									textViewMenuState.setText("���ճɹ�");
									// mHandler.postDelayed(controlStateRunnable, 1000);
									mHandler.post(controlStateRunnable);
									Log.d("takePhoto",Parameter.strViewMenuState);
								}
								else
								{
									textViewMenuState.setText("������룺0x01");		//���洢SD����������ʱ���򱨳��������0x01
									mHandler.post(controlStateRunnable);
								}
							}
							Parameter.boolMenuControlState=false;
						}
						catch(Exception e)
						{
							android.os.Process.killProcess(android.os.Process.myPid());  
						}
						break;
					case 6://��Ƶ
						resetMenu();
						Parameter.boolMenuControlState=true;
						if(Parameter.addr == 1)
						{
							if(Parameter.judgeExist("/mnt/udisk1/video"))
							{
								Parameter.intLevelOfGuangZi=0;
								Parameter.boolShowGuangZi=false;
								com3Send(3, Parameter.intLevelOfGuangZi);
								mPreview.camera_release();
								Parameter.inPhoto=false;
								Parameter.uartInt=0;
								Intent intent = new Intent();
								intent.setClass(MainActivity.this,MediaRecorder_c.class);
								MainActivity.this.startActivity(intent);
								finish();
							}
							else
							{
								textViewMenuState.setText("δ��⵽U��");
								mHandler.post(controlStateRunnable);
							}
						}
						else
						{
							if(Parameter.judgeExist("/mnt/sdcard2/video"))
							{
								Parameter.intLevelOfGuangZi=0;
								Parameter.boolShowGuangZi=false;
								com3Send(3, Parameter.intLevelOfGuangZi);
								mPreview.camera_release();
								Parameter.inPhoto=false;
								Parameter.uartInt=0;
								Intent intent = new Intent();
								intent.setClass(MainActivity.this,MediaRecorder_c.class);
								MainActivity.this.startActivity(intent);
								finish();
							}
							else
							{
								textViewMenuState.setText("������룺0x01");
								mHandler.post(controlStateRunnable);
							}
						}
						break;
					case 7://һ������
						textViewMenuState.setText("��ʼ����...");
						Parameter.moveFiles();
						textViewMenuState.setText("������ɣ�");
						mHandler.post(controlStateRunnable);
						break;
					default:	break;
					}
					
					Parameter.imageYFloat=1234;
					Log.d("ffmright","click :button click :"+Parameter.uartInt+":"+Parameter.imageXFloat+":"+Parameter.imageYFloat);
					break;
				case 8://��������
					resetMenu();
					textViewMenuState.setText("��������");
					Parameter.buttonSelect=1;
					Parameter.boolMenuControlState=true;
					break;
				case 9://��Ƶ
					resetMenu();
					if(Parameter.addr == 1)
					{
						if(Parameter.judgeExist("/mnt/udisk1/video"))
						{
							Parameter.intLevelOfGuangZi=0;
							Parameter.boolShowGuangZi=false;
							com3Send(3, Parameter.intLevelOfGuangZi);
							mPreview.camera_release();
							Parameter.inPhoto=false;
							Parameter.uartInt=0;
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,MediaRecorder_c.class);
							MainActivity.this.startActivity(intent);
							finish();
						}
						else
						{
							textViewMenuState.setText("δ��⵽U��");
							mHandler.post(controlStateRunnable);
						}
					}
					else
					{
						if(Parameter.judgeExist("/mnt/sdcard2/video"))
						{
							Parameter.intLevelOfGuangZi=0;
							Parameter.boolShowGuangZi=false;
							com3Send(3, Parameter.intLevelOfGuangZi);
							mPreview.camera_release();
							Parameter.inPhoto=false;
							Parameter.uartInt=0;
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,MediaRecorder_c.class);
							MainActivity.this.startActivity(intent);
							finish();
						}
						else
						{
							textViewMenuState.setText("������룺0x01");
							mHandler.post(controlStateRunnable);
						}
					}
					break;
				case 10://����
					break;
				case 11://��
					Log.d("MainActivity","boolMenuState"+Parameter.boolMenuState+"Control"+Parameter.boolMenuControlState);
					if(Parameter.boolMenuState){
						if(Parameter.imageYFloat<Parameter.menuTop+6*Parameter.moveDistance){
							Parameter.buttonSelect=Parameter.buttonSelect+1;
							Parameter.imageYFloat=Parameter.imageYFloat+Parameter.moveDistance;
						}
					}else if(Parameter.boolMenuControlState){
						switch(Parameter.buttonSelect){
						case 1://����
							if(Parameter.zengyi>1){
								Parameter.zengyi=Parameter.zengyi-1;
								com3Send(1, Parameter.zengyi);
								textZengyi.setText(String.valueOf(Parameter.zengyi));
							}else{
								Parameter.zengyi=1;
								com3Send(1, Parameter.zengyi);
								textZengyi.setText(String.valueOf(Parameter.zengyi));
							}
							break;
						case 2://����
							if(Parameter.intTiaoJiao>1){
								Parameter.intTiaoJiao=Parameter.intTiaoJiao-1;
								com3Send(2, Parameter.intTiaoJiao);
								textTiaoJiao.setText(String.valueOf(Parameter.intTiaoJiao));
							}else{
								Parameter.intTiaoJiao=1;
								com3Send(2, Parameter.intTiaoJiao);
								textTiaoJiao.setText(String.valueOf(Parameter.intTiaoJiao));
							}
							break;
						case 3://ģʽ
							if(Parameter.moshi>0)Parameter.moshi=Parameter.moshi-1;
							else Parameter.moshi=2;
							com3Send(0, Parameter.moshi);
							break;
						// case 4://���Ӽ���
						// 	if(Parameter.intLevelOfGuangZi>0)Parameter.intLevelOfGuangZi=Parameter.intLevelOfGuangZi-1;
						// 	else Parameter.intLevelOfGuangZi=3;
						// 	com3Send(3, Parameter.intLevelOfGuangZi);
						// 	break;
						default:	break;
						}
					}
					break;
				case 12://���Ӽ���
					//ͨ������ѭ��0��1��2��3
					if(Parameter.intLevelOfGuangZi<3)
					{
						Parameter.intLevelOfGuangZi=Parameter.intLevelOfGuangZi+1;
						Parameter.boolShowGuangZi=true;
					}
					else
					{
						Parameter.intLevelOfGuangZi=0;
						Parameter.boolShowGuangZi=false;
					}
					com3Send(3, Parameter.intLevelOfGuangZi);
					// Parameter.buttonSelect=4;
					// Parameter.boolMenuControlState=true;
					break;
				case 13://�л��洢λ��
					if(Parameter.addr==1)
						Parameter.addr=2;
					else
					{
						if(Parameter.judgeExist("/mnt/udisk1/photo"))
						{
							Parameter.addr=1;
						}
						else
						{
							textViewMenuState.setText("���Ȳ���U��");
							mHandler.post(controlStateRunnable);
						}
					}
					break;
				case 14:	//�ļ�����
					//���ڼ���+1
					activityCtrl.cntActivity++;
					Intent intent = new Intent(MainActivity.this,FilesActivity.class);
					startActivity(intent);
					break;
				case 66:
					resetMenu();
					android.os.Process.killProcess(android.os.Process.myPid());   
					break;
				default:
					break;
				}
				break;
			default: break;

			}
		}
		private void resetMenu(){
			if(dialog!=null)dialog.dismiss();
			Parameter.boolMenuState=false;
			Parameter.boolMenuControlState=false;
			Parameter.boolSelectGuangZi=false;
			textViewMenuState.setText("");
			Parameter.imageYFloat=1234;
		}
		private void com3Send(int d1,int d2){
			int[] ints={d1,d2,d2+d1};
			Parameter.intComSend=ints;
			Parameter.boolComSend=true;
		}
	};
	    private Runnable controlStateRunnable = new Runnable() {
	        @Override
	        public void run() {
	            try{
	            	Thread.sleep(500);
	            	textViewMenuState.setText("");
	            }catch(Exception e){}
	            
	        }
	    };
	   
	    
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			Log.d("ffmright","Mainactivity destroy");
			super.onDestroy();
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
		
	    static {
	        System.loadLibrary("serial");
		}
	}
