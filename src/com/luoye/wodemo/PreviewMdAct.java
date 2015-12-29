package com.luoye.wodemo;

import android.app.*;
import android.content.*;
import android.os.*;
import android.webkit.*;
import android.view.View.*;
import android.view.GestureDetector.*;
import android.view.*;
import android.widget.*;

public class PreviewMdAct extends Activity
implements OnGestureListener,OnTouchListener
{
	private GestureDetector detector;
	MdWebView wv;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_compose);
		wv=(MdWebView)findViewById(R.id.previewmdWebView1);
		
		Intent intent=getIntent();
		String data=intent.getStringExtra("data");
		String title=intent.getStringExtra("title");
		setTitle(title);
		wv.setOnTouchListener(this);
		StringBuilder sb=new StringBuilder();
		sb.append("<html>\n<head>\n\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
		//sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"markdown.css\">\n");
		sb.append("<style type=\"text/css\">\n");
		sb.append(IO.getFromAssets(this, "markdown.css"));
		sb.append("</style>");
		sb.append("</head>\n<body>\n");
		sb.append(IO.md2html(data));
		sb.append("\n</body>\n");
		sb.append("</html>");
		wv.loadData(sb.toString());
		
		detector = new GestureDetector(this, this);
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
	
	@Override
	public boolean onDown(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void onShowPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onSingleTapUp(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent p1, MotionEvent p2, float dex, float dey)
	{
		// TODO: Implement this method
		//支持左滑关闭窗口
		if((-dex)>50&&Math.abs(dey)<Math.abs(dex))
			finish();
				
		return true;
	}

	@Override
	public void onLongPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		// TODO: Implement this method

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO: Implement this method
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		// TODO: Implement this method
		detector.onTouchEvent(p2);
		//返回假很重要
		return false;
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
