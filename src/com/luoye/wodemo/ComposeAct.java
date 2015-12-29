package com.luoye.wodemo;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import android.content.*;
import java.io.*;
import org.tautua.markdownpapers.*;
import org.tautua.markdownpapers.parser.*;
import java.lang.reflect.*;
import android.util.*;
import android.text.*;
import java.util.*;
import org.jsoup.*;

public class ComposeAct extends Activity
{
    /** Called when the activity is first created. */
	ActionBar actionBar;
	EditText tb_main,tb_title;
	public static final int OPEN_FILE_CODE=1;

	String path=null;
	//标记文件是否被修改
	//boolean isModify=false;
	//final String UNOPEN_FILE_STRING="未打开文件";
	//static int theme=0;
	final static String COMPOSE_URL="http://s.wodemo.net/admin/site/compose";
	String cookie=null;
	SharedPreferences sp;
	Intent intent;

	private DESCoder des;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{

        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose);

		tb_main = (EditText)findViewById(R.id.compose_main_tb);
		tb_title = (EditText)findViewById(R.id.compose_title_tb);
		//tb_main.addTextChangedListener(mTextWatcher);
		actionBar = getActionBar();
		//actionBar.setSubtitle(UNOPEN_FILE_STRING);
		// 是否显示应用程序图标，默认为true
		actionBar.setDisplayShowHomeEnabled(true);
		// 是否显示应用程序标题，默认为true
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		forceShowOverflowMenu();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		sp = getSharedPreferences("mo-user", MODE_PRIVATE);
		des = new DESCoder(sp.getString("time", ""));
		//沉浸状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window window = getWindow();
			// 透明状态栏
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
							WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		cookie = sp.getString("cookie", "");
		if (cookie.equals(""))
		{
			showToast("未登录");
		}
		else
		{
			showToast("当前站点：" + sp.getString("curSiteName", "") + "\n" + "当前分组：" + sp.getString("curGroupName", ""));
			cookie = des.ebotongDecrypto(cookie);
			//showToast(cookie+"++++"+sp.getString("time",""));
		}
    }

	/*
	 *发布文章，7个参数
	 */
	public static void publishCompose(final Activity act, final String url, final String cookie
									  , final String title, final String content
									  , final String site_id, final String cid)
	{
		final Map<String,String> postData=new HashMap<String,String>();
		final ProgressDialog pa=ProgressDialog.show(act, "", "初始化数据中…");
		pa.setCancelable(false);
		//获取token
		new Thread(){
			public void run()
			{
				final String ret=Http.get(url, cookie);
				act.runOnUiThread(new Runnable(){
						@Override
						public void run()
						{
							// TODO: Implement this method
							//这里是UI线程
							final String token = Jsoup.parse(ret)
								.select("[name=validatetoken]")
								.first()
								.attr("value");

							if (token.equals(""))
							{
								pa.dismiss();
								Toast.makeText(act, "初始化数据失败", 2000).show();
								return;
							}
							else
							{
								pa.setMessage("文章发布中…");
								//发布文章
								new Thread(){
									public void run()
									{
										postData.put("validatetoken", token);
										postData.put("title", title);
										postData.put("contents__0", content);
										postData.put("type[0]", "text");
										postData.put("texttype__0", "markdown");
										postData.put("order__0", "0");
										postData.put("public", "on");
										postData.put("site_id", site_id);
										postData.put("cid", cid);
										final String ret=Http.multipartDataPost(COMPOSE_URL, postData, cookie);
										act.runOnUiThread(new Runnable(){
												@Override
												public void run()
												{
													// TODO: Implement this method
													//这里是UI线程
													if (ret.indexOf("创建成功") != -1)
													{
														pa.dismiss();
														AlertDialog al=new AlertDialog.Builder(act)
															.setTitle("提示")
															.setMessage("文章发布成功")
															.setPositiveButton("确定", null)
															.create();
														al.show();
														return;
													}
													else
													{
														pa.dismiss();
														Toast.makeText(act, "文章发布失败", 2000).show();
														AlertDialog al=new AlertDialog.Builder(act)
															.setTitle("提示")
															.setMessage("文章发布失败")
															.setPositiveButton("确定", null)
															.create();
														al.show();
														return;
													}
												}
											});


									}
								}.start();
							}

						}
					});
			}
		}.start();

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		// TODO: Implement this method
		switch (item.getItemId())
		{
				//返回
			case android.R.id.home:
				finish();
				break;
				//发布
			case R.id.menu_compose_publish:
				//android.R.drawable.ic_menu_share
				String title=tb_title.getText().toString();
				String content=tb_main.getText().toString();
				if (!sp.getString("cookie", "").equals(""))
				{
					publishCompose(this, COMPOSE_URL, cookie
								   , title, content
								   , sp.getString("curSite", ""), sp.getString("curGroup", "0"));
				}
				else
				{
					showToast("请先登录");
				}
				break;

				//预览
			case R.id.menu_compose_preview:

				intent = new Intent(ComposeAct.this, PreviewMdAct.class);
				intent.putExtra("title", "效果预览");
				intent.putExtra("data", tb_main.getText().toString());
				startActivity(intent);

				break;
				//帮助
			case R.id.menu_compose_help:

				intent = new Intent(ComposeAct.this, PreviewMdAct.class);
				intent.putExtra("title", "帮助");
				intent.putExtra("data", IO.md2html(IO.getFromAssets(this, "help.md")));
				startActivity(intent);

				break;

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 如果设备有物理菜单按键，需要将其屏蔽才能显示OverflowMenu
	 */
	private void forceShowOverflowMenu()
	{
		try
		{
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
				.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null)
			{
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.compose_actionbar_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		setOverflowIconVisible(featureId, menu);
		return super.onMenuOpened(featureId, menu);
	}

	/**
	 * 显示OverflowMenu的Icon
	 * 
	 * @param featureId
	 * @param menu
	 */
	private void setOverflowIconVisible(int featureId, Menu menu)
	{
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null)
		{
			if (menu.getClass().getSimpleName().equals("MenuBuilder"))
			{
				try
				{
					Method m = menu.getClass().getDeclaredMethod(
						"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				}
				catch (Exception e)
				{
					Log.d("OverflowIconVisible", e.getMessage());
				}
			}
		}
	}


	public void showToast(CharSequence text)
	{
		Toast.makeText(this, text, 2000).show();

	}


}

