package com.qcloud.cssg;

import java.util.TreeMap;

import com.qcloud.util.Constants;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;

public class COSFederationUtils {
	
	public static Response getCredentials() {
		TreeMap<String, Object> config = new TreeMap<String, Object>();

        try {
            //这里的 SecretId 和 SecretKey 代表了用于申请临时密钥的永久身份（主账号、子账号等），子账号需要具有操作存储桶的权限。
            // 替换为您的云 api 密钥 SecretId
            config.put("secretId", Constants.SECRETID);
            // 替换为您的云 api 密钥 SecretKey
            config.put("secretKey", Constants.SECRETKEY);

            // 设置域名: 
            // 如果您使用了腾讯云 cvm，可以设置内部域名
            //config.put("host", "sts.internal.tencentcloudapi.com");

            // 临时密钥有效时长，单位是秒，默认 1800 秒，目前主账号最长 2 小时（即 7200 秒），子账号最长 36 小时（即 129600）秒
            config.put("durationSeconds", Constants.DURATIONSECONDS);

            // 换成您的 bucket
            config.put("bucket", Constants.BUCKETID);
            // 换成 bucket 所在地区
//            config.put("region", Constants.REGION);
            config.put("region", Constants.BUCKETREGION);

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径
            // 列举几种典型的前缀授权场景：
            // 1、允许访问所有对象："*"
            // 2、允许访问指定的对象："a/a1.txt", "b/b1.txt"
            // 3、允许访问指定前缀的对象："a*", "a/*", "b/*"
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefixes", new String[] {
                    "mail.huwing.cn/*"
            });

            // 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
            // 简单上传、表单上传和分块上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
            		"name/cos:*",
                     // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分块上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload",
                    "name/cos:GetObject"
            };
            config.put("allowActions", allowActions);
            Response response = CosStsClient.getCredential(config);
            if(null != response) {
           /** 	System.out.println(response.credentials.tmpSecretId);
                System.out.println(response.credentials.tmpSecretKey);
                System.out.println(response.credentials.sessionToken);**/
                return response;
            }
           
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("no valid secret !");
        }
		
		return null;
	}
	
	
	public static void main(String[] args) {
		getCredentials();
	}

}
