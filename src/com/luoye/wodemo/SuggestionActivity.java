package com.luoye.wodemo;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import java.util.*;
import android.content.*;

public class SuggestionActivity extends Activity
{
	String userCookie;
	Map<String,String> postdata,cookies;
	EditText suggestion_msg;
	Button suggestion_submit;
	Http http;
	String returnHtml;
	DESCoder des;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestion);

		suggestion_msg = (EditText)findViewById(R.id.suggestion_msg);
		suggestion_submit = (Button)findViewById(R.id.suggestion_submit);
		http = new Http();
		sp = getSharedPreferences("mo-user", MODE_PRIVATE);
		des=new DESCoder(sp.getString("time",""));
		suggestion_submit.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					if (!suggestion_msg.getText().toString().equals(""))
					{
						if (!sp.getString("cookie", "").equals(""))
						{
							postdata = new HashMap<String,String>();
							postdata.put("fid",getFid());
							postdata.put("act", "file_talk");
							postdata.put("content", suggestion_msg.getText().toString()+getTail());

							showToast("提交中……");
							new Thread(new Runnable(){

									@Override
									public void run()
									{
										// TODO: Implement this method
										returnHtml = http.post(getPostUrl(), postdata, http.getCookie(des.ebotongDecrypto(sp.getString("cookie", ""))))[1];
										handler.sendEmptyMessage(0);
									}
								}).start();

						}
						else
						{
							showToast("登录后才能提交");
						}
					}
					else
					{
						showToast("提交的内容不能为空");
					}
				}
			});
		
	}
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method
			super.handleMessage(msg);
			if (msg.what == 0)
			{
				if (returnHtml.indexOf("留言板") != -1)
				{
					new AlertDialog.Builder(SuggestionActivity.this)
						.setTitle("提示")
						.setMessage("提交成功")
						.setPositiveButton("确定",null)
						.show();
				}
				else
				{
					new AlertDialog.Builder(SuggestionActivity.this)
						.setTitle("提示")
						.setMessage("提交失败")
						.setPositiveButton("确定",null)
						.show();
				}
			}
		}

	};

	public void showToast(String text)
	{
		Toast.makeText(this, text, 2000).show();
	}

	static {
		System.loadLibrary("data");
	}
	private native String getPostUrl();
	private native String getTail();
	private native String getFid();
}
