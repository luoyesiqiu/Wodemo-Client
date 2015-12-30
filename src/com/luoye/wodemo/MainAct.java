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
import cn.bmob.v3.*;
import android.telephony.*;
import cn.bmob.v3.listener.*;

public class MainAct extends Activity implements OnTouchListener
{

	//post数据，cookie
	Map<String,String> postdata,cookies;
	String userCookie;
	String loginUrl,returnHtml,returnUrl;
	Http http;
	SharedPreferences sp;
	LoginDialog ad;
	DESCoder des;
	LinearLayout linear_upload,linear_compose,linear_getHtml,linear_about,linear_login,linear_setting,linear_browser;
	TextView tv_status,tv_userName;
	//存储站点名称的
	String[] siteName =null;
	//存储站点id的
	String[] siteId=null;
	//存储分组名称的
	String[] groupName =null;
	//存储分组id的
	String[] groupId=null;
	JSONObject sitesObj;
	public static String APP_UA="Mozilla/5.0 (Linux; U; Android 4.4.2; zh-cn; CHM-UL00 Build/HonorCHM-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		linear_login = (LinearLayout)findViewById(R.id.linear_login);
		linear_upload = (LinearLayout)findViewById(R.id.linear_upload);
		linear_compose = (LinearLayout)findViewById(R.id.linear_compose);
		linear_getHtml = (LinearLayout)findViewById(R.id.linear_getHtml);
		linear_about = (LinearLayout)findViewById(R.id.linear_about);
		linear_setting = (LinearLayout)findViewById(R.id.linear_setting);
		linear_browser = (LinearLayout)findViewById(R.id.linear_browser);
		tv_status = (TextView)findViewById(R.id.tv_loginstatus);
		tv_userName = (TextView)findViewById(R.id.tv_userName);

