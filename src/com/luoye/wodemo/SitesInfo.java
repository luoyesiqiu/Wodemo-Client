package com.luoye.wodemo;
import org.json.*;
import java.util.*;


public class SitesInfo
{
	private JSONObject text;

	public SitesInfo(JSONObject text)
	{
		this.text = text;
	}
	/*
	 *通过站点id得到站点下所有分组的名称
	 */
	public String[] getAllGroupNameBySiteId(String site_id)
	{
		//List<String> groupsName=new ArrayList<String>();
		String[] groupsName=null;
		try
		{
			//获取网站数组
			JSONArray jaSites =text.getJSONArray("sites");
			
			int index=0;

			//通过比较查找存储的站点的id
			//，进而获取该站点在站点数组中的位置
			for (int i=0;i < jaSites.length();i++)
			{
				if (site_id.equals(jaSites.getJSONObject(i).getString("siteId")))
				{
					index= i;
					break;
				}
			}
			//获取该站点的所有分组
			JSONArray jaGroups=jaSites.getJSONObject(index).getJSONArray("siteGroups");
			groupsName=new String[jaGroups.length()];
			for (int i=0;i < jaGroups.length();i++)
			{

				//注意-1，以前-1是有自动分类，现在不用
				String temp= jaGroups.getJSONObject(i).getString("groupName");
				groupsName[i]=temp;
			}
		}
		catch (Exception e)
		{
		}

		return groupsName;
	}


	/*
	 *通过站点id得到站点下所有分组的id
	 */
	public String[] getAllGroupIdBySiteId(String site_id)
	{
		//List<String> groupsName=new ArrayList<String>();
		String[] groupsId=null;
		try
		{
			//获取网站数组
			JSONArray jaSites =text.getJSONArray("sites");
			int index=0;

			//通过比较查找存储的站点的id
			//，进而获取该站点在站点数组中的位置
			for (int i=0;i < jaSites.length();i++)
			{
				if (site_id.equals(jaSites.getJSONObject(i).getString("siteId")))
				{
					index = i;
					
					break;
				}
			}
			//获取该站点的所有分组
			JSONArray jaGroups=jaSites.getJSONObject(index).getJSONArray("siteGroups");
			groupsId=new String[jaGroups.length()];
			for (int i=0;i < jaGroups.length();i++)
			{

				//注意-1，以前-1是有自动分类，现在不用
				String temp= jaGroups.getJSONObject(i).getString("groupId");
				groupsId[i]=temp;
			}
		}
		catch (Exception e)
		{
		}

		return groupsId;
	}

	/*
	 *获取所有站点id
	 */
	public String[] getAllSitesId()
	{
		//List<String> sitesId=new ArrayList<String>();
		String[] sitesId=null;
		try
		{
			JSONArray jaSites =text.getJSONArray("sites");
			sitesId=new String[jaSites.length()];
			for (int i=0;i < jaSites.length();i++)
			{
				sitesId[i]=jaSites.getJSONObject(i).getString("siteId");
			}
		}
		catch (JSONException e)
		{}

		return sitesId;
	}
	/*
	 *获取所有站点名称
	 */
	public String[] getAllSitesName()
	{
		//List<String> sitesId=new ArrayList<String>();
		String[] sitesName=null;
		try
		{
			JSONArray jaSites =text.getJSONArray("sites");
			sitesName=new String[jaSites.length()];
			for (int i=0;i < jaSites.length();i++)
			{
				sitesName[i]=jaSites.getJSONObject(i).getString("siteName");
			}
		}
		catch (JSONException e)
		{}

		return sitesName;
	}
	/*
	 *获取第一个站点id
	 */
	public String getFirstSiteId()
	{
		try
		{
			return text.getJSONArray("sites").getJSONObject(0).getString("siteId");
		}
		catch (JSONException e)
		{}
		return null;
	}
	/*
	 *获取第一个站点名称
	 */
	public String getFirstSiteName()
	{
		try
		{
			return text.getJSONArray("sites").getJSONObject(0).getString("siteName");
		}
		catch (JSONException e)
		{}
		return null;
	}
	/*
	 *获取第一个站点的第一个分组id
	 */
	public String getFirstGroupId()
	{
		try
		{
			return text.getJSONArray("sites").getJSONObject(0).getJSONArray("siteGroups").getJSONObject(0).getString("groupId");
		}
		catch (JSONException e)
		{}
		return null;
	}
	/*
	 *获取第一个站点的第一个分组id
	 */
	public String getFirstGroupName()
	{
		try
		{
			return text.getJSONArray("sites").getJSONObject(0).getJSONArray("siteGroups").getJSONObject(0).getString("groupName");
		}
		catch (JSONException e)
		{}
		return null;
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return text.toString();
	}

}
