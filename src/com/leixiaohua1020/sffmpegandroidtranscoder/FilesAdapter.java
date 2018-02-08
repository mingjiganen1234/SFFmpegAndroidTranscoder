package com.leixiaohua1020.sffmpegandroidtranscoder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

/**
 * �Զ���������
 */
public class FilesAdapter extends BaseAdapter {

    /**
     * ������
     */
    Context context;

    /**
     * ����
     */
    File[] files;

    // �����б�����
    ArrayList<File> fileList;
    //��ǰitem��
    public int itemSelect = 0;
    //���item��
    public int itemMax = 0;


    // ���ء����� XML��ϵͳ���� XmlPullParser xpp��
    LayoutInflater layoutInflater;

    /**
     * �ļ������������췽��
     *
     * @param context   ������
     * @param files     ����
     */
    public FilesAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
        this.itemMax = files.length;
        // ��÷���ʵ��
        layoutInflater = LayoutInflater.from(context);

//        layoutInflater = (LayoutInflater) context.getSystemService(
//                Context.LAYOUT_INFLATER_SERVICE);
    }



    /**
     * �����������
     *
     * @return
     */
    @Override
    public int getCount() {
        return files.length;
    }

    /**
     * ����ض�λ�õ�����
     *
     * @param i λ��
     * @return
     */
    @Override
    public File getItem(int i) {
        return files[i];
    }

    /**
     * ����ض�λ�����ݵ� ID
     *
     * @param i λ��
     * @return  ���������ݿ��� PK(id)
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }
    
    public void setSelect(int posi)
    {
    	this.itemSelect= posi;
    	notifyDataSetChanged();
    }

    /**
     * ������ͼ��
     *
     * @param i             λ��
     * @param convertView   �ɸ��õ���ͼ������ null (��Ҫ�½�)
     * @param viewGroup     ��������ͼ
     * @return
     */
    @Override
    public View getView(
            int i,
            View convertView,
            ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            // û�пɸ��ã���Ҫ����
            // �����ܴ� �����ļ���XML ���� �ؼ��Ͳ��ֵ�
            convertView = layoutInflater.inflate(R.layout.linear_items, null);

            // ÿ����ͼ����Ҫһ�� viewHolder
            // ����ViewHolder��View convertView������
            holder = new ViewHolder(convertView);

            // ��ͼ�������������ͼ�ṹ
            convertView.setTag(holder);
        } else {
            // �и�����ͼ�����������ֱ�ӻ�ýṹ
            holder = (ViewHolder) convertView.getTag();
        }

        //�������ڱ��ѡ����
        if(itemSelect==i)
        {
        	convertView.setBackgroundResource(R.color.blue1);
        }
        else
        {
        	convertView.setBackgroundResource(R.color.white);
        }
        
        Log.d("viewHolder", holder.id + " : " + i);

        // ��������
        holder.bindData(files[i]);

        return convertView;
    }

    static int counter = 1;

    /**
     * ViewHolder ģʽ
     */
    static class ViewHolder {

        ImageView icon;
        TextView title;
        TextView info;
        TextView fileLength;
        int id;


        /**
         * ���췽��
         * �õ�View v   ͨ��findViewById��ò�������
         * @param v
         */
        public ViewHolder(View v) {
            icon = (ImageView) v.findViewById(R.id.imageView_icon);
            title = (TextView) v.findViewById(R.id.textView_name);
            info = (TextView) v.findViewById(R.id.textView_info);
            fileLength = (TextView) v.findViewById(R.id.textView_length);
            id = counter++;
        }


        public void bindData(File file) {
            icon.setImageResource(
                    file.isDirectory()
                            ? R.drawable.folder
                            : R.drawable.folder);

        	String name = file.getName();
            title.setText(name);

            if(file.isDirectory())
            {
                info.setText("�ļ���");
            }
            else
            {
            	if(name.substring(name.length()-3).equals("mp4"))
            	{
            		info.setText("MP4�ļ�");
            	}
            	else if(name.substring(name.length()-3).equals("jpg"))
            	{
            		info.setText("JPEG�ļ�");
            	}
            	else
            	{
            		info.setText("");
            	}
            }

            fileLength.setText(
                    file.isDirectory()
                            ? String.format("%d ���ļ�", file.list().length)
                            :String.format("%d B", file.length())
                             );
        }
    }
}
