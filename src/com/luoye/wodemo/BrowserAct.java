package com.luoye.wodemo;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.graphics.*;
import android.view.*;
import android.content.*;
import android.webkit.*;
import java.util.*;
import android.view.View.*;
import java.lang.reflect.*;
import android.util.*;

public class BrowserAct extends Activity implements HttpResult
{
	WebView mWebView;
	SharedPreferences sp;
	
	WebSettings settings;
	private ProgressBar pb;

	private String htmlSource;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		sp=getSharedPreferences("mo-user",MODE_PRIVATE);
		mWebView=(WebView)findViewById(R.id.browserWebView1);
		pb = (ProgressBar)findViewById(R.id.browserProgressBar1);
		settings=mWebView.getSettings();
		//启用脚本
		settings.setJavaScriptEnabled(true);
		//启用缩放
		settings.setSupportZoom(true);          //支持缩放
        //settings.setBuiltInZoomControls(true);  //启用内置缩放装置
		//脚本接口
		mWebView.addJavascriptInterface(this, "local_obj");

		
		settings.setAllowFileAccess(true);
		settings.setRenderPriority(WebSettings.RenderPriority.NORMAL) ;
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setWebChromeClient(new MyWebChromeClient());
		//String cookie=new DESCoder(sp.getString("time","")).ebotongDecrypto(sp.getString("cookie",""));
		
		//synCookies(this,"http://"+sp.getString("curSiteName","zyw8")+".wodemo.com",cookie);
		new Thread(){
			public void run()
			{
				mWebView.loadUrl("http://"+sp.getString("curSiteName","zyw8")+".wodemo.com");
			}
		}.start();
		
		//showToast(cookie);
		//显示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		//沉浸状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window window = getWindow();
			// 透明状态栏
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
							WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		
		mWebView.setDownloadListener(new DownloadListener(){

				@Override
				public void onDownloadStart (String p1, String p2, String p3, String p4, long p5)
				{
					// TODO: Implement this method
				}
			});
		forceShowOverflowMenu();
	}
	public static void synCookies(Context context, String url,String cookie) {  
	    CookieSyncManager.createInstance(context);  
	    CookieManager cookieManager = CookieManager.getInstance();  
	    cookieManager.setCookie(url, cookie); 	    		
	    CookieSyncManager.getInstance().sync();  
	}
	final class MyWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading (WebView view, String url)
		{
		//	getWindow().setTitle("载入中…");
		
		pb.setVisibility(View.VISIBLE);
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onPageFinished (WebView webview, String url)
		{
			getWindow().setTitle(webview.getTitle());
			//去掉链接下划线
			webview.loadUrl("javascript:var es=document.getElementsByTagName('a');for(i in es){es[i].style.textDecoration='none';}");
			//获取网页源码接口
			webview.loadUrl("javascript:window.local_obj.getResult('<html>'+" + "document.getElementsByTagName('html')[0].innerHTML+'</html>');");
		}

	}
	@Override
	public void getResult (String html)
	{
		// TODO: Implement this method
		htmlSource=html;

	}

	
	final class MyWebChromeClient extends WebChromeClient
	{
		@Override
		public void onProgressChanged (WebView view, int newProgress)
		{
			if (newProgress == 100){
				pb.setVisibility(View.INVISIBLE);
				newProgress = 0;
				}
			pb.setProgress(newProgress);
		}
	}

	@Override
	public void onBackPressed ()
	{
		// TODO: Implement this method
		mWebView.goBack();
	}

	public void showToast(CharSequence text)
	{
		Toast.makeText(this, text, 2000).show();

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

				//刷新
			case R.id.menu_browser_refresh:
				mWebView.reload();
				break;
				//后退
			case R.id.menu_browser_back:
				mWebView.goBack();
				break;
				//停止
			case R.id.menu_browser_stop:
				mWebView.stopLoading();
				break;
				//前进
			case R.id.menu_browser_forward:
				mWebView.goForward();
				break;
				//获取源码
			case R.id.menu_browser_html:
				ScrollView sv=new ScrollView(this);
				EditText ed=new EditText(this);
				ed.setText(htmlSource);
				sv.addView(ed);
				AlertDialog al=new AlertDialog.Builder(this)
				.setTitle("网页源码")
				.setView(sv)
				.setPositiveButton("确定",null)
				.create();
				al.show();
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
		inflater.inflate(R.menu.browser_actionbar_menu, menu);
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
}
