package com.leixiaohua1020.sffmpegandroidtranscoder;

public class serial {
	
	public native int 	Open(int Port,int Rate);
	public native int 	Close();
	public native int[]	Read();
	public native int	Write(int[] buffer,int len);

}