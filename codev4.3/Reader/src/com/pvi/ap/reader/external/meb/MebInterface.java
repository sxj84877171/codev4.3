package com.pvi.ap.reader.external.meb;

import java.util.List;

/**
 * MEB�ļ��ӿ�
 * @author rd037
 *
 */
public interface MebInterface {
	//��ȡĿ¼��Ϣ
	public List<Chapters> openMebFile(String filePath);
	//�򿪲���Ҫ���ܵ�MEB�½�
	public String getFileContent(String filePath,int startFile,int fileLength);
	//����Ҫ���ܵ�MEB�½�
	public String getFileFlagContent(String filePath,int startFile,int fileLength,String certPath,String regcode,String userid,String password);
	//��ȡ����ͼƬ
	public byte[] getCover(String filePath);
}
