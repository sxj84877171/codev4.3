package com.pvi.ap.reader.external.meb;

import it.sauronsoftware.base64.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.text.Html;
import android.text.Spanned;

import com.pvi.ap.reader.data.common.DencryptHelper;
import com.pvi.ap.reader.data.common.FileHelper;
import com.pvi.ap.reader.data.common.Logger;


/**
 * 
 * @author rd037
 *
 */
public class MebInterfaceImpl implements MebInterface {
	private int id=0;//全局参数
	private final static String TAG="MebInterfaceImpl";
	@Override
	public List<Chapters> openMebFile(String filePath) {
		// TODO Auto-generated method stub
		List<Chapters> fileStrings=new ArrayList<Chapters>();
		List<HashMap<String,String>> sList=new ArrayList<HashMap<String,String>>();
		
		Chapters chapters=null;
		RandomAccessFile fis=null;
		String mebName=null;
		try
		{
		File file =new File(filePath);
		fis=new RandomAccessFile(file,"r"); 
		//文件描述区段
		byte[] filedescript = new byte[32];
		fis.read(filedescript,0,32);
		//System.arraycopy(byt, 0, filedescript, 0, 32);
//		byte[] bt_filetype = new byte[4];//文件类型标识
//		byte[] bt_fileversion = new byte[2];//文件格式版本号
//		byte[] bt_ebooktype = new byte[2];//电子书类型
//		byte[] bt_createdate = new byte[4];//文件创建日期
//		byte[] bt_updatedate = new byte[4];//文件修改日期
		byte[] bt_metainfo = new byte[4];//META-INF文件区段起始位置
		byte[] bt_dataindex = new byte[4];//数据区段索引表起始位置
//		byte[] bt_filedesclength = new byte[4];//文件描述区段长度
//		byte[] bt_remain = new byte[4];//保留字段
//		System.arraycopy(filedescript, 0, bt_filetype, 0, 4);
//		System.arraycopy(filedescript, 4, bt_fileversion, 0, 2);
//		System.arraycopy(filedescript, 6, bt_ebooktype, 0, 2);
//		System.arraycopy(filedescript, 8, bt_createdate, 0, 4);
//		System.arraycopy(filedescript, 12, bt_updatedate, 0, 4);
		System.arraycopy(filedescript, 16, bt_metainfo, 0, 4);
		int bt_metainfo_sum=Integer.parseInt(DencryptHelper.getHexString(bt_metainfo),16);
		System.arraycopy(filedescript, 20, bt_dataindex, 0, 4);
		int bt_dataindex_sum=Integer.parseInt(DencryptHelper.getHexString(bt_dataindex),16);
//		System.arraycopy(filedescript, 24, bt_filedesclength, 0, 4);
//		System.arraycopy(filedescript, 28, bt_remain, 0, 4);
		//Metainfo 文件区段
		byte[] bt_filesegnum = new byte[2];//段内文件个数
		fis.read(bt_filesegnum, 0, 2);
		//System.arraycopy(byt, 32, bt_filesegnum, 0, 2);
		int li_filesegnum = Integer.parseInt(DencryptHelper.getHexString(bt_filesegnum),16);
		//file map length
		byte[] bt_filemaplength = new byte[2];//文件映射表长度
		fis.read(bt_filemaplength, 0, 2);
		//System.arraycopy(byt, 34, bt_filemaplength, 0, 2);
		int li_filemaplength = Integer.parseInt(DencryptHelper.getHexString(bt_filemaplength),16);	
		//all file seg info
		//byte[] bt_allfileseg = new byte[li_filemaplength];
		//System.arraycopy(byt, 36, bt_allfileseg, 0, li_filemaplength);
		byte[] bt_fileid = new byte[2];//文件ID
		byte[] bt_filestartpos = new byte[4];//文件起始位置
		byte[]bt_filelength= new byte[4];//文件长度
		byte[]bt_filenamelength= new byte[2];//文件名称长度
		int li_filenamelength;
		int li_filelength;
		int ls_filestartpos;
		for(int k=0;k<li_filesegnum;k++){
			fis.read(bt_fileid, 0, 2);
			fis.read(bt_filestartpos, 0, 4);
			fis.read(bt_filelength, 0, 4);
			fis.read(bt_filenamelength, 0, 2);
			//String ls_Fileid = String.valueOf(Integer.parseInt(DencryptHelper.getHexString(bt_fileid),16));
			ls_filestartpos = Integer.parseInt(DencryptHelper.getHexString(bt_filestartpos),16);
			li_filelength = Integer.parseInt(DencryptHelper.getHexString(bt_filelength),16);
			li_filenamelength = Integer.parseInt(DencryptHelper.getHexString(bt_filenamelength),16);
			byte[]bt_filename= new byte[li_filenamelength];//文件名称
			fis.read(bt_filename, 0, li_filenamelength);
			String ls_filename = new String(bt_filename);
			if(ls_filename.endsWith(".ncx")){
				byte[] xmllen=new byte[li_filelength];
				fis.seek(bt_metainfo_sum + ls_filestartpos);
				fis.read(xmllen, 0, li_filelength);
				//System.out.println(new String(xmllen));
				String str_stuff = null;
				try { 
					str_stuff = new String(xmllen);
					//Logger.i(TAG, str_tst);
					str_stuff = str_stuff.replace("&amp;ldquo;", "“");//to avoid a bug in MEB file
					str_stuff = str_stuff.replace("&amp;rdquo;", "”");
				}catch (OutOfMemoryError e) {
					str_stuff = null;
				}
				//StringReader sr=null;
				try
				{ 
					DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
					DocumentBuilder dombuilder=domfac.newDocumentBuilder();
			        //String xmlStr = new String(xmllen,"UTF-8");  
			        //sr = new StringReader(xmlStr);   
					ByteArrayInputStream byte_stream = null;
					if( str_stuff == null) {
						byte_stream = new ByteArrayInputStream(xmllen);
					}else {
						byte_stream = new ByteArrayInputStream(str_stuff.getBytes());
					}
			        //InputSource is = new InputSource(sr);   
					InputSource is = new InputSource(byte_stream);
			        Document doc=dombuilder.parse(is);
			        Element root=doc.getDocumentElement();
			        NodeList nList= root.getElementsByTagName("text");
			        mebName=nList.item(0).getFirstChild().getNodeValue();
			        Element npel;
			        NamedNodeMap namedNodeMap;
			        NodeList navList= root.getElementsByTagName("navPoint");//获取navPoint
			        
			        for(int j=0;j<navList.getLength();j++){
			        	 npel=(Element) navList.item(j);
			        	namedNodeMap=navList.item(j).getAttributes();//根据navPoint节点的属性个数判断是卷还是章节
			        	
			        	if(namedNodeMap.getLength()==1){
			        		nList=npel.getElementsByTagName("text");//获取text
			        		String navNode=nList.item(0).getFirstChild().getNodeValue();//获取卷text的值
			        		HashMap<String,String> 	map=new HashMap<String,String>();
			        		 map.put("name", navNode);
			        		 map.put("flag", "0");
			        		 sList.add(map);
			        		//System.out.println("navNode="+navNode);
			        		for( k=1;k<nList.getLength();k++){
			        			String navNodes=nList.item(k).getFirstChild().getNodeValue();
			        			HashMap<String,String>	map1=new HashMap<String,String>();
			        			map1.put("name", navNodes);
				        		 map1.put("flag", "1");
			        			//System.out.println("navNodes="+navNodes);
			        			sList.add(map1);
			        		}
			        	}
			        }
				}catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			
		}
		
		byte[] bt_alldatasegnum = new byte[2];//数据区段总个数
		byte[] bt_alldataseglength = new byte[2];//区段映射表长度
		fis.seek(bt_dataindex_sum);
		fis.read(bt_alldatasegnum, 0, 2);
		fis.read(bt_alldataseglength, 0, 2);
		int li_alldatasegnum = Integer.parseInt(DencryptHelper.getHexString(bt_alldatasegnum),16);
	//	int li_alldataseglength = Integer.parseInt(DencryptHelper.getHexString(bt_alldataseglength),16);
		byte[]  bt_datasegid = new byte[2];//数据区段id
		byte[] bt_datasegstartpos = new byte[4];//区段起始位置
		byte[]  bt_dataseglength = new byte[4];//区段长度
		 int bt_datasegid_num,bt_datasegstartpos_num,bt_dataseglength_num;
		 long file_pos;
		for(int seg=0;seg<li_alldatasegnum;seg++){
			fis.read(bt_datasegid, 0, 2);
			fis.read(bt_datasegstartpos, 0, 4);
			fis.read(bt_dataseglength, 0, 4);
			bt_datasegid_num= Integer.parseInt(DencryptHelper.getHexString(bt_datasegid),16);
			bt_datasegstartpos_num= Integer.parseInt(DencryptHelper.getHexString(bt_datasegstartpos),16);
			bt_dataseglength_num= Integer.parseInt(DencryptHelper.getHexString(bt_dataseglength),16);
			file_pos = fis.getFilePointer();
			fis.seek(bt_datasegstartpos_num);
			
			byte[] datasegid = new byte[2];//数据区段ID
			byte[] filenum = new byte[2];//文件个数=null;
			byte[] filemaplength = new byte[2];//文件映射表长度
			fis.read(datasegid, 0, 2);
			fis.read(filenum, 0, 2);
			fis.read(filemaplength, 0, 2);
			int filenum_sum=Integer.parseInt(DencryptHelper.getHexString(filenum),16);
			byte[]  fileid = new byte[2];//文件ID
			byte[] zipflag= new byte[1];//文件压缩标识=null;
			byte[] billingflag = new byte[1];//文件计费标识
			byte[] billingid = new byte[24];//文件计费ID
			byte[] filestartpos = new byte[4];//文件起始位置
			byte[] filelength = new byte[4];//文件长度信息
			byte[] filenamelength= new byte[2];//文件名称长度 
			for(int j=0;j<filenum_sum;j++){
				fis.read(fileid, 0, 2);
				fis.read(zipflag, 0, 1);
				fis.read(billingflag, 0, 1);
				fis.read(billingid, 0, 24);
				fis.read(filestartpos, 0, 4);
				fis.read(filelength, 0, 4);
				fis.read(filenamelength, 0, 2);
				int	startFile=Integer.parseInt(DencryptHelper.getHexString(bt_datasegstartpos),16)+Integer.parseInt(DencryptHelper.getHexString(filestartpos),16);
				
				//System.out.println("name="+sList.get(seg).get("name"));
				//System.out.println("flag="+sList.get(seg).get("flag"));
				if(id<sList.size()){
				String flag=sList.get(id).get("flag");
				 if(flag.equals("0")){
					 for(int i=0;i<=1;i++){
				     chapters=new Chapters(startFile,Integer.parseInt(DencryptHelper.getHexString(filelength),16),sList.get(id).get("name"),Integer.parseInt(DencryptHelper.getHexString(billingflag),16),mebName,sList.get(id).get("flag"));
				      fileStrings.add(chapters);
				      id++;
					 }
				  }else if(flag.equals("1")){
				       chapters=new Chapters(startFile,Integer.parseInt(DencryptHelper.getHexString(filelength),16),sList.get(id).get("name"),Integer.parseInt(DencryptHelper.getHexString(billingflag),16),mebName,sList.get(id).get("flag"));
					   fileStrings.add(chapters); 
					   id++;
				      }
				}
				fis.skipBytes(Integer.parseInt(DencryptHelper.getHexString(filenamelength),16));
			}
			fis.seek(file_pos);
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				fis.close();
				fis = null;
				// Get a Runtime object
			     Runtime r = Runtime.getRuntime();
			      // Collect garbage at the start of the program
			      r.gc();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return fileStrings ;
		}
	

	
	//获取KeyValue值
	public String getKeyValue(String response) {
		// TODO Auto-generated method stub
		String ls_KeyValue = null;
		StringReader sr=null;
		try
		{
			DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
			DocumentBuilder dombuilder=domfac.newDocumentBuilder();
	        String xmlStr = new String(response.getBytes(),"UTF-8");   
	         sr = new StringReader(xmlStr);   
	        InputSource is = new InputSource(sr);   
	        Document doc=dombuilder.parse(is);
	        Element root=doc.getDocumentElement();
	        Element eTitle = (Element)root.getElementsByTagName("KeyValue").item(0);
 		    ls_KeyValue = eTitle.getFirstChild().getNodeValue();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ls_KeyValue;
	}
	
	
	//获取证书并解密
	public String getCr(String regcode, String userid, String password,
			String crFile) {
		// TODO Auto-generated method stub
		String response = null;
		FileInputStream fis=null;
		try 
		{
		File file =new File(crFile);
		fis=new FileInputStream(file); ;
		byte[] resbt = new byte[(int) file.length()];
		fis.read(resbt);//读取byte
		byte[] param = new byte[16];				//前16位
		byte[] cert = new byte[resbt.length-16];	//从17位开始到最后
		System.arraycopy(resbt, 0, param, 0, 16);
	    System.arraycopy(resbt, 16, cert, 0, resbt.length-16);
	    String str = regcode+password+userid;
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");   
		messageDigest.update(str.getBytes());  
		byte rek1[] = messageDigest.digest();
		response=new String(DencryptHelper.Decrypt(cert, rek1, param));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{   
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return response;
	}
	//book.ncx
	public List<String> getS( byte[] byt) {
		// TODO Auto-generated method stub
		List<String> sList=new ArrayList<String>();
		try
		{
			DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
			DocumentBuilder dombuilder=domfac.newDocumentBuilder();
	        String xmlStr = new String(byt,"UTF-8");  
	        StringReader sr = new StringReader(xmlStr);   
	        InputSource is = new InputSource(sr);   
	        Document doc=dombuilder.parse(is);
	        Element root=doc.getDocumentElement();
	       // NodeList nList= root.getElementsByTagName("text");
	      //获取父节点  System.out.println(root.getElementsByTagName("text").item(0).getParentNode().getNodeName());
//	        for(int i=0;i<nList.getLength();i++){
//	        	Node nd=nList.item(i);
//	        	if(nd.hasChildNodes()){
//	        		System.out.println(nd.getFirstChild().getNodeValue());
//	        	}
//	        }
	        NodeList nList;
	        Element npel;
	        NamedNodeMap namedNodeMap;
	        NodeList navList= root.getElementsByTagName("navPoint");//获取navPoint
	        for(int j=0;j<navList.getLength();j++){
	        	 npel=(Element) navList.item(j);
	        	namedNodeMap=navList.item(j).getAttributes();//根据navPoint节点的属性个数判断是卷还是章节
	        	if(namedNodeMap.getLength()==1){
	        		nList=npel.getElementsByTagName("text");//获取text
	        		String navNode=nList.item(0).getFirstChild().getNodeValue();//获取卷text的值
	        		for(int k=1;k<nList.getLength();k++){
	        			String navNodes=navNode+nList.item(k).getFirstChild().getNodeValue();
	        			sList.add(navNodes);
	        		}
	        	}
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sList;
	}
	public String getMebName(byte[] byt){
		String mebName=null;
		StringReader sr=null;
		try
		{
			DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
			DocumentBuilder dombuilder=domfac.newDocumentBuilder();
	        String xmlStr = new String(byt,"UTF-8");  
	        sr = new StringReader(xmlStr);   
	        InputSource is = new InputSource(sr);   
	        Document doc=dombuilder.parse(is);
	        Element root=doc.getDocumentElement();
	        NodeList nList= root.getElementsByTagName("text");
	        mebName=nList.item(0).getFirstChild().getNodeValue();
	        
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mebName; 
	}
	//不需要解密所调用的方法
	@Override
	public String getFileContent(String filePath, int startFile, int fileLength) {
		// TODO Auto-generated method stub
		String filecontent=null;
		RandomAccessFile fis=null;
		
		try {
			
			File file =new File(filePath);
			fis=new RandomAccessFile(file,"r"); 
			byte [] charptLength=new byte[fileLength];
			
			fis.seek(startFile);
			
			fis.read(charptLength, 0, fileLength); 
			
			filecontent=new String(FileHelper.unzipString(charptLength).getBytes(),"UTF-8");
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fis.close();
				fis = null;
				// Get a Runtime object
			     Runtime r = Runtime.getRuntime();
			      // Collect garbage at the start of the program
			      r.gc();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filecontent;
	}
	//需要解密所调用的方法
	@Override
	public String getFileFlagContent(String filePath, int startFile,
			int fileLength, String certPath, String regcode, String userid,
			String password) {
		// TODO Auto-generated method stub
		String filecontent=null;
		RandomAccessFile fis=null;
		byte [] param=null;
		byte [] content=null;
		byte[] base64=null;
		String str=null;
		
		try {
			File file =new File(filePath);
			fis=new RandomAccessFile(file,"r"); 
			byte [] charptLength=new byte[fileLength];
			fis.seek(startFile);
			fis.read(charptLength, 0, fileLength); 
			param = new byte[16];				//前16位
			content = new byte[fileLength-16];	//从17位开始到最后
		    System.arraycopy(charptLength, 0, param, 0, 16);
		    System.arraycopy(charptLength, 16, content, 0, fileLength-16);
		    str =getKeyValue(getCr(regcode, userid, password, certPath));
		    base64 =Base64.decode(str.getBytes("UTF-8"));
			filecontent=new String(FileHelper.unzipString(DencryptHelper.Decrypt(content, base64,param)).getBytes(),"UTF-8");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fis.close();
				fis = null;
				// Get a Runtime object
			     Runtime r = Runtime.getRuntime();
			      // Collect garbage at the start of the program
			      r.gc();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filecontent;
	}
	@Override
	public byte[] getCover(String filePath) {
		// TODO Auto-generated method stub
		RandomAccessFile fis=null;
		try
		{
		File file =new File(filePath);
		fis=new RandomAccessFile(file,"r"); 
		
		//文件描述区段
		byte[] filedescript = new byte[32];
		fis.read(filedescript,0,32);
		//System.arraycopy(byt, 0, filedescript, 0, 32);
//		byte[] bt_filetype = new byte[4];//文件类型标识
//		byte[] bt_fileversion = new byte[2];//文件格式版本号
//		byte[] bt_ebooktype = new byte[2];//电子书类型
//		byte[] bt_createdate = new byte[4];//文件创建日期
//		byte[] bt_updatedate = new byte[4];//文件修改日期
		byte[] bt_metainfo = new byte[4];//META-INF文件区段起始位置
		byte[] bt_dataindex = new byte[4];//数据区段索引表起始位置
//		byte[] bt_filedesclength = new byte[4];//文件描述区段长度
//		byte[] bt_remain = new byte[4];//保留字段
//		System.arraycopy(filedescript, 0, bt_filetype, 0, 4);
//		System.arraycopy(filedescript, 4, bt_fileversion, 0, 2);
//		System.arraycopy(filedescript, 6, bt_ebooktype, 0, 2);
//		System.arraycopy(filedescript, 8, bt_createdate, 0, 4);
//		System.arraycopy(filedescript, 12, bt_updatedate, 0, 4);
		System.arraycopy(filedescript, 16, bt_metainfo, 0, 4);
		int bt_metainfo_sum=Integer.parseInt(DencryptHelper.getHexString(bt_metainfo),16);
		System.arraycopy(filedescript, 20, bt_dataindex, 0, 4);
		int bt_dataindex_sum=Integer.parseInt(DencryptHelper.getHexString(bt_dataindex),16);
//		System.arraycopy(filedescript, 24, bt_filedesclength, 0, 4);
//		System.arraycopy(filedescript, 28, bt_remain, 0, 4);
		//Metainfo 文件区段
		byte[] bt_filesegnum = new byte[2];//段内文件个数
		fis.read(bt_filesegnum, 0, 2);
		//System.arraycopy(byt, 32, bt_filesegnum, 0, 2);
		int li_filesegnum = Integer.parseInt(DencryptHelper.getHexString(bt_filesegnum),16);
		//file map length
		byte[] bt_filemaplength = new byte[2];//文件映射表长度
		fis.read(bt_filemaplength, 0, 2);
		//System.arraycopy(byt, 34, bt_filemaplength, 0, 2);
		int li_filemaplength = Integer.parseInt(DencryptHelper.getHexString(bt_filemaplength),16);	
		//all file seg info
		//byte[] bt_allfileseg = new byte[li_filemaplength];
		//System.arraycopy(byt, 36, bt_allfileseg, 0, li_filemaplength);
		byte[] bt_fileid = new byte[2];//文件ID
		byte[] bt_filestartpos = new byte[4];//文件起始位置
		byte[] bt_filelength= new byte[4];//文件长度
		byte[] bt_filenamelength= new byte[2];//文件名称长度
		int li_filenamelength;
		int li_filelength;
		int ls_filestartpos;
		for(int k=0;k<li_filesegnum;k++){
			fis.read(bt_fileid, 0, 2);
			fis.read(bt_filestartpos, 0, 4);
			fis.read(bt_filelength, 0, 4);
			fis.read(bt_filenamelength, 0, 2);
			String ls_Fileid = String.valueOf(Integer.parseInt(DencryptHelper.getHexString(bt_fileid),16));
			ls_filestartpos = Integer.parseInt(DencryptHelper.getHexString(bt_filestartpos),16);
			li_filelength = Integer.parseInt(DencryptHelper.getHexString(bt_filelength),16);
			li_filenamelength = Integer.parseInt(DencryptHelper.getHexString(bt_filenamelength),16);
			byte[]bt_filename= new byte[li_filenamelength];//文件名称
			fis.read(bt_filename, 0, li_filenamelength);
			String ls_filename = new String(bt_filename);
			if(ls_filename.endsWith("600800.jpg")){
				byte[] xmllen=new byte[li_filelength];
				fis.seek(bt_metainfo_sum + ls_filestartpos);
				fis.read(xmllen, 0, li_filelength);
				return xmllen;
			}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				fis.close();
				fis = null;
				// Get a Runtime object
			     Runtime r = Runtime.getRuntime();
			      // Collect garbage at the start of the program
			      r.gc();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
