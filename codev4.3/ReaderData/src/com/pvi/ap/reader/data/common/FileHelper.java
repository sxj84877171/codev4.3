package com.pvi.ap.reader.data.common;

import java.io.*;
import java.util.zip.*;

/**
 * This Class is used for file operations, such as create,delete,copy,cut,zip,unzip and others.
 * @author rd032
 * (C) Copyright 2010-2013, by www.pvi.com.tw.
 */
public class FileHelper
{
	/**
	 * Copy File
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	private static void copy(File source, File target) throws IOException 
	  {
		  File tar = new File(target, source.getName());
		  if (source.isDirectory()) 
		  {
			  tar.mkdir();
			  File[] fs = source.listFiles();
			  for (int i = 0; i < fs.length; i++) 
			  {
				  copy(fs[i], tar);
			  }
		  } 
		  else 
		  {
			  InputStream is = new FileInputStream(source);
			  OutputStream os = new FileOutputStream(tar);
			  byte[] buf = new byte[1024];
			  int len = 0;
			  while ((len = is.read(buf)) != -1) 
			  {
				  os.write(buf, 0, len);
			  }
			  is.close();
			  os.close();
		  }
	  }

	 /**
	  * Copy File
	  * @param source
	  * @param target
	  */
	  public static void copy(String source, String target)
	  {
		  File sour = new File(source);
		  File tar = new File(target);

		  try 
		  {
			  copy(sour, tar);
		  } 
		  catch (IOException e) 
		  {
			  e.printStackTrace();
		  }
	  }
	  /**
	   * delete file
	   * @param file
	   */
	  private static void delete(File file) 
	  {
		  if(file.isDirectory())
		  {
			  File[] fs = file.listFiles();
			  for (int i = 0; i < fs.length; i++) 
			  {
				  delete(fs[i]);
			  }
			  file.delete();
		  } 
		  else 
		  {
			  file.delete();
		  }
	  }
	  
	  /**
	   * delete file
	   * @param path
	   */
	  public static void delete(String path) 
	  {
		  File file = new File(path);
		  delete(file);
	  }

	  /**
	   * Zip File
	   * @param zipFileName
	   * @param inputPath
	   */
	  public static void zip(String zipFileName, String inputPath) 
	  {
		  File inputFile = new File(inputPath);
		  ZipOutputStream out;
		  try 
		  {
			  out = new ZipOutputStream(new FileOutputStream(zipFileName));
			  zip(out, inputFile, inputFile.getName());
			  out.close();
		  } 
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
	  }

	 
	  /**
	   * Zip File
	   * @param out
	   * @param f
	   * @param base
	   * @throws Exception
	   */
	  private static void zip(ZipOutputStream out, File f, String base)
	  		throws Exception 
	  {
		  if (f.isDirectory()) 
		  {
			  File[] fs = f.listFiles();
			  base += "/";
			  out.putNextEntry(new ZipEntry(base)); 
			  for (int i = 0; i < fs.length; i++) 
			  {
				  zip(out, fs[i], base + fs[i].getName());
			  }
		  	} 
		  else 
		  {
			  out.putNextEntry(new ZipEntry(base));
			  InputStream is = new FileInputStream(f);
			  byte[] buf = new byte[1024];
			  int len = 0;
			  while ((len = is.read(buf)) != -1) 
			  {
				  out.write(buf, 0, len);
			  }
			  is.close();
		  }
	  }

	  /**
	   * UnZip File
	   * @param zipFile
	   * @param desPath
	   */
	  public static void unzip(String zipFile, String desPath) 
	  {
		  OutputStream out = null;
		  ZipInputStream is;
		  try
		  {
			  is = new ZipInputStream(new FileInputStream(zipFile));
			  ZipEntry entry = null;
			  while ((entry = is.getNextEntry()) != null) 
			  {
				  File f = new File(desPath + "\\" + entry.getName());
				  if (entry.isDirectory()) 
				  {
					  f.mkdir();
				  } 
				  else 
				  {
					  out = new FileOutputStream(f);
					  byte[] buf = new byte[1024];
					  int len = 0;
					  while ((len = is.read(buf)) != -1) 
					  {
						  out.write(buf, 0, len);
					  }
					  out.close();
				  }
			  }
			  is.close();
		  } 
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
	  	}
	  
	  /**
	   * UnZip String
	   * @param by
	   * @return
	   */
	  public static String unzipString(byte[] by) 
	  {       
		    ByteArrayInputStream bis = null;       
		    ByteArrayOutputStream bos = null;       
		    GZIPInputStream is = null;       
		    byte[] buf = null;       
		    try 
		    {       
		      bis = new ByteArrayInputStream(by);       
		      bos = new ByteArrayOutputStream();       
		      is = new GZIPInputStream(bis);       
		      buf = new byte[1024];       
		      int len;       
		      while ((len = is.read(buf)) != -1) {       
		        bos.write(buf, 0, len);       
		      }       
		      is.close();       
		      bis.close();       
		      bos.close();       
		      return new String(bos.toByteArray());       
		    } catch (Exception ex) 
		    {       
		      return null;       
		    } finally {       
		      bis = null;       
		      bos = null;       
		      is = null;       
		      buf = null;       
		    }       
		  }      
	  
	  	/**
	  	 * Create File
	  	 * @param path
	  	 */
	  	public static void createFile(String path) 
	  	{
	  		File file = new File(path);
	  		try 
	  		{
	  			file.createNewFile();
	  		} 
	  		catch(IOException e) 
	  		{
	  			e.printStackTrace();
	  		}
	  	}
	  	
	  	/**
	  	 * Create Directory
	  	 * @param path
	  	 */
	  	public static void createDir(String path) 
	  	{
	  		File file = new File(path);
	  		file.mkdirs();
	  	}
	  	
	  	/**
	  	 * Cut File
	  	 * @param source
	  	 * @param target
	  	 */
	  	public static void cutTo(String source, String target) 
	  	{
	  		File sourFile = new File(source);
	  		File tarFile = new File(target);
	  		if (sourFile.isFile()) 
	  		{
	  			if (tarFile.isDirectory()) 
	  			{
	  				sourFile.renameTo(tarFile);
	  			}
	  		} 
	  		else 
	  		{
	  			copy(source, target);
	  			delete(source);
	  		}
	  }
}
