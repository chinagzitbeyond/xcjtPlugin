package com.qcloud.cssg;

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
import com.qcloud.util.Constants;
import com.qcloud.util.FileUtil;

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
    private String localFilePath = "E:/download/test.jpg";

    /**
     * 高级接口上传对象
     */
    public  void transferUploadFile(String localFilePath) throws InterruptedException, IOException, NoSuchAlgorithmException {
        //.cssg-snippet-body-start:[transfer-upload-file]
        // 示例1：测试通过
   /**     // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = Constants.BUCKETID;
        String key = "mail.huwing.cn/245mm日用/10片/test21.jpg";
        File localFile = new File(localFilePath);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        // 本地文件上传
        Upload upload = transferManager.upload(putObjectRequest);
        // 等待传输结束（如果想同步的等待上传结束，则调用 waitForCompletion）
        UploadResult uploadResult = upload.waitForUploadResult();
        System.out.println(uploadResult.getKey());
        
//        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getETag(), Constants.BUCKETID);
        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getKey(), Constants.BUCKETID);
        System.out.println(url);
  **/    
      
    	
        // 示例2：对大于分块大小的文件，使用断点续传 测试通过
        // 步骤一：获取 PersistableUpload
    	String bucketName = Constants.BUCKETID;
    	String key = "mail.huwing.cn/245mm日用/10片/test30.jpg";
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
        System.out.println("helloworld");
//        return upload;
        // 保存 persistableUpload
        
        // 步骤二：当由于网络等问题，大文件的上传被中断，则根据 PersistableUpload 恢复该文件的上传，只上传未上传的分块
//        Upload newUpload = transferManager.resumeUpload(persistableUpload);
         // 等待传输结束（如果想同步的等待上传结束，则调用 waitForCompletion）
        
//        UploadResult uploadResult = newUpload.waitForUploadResult();
        
//        return uploadResult;
      
        //.cssg-snippet-body-end
    }
    
    /**
     * 高级接口上传对象
     */
    public  void transferUploadFile() throws InterruptedException, IOException, NoSuchAlgorithmException {
        //.cssg-snippet-body-start:[transfer-upload-file]
        // 示例1：测试通过
   /**     // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = Constants.BUCKETID;
        String key = "mail.huwing.cn/245mm日用/10片/test21.jpg";
        File localFile = new File(localFilePath);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        // 本地文件上传
        Upload upload = transferManager.upload(putObjectRequest);
        // 等待传输结束（如果想同步的等待上传结束，则调用 waitForCompletion）
        UploadResult uploadResult = upload.waitForUploadResult();
        System.out.println(uploadResult.getKey());
        
//        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getETag(), Constants.BUCKETID);
        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getKey(), Constants.BUCKETID);
        System.out.println(url);
  **/    
      
    	
        // 示例2：对大于分块大小的文件，使用断点续传 测试通过
        // 步骤一：获取 PersistableUpload
    	String bucketName = Constants.BUCKETID;
    	String key = "mail.huwing.cn/245mm日用/10片/test30.jpg";
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
        System.out.println("helloworld");
//        return upload;
        // 保存 persistableUpload
        
        // 步骤二：当由于网络等问题，大文件的上传被中断，则根据 PersistableUpload 恢复该文件的上传，只上传未上传的分块
//        Upload newUpload = transferManager.resumeUpload(persistableUpload);
         // 等待传输结束（如果想同步的等待上传结束，则调用 waitForCompletion）
        
//        UploadResult uploadResult = newUpload.waitForUploadResult();
        
//        return uploadResult;
      
        //.cssg-snippet-body-end
    }

    // .cssg-methods-pragma

    private void initClient() {
//        String secretId = "COS_SECRETID";
//        String secretKey = "COS_SECRETKEY";
//        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
//        // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
//        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
//        Region region = new Region("COS_REGION");
//        ClientConfig clientConfig = new ClientConfig(region);
//        // 3 生成 cos 客户端。
//        this.cosClient = new COSClient(cred, clientConfig);
        this.cosClient = ServiceCredential.useTempInitCredential();


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
        TransferUploadObject example = new TransferUploadObject();
        example.initClient();
        
        // 高级接口上传对象
//        Upload result = example.transferUploadFile();
        example.transferUploadFile();
        UploadResult uploadResult = upload.waitForUploadResult();
        System.out.println(uploadResult.getKey());
        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getKey(), Constants.BUCKETID);
        System.out.println(url);

        // .cssg-methods-pragma

        // 使用完成之后销毁 Client，建议 Client 保持为单例
        example.cosClient.shutdown();
        System.out.println("helloworld");
        
        
    }
    /**
     * 可以正常在浏览器打开的照片
     * http://dwh-1257583875.cos.ap-guangzhou.myqcloud.com/mail.huwing.cn/245mm%E6%97%A5%E7%94%A8/10%E7%89%87/test19.jpg?sign=q-sign-algorithm%3Dsha1%26q-ak%3DAKIDpcLepzIanIetXxQqaG9vRzw515lGmiwptxZFBekEFNdL_oADAUq0k5_fvNntYaVd%26q-sign-time%3D1640425509%3B1671961509%26q-key-time%3D1640425509%3B1671961509%26q-header-list%3Dhost%26q-url-param-list%3D%26q-signature%3Db1e6fe8ec048bbf09f69540daf89ff13fe358646&x-cos-security-token=l8yopCRhgG8zsfIKN65UTTwja5YFQM1ad64391cd4c8439163cafa5ce2739209bVcgIsF-iaR9_YE2ApPJhEdjO4FUTFsNRKaUpMXIaYojSiZkD--1eVNJuWZ98qxjNdpdfusat_gKZA3cnWPz_gSltlDPc-6845vuiIKnkgBWJCoBBGNN97LEecYrMDyHUAdNvHND3nrLQiHN049vJqaHtS2of0EccxzFSlgbNelkUSvPoyu19YzFlcdS_IrCjDjrGwox0Eogu01wgdjlxb8kYt1PbJupa0GhzyEJWBDvAzz2ePH2z6poEcUxBM7QBI9xlUPlOQHKYG6_RiwT5YSeB13PCc-pey6W-Nt7uBd8qcC1XipjcTyVeJvEYhzf6tx8DBE17Rk2grmMBBTz_i7wD1Y6akJB4EnttXsF2fbep7osOTVLvDdHfZ2aG9RoAxtXdygy_1akg0uQc74p2oAjdXzM4l7lAuKL0syHpQh4QKx-GQXH35iX6RBl63QCNuG-LR6_rDv8-1_yYPofT17PXZu-_VpffeGmuNgCqcMWLx3GaOje_aOcjThbN5qdTmJdj-NCjFtGRPoIfLrwH4RxsOwUpnFi5she4SbQq0SjPlo3ZkH5YfcK2UzK_IkdszV5GKrSY0SM6lzxBvv6vaY6-VzdKwF3h2MfevGSFO-SSR-3c-ugpZEdkQgxQIfGBGQK3Q6ZBswQ3mFxT1Tu0Dw
	 *	
     */
    
    /**
     * @deprecated 上传照片到腾讯云并返回该图片链接地址
     * @param path
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getReturnUploadUrl(String path) throws InterruptedException, IOException,        NoSuchAlgorithmException {
        TransferUploadObject example = new TransferUploadObject();
        example.initClient();
        
        // 高级接口上传对象
//        Upload result = example.transferUploadFile();
        example.transferUploadFile(path);
        UploadResult uploadResult = upload.waitForUploadResult();
        System.out.println(uploadResult.getKey());
        String url = COSUrlGenerateUtils.getUrlStr(cosClient, uploadResult.getKey(), Constants.BUCKETID);
        System.out.println(url);

        // .cssg-methods-pragma

        // 使用完成之后销毁 Client，建议 Client 保持为单例
        example.cosClient.shutdown();
        System.out.println("helloworld");
    	
    	return url;
    }
    
    
}