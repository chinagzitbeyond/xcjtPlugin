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
import java.security.KeyPair;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class BucketLifecycle {

    private COSClient cosClient;
    private TransferManager transferManager;

    private String uploadId;
    private List<PartETag> partETags;
    private String localFilePath;

    /**
     * 设置存储桶生命周期
     */
    public void putBucketLifecycle() throws InterruptedException, IOException, NoSuchAlgorithmException {
        //.cssg-snippet-body-start:[put-bucket-lifecycle]
        List<BucketLifecycleConfiguration.Rule> rules = new ArrayList<BucketLifecycleConfiguration.Rule>();
        // 规则1  30天后删除路径以 hongkong_movie/ 为开始的文件
        BucketLifecycleConfiguration.Rule deletePrefixRule = new BucketLifecycleConfiguration.Rule();
        deletePrefixRule.setId("delete prefix xxxy after 30 days");
        deletePrefixRule.setFilter(new LifecycleFilter(new LifecyclePrefixPredicate("hongkong_movie/")));
        // 文件上传或者变更后, 30天后删除
        deletePrefixRule.setExpirationInDays(30);
        // 设置规则为生效状态
        deletePrefixRule.setStatus(BucketLifecycleConfiguration.ENABLED);
        
        // 规则2  20天后沉降到低频，一年后删除
        BucketLifecycleConfiguration.Rule standardIaRule = new BucketLifecycleConfiguration.Rule();
        standardIaRule.setId("standard_ia transition");
        standardIaRule.setFilter(new LifecycleFilter(new LifecyclePrefixPredicate("standard_ia/")));
        List<BucketLifecycleConfiguration.Transition> standardIaTransitions = new ArrayList<BucketLifecycleConfiguration.Transition>();
        BucketLifecycleConfiguration.Transition standardTransition = new BucketLifecycleConfiguration.Transition();
        standardTransition.setDays(20);
        standardTransition.setStorageClass(StorageClass.Standard_IA.toString());
        standardIaTransitions.add(standardTransition);
        standardIaRule.setTransitions(standardIaTransitions);
        standardIaRule.setStatus(BucketLifecycleConfiguration.ENABLED);
        standardIaRule.setExpirationInDays(365);
        
        // 将两条规则添加到策略集合中
        rules.add(deletePrefixRule);
        rules.add(standardIaRule);
        
        // 生成 bucketLifecycleConfiguration
        BucketLifecycleConfiguration bucketLifecycleConfiguration =
                new BucketLifecycleConfiguration();
        bucketLifecycleConfiguration.setRules(rules);
        
        // 存储桶的命名格式为 BucketName-APPID
        String bucketName = "examplebucket-1250000000";
        SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest =
                new SetBucketLifecycleConfigurationRequest(bucketName, bucketLifecycleConfiguration);
        
        // 设置生命周期
        cosClient.setBucketLifecycleConfiguration(setBucketLifecycleConfigurationRequest);
        
        //.cssg-snippet-body-end
    }

    /**
     * 获取存储桶生命周期
     */
    public void getBucketLifecycle() throws InterruptedException, IOException, NoSuchAlgorithmException {
        //.cssg-snippet-body-start:[get-bucket-lifecycle]
        // 存储桶的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = "examplebucket-1250000000";
        BucketLifecycleConfiguration queryLifeCycleRet =
                cosClient.getBucketLifecycleConfiguration(bucketName);
        List<BucketLifecycleConfiguration.Rule> ruleLists = queryLifeCycleRet.getRules();
        
        //.cssg-snippet-body-end
    }

    /**
     * 删除存储桶生命周期
     */
    public void deleteBucketLifecycle() throws InterruptedException, IOException, NoSuchAlgorithmException {
        //.cssg-snippet-body-start:[delete-bucket-lifecycle]
        //存储桶的命名格式为 BucketName-APPID
        String bucketName = "examplebucket-1250000000";
        cosClient.deleteBucketLifecycleConfiguration(bucketName);
        
        //.cssg-snippet-body-end
    }

    // .cssg-methods-pragma

    private void initClient() {
        String secretId = "COS_SECRETID";
        String secretKey = "COS_SECRETKEY";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("COS_REGION");
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        this.cosClient = new COSClient(cred, clientConfig);

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
        BucketLifecycle example = new BucketLifecycle();
        example.initClient();

        // 设置存储桶生命周期
        example.putBucketLifecycle();

        // 获取存储桶生命周期
        example.getBucketLifecycle();

        // 删除存储桶生命周期
        example.deleteBucketLifecycle();

        // .cssg-methods-pragma

        // 使用完成之后销毁 Client，建议 Client 保持为单例
        example.cosClient.shutdown();
    }

}