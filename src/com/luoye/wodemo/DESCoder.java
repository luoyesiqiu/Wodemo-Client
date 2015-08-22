package com.luoye.wodemo;
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream; 
import java.security.*;  
import javax.crypto.Cipher;  
import javax.crypto.CipherInputStream;  
import javax.crypto.CipherOutputStream;  
import javax.crypto.SecretKeyFactory;  
import javax.crypto.spec.DESKeySpec;
import android.util.*;  

public class DESCoder
{  
	// a weak key  
	private static String encoding = "UTF-8"; 
	// 密钥  
	private String sKey = "";


	public DESCoder(String sKey)
	{  
		this.sKey = sKey;  
	} 

	/** 
	 * 加密字符串 
	 */  
	public String ebotongEncrypto(String str)
	{  
		String result = str;  
		if (str != null && str.length( ) > 0)
		{ 
			try
			{ 
				byte[] encodeByte = symmetricEncrypto( str.getBytes( encoding ) );  
				result = new String( Base64.encode( encodeByte, Base64.DEFAULT ), encoding );

			}
			catch (Exception e)
			{
				e.printStackTrace( );  
			}  
		}  
		return result;  
	} 

	/** 
	 * 解密字符串 
	 */  
	public String ebotongDecrypto(String str)
	{  
		String result = str;  
		if (str != null && str.length( ) > 0)
		{ 
			try
			{ 
				byte[] encodeByte = Base64.decode( str, Base64.DEFAULT );  

				byte[] decoder = symmetricDecrypto( encodeByte );  
				result = new String( decoder, encoding );  
			}
			catch (Exception e)
			{
				e.printStackTrace( );  
			}  
		}  
		return result;  
	}   /** 
	 * 加密byte[] 
	 */  
	public byte[] ebotongEncrypto(byte[] str)
	{  
		byte[] result = null; 
		if (str != null && str.length > 0)
		{ 
			try
			{ 
				byte[] encodeByte = symmetricEncrypto( str );  
				result = Base64.encode( encodeByte, Base64.DEFAULT );  

			}
			catch (Exception e)
			{
				e.printStackTrace( );  
			}  
		}  
		return result;  
	} 

	/** 
	 * 解密byte[] 
	 */  
	public byte[] ebotongDecrypto(byte[] str)
	{  
		byte[] result = null; 
		if (str != null && str.length > 0)
		{ 
			try
			{ 

				byte[] encodeByte = Base64.decode( new String( str, encoding ), Base64.DEFAULT );  
				//byte[] encodeByte = base64decoder.decodeBuffer(new String(str));  
				byte[] decoder = symmetricDecrypto( encodeByte );  
				result = new String( decoder ).getBytes( encoding );  
				result = new String( decoder ).getBytes( );  
			}
			catch (Exception e)
			{
				e.printStackTrace( );  
			}  
		}  
		return result;  
	} 


	/** 
	 * 对称加密字节数组并返回 
	 * 
	 * @param byteSource 需要加密的数据 
	 * @return           经过加密的数据 
	 * @throws Exception 
	 */  
	public byte[] symmetricEncrypto(byte[] byteSource) throws Exception
	{  
		try
		{ 
			int mode = Cipher.ENCRYPT_MODE;  
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DES" ); 
			byte[] keyData = sKey.getBytes( );  
			DESKeySpec keySpec = new DESKeySpec( keyData );  
			Key key = keyFactory.generateSecret( keySpec );  
			Cipher cipher = Cipher.getInstance( "DES" ); 
			cipher.init( mode, key );

			byte[] result = cipher.doFinal( byteSource );  
			return result;  
		}
		catch (Exception e)
		{
			throw e; 
		}
		finally
		{ 
		}  
	} 

	/** 
	 * 对称解密字节数组并返回 
	 * 
	 * @param byteSource 需要解密的数据 
	 * @return           经过解密的数据 
	 * @throws Exception 
	 */  
	public byte[] symmetricDecrypto(byte[] byteSource) throws Exception
	{  
		try
		{ 
			int mode = Cipher.DECRYPT_MODE;  
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DES" ); 
			byte[] keyData = sKey.getBytes( );  
			DESKeySpec keySpec = new DESKeySpec( keyData );  
			Key key = keyFactory.generateSecret( keySpec );  
			Cipher cipher = Cipher.getInstance( "DES" ); 
			cipher.init( mode, key );
			byte[] result = cipher.doFinal( byteSource );  
			return result;  
		}
		catch (Exception e)
		{
			throw e; 
		}
		finally
		{ 

		}  
	} 
	/** 
	 * 散列算法 
	 * 
	 * @param byteSource 
	 *            需要散列计算的数据 
	 * @return 经过散列计算的数据 
	 * @throws Exception 
	 */  
	public static byte[] hashMethod(byte[] byteSource) throws Exception
	{  
		try
		{ 
			MessageDigest currentAlgorithm = MessageDigest.getInstance( "SHA-1" ); 
			currentAlgorithm.reset( );  
			currentAlgorithm.update( byteSource );  
			return currentAlgorithm.digest( );  
		}
		catch (Exception e)
		{
			throw e; 
		}  
	} 


