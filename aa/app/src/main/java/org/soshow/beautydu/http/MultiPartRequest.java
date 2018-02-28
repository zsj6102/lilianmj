package org.soshow.beautydu.http;

import java.io.File;
import java.util.Map;

/**
 * 
 * @Time: 2015年9月7日 下午4:12:06
 * @Description: None
 */
public interface MultiPartRequest {

    public void addFileUpload(String param,File file); 
    
    public void addStringUpload(String param,String content); 
    
    public Map<String,File> getFileUploads();
    
    public Map<String,String> getStringUploads(); 
}