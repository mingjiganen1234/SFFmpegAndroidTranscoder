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
		
		//窗口计数+1
		activityCtrl.cntActivity++;
		btnMouse=(ImageView)findViewById(R.id.btnMouse);
		Parameter.imageYFloat=1234;
		Parameter.inPhoto=true;
		/*一开始的时候把这个正方形消失掉*/
		ObjectAnimator y = ObjectAnimator.ofFloat(btnMouse, "y", btnMouse.getY(), Parameter.imageYFloat);
		ObjectAnimator x = ObjectAnimator.ofFloat(btnMouse, "x", btnMouse.getX(), Parameter.imageXFloat);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(x, y);
		animatorSet.start();
		
		Parameter.clickThread=true;
		
		// 在IntentFilter中选择你要监听的行为    
		/*IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);// sd卡被插入，且已经挂载    
		intentFilter.setPriority(1000);// 设置最高优先级    
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// sd卡存在，但还没有挂载    
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);// sd卡被移除    
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);// sd卡作为 USB大容量存储被共享，挂载被解除    
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);// sd卡已经从sd卡插槽拔出，但是挂载点还没解除    
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);// 开始扫描    
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);// 扫描完成    
		intentFilter.addDataScheme("file");    
		registerReceiver(broadcastRec, intentFilter);// 注册监听函数   
		Log.d("MainActivity", "MainActivity oncreate");*/

		// 异常处理，不需要处理时注释掉这两句即可！   
		//CrashHandler crashHandler = CrashHandler.getInstance();    
		// 注册crashHandler    
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
		
		/*菜单内容的初始化*/
		Parameter.boolMenuState=false;
		textViewMenuState.setText("");
		Log.d("ffmright","MainActivity com3 open");

		//判断U盘是否存在
		if(Parameter.judgeExist("/mnt/udisk1/photo"))
		{
			Parameter.addr = 1;
		}
		else
		{
			Parameter.addr = 2;
		}
		
		//启动新的线程
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
				if (action.equals("android.intent.action.MEDIA_MOUNTED")){    					//插入
					Parameter.addr=1;
					Parameter.udiskExist=true;
					Log.d("MainActivity", "detect sd is in"+action);
				}else if(action.equals("android.intent.action.MEDIA_BAD_REMOVAL")){		//拔出
					Parameter.addr=2;
					Parameter.udiskExist=false;
					Log.d("MainActivity", "detect sd is out"+action);
				}else if(action.equals("android.intent.action.MEDIA_REMOVAL")){				//拔出
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
					//判断U盘是否存在
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
					//持续刷新界面
					Message msg = new Message();
					msg.what = 1; 						//消息(一个整型值)
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					android.os.Process.killProcess(android.os.Process.myPid());  
				}
			} while (true);
		}
	}
	
	/*Uart触发事件*/
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
					
					 /*测试用
					  * i++;
					 if(i==10000){
						 RX = new int[1];
						 RX[0] = 9;
						 Log.d("串口","09");
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
						//将接收到的值转换为数值
						Parameter.uartInt=0;
						for(int j=0;j<RX.length;j++){
							Parameter.uartInt+=RX[j]*Math.pow(256, j);
						}
						//通过数值范围判断指令内容
						if(Parameter.uartInt>99 && Parameter.uartInt<201){
								Parameter.intDianliang=Parameter.uartInt-100;
						}
						else if(Parameter.uartInt>=10000){
							Parameter.intGuangZi=Parameter.uartInt-10000;
						}
						else if(Parameter.inPhoto && Parameter.uartInt<99){
							Message msg = new Message();
							msg.what = 3;									//刷新窗体消息(一个整型值)
							mHandler.sendMessage(msg);
						}
						Log.d("串口", String.valueOf(Parameter.uartInt));
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
	
	//在主线程里面处理消息并更新UI界面
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
				//更新时间
				long sysTime = System.currentTimeMillis();
				CharSequence sysTimeStr = DateFormat.format("yyyy/MM/dd-kk:mm:ss", sysTime);
				timeText.setText(sysTimeStr);
				dianliang.setText(Parameter.intDianliang+"%");
				textTiaoJiao.setText(String.valueOf(Parameter.intTiaoJiao));
				textZengyi.setText(String.valueOf(Parameter.zengyi));
				//菜单显示与消失
				if(Parameter.boolMenuState)
					imageMenu.setBackgroundResource(R.drawable.menu_white);
				else
					imageMenu.setBackgroundResource(R.drawable.menu_transparent);
				//修改存储地址的显示
				if(Parameter.addr == 1)
				{	
					saveaddr.setText("U盘");
				}
				else
				{
					saveaddr.setText("SD");
				}
				//修改模式的显示
				switch(Parameter.moshi){
					case 0:
						textMoshi.setText("融合");
						break;
					case 1:
						textMoshi.setText("可见");
						break;
					case 2:
						textMoshi.setText("紫外");
						break;
					default: break;
				}
				//更新光子计数值
				if(Parameter.boolShowGuangZi){
					String str="光子计数"+":"+Parameter.intGuangZi;
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
				/*处理按键请求*/
				switch (Parameter.uartInt) {
				case 0://调焦
					resetMenu();
					textViewMenuState.setText("调焦");
					Parameter.buttonSelect=2;
					Parameter.boolMenuControlState=true;
					break;
				case 1://拍照
					// resetMenu();
					// Parameter.buttonSelect=5;
					try
					{
						if(Parameter.addr==1)
						{
							if(Parameter.judgeExist("/mnt/udisk1/photo"))
							{
								mPreview.takePhoto();
								textViewMenuState.setText("拍照成功");
								mHandler.post(controlStateRunnable);
							}
							else
							{
								textViewMenuState.setText("未检测到U盘");
								mHandler.post(controlStateRunnable);
							}
						}
						else
						{
							if(Parameter.judgeExist("/mnt/sdcard2/photo"))
							{
								mPreview.takePhoto();
								textViewMenuState.setText("拍照成功");
								mHandler.post(controlStateRunnable);
								// mHandler.postDelayed(controlStateRunnable, 1000);
								Log.d("takePhoto",Parameter.strViewMenuState);
							}
							else
							{
								textViewMenuState.setText("错误代码：0x01");		//当存储SD卡出现问题时，则报出错误代码0x01
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
				case 2://关机键暂不处理
					break;
				case 3://上
					if(Parameter.boolMenuState){
						if(Parameter.imageYFloat>Parameter.menuTop){
							Parameter.buttonSelect=Parameter.buttonSelect-1;
							Parameter.imageYFloat=Parameter.imageYFloat-Parameter.moveDistance;
						}
					}else if(Parameter.boolMenuControlState){
						switch(Parameter.buttonSelect){
							case 1://增益
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
							case 2://调焦
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
							case 3://模式
								if(Parameter.moshi<2)Parameter.moshi=Parameter.moshi+1;
								else Parameter.moshi=0;
								com3Send(0, Parameter.moshi);
								break;
							// case 4://光子计数
							// 	if(Parameter.intLevelOfGuangZi<3)Parameter.intLevelOfGuangZi=Parameter.intLevelOfGuangZi+1;
							// 	else Parameter.intLevelOfGuangZi=0;
							// 	com3Send(3, Parameter.intLevelOfGuangZi);
							// 	break;
							default:	break;
						}
					}
					break;
				case 4://弹出菜单
					if(Parameter.boolMenuControlState) {
						break;
					}
					if(!Parameter.boolMenuState){
						imageMenu.bringToFront();					//将图片放在最上层
						Parameter.imageYFloat=Parameter.menuTop;	//将选中框选中第一个
						Parameter.buttonSelect=1;					//将选择编号设置为第一个
						Parameter.boolMenuState=true;				//将是否是菜单状态设置为“是”
					}
					break;
				case 5://模式
					resetMenu();
					textViewMenuState.setText("修改模式");
					Parameter.buttonSelect=3;
					Parameter.boolMenuControlState=true;
					break;
				case 6://返回
					resetMenu();
					break;
				case 7://确认
					if(!Parameter.boolMenuState) break;//如果当前不存在菜单的话跳过处理
					Parameter.boolMenuState=false;
					switch(Parameter.buttonSelect){
					case 1://增益
						textViewMenuState.setText("调节增益");
						Parameter.boolMenuControlState=true;
						break;
					case 2://调焦
						textViewMenuState.setText("调焦");
						Parameter.boolMenuControlState=true;
						break;
					case 3://模式
						textViewMenuState.setText("修改模式");
						Parameter.boolMenuControlState=true;
						break;
					case 4://光子计数
						//通过按键循环0、1、2、3
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
					case 5://拍照
						try
						{
							if(Parameter.addr==1)
							{
								if(Parameter.judgeExist("/mnt/udisk1/photo"))
								{
									mPreview.takePhoto();
									textViewMenuState.setText("拍照成功");
									mHandler.post(controlStateRunnable);
								}
								else
								{
									textViewMenuState.setText("未检测到U盘");
									mHandler.post(controlStateRunnable);
								}
							}
							else
							{
								if(Parameter.judgeExist("/mnt/sdcard2/photo"))
								{
									mPreview.takePhoto();
									textViewMenuState.setText("拍照成功");
									// mHandler.postDelayed(controlStateRunnable, 1000);
									mHandler.post(controlStateRunnable);
									Log.d("takePhoto",Parameter.strViewMenuState);
								}
								else
								{
									textViewMenuState.setText("错误代码：0x01");		//当存储SD卡出现问题时，则报出错误代码0x01
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
					case 6://视频
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
								textViewMenuState.setText("未检测到U盘");
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
								textViewMenuState.setText("错误代码：0x01");
								mHandler.post(controlStateRunnable);
							}
						}
						break;
					case 7://一键拷贝
						textViewMenuState.setText("开始拷贝...");
						Parameter.moveFiles();
						textViewMenuState.setText("拷贝完成！");
						mHandler.post(controlStateRunnable);
						break;
					default:	break;
					}
					
					Parameter.imageYFloat=1234;
					Log.d("ffmright","click :button click :"+Parameter.uartInt+":"+Parameter.imageXFloat+":"+Parameter.imageYFloat);
					break;
				case 8://调节增益
					resetMenu();
					textViewMenuState.setText("调节增益");
					Parameter.buttonSelect=1;
					Parameter.boolMenuControlState=true;
					break;
				case 9://视频
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
							textViewMenuState.setText("未检测到U盘");
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
							textViewMenuState.setText("错误代码：0x01");
							mHandler.post(controlStateRunnable);
						}
					}
					break;
				case 10://开灯
					break;
				case 11://下
					Log.d("MainActivity","boolMenuState"+Parameter.boolMenuState+"Control"+Parameter.boolMenuControlState);
					if(Parameter.boolMenuState){
						if(Parameter.imageYFloat<Parameter.menuTop+6*Parameter.moveDistance){
							Parameter.buttonSelect=Parameter.buttonSelect+1;
							Parameter.imageYFloat=Parameter.imageYFloat+Parameter.moveDistance;
						}
					}else if(Parameter.boolMenuControlState){
						switch(Parameter.buttonSelect){
						case 1://增益
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
						case 2://调焦
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
						case 3://模式
							if(Parameter.moshi>0)Parameter.moshi=Parameter.moshi-1;
							else Parameter.moshi=2;
							com3Send(0, Parameter.moshi);
							break;
						// case 4://光子计数
						// 	if(Parameter.intLevelOfGuangZi>0)Parameter.intLevelOfGuangZi=Parameter.intLevelOfGuangZi-1;
						// 	else Parameter.intLevelOfGuangZi=3;
						// 	com3Send(3, Parameter.intLevelOfGuangZi);
						// 	break;
						default:	break;
						}
					}
					break;
				case 12://光子计数
					//通过按键循环0、1、2、3
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
				case 13://切换存储位置
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
							textViewMenuState.setText("请先插入U盘");
							mHandler.post(controlStateRunnable);
						}
					}
					break;
				case 14:	//文件管理
					//窗口计数+1
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
