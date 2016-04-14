package com.pvi.ap.reader.external.meb;

import java.util.List;

/**
 * MEB文件接口
 * @author rd037
 *
 */
public interface MebInterface {
	//获取目录信息
	public List<Chapters> openMebFile(String filePath);
	//打开不需要解密的MEB章节
	public String getFileContent(String filePath,int startFile,int fileLength);
	//打开需要解密的MEB章节
	public String getFileFlagContent(String filePath,int startFile,int fileLength,String certPath,String regcode,String userid,String password);
	//获取封面图片
	public byte[] getCover(String filePath);
}
