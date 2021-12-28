package osc;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.COSEncryptionClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.*;
import com.qcloud.cos.exception.*;
import com.qcloud.cos.model.*;
import com.qcloud.cos.internal.crypto.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.utils.DateUtils;

import osc.util.Constants;

import com.qcloud.cos.transfer.*;
import com.qcloud.cos.model.inventory.*;
import com.qcloud.cos.model.inventory.InventoryFrequency;

import java.io.*;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.net.URL;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;

import java.security.KeyPair;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class TransferUploadObject {

    private static COSClient cosClient;
    private TransferManager transferManager;
    private static  Upload upload;

    private String uploadId;
    private List<PartETag> partETags;

    /**
     * 高级接口上传对象普通文件
     */
    public  void transferUploadFile(String localFilePath,String bucketName,String key) throws InterruptedException, IOException, NoSuchAlgorithmException {
        File localFile = new File(localFilePath);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        // 本地文件上传
        upload = transferManager.upload(putObjectRequest);
    }
    
    
    /**
     * 高级接口上传对象大文件
     */
    public  void transferUploadBigFile(String localFilePath,String bucketName,String key) throws InterruptedException, IOException, NoSuchAlgorithmException {
        File localFile = new File(localFilePath);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        // 本地文件上传
        PersistableUpload persistableUpload = null;
        // 设置 SDK 内部简单上传或每个分块上传速度为8MB/s，单位：bit/s
        // 注意：大于分块阈值的 File 类型数据上传，会并发多分块上传，需要调整线程池大小，以控制上传速度
        putObjectRequest.setTrafficLimit(64*1024*1024);
        upload = transferManager.upload(putObjectRequest);
        // 等待"分块上传初始化"完成，并获取到 persistableUpload （包含 uploadId 等）
        while(persistableUpload == null) {
            persistableUpload = upload.getResumeableMultipartUploadId();
            Thread.sleep(100);
            if(upload.isDone()) {
            	break;
            }
        }
    }

    private void initClient(String secretId,String secretKey,int durationSeconds,String bucket,String bucketRegion) {
        this.cosClient = ServiceCredential.useTempInitCredential(secretId,secretKey,durationSeconds,bucket,bucketRegion);
        // 高级接口传输类
        // 线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        transferManager = new TransferManager(cosClient, threadPool);
        // 设置高级接口的分块上传阈值和分块大小为10MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(10 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(10 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
    }
    
    

    public static void main(String[] args) throws InterruptedException, IOException,        NoSuchAlgorithmException {
//    	getReturnUploadBigFileUrl(
//    			"E:/download/test.jpg",
//    			"",
//    			Constants.SECRETID,
//    			Constants.SECRETKEY,
//    			Constants.DURATIONSECONDS,
//    			Constants.BUCKETID,
//    			Constants.BUCKETREGION
//    			);
    	getReturnUploadOrdinaryFileUrl(
    			"E:/download/test.jpg",
    			"",
    			Constants.SECRETID,
    			Constants.SECRETKEY,
    			Constants.DURATIONSECONDS,
    			Constants.BUCKETID,
    			Constants.BUCKETREGION
    			);
    }
    
    /**
     * @deprecated 上传照片到腾讯云并返回该图片链接地址
     * @param path
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getReturnUploadBigFileUrl(String localFilePath,
    		String key,
    		String secretId,
    		String secretKey,
    		int durationSeconds,
    		String bucket,
    		String bucketRegion) throws InterruptedException, IOException,        NoSuchAlgorithmException {
        TransferUploadObject example = new TransferUploadObject();
        example.initClient(secretId, secretKey, durationSeconds, bucket, bucketRegion);
        example.transferUploadBigFile(localFilePath,bucket,key);
        UploadResult uploadResult = upload.waitForUploadResult();
        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getKey(),bucket);
        example.cosClient.shutdown();
    	
    	return url;
    }
   
    /**
     * @deprecated 上传照片到腾讯云并返回该图片链接地址
     * @param path
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getReturnUploadOrdinaryFileUrl(String localFilePath,String key,String secretId,String secretKey,int durationSeconds,String bucket,String bucketRegion) throws InterruptedException, IOException,        NoSuchAlgorithmException {
        TransferUploadObject example = new TransferUploadObject();
        example.initClient(secretId, secretKey, durationSeconds, bucket, bucketRegion);
        example.transferUploadFile(localFilePath,bucket,key);
        UploadResult uploadResult = upload.waitForUploadResult();
        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getKey(), bucket);
        example.cosClient.shutdown();
    	
    	return url;
    }
    
}