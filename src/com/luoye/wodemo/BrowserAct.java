package com.luoye.wodemo;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.graphics.*;
import android.view.*;
import android.content.*;
import android.webkit.*;
import java.util.*;

public class BrowserAct extends Activity
{
	WebView mWebView;
	SharedPreferences sp;
	Map<String,String> header;
	WebSettings settings;
	private ProgressBar pb;
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
		settings.setAllowFileAccess(true);
		settings.setRenderPriority(WebSettings.RenderPriority.HIGH) ;
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setWebChromeClient(new MyWebChromeClient());
		String cookie=new DESCoder(sp.getString("time","")).ebotongDecrypto(sp.getString("cookie",""));
		header=new HashMap<String,String>();
		header.put("Cookie",cookie);
		mWebView.loadUrl("http://"+sp.getString("curSiteName","zyw8")+".wodemo.com",header);
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
			
	}
	final class MyWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading (WebView view, String url)
		{
		//	getWindow().setTitle("载入中…");
		
		pb.setVisibility(View.VISIBLE);
			view.loadUrl(url,header);
			return true;
		}
		@Override
		public void onPageFinished (WebView webview, String url)
		{
			getWindow().setTitle(webview.getTitle());
			//去掉链接下划线
			webview.loadUrl("javascript:var es=document.getElementsByTagName('a');for(i in es){es[i].style.textDecoration='none';}");
		}

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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
				/*
				 * 将actionBar的HomeButtonEnabled设为ture，
				 * 
				 * 将会执行此case
				 */
			case android.R.id.home:
				
				finish();
				break;

		}
		return super.onOptionsItemSelected(item);
	}
	
}
