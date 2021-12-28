package osc;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
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
	public static COSClient usePermanentInitCredential(String secretId,String secretKey,String bucketRegion) {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(bucketRegion);
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
		return cosClient;
	}
	
	/**
	 * @deprecated 临时秘钥初始化凭证
	 */
	public static COSClient  useTempInitCredential(String secretId,String secretKey,int durationSeconds,String bucket,String bucketRegion) {
		Response response = COSFederationUtils.getCredentials(secretId,secretKey,durationSeconds,bucket,bucketRegion);
		String tmpSecretId =  response.credentials.tmpSecretId;
		String tmpSecretKey = response.credentials.tmpSecretKey;
		String sessionToken = response.credentials.sessionToken;
		BasicSessionCredentials cred = new BasicSessionCredentials(tmpSecretId, tmpSecretKey, sessionToken);
		Region region = new Region(bucketRegion);
		ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
		return cosClient;
	}
	
}
