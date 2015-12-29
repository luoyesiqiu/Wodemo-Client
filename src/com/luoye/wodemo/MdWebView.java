package com.luoye.wodemo;
import android.content.*;
import android.webkit.*;
import android.util.*;
import android.view.GestureDetector.*;
import android.view.*;
import android.widget.*;

public class MdWebView extends WebView

{
	Context context;
	
	public MdWebView(Context context)
	{
		
		super(context);
		this.context=context;
		
		
		init();
	}
	
	public MdWebView(Context context,AttributeSet attr)
	{
		super(context,attr);
		this.context=context;
		init();
	}
	public void loadData(String data)
	{
		loadData(data,"text/html;charset=UTF-8",null);
		//Toast.makeText(context,"滑动",5000).show();
	}
	
	private void init()
	{
		getSettings().setJavaScriptEnabled(true);
		getSettings().setDefaultTextEncodingName("utf-8");
	}
	
}
