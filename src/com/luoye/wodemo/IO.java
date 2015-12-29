package com.luoye.wodemo;
import java.io.*;
import org.tautua.markdownpapers.*;
import org.tautua.markdownpapers.parser.*;
import android.content.*;

public class IO
{
	public static String getFromAssets(Context context, String fileName)
	{ 
		StringBuilder result=new StringBuilder();
		try
		{ 
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName)); 
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line="";

			while ((line = bufReader.readLine()) != null)
				result.append(line + "\n");

		}
		catch (Exception e)
		{ 
			e.printStackTrace(); 
		}
		return result.toString();
    } 

	public static String readFile(File file)
	{
		BufferedReader bufr = null;
		StringBuilder sb = null;
		try
		{
			bufr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String temp=null;
			sb = new StringBuilder();
			for (;;)
			{
				temp = bufr.readLine() ;
				if (temp != null)
					sb.append(temp + "\n");
				else
				{
					break;
				}
			}
		}
		catch (IOException e)
		{}
		finally
		{
			try
			{
				if (bufr != null)
					bufr.close();
			}
			catch (IOException e)
			{}
		}
		return sb.toString();
	}
	public static void writeFile(File file, String data)
	{
		BufferedWriter bw=null;
		try
		{
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(data, 0, data.length());
			bw.flush();
		}
		catch (IOException e)
		{}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException e)
			{}
		}
	}
	public static String md2html(String text)
	{

		ByteArrayInputStream bis=null;
		ByteArrayOutputStream bos = null;
		InputStreamReader isr=null;
		OutputStreamWriter osw = null;
		String str=null;
		try
		{

			bis = new ByteArrayInputStream(text.getBytes());
			bos = new ByteArrayOutputStream();
			isr = new InputStreamReader(bis);
			osw = new OutputStreamWriter(bos);

			Markdown md=new Markdown();

			md.transform(isr, osw);

		}
		catch (ParseException e)
		{

		}

		try
		{
			isr.close();
		}
		catch (IOException e)
		{}
		try
		{
			osw.close();
		}
		catch (IOException e)
		{}

		str = bos.toString();


		return str;
	}
}
