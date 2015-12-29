package com.luoye.wodemo;

import android.app.*;
import android.os.*;
import java.io.*;
import java.util.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.view.*;
import android.graphics.drawable.*;
import android.util.*;
import android.content.*;
import android.net.*;
import java.text.*;
public class FileListAct extends Activity
{

	ListView list;
	static List<File> dirs;
	static List<File> files;
	//缓存上层目录
	/*******************
	 *缓存有两种情况被清空
	 *1，按返回上层目录之后。
	 *2，重新载入Activity后
	 ********************/
	static List<File> dirs_cache;
	static List<File> files_cache;

	Map<String,String> map_paths_name,map_files;

	static String broadcastAction="Action";
	static String selectedFilePath=null;
	static String curpath="/mnt/sdcard";
	static String filter="";
	List<Map<String, Object>> items;
	Map<String, Object> item;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist);
		list = (ListView)findViewById(R.id.file_list);

		try
		{
			//getListFromPath(curpath);
			loadList();
		}
		catch (Exception e)
		{//错误时获取根目录
			curpath = "/";
			getListFromPath(curpath);
			loadList();
		}
		//这些事件放在载入列表之后，可以加快载入列表的速度
		//上下文菜单事件
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
				@Override
				public void onCreateContextMenu(ContextMenu p1, View p2, ContextMenu.ContextMenuInfo p3)
				{ 
					if (((AdapterContextMenuInfo)p3).position > (dirs.size()))
						p1.add(0, 0, 0, "查看图片");
				}
			});
		//设置列表单击事件
		list.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int position, long p4)
				{
					// TODO: Implement this method
					//点击返回上级时
					if (position == 0 && !curpath.equals("/"))
					{
						try
						{
							curpath = new File(curpath).getParent();
							//缓存都不为空时才能使用缓存
							if (dirs_cache != null && files_cache != null)
							{
								dirs = dirs_cache;
								files = files_cache;
								//使用后清空缓存
								dirs_cache = null;
								files_cache = null;
							}
							//没有缓存就重新获取列表
							else
							{
								getListFromPath(curpath);
							}

							loadList();
						}
						catch (Exception e)
						{
							showToast("抱歉，发生了错误！");
						}

					}

					//点击文件夹时,这里要注意数组的范围
					else if ((position == 0 && curpath.equals("/")) || (position < (dirs.size() + 1) && position > 0))
					{
						try
						{
							dirs_cache = dirs;
							files_cache = files;

							//对根目录的处理
							if (curpath.equals("/"))
							{
								curpath = "";
								curpath = curpath + "/" + dirs.get(position).getName();
							}
							else
							{
								curpath = curpath + "/" + dirs.get(position - 1).getName();
							}
							getListFromPath(curpath);
							loadList();
						}
						catch (Exception e)
						{
							//发生异常时，当前路径不变，并重新载入列表
							File f=new File(curpath);
							curpath = f.getParent();
							getListFromPath(curpath);
							loadList();
							showToast("抱歉，发生了错误！");
						}
					}

					//点击文件时
					else if (position > (dirs.size() - 1))
					{
						//if ((curpath.charAt(curpath.length() - 1) == '/'))
						//curpath = curpath.substring(0, curpath.length() - 1);
						selectedFilePath =  curpath + "/" + files.get(position - 1 - dirs.size()).getName();

						showToast("文件：" + files.get(position - 1 - dirs.size()).getName() + " 已添加到列表");
						//结束
						//FileListActivity.this.finish();
						//发送广播
						Intent intent=new Intent();
						intent.setAction(broadcastAction);
						sendBroadcast(intent);
					}

				}
			});
		//显示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//沉浸状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window window = getWindow();
			// 透明状态栏
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
							WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}
