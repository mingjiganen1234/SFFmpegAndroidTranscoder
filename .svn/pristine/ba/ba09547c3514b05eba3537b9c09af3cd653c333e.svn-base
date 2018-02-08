//test
#include <jni.h>
#include <stdio.h>
#include <media/ov5640.h>
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_leixiaohua1020_sffmpegandroidtranscoder_I2C
 * Method:    write
 * Signature: (I)V
 */
void  Java_com_leixiaohua1020_sffmpegandroidtranscoder_I2CFPGA_write
  (JNIEnv *env, jobject thiz)
  {
	  ov5640_test();
	  printf("invoke set from cycl \n");
  }

/*
 * Class:     com_leixiaohua1020_sffmpegandroidtranscoder_I2C
 * Method:    read
 * Signature: ()Ljava/lang/String;
 */
jstring Java_com_leixiaohua1020_sffmpegandroidtranscoder_I2CFPGA_read
  (JNIEnv *env, jobject thiz) {
	  printf("invoke get \n");
	  return (*env)->NewStringUTF(env,"HELLO FROM JNI!");
  }

  
  #ifdef __cplusplus
}
#endif