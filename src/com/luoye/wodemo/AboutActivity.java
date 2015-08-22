package com.luoye.wodemo;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.graphics.*;

public class AboutActivity extends Activity
{
	TextView tv_author,tv_wodemo,tv_help;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		tv_author=(TextView)findViewById(R.id.about_tv_author);
		tv_wodemo=(TextView)findViewById(R.id.about_tv_wodemo);
		tv_help=(TextView)findViewById(R.id.about_tv_help);
		
		tv_author.setText(getAuthor());
		tv_wodemo.setText(getWodemo());
		tv_help.setText(getHelp());
	}
	static {
		System.loadLibrary("data");
	}
	
	private native String getAuthor();
	private native String getWodemo();
	private native String getHelp();
	//private native String getAuthor();
}