//点击Context菜单的项时发生
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo menuInfo =
			(AdapterContextMenuInfo) item.getMenuInfo();
		// TODO: Implement this method
		if (item.getItemId() == 0)
		{
			//预览
			//showToast(files.get(menuInfo.position - dirs.size()-1).getName());
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(new File(curpath + "/" + files.get(menuInfo.position - dirs.size() - 1)));
			intent.setDataAndType(uri, "image/*");
			startActivity(intent);

		}
		return super.onOptionsItemSelected(item);
	}
	//在调用该方法之前务必先调用getListFromPath方法
	public void loadList()
	{
		String tempName=null;
		//getListFromPath(curpath);
		items = new ArrayList<Map<String,Object>>(); 
		//添加返回上级目录项
		if (!curpath.equals("/"))
		{
			item = new HashMap<String, Object>();  
			item.put("iconItem", R.drawable.folder);  
			item.put("nameItem", ".."); 
			item.put("sizeItem", "");
			item.put("modifyItem", "");
			items.add(item);  
		}
		//添加文件夹项
        for (int i = 0; i < dirs.size() ; i++)
		{  
            item = new HashMap<String, Object>();  
            item.put("iconItem", R.drawable.folder);  
            item.put("nameItem", dirs.get(i).getName()); 
			item.put("sizeItem", "");
			item.put("modifyItem", getDate(dirs.get(i).lastModified()));
            items.add(item);  
        }  
		//添加文件项
		for (int i = 0; i < files.size() ; i++)
		{
			item = new HashMap<String, Object>();  
			tempName = files.get(i).getName();
			if (tempName.matches(".*.jpg|.*.png|.*.bmp|.*.gif$"))
				item.put("iconItem", R.drawable.image); 
			else if (tempName.matches(".*.apk$"))
				item.put("iconItem", R.drawable.apk); 
			else if (tempName.matches(".*.jar$"))
				item.put("iconItem", R.drawable.jar); 
			else if (tempName.matches(".*.doc|.*.docx$"))
				item.put("iconItem", R.drawable.word); 
			else if (tempName.matches(".*.xls|.*.xlsx$"))
				item.put("iconItem", R.drawable.excel);
			else if (tempName.matches("(.*.txt|.*.chm|.*.umd|.*.java|.*.lrc|.*.php|.*.cpp|.*.py|.*.js|.*.css)$"))
				item.put("iconItem", R.drawable.txt);
			else if (tempName.matches(".*.mp3|.*.amr|.*.mp4|.*.3gp|.*.flv|.*.wmv|.*.rmvb|.*.ape|.*.flac$"))
				item.put("iconItem", R.drawable.media); 
			else if (tempName.matches(".*.zip|.*.rar|.*.7z$"))
				item.put("iconItem", R.drawable.zip);
			else if (tempName.matches(".*.html|.*.htm$"))
				item.put("iconItem", R.drawable.html_icon);
			else
				item.put("iconItem", R.drawable.comic);

			item.put("nameItem", files.get(i).getName());  
			item.put("sizeItem", formetFileSize(getFileSizes(files.get(i))));
			item.put("modifyItem", getDate(files.get(i).lastModified()));
			items.add(item);  
		}


		//实例化一个适配器  
		SimpleAdapter adapter = 
			new SimpleAdapter(this, items, 
							  R.layout.filelist_item, 					  
							  new String[]{"iconItem", "nameItem","modifyItem","sizeItem"}, 
							  new int[]{R.id.filelist_icon, R.id.filelist_name,R.id.filelist_date,R.id.filelist_size});  
		list.setAdapter(adapter);
		//显示的更加人性化
		setTitle(getShortPath(curpath));
		//getActionBar().setTitle(getShortPath(curpath));
	}

	public static String getShortPath(String path)
	{
		int n=0,p=0;
		for (int i=0;i < path.length();i++)
			if (path.charAt(i) == '/')
				n++;

		if (n >= 3)
		{
			for (int i=0;i < path.length();i++)
			{
				if (path.charAt(i) == '/')
					p++;
				if (p == n - 1)
				{
					String newPath = "..." + path.substring(i, path.length());
					return newPath;
				}
			}
		}

		return path;
	}
	//载入列表到数组
	public static  void getListFromPath(String path)
	{
		files = new ArrayList<File>();
        dirs = new ArrayList<File>();

        File file = new File(path);
        File[] files_all = file.listFiles();

		//不是根目录添加返回项
		for (int i=0;i < files_all.length;i++)
		{
			//添加文件夹名
			if (files_all[i].isDirectory())
			{
				dirs.add(files_all[i]);
			}
			//添加文件名
			else if (files_all[i].isFile())
			{
				files.add(files_all[i]);
			}
		}
		//对数组进行排列
		Collections.sort(dirs, new FileNameSort());
		Collections.sort(files, new FileNameSort());
	}

	public void showToast(String text)
	{
		Toast.makeText(FileListAct.this, text, 2000).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		menu.add(0, 0, 0, "返回");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		switch (item.getItemId())
		{
			case 0:
				finish();
				break;
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);

	}
	public String getDate(long time)
	{
		//String nowStr;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date curDate = new Date(time);//获取当前时间 
		return  sdf.format(curDate);//转换时间格式

	}
	public static long getFileSizes(File f)
	{
		//取得文件大小
		long s=0;
		try
		{
			s = f.length();
		}
		catch (Exception e)
		{
		}
		return s;
	}
	
	public static  String formetFileSize(long fileS)
	{
		//转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "b";
			if (fileS == 0)
				fileSizeString = fileS + ".0b";
		}
		else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "Kb";
		}
		else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "Mb";
		}
		else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "Gb";
		}
		return fileSizeString;
	}
}
