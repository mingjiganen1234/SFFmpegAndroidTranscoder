package com.leixiaohua1020.sffmpegandroidtranscoder;

import android.content.Context; 
import android.graphics.drawable.ColorDrawable; 
import android.view.LayoutInflater; 
import android.view.MotionEvent; 
import android.view.View; 
import android.widget.Button; 
import android.widget.PopupWindow; 

public class ShowTiaoJiao extends PopupWindow  { 
	private View view; 
	private Button btn_zengyi_add,btn_tiaojiao_dec,btn_tiaojiao_auto;
	  
	//  private Button btn_take_photo, btn_pick_photo, btn_cancel; 
	
	public ShowTiaoJiao(Context mContext, View.OnClickListener itemsOnClick) { 
  
    this.view = LayoutInflater.from(mContext).inflate(R.layout.tiaojiaomenu, null); 
    /*button*/
    btn_zengyi_add = (Button) view.findViewById(R.id.btn_tj_add);
    btn_tiaojiao_dec = (Button) view.findViewById(R.id.btn_tj_dec); 
    btn_tiaojiao_auto = (Button) view.findViewById(R.id.btn_tj_auto); 
    // 设置按钮监听 
    btn_zengyi_add.setOnClickListener(itemsOnClick); 	
    btn_tiaojiao_dec.setOnClickListener(itemsOnClick); 	
    btn_tiaojiao_auto.setOnClickListener(itemsOnClick); 	
    // 设置外部可点�? 
    this.setOutsideTouchable(true); 
    // mMenuView添加OnTouchListener监听判断获取触屏位置如果在�?�择框外面则�?毁弹出框 
    this.view.setOnTouchListener(new View.OnTouchListener() { 
  
      public boolean onTouch(View v, MotionEvent event) { 
  
        int height = view.findViewById(R.id.popwindow_layout).getTop(); 
  
        int y = (int) event.getY(); 
        if (event.getAction() == MotionEvent.ACTION_UP) { 
          if (y < height) { 
            dismiss(); 
          } 
        } 
        return true; 
      } 
    }); 
  
  
    /* 设置弹出窗口特征 */
    // 设置视图 
    this.setContentView(this.view); 
    // 设置弹出窗体的宽和高 
   this.setHeight(100); 
    this.setWidth(200); 
  
    // 设置弹出窗体可点�? 
    this.setFocusable(true); 
  
    // 实例化一个ColorDrawable颜色为半透明 
    ColorDrawable dw = new ColorDrawable(0xb0000000); 
    // 设置弹出窗体的背�? 
    this.setBackgroundDrawable(dw); 
    // 设置弹出窗体显示时的动画，从底部向上弹出 
    this.setAnimationStyle(R.style.PopMenu_anim); 
  
  } 
} 