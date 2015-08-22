package com.luoye.wodemo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.*;
import android.os.*;
import android.view.View.*;
import android.view.*;
import org.jsoup.*;
import android.app.*;

public class UpLoadActivity extends Activity implements OnClickListener,Runnable,UploadStatus
{
	private DESCoder des;
	SharedPreferences sp;
	ListView list;
	Bitmap bitmapItem=null;
	ProgressDialog pa;
	Button bn_add,bn_clear,bn_upload,upload_back;
	List<Map<String, Object>> items ;
	Map<String, Object> item;
	SimpleAdapter adapter;
	//SimpleDateFormat formatter;
	String preferencePath;
	AlertDialog.Builder adlog;
	int curIndex;
	StringBuilder uploadLog;
	String token;
	String userCookie;
	final String THIS_ACTION="Action";
	Http http;
	//handler状态：0上传初始化失败,1上传初始化成功，2上传完一张图片，3上传完全部图
	final int HANDLER_MSG_UPLOAD_INIT_FAIL=0,HANDLER_MSG_UPLOAD_INIT_OK=1, HANDLER_MSG_UPLOAD_OK=2,HANDLER_MSG_UPLOAD_OK_ALL=3;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
//		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		bn_add = (Button)findViewById(R.id.bn_add2);
		bn_clear = (Button)findViewById(R.id.bn_clear2);
		bn_upload = (Button)findViewById(R.id.bn_upload);
		upload_back = (Button)findViewById(R.id.upload_back);
		list = (ListView)findViewById(R.id.list_upload_file);

