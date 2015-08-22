package com.luoye.wodemo;

import android.app.Activity;
import android.os.*;
import android.content.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import java.util.*;
import java.util.regex.*;
import android.view.View.*;
import android.widget.*;
import android.view.*;
import android.util.*;
import org.jsoup.select.*;

public class GetCodeActivity extends Activity implements OnClickListener
{

	RadioButton code_rb_html,code_rb_css,code_rb_js;
	Button code_submit,code_refresh;
	EditText code_tb_code,code_url;
	Http http;
	String tempH,returnHtml;
	String url,newUrl;
	String tag;
	String cookie="lang=zh;";
	//String userCookie;
	SharedPreferences sp,sp_user;
	DESCoder des;
	private String contentType;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getcode);

		code_rb_html = (RadioButton)findViewById(R.id.code_rb_html);
		code_rb_css = (RadioButton)findViewById(R.id.code_rb_css);
		code_rb_js = (RadioButton)findViewById(R.id.code_rb_js);

		code_refresh = (Button)findViewById(R.id.code_refresh);

		code_tb_code = (EditText)findViewById(R.id.code_tb_code);
		code_url = (EditText)findViewById(R.id.code_url);

		//code_submit.setOnClickListener(this);
		code_refresh.setOnClickListener(this);

		code_rb_html.setOnClickListener(this);
		code_rb_css.setOnClickListener(this);
		code_rb_js.setOnClickListener(this);

		sp = getSharedPreferences("mo-code", MODE_PRIVATE);
		sp_user = getSharedPreferences("mo-user", MODE_PRIVATE);
		code_url.setText(sp.getString("url", "http://"));
		cookie = sp_user.getString("cookie", "lang=zh;");
		code_tb_code.setText(sp.getString("css", "没有任何内容"));
		des = new DESCoder(sp_user.getString("time", ""));
		http = new Http();
	}

	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		switch (p1.getId())
		{
				//html
			case R.id.code_rb_html:
				code_tb_code.setText(sp.getString("html", "没有任何内容"));
				break;
				//css
			case R.id.code_rb_css:
				code_tb_code.setText(sp.getString("css", "没有任何内容"));
				break;
				//javascript
			case R.id.code_rb_js:
				code_tb_code.setText(sp.getString("js", "没有任何内容"));
				break;
				//刷新
			case R.id.code_refresh:
				code_url.setText(code_url.getText().toString().replaceAll(" ",""));
				if ((!code_url.getText().toString().equals("") &&! code_url.getText().toString().equals("http://"))&&code_url.getText().toString().length()>7)
				{
					click();
				}
				else
				{
					showToast("请先输入正确的网址");
				}
				break;

		}
	}

	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method
			if (msg.what == 0)
			{
				if (returnHtml.equals(""))
				{
					showToast("抱歉，没有获取到任何内容");
					code_tb_code.setText(sp.getString(tag, "没有任何内容"));
					return;
				}
				code_tb_code.setText(returnHtml);
				sp.edit().putString(tag, returnHtml).commit();
			}
			else if (msg.what == 1)
			{
				showToast("抱歉，发生了错误");
			}
		}


	};

	private void click()
	{
		url = code_url.getText().toString();
		sp.edit().putString("url", url).commit();

		if (code_rb_css.isChecked())
		{
			tag = "css";
			contentType = "</script><link href=\"(.*site\\.css)\"";
			code_tb_code.setText("正在获取内容……");
			new Thread(new Runnable(){

					@Override
					public void run()
					{
						// TODO: Implement this method
						newUrl = fenZu(http.get(des.ebotongDecrypto(url), cookie), contentType, 1);
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									// TODO: Implement this method
									returnHtml = http.get(newUrl, des.ebotongDecrypto(cookie));
									handler.sendEmptyMessage(0);
								}


							}).start();
					}
				}).start();
		}
		else if (code_rb_js.isChecked())
		{
			tag = "js";
			contentType = "(?!http://s.wodemo.com|http://s.wodemo.net)http://.*/skin/.*/\\d*/site.js";
			code_tb_code.setText("正在获取内容……");
			//url = code_url.getText().toString();
			new Thread(new Runnable(){

					@Override
					public void run()
					{
						// TODO: Implement this method
						newUrl = fenZu(http.get(url, des.ebotongDecrypto(cookie)), contentType, 0);
						//Log.d(">>>>>>>",newUrl);
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									// TODO: Implement this method
									try
									{
										returnHtml = http.get(newUrl, des.ebotongDecrypto(cookie));
									}
									catch (Exception e)
									{
										handler.sendEmptyMessage(0);
									}
									handler.sendEmptyMessage(0);
								}


							}).start();
					}


				}).start();
		}
		else if (code_rb_html.isChecked())
		{
			tag = "html";
			code_tb_code.setText("正在获取内容……");
			//url = code_url.getText().toString();
			new Thread(new Runnable(){

					@Override
					public void run()
					{
						// TODO: Implement this method
						returnHtml = http.get(url, des.ebotongDecrypto(cookie));
						handler.sendEmptyMessage(0);
					}


				}).start();
		}
	}

	

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

	public String fenZu(String str, String pattern, int group)
	{
		String temp="";
		try
		{
			Pattern p= Pattern.compile(pattern);
			Matcher m = p.matcher(str);
			if (m.find())
			{
				temp = m.group(group);
			}

			return temp;
		}
		catch (Exception e)
		{
			return "";
		}
	}
	public void showToast(String text)
	{
		Toast.makeText(GetCodeActivity.this, text, 2000).show();
	}

}
