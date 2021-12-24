package com.qcloud.util.entity;

import java.io.Serializable;

/**
 * @deprecated 联合凭证实体
 * @author Mr Yuan
 */
public class FederationBean implements Serializable{


	private static final long serialVersionUID = 4151363503852376099L;
	
	/**
	 * @deprecated 凭证实体
	 *
	 */
	 public class CredentialsBean implements Serializable{
		
		String Token;
		String TmpSecretId;
		String TmpSecretKey;
		public String getToken() {
			return Token;
		}
		public void setToken(String token) {
			Token = token;
		}
		public String getTmpSecretId() {
			return TmpSecretId;
		}
		public void setTmpSecretId(String tmpSecretId) {
			TmpSecretId = tmpSecretId;
		}
		public String getTmpSecretKey() {
			return TmpSecretKey;
		}
		public void setTmpSecretKey(String tmpSecretKey) {
			TmpSecretKey = tmpSecretKey;
		}
		
		
	}
	private String ExpiredTime;
	private String RequestId;
	private CredentialsBean Credentials;
	
	
	public CredentialsBean getCredentials() {
		return Credentials;
	}
	public void setCredentials(CredentialsBean credentials) {
		Credentials = credentials;
	}
	public String getExpiredTime() {
		return ExpiredTime;
	}
	public void setExpiredTime(String expiredTime) {
		ExpiredTime = expiredTime;
	}
	public String getRequestId() {
		return RequestId;
	}
	public void setRequestId(String requestId) {
		RequestId = requestId;
	}
	
	

}
