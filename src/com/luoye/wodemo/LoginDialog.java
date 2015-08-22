package com.luoye.wodemo;
import android.app.*;
import android.content.*;
import android.view.*;
import android.app.AlertDialog.*;
import android.content.DialogInterface.*;
import android.widget.*;

public class LoginDialog extends AlertDialog
{
	static LayoutInflater layoutInflater;
	View view;
	String username,pwd;

	public LoginDialog(Context context)
	{
		super(context);
		configView(context);
	}

	public String getUserName()
	{
		return ((EditText)view.findViewById(R.id.tb_username)).getText().toString();
	}

	public String getPwd()
	{
		return ((EditText)view.findViewById(R.id.tb_pwd)).getText().toString();
	}

	public LoginDialog setUserName(String text)
	{
		((EditText)view.findViewById(R.id.tb_username)).setText(text);
		return this;
	}

	public LoginDialog setPwd(String text)
	{
		((EditText)view.findViewById(R.id.tb_pwd)).setText(text);
		return this;
	}

	private void configView(Context context)
	{
		layoutInflater = LayoutInflater.from(context);
		view = layoutInflater.inflate(R.layout.dialog, null);
		setView(view);
	}

	
	public LoginDialog setTitle(String title)
	{
		// TODO: Implement this method
		super.setTitle(title);
		return this;
	}

	
	public LoginDialog  setLoginButton(String text, DialogInterface.OnClickListener event)
	{
		this.setButton2(text, event);
		return this;
	}

	public LoginDialog  setClearButton(String text,DialogInterface.OnClickListener event)
	{
		
		this.setButton3(text,event);
		return this;
	}
	
	public LoginDialog setCancelButton(String text,DialogInterface.OnClickListener event)
	{
		this.setButton(text,event );
		return this;
	}

}
