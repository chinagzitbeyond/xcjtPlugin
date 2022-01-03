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
 * @deprecated ��Ѷ����ʱƾ֤������
 *
 */
public class CertificateUtils {

	/**
	 * @deprecated ȡ������ƾ֤ʵ��
	 * @return FederationBean
	 */
	public static FederationBean getFederation() {

		try {

			// ʵ����һ����֤���������Ҫ������Ѷ���˻�secretId��secretKey,�˴�����ע����Կ�Եı���
			// ��Կ��ǰ��https://console.cloud.tencent.com/cam/capi��վ���л�ȡ
			Credential cred = new Credential(Constants.SECRETID, Constants.SECRETKEY);
			// ʵ����һ��httpѡ���ѡ�ģ�û�����������������
			HttpProfile httpProfile = new HttpProfile();
			httpProfile.setEndpoint(Constants.ENDPOINTURL);
			// ʵ����һ��clientѡ���ѡ�ģ�û�����������������
			ClientProfile clientProfile = new ClientProfile();
			clientProfile.setHttpProfile(httpProfile);
			// ʵ����Ҫ�����Ʒ��client����,clientProfile�ǿ�ѡ��
			StsClient client = new StsClient(cred, Constants.REGION, clientProfile);
			// ʵ����һ���������,ÿ���ӿڶ����Ӧһ��request����
			AssumeRoleRequest req = new AssumeRoleRequest();
			req.setRoleArn(Constants.ROLEARN);
			req.setRoleSessionName(Constants.ROLESESSIONNAME);
			req.setDurationSeconds((long) Constants.DURATIONSECONDS);
			// ���ص�resp��һ��AssumeRoleResponse��ʵ��������������Ӧ
			AssumeRoleResponse resp = client.AssumeRole(req);

			// ���json��ʽ���ַ����ذ�
			String jsonStr = AssumeRoleResponse.toJsonString(resp);
			if (!TextUtils.isEmpty(jsonStr)) {
				FederationBean entity = JSONObject.parseObject(AssumeRoleResponse.toJsonString(resp),
						FederationBean.class);
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
