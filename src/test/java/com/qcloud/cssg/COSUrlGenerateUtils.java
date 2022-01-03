package com.qcloud.cssg;

import java.net.URL;
import java.util.Date;

import com.qcloud.cos.COSClient;
/**
 * @deprecated 腾讯云文件url处理
 * @author 
 *
 */
public class COSUrlGenerateUtils {
	
	/**
	 * @deprecated url生成
	 * @param cosClient
	 * @param eTag 
	 * @param bucketName
	 * @return
	 */
	public static String getUrlStr(COSClient cosClient,String eTag,String bucketName) {
		// 设置URL过期时间为1年 3600l* 1000*24*365*1
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 1);
        // 生成URL
        URL url = cosClient.generatePresignedUrl(bucketName, eTag, expiration);
        if (url != null) {
        	System.out.println(url);
        	String[] split = url.toString().split("\\?");
            return split[0];
        }
        return null;
		
	}

}