		linear_upload.setOnTouchListener(this);
		linear_compose.setOnTouchListener(this);
		linear_getHtml.setOnTouchListener(this);
		linear_about.setOnTouchListener(this);
		linear_login.setOnTouchListener(this);
		linear_setting.setOnTouchListener(this);
		linear_browser.setOnTouchListener(this);
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
						new Thread(loginRunnable).start();
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
					Dialog al=new AlertDialog.Builder(MainAct.this)
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
			tv_userName.setText("(" + sp.getString("curSiteName", "") + ")");
		}
		//沉浸状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window window = getWindow();
			// 透明状态栏
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
							WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		//此处可以写用户统计
		//初始化Bmob
		Bmob.initialize(this,JniBridge.getBmobKey());
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		MoUser user=new MoUser();
		user.setName(sp.getString("user","游客"));
		user.setId(tm.getDeviceId());
		user.save(this, new SaveListener(){
				@Override
				public void onSuccess()
				{
					// TODO: Implement this method
					
				}

				@Override
				public void onFailure(int p1, String p2)
				{
					// TODO: Implement this method
				}
		});
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
					String info=getSiteInfoFromHtml(returnHtml);
					
					String initGroupId = null;
					String initGroupName = null;
					String initSiteName = null;
					String initSiteId = null;
					try
					{
						JSONObject jo=new JSONObject(info);
						initGroupId=new SitesInfo(jo).getFirstGroupId();
						initGroupName=new SitesInfo(jo).getFirstGroupName();
						initSiteId=new SitesInfo(jo).getFirstSiteId();
						initSiteName=new SitesInfo(jo).getFirstSiteName();
					}
					catch (JSONException e)
					{
						//e.toString();
					}
					sp.edit().putString("siteInfo", info).commit();
					sp.edit().putString("time", curTime).commit();
					sp.edit().putString("curGroupName", initGroupName).commit();
					sp.edit().putString("curGroup", initGroupId).commit();
					sp.edit().putString("curSiteName", regex(initSiteName,"\\(([\\w_]+)\\)",1)).commit();
					sp.edit().putString("curSite", initSiteId).commit();
					showToast("登录成功，用户信息已保存");
					tv_status.setText("已登录");
					tv_userName.setText("(" + regex(initSiteName,"\\(([\\w_]+)\\)",1)+ ")");
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
	public String regex(String text, String pattern,int group)
	{
		
		Pattern p= Pattern.compile(pattern);
		Matcher m= p.matcher(text);
		if (m.find())
		{
			return m.group(1);
		}
		return "";
	}
	
	Runnable loginRunnable=new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				String[] data=http.post(loginUrl, postdata, cookies);
				returnHtml = data[1];
				userCookie = data[2];
				System.out.println(data);
			}
			catch (Exception e)
			{}
			handler.sendEmptyMessage(0);
		}

	};
	


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
					p1.setBackgroundColor(0xff00C13F);
					intent = new Intent();
					intent.setClass(MainAct.this, UpLoadAct.class);
					startActivity(intent);
					break;
					//撰文
				case R.id.linear_compose:
					p1.setBackgroundColor(0xff3399ff);
					intent = new Intent();
					intent.setClass(MainAct.this, ComposeAct.class);
					startActivity(intent);
					break;
					//代码获取
				case R.id.linear_getHtml:
					p1.setBackgroundColor(0xffFF7F24);
					intent = new Intent();
					intent.setClass(MainAct.this, GetCodeAct.class);
					startActivity(intent);
					break;
					//浏览器
				case R.id.linear_browser:
					p1.setBackgroundColor(0xffFF76BC);
					intent = new Intent();
					intent.setClass(MainAct.this, BrowserAct.class);
					startActivity(intent);
					break;
					//关于
				case R.id.linear_about:
					p1.setBackgroundColor(0xff3399ff);
					intent = new Intent(MainAct.this, PreviewMdAct.class);
					intent.putExtra("title", "帮助");
					intent.putExtra("data", JniBridge.getHelp());
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
					//设置
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
				try
				{
					sitesObj=new JSONObject(siteInfo);
					siteId = new SitesInfo(sitesObj).getAllSitesId();
					siteName=new SitesInfo(sitesObj).getAllSitesName();
				}
				catch (JSONException e)
				{}
				//查找存储的站点在总数据中的索引
				//找不到就是默认站点的索引
				int index=siteId.length - 1;
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
							sp.edit().putString("curSiteName", (siteAllName =regex( siteName[which],"\\(([\\w_]+)\\)",1))).commit();
							//修改网站后，分组改成自动分类
							sp.edit().putString("curGroup",new SitesInfo(sitesObj).getAllGroupIdBySiteId(sp.getString("curSite",""))[0] ).commit();
							sp.edit().putString("curGroupName",new SitesInfo(sitesObj).getAllGroupNameBySiteId(sp.getString("curSite",""))[0]).commit();
							tv_userName.setText("(" + sp.getString("curSiteName", "") + ")");
							
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", null).show();
			}
			else//siteInfo.equals("")
			{
				showToast("请先登录");
			}
		}
		//设置默认上传分组
		else if (view.getId() == R.id.dialog_setting_defaultGroup)
		{

			if (!siteInfo.equals(""))
			{
				try
				{
					
					sitesObj=new JSONObject(siteInfo);
					groupId = new SitesInfo(sitesObj).getAllGroupIdBySiteId(sp.getString("curSite",""));
					groupName=new SitesInfo(sitesObj).getAllGroupNameBySiteId(sp.getString("curSite",""));
					
				}
				catch (Exception e)
				{
					showToast(e.toString());
				}
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
					.setTitle("设置默认分组")
					.setSingleChoiceItems(groupName, index,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							sp.edit().putString("curGroup", groupId[which]).commit();
							sp.edit().putString("curGroupName", groupName[which]).commit();
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", null).show();
			}
			else
			{
				showToast("请先登录");//siteInfo.equals("")
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
	
	private Map<String,String> getSitesByHtml(String html)
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
	private Map<String,String> getGroupsBySiteId(String html, String siteId)
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
			Toast.makeText(MainAct.this, "再按一次返回键退出", 0).show();
		}
		else
		{
			finish();
			System.exit(0);
		}
	}
	

}
