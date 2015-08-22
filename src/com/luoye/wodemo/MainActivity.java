package com.luoye.wodemo;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.view.View.*;
import android.graphics.drawable.*;
import android.graphics.*;
import java.util.*;
import java.io.*;
import org.jsoup.*;
import android.util.*;
import java.net.*;
import org.jsoup.nodes.*;
import java.util.regex.*;
import org.jsoup.select.*;
import org.json.*;

public class MainActivity extends Activity implements OnTouchListener,Runnable
{

	//post数据，cookie
	Map<String,String> postdata,cookies;
	String userCookie;
	String loginUrl,returnHtml,returnUrl;
	Http http;
	SharedPreferences sp;
	LoginDialog ad;
	DESCoder des;
	LinearLayout linear_upload,linear_suggestion,linear_getHtml,linear_about,linear_login,linear_setting;
	TextView tv_status,tv_userName;
	//存储站点名称的
	String[] siteName =null;
	//存储站点id的
	String[] siteId=null;
	//存储分组名称的
	String[] groupName =null;
	//存储分组id的
	String[] groupId=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		//requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		linear_login = (LinearLayout)findViewById(R.id.linear_login);
		linear_upload = (LinearLayout)findViewById(R.id.linear_upload);
		linear_suggestion = (LinearLayout)findViewById(R.id.linear_suggestion);
		linear_getHtml = (LinearLayout)findViewById(R.id.linear_getHtml);
		linear_about = (LinearLayout)findViewById(R.id.linear_about);
		linear_setting = (LinearLayout)findViewById(R.id.linear_setting);
		tv_status = (TextView)findViewById(R.id.tv_loginstatus);
		tv_userName=(TextView)findViewById(R.id.tv_userName);
		