		bn_add.setOnClickListener(this);
		bn_clear.setOnClickListener(this);
		bn_upload.setOnClickListener(this);
		upload_back.setOnClickListener(this);
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(THIS_ACTION);
		registerReceiver(br, intentFilter);
		FileListActivity.curpath = Environment.getExternalStorageDirectory().getAbsolutePath();
		uploadLog = new StringBuilder();
		initAdapter();
		sp = getSharedPreferences("mo-user", MODE_PRIVATE);
		userCookie = sp.getString("cookie", "lang=zh;");
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
				@Override
				public void onCreateContextMenu(ContextMenu p1, View p2, ContextMenu.ContextMenuInfo p3)
				{
					// TODO: Implement this method
					//参数：id,不懂,组
					p1.add(1, 0, 0, "修改文件信息");
					p1.add(0, 1, 1, "移除该文件");

				}
			});

		http = new Http();
		if (sp.getString("cookie", "").equals(""))
		{
			showToast("未登录");
		}
		else{
			showToast("当前站点："+sp.getString("curSiteName","")+"\n"+"当前分组："+sp.getString("curGroupName",""));
		}
    }
	
	@Override
	public void uploadComplete(boolean isSuccess, String statusText)
	{
		// TODO: Implement this method
		curIndex++;
		handler.sendEmptyMessage(HANDLER_MSG_UPLOAD_OK);
		//添加上传结果信息
		if (isSuccess)
			uploadLog.append("文件:" + items.get(curIndex - 1).get("fileName") + " 上传成功\n");
		else
			uploadLog.append("文件:" + items.get(curIndex - 1).get("fileName") + " 上传失败\n");
		//上传完一个文件
		if (curIndex < items.size())
		{
			new Thread(this).start();
		}
		//全部文件上传完成
		if (curIndex == items.size())
		{
			handler.sendEmptyMessage(HANDLER_MSG_UPLOAD_OK_ALL);
		}
	}


	//点击Context菜单的项时发生
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterContextMenuInfo menuInfo =
			(AdapterContextMenuInfo) item.getMenuInfo();
		// TODO: Implement this method
		if (item.getItemId() == 1)
		{
			//移除项
			items.remove(menuInfo.position);
			adapter.notifyDataSetChanged();
			showToast("移除成功");
		}
		else if (item.getItemId() == 0)
		{
			LayoutInflater li=LayoutInflater.from(this);
			final View view=li.inflate(R.layout.dialog_desc, null);
			final EditText dialog_tb_desc =(EditText)view.findViewById(R.id.dialog_tb_desc);
			final EditText dialog_tb_filename =(EditText)view.findViewById(R.id.dialog_tb_filename);
			final CheckBox dialog_cb =(CheckBox)view.findViewById(R.id.dialog_cb_desc);
			//如果存有，就显示
			dialog_tb_desc.setText(items.get(menuInfo.position).get("desc").toString());
			dialog_tb_filename.setText(items.get(menuInfo.position).get("newFileName").toString());
			//文件公开？
			if (items.get(menuInfo.position).get("public").equals(""))
				dialog_cb.setChecked(false);
			else
				dialog_cb.setChecked(true);

			Dialog dl=new AlertDialog.Builder(this)
				.setTitle("文件信息")
				.setView(view)
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
						items.get(menuInfo.position).put("desc", dialog_tb_desc.getText().toString());
						items.get(menuInfo.position).put("newFileName", dialog_tb_filename.getText().toString());

//						showToast(items.get(menuInfo.position).get("newFileName").toString());
						if (!dialog_cb.isChecked())
							items.get(menuInfo.position).put("public", "");
						else
							items.get(menuInfo.position).put("public", "on");
						showToast("修改成功");
					}
				})
				.create();
			dl.show();

		}
		return super.onOptionsItemSelected(item);
	}


	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method
			//handler状态：0上传初始化失败,1上传初始化成功，2上传完一个文件，3上传完全部文件
			if (msg.what == HANDLER_MSG_UPLOAD_OK_ALL)
			{
				pa.dismiss();
				adlog = new AlertDialog.Builder(UpLoadActivity.this);
				adlog.setTitle("上传结果")
					.setMessage(uploadLog.toString())
					.setNegativeButton("确定", null)
					//.setIcon(android.R.drawable.ic_menu_help)
					.show();
				uploadLog.delete(0, uploadLog.length() - 1);
				//showToast("分解完成");
			}
			else if (msg.what == HANDLER_MSG_UPLOAD_INIT_FAIL)
			{
				pa.dismiss();
				adlog = new AlertDialog.Builder(UpLoadActivity.this);
				adlog.setTitle("上传未完成")
					.setMessage("上传初始化数据失败，请重试")
					.setNegativeButton("确定", null)
					//.setIcon(android.R.drawable.ic_menu_help)
					.show();
				showToast("上传未完成");
				//uploadLog.delete(0,uploadLog.length()-1);
			}
			else if (msg.what == HANDLER_MSG_UPLOAD_INIT_OK || msg.what == HANDLER_MSG_UPLOAD_OK)
			{
				pa.setMessage(String.format("正在上传(%d/%d)……", curIndex + 1, items.size()));
			}
		}

	};

	//按钮事件*****************
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		if (p1.getId() == R.id.bn_add2)
		{

			Intent intent=new Intent();
			intent.setClass(UpLoadActivity.this, FileListActivity.class);
			startActivity(intent);
			//overridePendingTransition(R.anim.out_to_bottom, R.anim.in_from_bottom);
		}
		else if (p1.getId() == R.id.bn_clear2)
		{
			if (items.size() > 0)
			{
				adlog = new AlertDialog.Builder(this);
				adlog.setTitle("提示")
					.setMessage("确定清空列表吗？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							// TODO: Implement this method
							items.clear();
							//刷新列表
							adapter.notifyDataSetChanged();
						}
					})
					.setNegativeButton("取消", null)
					.show();
			}
		}
		else if (p1.getId() == R.id.bn_upload)
		{

			if (items.size() > 0)
			{
				if (sp.getString("cookie", "").equals(""))
				{
					showToast("请先登录");
					return;
				}
				pa = ProgressDialog.show(UpLoadActivity.this, null, "正在准备数据……");
				//让它不可返回
				pa.setCancelable(false);
				curIndex = 0;
				new Thread(new Runnable(){

						@Override
						public void run()
						{
							// TODO: Implement this method
							try
							{
								des = new DESCoder(sp.getString("time", ""));

								String returnHtml=http.get("http://s.wodemo.net/admin"
														   , des.ebotongDecrypto(userCookie));
								//获取token
								token = Jsoup.parse(returnHtml)
									.select("[name=validatetoken]")
									.first()
									.attr("value");

								handler.sendEmptyMessage(HANDLER_MSG_UPLOAD_OK);
								new Thread(UpLoadActivity.this).start();
							}
							catch (Exception e)
							{
								//异常，发送初始化失败
								handler.sendEmptyMessage(HANDLER_MSG_UPLOAD_INIT_FAIL);
							}
						}
					}).start();

			}
			else if (items.size() == 0)
			{
				showToast("请先添加文件");
			}


		}
		else if (p1.getId() == R.id.upload_back)
		{
			p1.setBackgroundColor(0xff3399ff);
			finish();
		}
	}

	//上传文件
	@Override
	public void run()
	{
		// TODO: Implement this method
		des = new DESCoder(sp.getString("time", ""));
		//在这里添加上传文件的信息
		Map<String,String> pa=new HashMap<String,String>();
		//pa.put("public", "on");
		//token
		pa.put("validatetoken", token);
		//站点id
		pa.put("site_id", sp.getString("curSite", ""));
		//分组id
		pa.put("cid", sp.getString("curGroup", "0"));
		Map<String,Object> obItem=items.get(curIndex);
		//文件描述
		pa.put("desc", obItem.get("desc").toString());
		//是否公开
		pa.put("public", obItem.get("public").toString());
		//文件名
		//if(!obItem.get("fileName").toString().equals(""))
		pa.put("filename", obItem.get("newFileName").toString());

		http.httpUpload("http://s.wodemo.net/admin/site/upload"
						, new File(items.get(curIndex).get("allPath").toString())
						, pa
						, des.ebotongDecrypto(userCookie)
						, this);
	}


	//载入文件列表
	Runnable loadFileList=new Runnable(){

		@Override
		public void run()
		{
			// TODO: Implement this method
			FileListActivity.getListFromPath(FileListActivity.curpath);
		}

	};
	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
	{
		// TODO: Implement this method
		return super.registerReceiver(receiver, filter);
	}

	//实例化一个广播接收器,收到广播说明选择了文件
	BroadcastReceiver br=new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context p1, Intent p2)
		{
			// TODO: Implement this method
			if (p2.getAction().equals(THIS_ACTION))
			{
				item = new HashMap<String, Object>();  
				String newFileName=new File(FileListActivity.selectedFilePath).getName();
				//路径，不包括文件名
				item.put("filePath", FileListActivity.curpath);
				item.put("fileName", new File(FileListActivity.selectedFilePath).getName());  
				item.put("fileSize", 
						 "(" + FileListActivity.formetFileSize(
							 FileListActivity.getFileSizes(
								 new File(FileListActivity.selectedFilePath))) 
						 + ")");
				//完整路径，包括文件名
				item.put("allPath", FileListActivity.selectedFilePath);
				item.put("desc", "");
				item.put("public", "on");
				//有后缀才获取短文件名
				if (newFileName.indexOf("") != -1)
					item.put("newFileName", newFileName.substring(0, newFileName.lastIndexOf(".")));
				else
					item.put("newFileName", newFileName);
				items.add(item);  

				adapter.notifyDataSetChanged();

				list.setSelection(items.size() - 1);
			}
		}
	};

	public void showToast(String text)
	{
		Toast.makeText(UpLoadActivity.this, text, 2000).show();
	}

	//初始化adapter
	public void initAdapter()
	{
		items = new ArrayList<Map<String,Object>>(); 
		//实例化一个适配器  
		adapter = new SimpleAdapter(UpLoadActivity.this, items,
									R.layout.upload_list_item,
									new String[]{ "fileName","fileSize","filePath"},
									new int[]{ R.id.upload_tv_filename,R.id.upload_tv_size,R.id.upload_tv_path}
									);  

		list.setAdapter(adapter);  

	}
	@Override
	protected void onResume()
	{
		//窗口被重新激活时发生
		// TODO: Implement this method
		
		//清空缓存数组
		FileListActivity.dirs_cache = null;
		FileListActivity.files_cache = null;
		//开启线程载入列表
		new Thread(loadFileList).start();
		
		super.onResume();
	} 

}
