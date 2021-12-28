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
import com.qcloud.cos.transfer.*;
import com.qcloud.cos.model.lifecycle.*;
import com.qcloud.cos.model.inventory.*;
import com.qcloud.cos.model.inventory.InventoryFrequency;
import com.tencent.cloud.Response;

import osc.util.Constants;

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
import java.security.KeyPair;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class TransferDownloadObject {

    private COSClient cosClient;
    private TransferManager transferManager;


   /**
    * @deprecated 下载
    * @param localFilePath
    * @param bucket
    * @param key
    * @throws InterruptedException
    * @throws IOException
    * @throws NoSuchAlgorithmException
    */
    public void transferDownloadObject(
    		String localFilePath,
    		String bucket,
    		String key
    		) throws InterruptedException, IOException, NoSuchAlgorithmException {
        File localDownFile = new File(localFilePath);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
        // 限流使用的单位是bit/s, 这里设置下载带宽限制为 10MB/s
        getObjectRequest.setTrafficLimit(80*1024*1024);
        // 下载文件
        Download download = transferManager.download(getObjectRequest, localDownFile);
        // 等待传输结束（如果想同步的等待上传结束，则调用 waitForCompletion）
        download.waitForCompletion();
        
    }

    /**
     * @deprecated 初始化
     * @param secretId
     * @param secretKey
     * @param durationSeconds
     * @param bucket
     * @param bucketRegion
     */
    private void initClient(
    		String secretId,
    		String secretKey,
    		int durationSeconds,
    		String bucket,
    		String bucketRegion
    		) {
    	
        this.cosClient = ServiceCredential.useTempInitCredential(
        		secretId, 
        		secretKey, 
        		durationSeconds, 
        		bucket, 
        		bucketRegion
        		);
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
    	
    	commonPublishDownload(
    			"E:/download/test7.jpg",
    			"",
    			Constants.SECRETID,
    			Constants.SECRETKEY,
    			Constants.DURATIONSECONDS,
    			Constants.BUCKETID,
    			Constants.BUCKETREGION
    			);
    }
    
    /**
     * @deprecated 腾讯云下载图片
     * @param localFilePath
     * @param key
     * @param secretId
     * @param secretKey
     * @param durationSeconds
     * @param bucket
     * @param bucketRegion
     * @throws InterruptedException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void commonPublishDownload(String localFilePath,String key,String secretId,String secretKey,int durationSeconds,String bucket,String bucketRegion)throws InterruptedException, IOException,        NoSuchAlgorithmException{
    	
    	   TransferDownloadObject example = new TransferDownloadObject();
           example.initClient(
        		   secretId,
        		   secretKey,
        		   durationSeconds,
        		   bucket,
        		   bucketRegion
        		   );

           // 高级接口下载对象
           example.transferDownloadObject(
        		   localFilePath,
        		   bucket,
        		   key
        		   );
           example.cosClient.shutdown();
    	
    }
    
    

}