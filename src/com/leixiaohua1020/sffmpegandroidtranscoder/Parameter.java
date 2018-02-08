package com.leixiaohua1020.sffmpegandroidtranscoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import android.hardware.Camera;
import android.text.TextUtils;

public class Parameter {
	public static Camera camera = null;
	public static int addr=2;								//视频存储介质：1-U盘，2-SD卡
	//state=1,start;state=2,stop. state=3,none
	public static int recoder_state=0;
	//
	public static int moshi=0;							//工作模式：0-融合，1-可见，2-紫外
	public static int zengyi=1;
	public static int tiaojiao=0;
	public static String videoName;
	public static int uartInt;
	public static float imageXFloat;
	public static float imageYFloat=1234;
	public static float menuTop=140;
	public static int moveDistance=25;
	public static boolean clickThread=true;
	public static int  buttonSelect=1;
	public static int  intDianliang=100;					//电池电量值
	public static int  intTiaoJiao=1;
	public static int  intGuangZi=0;
	public static int  intLevelOfGuangZi=0;						//光子计数：0->不显示，1->小框，2->中框，3->大框
	public static int[] intComSend=new int[3];
	
	public static String strViewMenuState="";
	
	public static boolean firstBoot=true;
	public static boolean showPop=true;
	public static boolean inPhoto=true;
	public static boolean boolMenuState=false;					//是否在显示菜单，只有“上”、“下”、“菜单”才会用到
	public static boolean boolMenuControlState=false;			//是否处于控制某个子功能的界面
	public static boolean boolShowGuangZi=false;				//是否开始光子计数
	public static boolean boolComSend=false;
	public static boolean boolSelectGuangZi=false;
	public static boolean takePhoto=false;
	public static boolean udiskExist = false;
	
	//删除文件夹及文件夹下内容
	public static void deleteFolderFile(String dir){
		if(!TextUtils.isEmpty(dir)){
			try{
				File file = new File(dir);
				if(file.isDirectory())	{
					File files[] = file.listFiles();
					for(int i=0;i<files.length;i++){
						deleteFolderFile(files[i].getAbsolutePath());
					}
				}
				if(!file.isDirectory()){
					file.delete();
				}else{
					if(file.listFiles().length == 0){
						//file.delete();
						;
					}
				}
			}
			catch(Exception e){}
		}
	}

	//复制文件
	public static void copyFile(String src,String des)
	{
		if(!TextUtils.isEmpty(src) && !TextUtils.isEmpty(des))
		{
			File fileSrc = new File(src);
			File fileDes = new File(des);
			if(fileSrc.exists())		//源文件存在
			{
				try
				{
					if(judgeExist("/mnt/udisk1/photo"))//U盘已插入
					{
						//先把文件路径创建好
						new File("/mnt/udisk1/video").mkdir();
						new File("/mnt/udisk1/photo").mkdir();
						//创建文件流，并通过通道的方式进行文件复制
						FileInputStream fis = new FileInputStream(fileSrc);
						FileOutputStream fos = new FileOutputStream(fileDes);
						fileDes.createNewFile();
						FileChannel fcin = fis.getChannel();
						FileChannel fcout = fos.getChannel();
						fcin.transferTo(0,fcin.size(),fcout);
						fis.close();
						fos.close();
						fcin.close();
						fcout.close();
					}
				}
				catch(Exception e){}
			}
		}
	}
	
	//移动文件
	//源是udisk
	//目的是udisk1
	public static void moveFiles()
	{
		try
		{
			File fileVideo = new File("/mnt/sdcard2/video");
			File filePhoto = new File("/mnt/sdcard2/photo");
			String pathSrc,pathDes;
			if(filePhoto.isDirectory())
			{
				File filesPhoto[] = filePhoto.listFiles();
				for(int i=0;i<filesPhoto.length;i++){
					pathSrc = filesPhoto[i].getAbsolutePath();
					pathDes = "/mnt/udisk1/photo" + pathSrc.substring(pathSrc.lastIndexOf("/"));
					copyFile(pathSrc,pathDes);
				}
				deleteFolderFile("/mnt/sdcard2/photo");
			}
			if(fileVideo.isDirectory())
			{
				File filesVideo[] = fileVideo.listFiles();
				for(int i=0;i<filesVideo.length;i++){
					pathSrc = filesVideo[i].getAbsolutePath();
					pathDes = "/mnt/udisk1/video" + pathSrc.substring(pathSrc.lastIndexOf("/"));
					copyFile(pathSrc,pathDes);
				}
				deleteFolderFile("/mnt/sdcard2/video");
			}
			new File("/mnt/sdcard2/LOST.DIR").delete();
		}
		catch(Exception e){}
	}

	//判断文件路径是否存在，最终判断U盘是否存在
	public static boolean judgeExist(String dir)
	{
			File f = new File(dir);
			if(f.exists())
				return true;
			else
				return f.mkdir();
	}

	public static void wrInfo()
	{
		try
		{
		    Calendar cal = Calendar.getInstance();
		    int month = cal.get(Calendar.MONTH) + 1;
		    int day = cal.get(Calendar.DATE);
		    int hh = cal.get(Calendar.HOUR_OF_DAY);
		    int min = cal.get(Calendar.MINUTE);
		    int sec = cal.get(Calendar.SECOND);
		    
			File fOut = new File("/mnt/sdcard/haibo_1.hex");
			fOut.createNewFile();
			byte[] buff = new byte[10];
			buff[0] = (byte)month;				//月
			buff[1] = (byte)day;					//日
			buff[2] = (byte)hh;						//时
			buff[3] = (byte)min;					//分
			buff[4] = (byte)sec;						//秒
  			buff[5] = (byte)intTiaoJiao;		//调焦
			buff[6] = (byte)zengyi;				//增益
			buff[7] = (byte)moshi;				//模式
			buff[8] = (byte)intDianliang;		//电量
			buff[9] = (byte)addr;				//存储位置
			FileOutputStream fsOut = new FileOutputStream(fOut);
			fsOut.write(buff);
			fsOut.close();
		}
		catch(Exception e){}
	}
	
	public static void rdInfo()
	{
		try
		{
		    Calendar cal = Calendar.getInstance();
		    int month = cal.get(Calendar.MONTH) + 1;
		    int day = cal.get(Calendar.DATE);
		    int hh = cal.get(Calendar.HOUR_OF_DAY);
		    int min = cal.get(Calendar.MINUTE);
			 int sec = cal.get(Calendar.SECOND);
			 int time1=0,time2=0;
			 time1=hh*3600+min*60+sec;
		    
			File fIn = new File("/mnt/sdcard/haibo_1.hex");
			if(fIn.exists())
			{
				byte[] buff = new byte[10];
				FileInputStream  fsIn = new FileInputStream (fIn);
				fsIn.read(buff,0,9);
				fsIn.close();
				time2=buff[2]*3600+buff[3]*60+buff[4];
				if(month == buff[0] && day == buff[1])
				{
					if(time1-time2>=0 && time1-time2<10)
					{
						intTiaoJiao = buff[5];			//调焦
						zengyi = buff[6];				//增益
						moshi = buff[7];				//模式
						intDianliang = buff[8];		//电量
						addr =buff[9];					//存储位置
					}
				}
			}
		}
		catch(Exception e){}
	}
}
