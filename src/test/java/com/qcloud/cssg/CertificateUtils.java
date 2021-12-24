package com.qcloud.cssg;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.qcloud.util.Constants;
import com.qcloud.util.entity.FederationBean;
import com.qcloud.util.entity.FederationBean.CredentialsBean;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sts.v20180813.StsClient;
import com.tencentcloudapi.sts.v20180813.models.AssumeRoleRequest;
import com.tencentcloudapi.sts.v20180813.models.AssumeRoleResponse;

/**
 * @author Mr Yuan
 * @deprecated 腾讯云临时凭证工具类
 *
 */
public class CertificateUtils {
	
	/**
	 * @deprecated 取的联合凭证实体
	 * @return FederationBean
	 */
	public static FederationBean getFederation() {
		
		try{

            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(Constants.SECRETID, Constants.SECRETKEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(Constants.ENDPOINTURL);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            StsClient client = new StsClient(cred, Constants.REGION, clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            AssumeRoleRequest req = new AssumeRoleRequest();
            req.setRoleArn(Constants.ROLEARN);
            req.setRoleSessionName(Constants.ROLESESSIONNAME);
            req.setDurationSeconds(Constants.DURATIONSECONDS);
            // 返回的resp是一个AssumeRoleResponse的实例，与请求对象对应
            AssumeRoleResponse resp = client.AssumeRole(req);

            // 输出json格式的字符串回包
            String jsonStr = AssumeRoleResponse.toJsonString(resp);
            if(!TextUtils.isEmpty(jsonStr)) {
                FederationBean entity = JSONObject.parseObject(AssumeRoleResponse.toJsonString(resp), FederationBean.class);
                System.out.println(jsonStr);
                return entity;
            }
            
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
		
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FederationBean entity = getFederation();
		CredentialsBean initEntity = entity.getCredentials();
		System.out.println(entity.getExpiredTime());
		System.out.println(initEntity.getTmpSecretId());
		
	}

}