	/** 
	 * 对文件srcFile进行加密输出到文件distFile 
	 * @param srcFile 明文文件 
	 * @param distFile 加密后的文件 
	 * @throws Exception 
	 */  
	public void EncryptFile(String srcFile, String distFile) throws Exception
	{  

		InputStream  is=null; 
		OutputStream out  = null; 
		CipherInputStream cis =null; 
		try
		{ 
			int mode = Cipher.ENCRYPT_MODE;  
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DES" ); 
			byte[] keyData = sKey.getBytes( );  
			DESKeySpec keySpec = new DESKeySpec( keyData );  
			Key key = keyFactory.generateSecret( keySpec );  
			Cipher cipher = Cipher.getInstance( "DES" ); 
			cipher.init( mode, key );
			is = new FileInputStream( srcFile );  
			out  = new FileOutputStream( distFile );  
			cis = new CipherInputStream( is, cipher );  
			byte[] buffer = new byte[1024]; 
			int r; 
			while ((r = cis.read( buffer )) > 0)
			{ 
				out.write( buffer, 0, r ); 
			}  
		}
		catch (Exception e)
		{
			throw e; 
		}
		finally
		{ 
			cis.close( );  
			is.close( );  
			out.close( );  
		}  
	} 

	/** 
	 * 解密文件srcFile到目标文件distFile 
	 * @param srcFile 密文文件 
	 * @param distFile 解密后的文件 
	 * @throws Exception 
	 */  
	public void DecryptFile(String srcFile, String distFile) throws Exception
	{  

		InputStream  is=null; 
		OutputStream out  = null; 
		CipherOutputStream cos =null; 
		try
		{ 
			int mode = Cipher.DECRYPT_MODE;  
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DES" ); 
			byte[] keyData = sKey.getBytes( );  
			DESKeySpec keySpec = new DESKeySpec( keyData );  
			Key key = keyFactory.generateSecret( keySpec );  
			Cipher cipher = Cipher.getInstance( "DES" ); 
			cipher.init( mode, key );
			byte[] buffer = new byte[1024]; 
			is = new FileInputStream( srcFile );  
			out  = new FileOutputStream( distFile );  
			cos = new CipherOutputStream( out, cipher );  

			int r; 
			while ((r = is.read( buffer )) >= 0)
			{ 
				cos.write( buffer, 0, r ); 
			}  

		}
		catch (Exception e)
		{
			throw e; 
		}
		finally
		{ 
			cos.close( );  
			is.close( );  
			out.close( );  
		}  
	} 


	/** 
	 * 对文件进行加密64位编码 
	 * @param srcFile 源文件 
	 * @param distFile 目标文件
	 */  
	public void  BASE64EncoderFile(String srcFile, String distFile)
	{  
		InputStream inputStream =null; 
		OutputStream out  = null; 
		try
		{ 
			inputStream = new FileInputStream( srcFile );  

			out  = new FileOutputStream( distFile );  
			byte[] buffer = new byte[1024]; 
			while (inputStream.read( buffer ) > 0)
			{ 
				out.write( ebotongEncrypto( buffer ) );  
			}  
		}
		catch (FileNotFoundException e)
		{  
			// TODO Auto-generated catch block  
			e.printStackTrace( );  
		}
		catch (IOException e)
		{  
			// TODO Auto-generated catch block  
			e.printStackTrace( );  
		}
		finally
		{ 
			try
			{ 
				out.close( );  
				inputStream.close( );  
			}
			catch (IOException e)
			{  
				// TODO Auto-generated catch block  
				e.printStackTrace( );  
			}  

		}  


	} 
	/** 
	 * 对文件进行解密64位解码 
	 * @param srcFile 源文件 
	 * @param distFile 目标文件
	 */  
	public void BASE64DecoderFile(String srcFile, String distFile)
	{  
		InputStream inputStream =null; 
		OutputStream out  = null; 
		try
		{ 
			inputStream = new FileInputStream( srcFile );  

			out  = new FileOutputStream( distFile );  
			byte[] buffer = new byte[1412]; 

			while (inputStream.read( buffer ) > 0)
			{ 
				out.write( ebotongDecrypto( buffer ) );  
			}  
		}
		catch (FileNotFoundException e)
		{  
			// TODO Auto-generated catch block  
			e.printStackTrace( );  
		}
		catch (IOException e)
		{  
			// TODO Auto-generated catch block  
			e.printStackTrace( );  
		}
		finally
		{ 
			try
			{ 
				out.close( );  
				inputStream.close( );  
			}
			catch (IOException e)
			{  
				// TODO Auto-generated catch block  
				e.printStackTrace( );  
			}  

		}  
	} 

} 
