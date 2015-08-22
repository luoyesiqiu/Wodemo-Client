package com.luoye.wodemo;
import org.apache.http.client.methods.*;
import org.apache.http.*;
import java.util.*;
import org.apache.http.message.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import org.apache.http.cookie.*;
import android.text.*;
import java.util.regex.*;
import org.apache.http.protocol.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import java.io.*;
import java.net.*;
import org.jsoup.Connection.*;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.*;
import org.apache.http.entity.*;
import java.nio.charset.*;

public class Http 
{
//Socket s;
	public String get(String url, String cookies)
	{
		String strResult="";
		HttpGet hg = new HttpGet(url);
		hg.addHeader("Cookie", cookies);
		HttpClient client=new DefaultHttpClient();
		try
		{
			HttpResponse response=client.execute(hg);
			if (response.getStatusLine().getStatusCode() == 200)
				strResult = EntityUtils.toString(response.getEntity(), "utf-8");
		}
		catch (Exception e)
		{}
		return strResult;
	}
	public String getCookie(Map<String,String> cookies)
	{
		StringBuilder sb=new StringBuilder();
		for (Map.Entry<String,String> cookie:cookies.entrySet())
		{
			sb.append(cookie.getKey() + "=");
			sb.append(cookie.getValue() + ";");

		}

		return sb.toString();
	}

	public  Object[] regex(String str, String pattern)
	{
		ArrayList<String> al=new ArrayList<String>();
		Pattern p= Pattern.compile(pattern);
		Matcher m= p.matcher(str);
		while (m.find())
		{
			al.add(m.group());
		}
		return al.toArray();
	}

	public  Map<String,String> getCookie(String cookies)
	{ 
		String pattern="(\\w+)\\=([^;$]+);";
		Map<String,String> mapCookie=new HashMap<String,String>();
		Pattern p= Pattern.compile(pattern);
		Matcher m= p.matcher(cookies);
		while (m.find())
		{
			mapCookie.put(m.group(1), m.group(2));
		}
		return mapCookie;
	} 

	public String[] post(String url, Map<String,String> postData, Map<String,String> cookies)
	{
		String data[]=new String[3];
		try
		{
			Connection con= Jsoup.connect(url);
			con.data(postData);
			con.cookies(cookies);
			//con.timeout(30 * 1000);

			Document doc= con.post();

			Connection.Response resp=con.response();
			Map<String,String> getCookie=resp.cookies();
			StringBuilder sb=new StringBuilder();

			for (Map.Entry<String,String> cookie:getCookie.entrySet())
			{
				sb.append(cookie.getKey() + "=");
				sb.append(cookie.getValue() + ";");

			}
			sb.append("lang=zh;");
			data[0] = doc.baseUri();
			data[1] = doc.toString();
			data[2] = sb.toString();

		}
		catch (Exception e)
		{
			data[1] = e.toString();

		}
		return data;
	}


	public synchronized String httpUpload(String url, File file, Map<String,String> params, String cookies, UploadStatus us)
	{
		String result="";
		HttpPost post = new HttpPost(url);//创建 HTTP POST 请求
		post.addHeader("Cookie", cookies);
		HttpClient client=new DefaultHttpClient();// 开启一个客户端 HTTP 请求 

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
		builder.setCharset(Charset.defaultCharset());
		builder.addBinaryBody("attachment", file);


		for (Map.Entry<String,String> p:params.entrySet())
		{
			//builder.addPart();
			builder.addTextBody(p.getKey()
								, p.getValue()
								, ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8));//设置请求参数
		}

		HttpEntity entity = builder.build();// 生成 HTTP POST 实体  	
		post.setEntity(entity);//设置请求参数
		try
		{
			HttpResponse response = client.execute(post);
			result = EntityUtils.toString(response.getEntity(), "utf-8");
			//result += "\n000000000000000000000000\n" + "\n00000000000000000000000\n";
			post.abort();

		}
		catch (Exception e)
		{
			result = e.toString();
		}
		if (result.indexOf("上传成功") != -1)
			us.uploadComplete(true, result);
		else
			us.uploadComplete(false, result);
		return result;
	}


}
