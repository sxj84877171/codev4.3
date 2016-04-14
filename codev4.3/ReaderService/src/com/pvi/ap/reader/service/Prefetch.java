package com.pvi.ap.reader.service;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

public class Prefetch {
	private  String TAG = "Prefetch" ;
//	public static void addTask(Context context,String IFName,int contentType, HashMap ahmHeaderMap, HashMap ahmNamePair) {
//		
//		
//		final ServApp app = ((ServApp) context);
//		
//		PrefetchTaskInfo taskInfo = new PrefetchTaskInfo(IFName,contentType,ahmHeaderMap,ahmNamePair);
//		synchronized(app.ptiStack){
//			app.ptiStack.push(taskInfo);
//		}
//
//	}
	private Stack<TaskInfo> stack = new Stack<TaskInfo>();
	private Thread thread = null;
	private class TaskInfo {
		String IFName;
		HashMap header;
		HashMap parameter;
		TaskInfo(String IFName, HashMap header, HashMap parameter) {
			this.IFName = IFName;
			this.header = (HashMap) header.clone();
			this.parameter = new HashMap(parameter);
		}
	}
	private class prefetchThread extends Thread {
		public void run() {
			super.run();
			TaskInfo task = null;
			while (true) {
				synchronized (stack) {
					try {
						task = stack.pop();
					} catch (Exception e) {
						thread = null;
						return;
					}
				}
				if (needPrefetch(task)) {
					doPrefetch(task);
				}
			}
		}
	}
	private boolean isList(TaskInfo task) {
		if (task.parameter.containsKey("start") && task.parameter.containsKey("count"))
			return true;
		return false;
	}
	private boolean needPrefetch(TaskInfo task) {
		try {
			int pages = NetCacheService.GetNetCachePage(task.IFName);
			Logger.i(TAG, pages + ":" + task.IFName);
			if (pages > 0) {
				if (isList(task)) {
					HashMap parameter = (HashMap) task.parameter.clone();
					Integer start = Integer.parseInt((String)parameter.get("start")) + Integer.parseInt((String)parameter.get("count"));
					parameter.put("start", start.toString());
					if (!CacheFile.exist(NetCacheService.GetPathName(task.IFName, parameter)))
						return true;
				} else if (task.IFName.equals("getChapterInfo")) {
					HashMap ret = CacheFile.read(NetCacheService.GetPathName(task.IFName, task.parameter));
					if (ret != null) {
						String nextID = chapterInfoNextID(ret);
						if (nextID != null) {
							HashMap parameter = (HashMap) task.parameter.clone();
							parameter.put("chapterId", nextID);
							if (!CacheFile.exist(NetCacheService.GetPathName(task.IFName, parameter)))
								return true;
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}
	private void doPrefetch(TaskInfo task) {
		if (isList(task)) {
			getList(task);
		} else if (task.IFName.equals("getChapterInfo")) {
			chapterInfo(task);
		}
	}
	public void addTask(String IFName, HashMap ahmHeaderMap, HashMap ahmNamePair) {
		TaskInfo task = new TaskInfo(IFName,ahmHeaderMap,ahmNamePair);
		synchronized(stack) {
			stack.push(task);
			if (thread == null) {
				thread = new prefetchThread();
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
		}
	}
	private String chapterInfoNextID(HashMap current) {
		try {
			byte[] xml = (byte[] )current.get("ResponseBody");
			String temp = CPManagerUtil.getStringFrombyteArray(xml);
			if(temp.contains("&quot"))
			{
				temp = temp.replaceAll("&quot", "");
			}
			xml = temp.getBytes();
			Document dom = CPManagerUtil.getDocumentFrombyteArray(xml);
			Element root = dom.getDocumentElement();
			NodeList nl = root.getElementsByTagName("NextChapter");
			if ((nl == null) || (nl.getLength() == 0)) 
				return null;
			Element element = (Element) nl.item(0);
			NodeList subnl = element.getElementsByTagName("chapterID");
			if ((subnl == null) || (subnl.getLength() == 0)) 
				return null;
			String nextID = subnl.item(0).getFirstChild().getNodeValue();
			return nextID;
	} catch (Exception e) {
			return null;
		}
	}
	private void chapterInfo(TaskInfo task) {
		HashMap ret = CacheFile.read(NetCacheService.GetPathName(task.IFName, task.parameter));
		if (ret == null) return;
		int chapters = NetCacheService.GetNetCachePage(task.IFName);
		HashMap parameter = (HashMap) task.parameter.clone();
		try {
			while (chapters > 0) {
				String nextID = chapterInfoNextID(ret);
				parameter.put("chapterId", nextID);
				ret = getFromNet(task.IFName,task.header, parameter);
				if (NetCacheService.ResponseCode(ret) == 0) {
					CacheFile.write(NetCacheService.GetPathName(task.IFName, parameter), ret,
							NetCacheService.GetNetCacheTime(task.IFName));
				}
				else
					return;
				chapters--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private HashMap getFromNet(String IFName, HashMap ahmHeaderMap, HashMap ahmNamePair) {
		try {
			Class c = Class
					.forName("com.pvi.ap.reader.data.external.manager.CPManager");
			Object o = c.newInstance();
			Method m = c.getMethod(
					IFName,
					new Class[] { Class.forName("java.util.HashMap"),
							Class.forName("java.util.HashMap") });
			Object ret = m.invoke(o, ahmHeaderMap, ahmNamePair);
			if (ret instanceof HashMap) {
				return (HashMap) ret;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private void getList(TaskInfo task) {
		try {
			HashMap parameter = (HashMap) task.parameter.clone();
			int start = Integer.parseInt((String)parameter.get("start"));
			int count = Integer.parseInt((String)parameter.get("count"));
			int pages = NetCacheService.GetNetCachePage(task.IFName);
			parameter.put("start", Integer.toString(start+count));
			parameter.put("count", Integer.toString(count*pages));
			HashMap ret = getFromNet(task.IFName,task.header, parameter);
			if ((ret == null) || (NetCacheService.ResponseCode(ret) != 0))
				return;
	
			String xml = new String((byte[])ret.get("ResponseBody"));
	
			ListContent lc = new ListContent(xml,task.IFName,count);
			HashMap target = (HashMap)ret.clone();
			parameter.put("count", Integer.toString(count));
			for(int i=0;i<pages;i++){
				final String onePageXml = lc.getOnePageFull(i);
				if (onePageXml == null) break;
				parameter.put("start", Integer.toString(start+count+i*count));
				target.put("ResponseBody",onePageXml.getBytes());//把分页后xml放入ret
				target.put("Content-Length", Integer.toString(onePageXml.length()));
				CacheFile.write(NetCacheService.GetPathName(task.IFName, parameter), target, NetCacheService.GetNetCacheTime(task.IFName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

//分页示例
//int prefetch = NetCacheService.GetNetCachePage(IFName);
//if (prefetch > 0) {
//	byte[] xml = (byte[] )ret.get("ResponseBody");
//	try {
//		Document dom = CPManagerUtil.getDocumentFrombyteArray(xml);
//		Element root = dom.getDocumentElement();
//		NodeList nl = root.getElementsByTagName("totalRecordCount");
//		int total = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
//		int current = Integer.parseInt((String)ahmNamePair.get("start"));
//		int items = Integer.parseInt((String)ahmNamePair.get("count"));
//		int start, count;
//		while (prefetch > 0) {
//			start = current+prefetch*items;
//			if (start <= total) {
//				count = ((start+items)>total)? (total+1-start):items;
//				HashMap NewNamePair = (HashMap)ahmNamePair.clone();
//				NewNamePair.put("start", start);
//				NewNamePair.put("count", count);
//				Prefetch.addTask(IFName, ahmHeaderMap, NewNamePair);
//			}
//			prefetch--;
//		}
//	} catch (Exception e) {
//	}
//}
//if (task.IFName.equals("getBlockContent")) {
//	HashMap target_para = (HashMap)parameter.clone();
//	target_para.put("count", Integer.toString(count));
//	HashMap target = null;
//	byte[] tar_xml,src_xml;
//	Document tar_dom, src_dom;
//	try {
//		target = CPManager.getBlockContent(task.header, target_para);
//		tar_xml = (byte[] )target.get("ResponseBody");
//		tar_dom = CPManagerUtil.getDocumentFrombyteArray(tar_xml);
//		doc2XmlFile(tar_dom, "/sdcard/target.xml");
//		src_xml = (byte[] )ret.get("ResponseBody");
//		src_dom = CPManagerUtil.getDocumentFrombyteArray(src_xml);
//		doc2XmlFile(src_dom, "/sdcard/source.xml");
//		Element root = src_dom.getDocumentElement();
//		NodeList nl = root.getElementsByTagName("totalRecordCount");
//		int total = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
//		int target_start = start + count;
//		int target_total = (total > target_start+count*pages)? (count*pages):(total+1-target_start);
//		int target_count = 0;
//		while (target_count < target_total) {
//			;
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//}
//public static boolean doc2XmlFile(Document document,String filename) 
//{ 
//  boolean flag = true; 
//  try 
//   { 
//        /** 将document中的内容写入文件中   */ 
//         TransformerFactory tFactory = TransformerFactory.newInstance();    
//         Transformer transformer = tFactory.newTransformer();  
//        /** 编码 */ 
//        //transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312"); 
//         DOMSource source = new DOMSource(document);  
//         StreamResult result = new StreamResult(new File(filename));    
//         transformer.transform(source, result);  
//     }catch(Exception ex) 
//     { 
//         flag = false; 
//         ex.printStackTrace(); 
//     } 
//    return flag;       
//}