		linear_upload.setOnTouchListener(this);
		linear_suggestion.setOnTouchListener(this);
		linear_getHtml.setOnTouchListener(this);
		linear_about.setOnTouchListener(this);
		linear_login.setOnTouchListener(this);
		linear_setting.setOnTouchListener(this);
		ad = new LoginDialog(this);
		http = new Http();
		ad.setLoginButton("登录", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					//post数据
					if (!ad.getUserName().equals("") || !ad.getPwd().equals(""))
					{
						postdata = new  HashMap<String,String>();
						postdata.put("username", ad.getUserName());
						postdata.put("userpass", ad.getPwd());
						//cookies
						cookies = new HashMap<String,String>();
						cookies.put("lang", "zh");
						showToast("正在登录……");
						new Thread(MainActivity.this).start();
					}
					else
					{
						showToast("用户名和密码都不能为空");
						ad.show();
					}
				}
			});
		ad.setClearButton("清除账户", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					Dialog al=new AlertDialog.Builder(MainActivity.this)
						.setTitle("提示")
						.setMessage("确定要清除用户信息吗？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								ad.setUserName("").setPwd("");
								sp.edit().clear().commit();
								
								tv_status.setText("未登录");
								tv_userName.setText("");
								showToast("账户信息已全部清除");
							}
						}).setNegativeButton("取消", null)
						.create();
					al.show();

				}
			});
		ad.setCancelButton("取消", null);
		ad.setTitle("登录我的磨");

		//返回撰文界面
		loginUrl = "http://wodemo.net/login?return_to=http%3A%2F%2Fs.wodemo.net%2Fadmin%2Fsite%2Fcompose";
		//loginUrl = "http://s.wodemo.com/admin";
		sp = getSharedPreferences("mo-user", MODE_PRIVATE);
		if (!sp.getString("user", "").equals("") && !sp.getString("cookie", "").equals(""))
		{
			tv_status.setText("已登录");
			tv_userName.setText("("+sp.getString("curSiteName","")+")");
		}
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					http.get(getWodemo(), "lang=zh;");
				}
			}).start();
    }

	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method
			//String allSite = "";
			String curTime=System.currentTimeMillis() + "";
			if (msg.what == 0)
			{
				//ad.cancel();
				if (returnHtml.indexOf("上传") != -1)
				{
					//加密解密类
					des = new DESCoder(curTime);

					sp.edit().putString("user", ad.getUserName()).commit();
					//加密存储
					sp.edit().putString("pwd", des.ebotongEncrypto(ad.getPwd())).commit();
					sp.edit().putString("cookie", des.ebotongEncrypto(userCookie)).commit();
					//存储站点信息
					sp.edit().putString("siteInfo", getSiteInfoFromHtml(returnHtml)).commit();
					sp.edit().putString("time", curTime).commit();
					sp.edit().putString("curGroupName", "自动分类").commit();
					sp.edit().putString("curGroup", "0").commit();
					sp.edit().putString("curSiteName", ad.getUserName()).commit();
					sp.edit().putString("curSite", "").commit();
					showToast("登录成功，用户信息已保存");
					tv_status.setText("已登录");
					tv_userName.setText("("+ad.getUserName()+")");
				}
				else
				{
					showToast("登录失败，请检查手机是否可以联网，用户名和密码是否正确");
					showToast("登录失败，请检查手机是否可以联网，用户名和密码是否正确");
					ad.show();
				}

			}
		}
	};
	public List<String> regex(String text, String pattern)
	{
		ArrayList<String> result=new ArrayList<String>();
		Pattern p= Pattern.compile(pattern);
		Matcher m= p.matcher(text);
		while (m.find())
		{
			result.add(m.group(1));
		}
		return result;
	}

	@Override
	public void run()
	{

		try
		{
			String[] data=http.post(loginUrl, postdata, cookies);
			returnHtml = data[1];
			userCookie = data[2];
		}
		catch (Exception e)
		{}
		handler.sendEmptyMessage(0);
	}


	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		// TODO: Implement this method
		//瓷砖被按下
		if (p2.getAction() == MotionEvent.ACTION_DOWN)
		{
			p1.setBackgroundColor(0xff101010);
		}
		//瓷砖被松开
		else if (p2.getAction() == MotionEvent.ACTION_UP)
		{
			Intent intent;
			switch (p1.getId())
			{
					//多上传文件
				case R.id.linear_upload:
					p1.setBackgroundColor(0x95FF7F24);
					intent = new Intent();
					intent.setClass(MainActivity.this, UpLoadActivity.class);
					startActivity(intent);
					break;
					//意见反馈
				case R.id.linear_suggestion:
					p1.setBackgroundColor(0xff3399ff);
					intent = new Intent();
					intent.setClass(MainActivity.this, SuggestionActivity.class);
					startActivity(intent);
					break;
					//代码获取
				case R.id.linear_getHtml:
					p1.setBackgroundColor(0xffFF7F24);
					intent = new Intent();
					intent.setClass(MainActivity.this, GetCodeActivity.class);
					startActivity(intent);
					break;
					//关于
				case R.id.linear_about:
					p1.setBackgroundColor(0x953399ff);
					intent = new Intent();
					intent.setClass(MainActivity.this, AboutActivity.class);
					startActivity(intent);
					break;
					
					
					//登录
				case R.id.linear_login:
					p1.setBackgroundColor(0xff3399ff);
					des = new DESCoder(sp.getString("time", ""));
					
					ad.show();
					ad.setUserName(sp.getString("user", ""));
					//密码解密
					ad.setPwd(des.ebotongDecrypto(sp.getString("pwd", "")));
					//showToast(des.ebotongDecrypto(sp.getString("pwd", "")));
					break;
				case R.id.linear_setting:
					p1.setBackgroundColor(0xff3399ff);
					LayoutInflater li=LayoutInflater.from(this);
					View dialog= li.inflate(R.layout.dialog_setting, null);
					Dialog dl=new AlertDialog.Builder(this)
						.setTitle("设置")
						.setView(dialog)
						.setNegativeButton("返回", null)
						.create();
					dl.show();
					break;
			}
		}

		return true;
	}
	//设置按钮的单击事件
	public void onSettingClick(View view)
	{
		String siteInfo="";
		siteInfo = sp.getString("siteInfo", "");
		//设置默认站点
		if (view.getId() == R.id.dialog_setting_defaultSite)
		{

			//showToast(siteInfo);
			if (!siteInfo.equals(""))
			{
				//↓↓↓↓↓↓↓↓↓↓*********************************
				try
				{
					JSONObject jo=new JSONObject(siteInfo);
					JSONArray jaSites =jo.getJSONArray("sites");
					//Map<String,String> map=new HashMap<String,String>();
					siteName = new String[jaSites.length()];
					siteId = new String[jaSites.length()];
					for (int i=0;i < siteId.length;i++)
					{
						//map.put(jaSites.getJSONObject(i).getString("siteId"),
						//jaSites.getJSONObject(i).getString("siteName"));
						siteName[i] = jaSites.getJSONObject(i).getString("siteName");
						siteId[i] = jaSites.getJSONObject(i).getString("siteId");
					}
				}
				catch (Exception e)
				{
					//showToast(e.toString());
				}
				//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*****************************
				//查找存储的站点在总数据中的索引
				//找不到就是默认站点的索引
				int index=siteId.length-1;
				for (int i=0;i < siteId.length;i++)
				{
					if (siteId[i].equals(sp.getString("curSite", "")))
					{
						index = i;
						break;
					}

				}
				new AlertDialog.Builder(this)
					.setTitle("设置默认站点")
					.setSingleChoiceItems(siteName, index,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							sp.edit().putString("curSite", siteId[which]).commit();
							String siteAllName="";
							//写入网站名
							sp.edit().putString("curSiteName", (siteAllName=siteName[which]).substring(siteAllName.indexOf("(")+1,siteAllName.indexOf(")"))).commit();
							//修改网站后，分组改成自动分类
							sp.edit().putString("curGroup", "0").commit();
							sp.edit().putString("curGroupName", "自动分类").commit();
							tv_userName.setText("("+sp.getString("curSiteName","")+")");
							//showToast("已设置，" + siteName[which] + " 为默认站点");
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", null).show();
			}
			else
			{
				showToast("请先登录");
			}
		}
		//设置默认上传分组
		else if (view.getId() == R.id.dialog_setting_defaultGroup)
		{

			if (!siteInfo.equals(""))
			{

				//↓↓↓↓↓↓↓↓↓↓↓↓↓↓*********************************
				try
				{
					//showToast("不为空");
					JSONObject jo=new JSONObject(siteInfo);
					//获取网站数组
					JSONArray jaSites =jo.getJSONArray("sites");
					//Map<String,String> map=new HashMap<String,String>();

					int indexSite=0;
					String tempCurSite=sp.getString("curSite", "");
					//通过比较查找存储的站点的id
					//，进而获取该站点在站点数组中的位置
					//，进而获取该站点的信息
					for (int i=0;i < jaSites.length();i++)
					{
						if (tempCurSite.equals(jaSites.getJSONObject(i).getString("siteId")))
						{
							indexSite = i;
							break;
						}
					}
					//获取该站点的所有分组
					JSONArray jaGroups=jaSites.getJSONObject(indexSite).getJSONArray("siteGroups");
					groupName = new String[jaGroups.length() + 1];
					groupId = new String[jaGroups.length() + 1];
					groupName[0] = "自动分类";
					groupId[0] = "0";
					for (int i=1;i < groupId.length;i++)
					{
						//map.put(jaSites.getJSONObject(i).getString("siteId"),
						//jaSites.getJSONObject(i).getString("siteName"));
						//注意-1
						groupName[i] = jaGroups.getJSONObject(i - 1).getString("groupName");
						groupId[i] = jaGroups.getJSONObject(i - 1).getString("groupId");
					}
				}
				catch (Exception e)
				{
					showToast(e.toString());
				}
				//↑↑↑↑↑↑↑↑↑↑↑↑↑*****************************
				//查找存储的分组在总数据中的索引
				int index=0;
				String tempCurGroup=sp.getString("curGroup", "");
				for (int i=0;i < groupId.length;i++)
				{
					if (groupId[i].equals(tempCurGroup))
					{
						index = i;
						break;
					}

				}
				new AlertDialog.Builder(this)
					.setTitle("设置上传默认分组")
					.setSingleChoiceItems(groupName, index,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							sp.edit().putString("curGroup", groupId[which]).commit();
							sp.edit().putString("curGroupName", groupName[which]).commit();
							//showToast("已设置，" + siteName[which] + " 为默认站点");
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", null).show();
			}
			else
			{
				showToast("请先登录");
			}
		}
	}

	public String getSiteInfoFromHtml(String text)
	{
		//把站点数据(站点信息，组信息)变成json
		JSONObject objMain=new JSONObject();
		JSONArray jaSite =new JSONArray();
		JSONArray jaGroup=null; //=new JSONArray();
		JSONObject site = null;//=new JSONObject();
		JSONObject group = null;//=new JSONObject();
		Map<String,String> sites=getSitesByHtml(text);
		try
		{
			for (Map.Entry<String,String> siteMap:sites.entrySet())
			{
				//放这里就显得很重要
				jaGroup = new JSONArray();
				for (Map.Entry<String,String> groupMap:getGroupsBySiteId(text, siteMap.getKey()).entrySet())
				{

					group = new JSONObject();
					group.put("groupName", groupMap.getValue());
					group.put("groupId", groupMap.getKey());

					jaGroup.put(group);
					//System.out.println(groupMap.getValue());

				}

				site = new JSONObject();
				site.put("siteName", siteMap.getValue());
				site.put("siteId", siteMap.getKey());
				site.put("siteGroups", jaGroup);
				//jaSite=new JSONArray();
				jaSite.put(site);
			}
			objMain.put("sites", jaSite);
			//System.out.println(objMain.toString());
		}
		catch (JSONException e)
		{}
		return objMain.toString();
	}
	public Map<String,String> getSitesByHtml(String html)
	{
		//获取我的磨的站点的id和名称
		Map<String,String> all=new HashMap<String,String>();
		try
		{
			Document doc=Jsoup.parse(html, "utf-8");
			Elements es= doc.getElementById("compose-site").children();
			for (Element e:es)
			{
				all.put(e.val(), e.text());
			}
		}
		catch (Exception e)
		{
		}
		return all;
	}
	public Map<String,String> getGroupsBySiteId(String html, String siteId)
	{
		//获取我的磨的站点的分组id和名称
		Map<String,String> all=new HashMap<String,String>();
		try
		{
			Document doc=Jsoup.parse(html, "utf-8");
			Elements es = doc.getElementById("site-category-" + siteId)
				.getElementsByTag("option");

			for (Element e:es)
			{
				all.put(e.val(), e.text());
			}
		}
		catch (Exception e)
		{}
		return all;
	}


	public void showToast(String text)
	{
		Toast.makeText(this, text, 2000).show();
	}


	long cur=0;
	@Override
	public void onBackPressed()
	{
		if (System.currentTimeMillis() - cur > 2000)
		{
			cur = System.currentTimeMillis();
			Toast.makeText(MainActivity.this, "再按一次返回键退出", 0).show();
		}
		else
		{
			finish();
			System.exit(0);
		}
	}
	static {
		System.loadLibrary("data");
	}
	private native String getWodemo();

}
