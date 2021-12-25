package com.qcloud.cssg;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.qcloud.util.Constants;
import com.tencent.cloud.Response;

/**
 * @deprecated 服务端凭证
 * @author 
 *
 */
public class ServiceCredential {

	/**
	 * @deprecated 永久密钥初始化凭证
	 */
	public static COSClient usePermanentInitCredential() {
		String secretId =  Constants.SECRETID;
		String secretKey = Constants.SECRETKEY;
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(Constants.BUCKETREGION);
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
		return cosClient;
	}
	
	/**
	 * @deprecated 临时秘钥初始化凭证
	 */
	public static COSClient  useTempInitCredential() {
		Response response = COSFederationUtils.getCredentials();
		String tmpSecretId =  response.credentials.tmpSecretId;
		String tmpSecretKey = response.credentials.tmpSecretKey;
		String sessionToken = response.credentials.sessionToken;
		BasicSessionCredentials cred = new BasicSessionCredentials(tmpSecretId, tmpSecretKey, sessionToken);
		Region region = new Region(Constants.BUCKETREGION);
		ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
		return cosClient;
	}
	
}
