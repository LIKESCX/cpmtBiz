package com.cpit.cpmt.biz.impl.exchange.operator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.AccessManageDao;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorInfoDao;
import com.cpit.cpmt.biz.impl.system.OAuth2Mgmt;
import com.cpit.cpmt.biz.utils.EmailUtil;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

@Service
public class AccessManageMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AccessManageMgmt.class);

	@Autowired
	private AccessManageDao accessManageDao;
	
	@Autowired
	private OperatorInfoMgmt operatorInfoMgmt;
	
	@Value("${secret.key.operator.id}")
	private String operatorKey;//运营商秘钥
	
	@Value("${secret.key.data}")
	private String dataKey;//数据加密秘钥==加密向量
	
	@Value("${secret.key.sign}")
	private String signKey;//签名秘钥
	
	@Value("${platform.operator.id}")
	private String platformId;//平台id
	
	@Value("${platform.interface.access.url}")
	private String accessUrl;//平台接口地址
	
	@Value("${https.platform.ca}")
	private String caCrt;//ca证书
	
	@Value("${https.platform.client}")
	private String clientCrt;//client证书
	
	@Value("${https.platform.client.key}")
	private String clientKey;//client密钥
	
	@Value("${ssl.client.keystore.url}")
	private String keyStoreFile;//keystore file
	
	@Value("${ssl.client.keystore.pwd}")
	private String keyStorePwd;//keystore pwd
	
	@Autowired
	private OAuth2Mgmt oauth2Mgmt;

	

	@Cacheable(cacheNames="operator-access-manage-by-id",key="#root.caches[0].name+#operatorId",unless="#result == null")
	public AccessManage getAccessManageInfoById(String operatorId) {
		
		return accessManageDao.selectByPrimaryKey(operatorId);
	}
	
	@Transactional
	public void addAccessManage(AccessManage accessManage) throws Exception {
		Integer ifAccess = accessManage.getIfAccess();
		if(ifAccess==AccessManage.IFACCESS_ON) {
			String operatorID = accessManage.getOperatorID();
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(operatorID);
			if(null==operatorInfo.getSecretKey()) {
				String keyCreate = "";
				OperatorInfoExtend operator = new OperatorInfoExtend();
				keyCreate = KeyCreate(16);
				if(keyCreate.equals(operatorKey)) {
					keyCreate = KeyCreate(16);
				}
				operator.setSecretKey(keyCreate);
				operator.setOperatorID(operatorID);
				operatorInfoMgmt.updateOperatorInfo(operator);
		    	//往oauth_token表里添加记录
		        oauth2Mgmt.addOAuth2(operatorID, keyCreate);
		        //发邮件
		        sendEmail(operatorInfo.getContactEmail(),keyCreate,operatorID);
			}
		}
		accessManageDao.insertSelective(accessManage);
	}

	public Page<AccessManage> getAccessManageList(AccessManage accessManage) {
		return accessManageDao.getAccessManageList(accessManage);
	}

	@Transactional
	@Caching(evict={
	 	@CacheEvict(cacheNames="operator-access-manage-by-id",key="#root.caches[0].name+#accessManage.operatorID")
	})
	public void updateAccessManage(AccessManage accessManage) {
		Integer ifAccess = accessManage.getIfAccess();
		if(null!=ifAccess) {
			String operatorID = accessManage.getOperatorID();
			AccessManage manage = accessManageDao.selectByPrimaryKey(operatorID);
			Integer oldIfAccess = manage.getIfAccess();
			OperatorInfoExtend infoExtend = operatorInfoMgmt.getOperatorInfoById(operatorID);
			if(oldIfAccess==AccessManage.IFACCESS_OFF && ifAccess==AccessManage.IFACCESS_ON && null==infoExtend.getSecretKey()) {
				OperatorInfoExtend operator = new OperatorInfoExtend();
				String keyCreate = "";
				keyCreate = KeyCreate(16);
				if(keyCreate.equals(operatorKey)) {
					keyCreate = KeyCreate(16);
				}
				operator.setSecretKey(keyCreate);
				operator.setOperatorID(operatorID);
				operatorInfoMgmt.updateOperatorInfo(operator);
				//往oauth_token表里添加记录
		        oauth2Mgmt.addOAuth2(operatorID, keyCreate);
				//发邮件
				sendEmail(infoExtend.getContactEmail(),keyCreate,operatorID);
			}
		}
		accessManageDao.updateByPrimaryKeySelective(accessManage);
	}

	@CacheEvict(cacheNames="operator-access-manage-by-id",key="#root.caches[0].name+#operatorId")
	public void delAccessManage(String operatorId) {
		accessManageDao.deleteByPrimaryKey(operatorId);
	}
	
	public static String KeyCreate(int KeyLength) {
        String base = "ABCDEF0123456789";
	    Random random = new Random();
	    StringBuffer Keysb = new StringBuffer();
	    // 生成指定位数的随机秘钥字符串
	    for (int i = 0; i < KeyLength; i++) {
	       int number = random.nextInt(base.length());
	       Keysb.append(base.charAt(number));
	    }
	    return Keysb.toString();
	}
	
	public void sendEmail(String email, String secretKey,String operatorId) {
		OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(operatorId);
		String subject = "秘钥信息-To"+operatorInfo.getOperatorName();
		/*String content = "以下信息用于访问平台https协议用：<br>"
		+"客户端秘钥:"+clientKey
		+"<br>客户端证书:"+clientCrt
		+"<br>ca证书:"+caCrt
		+"<br>【备注】<br>如采用java开发，可直接用下面keystore方式来访问平台的https协议，无需用上面3个证书。<br>"
		+"keystore file:"+keyStoreFile
		+"<br>keystore pwd:"+keyStorePwd
		+"<br>秘钥<br>"
		+"深圳安监平台密钥:"+operatorKey
		+"<br>贵公司运营商密钥:"+secretKey
		+"<br>签名秘钥:"+signKey
		+"<br>消息秘钥:"+dataKey
		+"<br>消息密钥初始化向量:"+dataKey
		+"<br>平台标识:"+platformId
		+"<br>平台接口地址:"+accessUrl;*/
		String content = changeContent(secretKey,operatorInfo);
		EmailUtil.send(subject, content,email);
	}
	
	public String changeContent(String secretKey,OperatorInfoExtend operatorInfo) {
		StringBuffer sb = new StringBuffer();
		BufferedInputStream bis = null;
		try {
			URL url = this.getClass().getClassLoader().getResource("emailTemplate/table.html");
			String urlString = url.toString();
			String fileUrl = urlString.substring(urlString.indexOf("/"));
			File file = new File(fileUrl);
			FileInputStream fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			int len = 0;
			byte[] temp = new byte[1024];
			while ((len = bis.read(temp)) != -1) {
				sb.append(new String(temp, 0, len));
			}
		} catch (Exception e) {
			logger.error("changeContent error:"+e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("changeContent error:"+e);
				}
			}
		}
		String newContent = new String();
		String content = sb.toString();
		newContent = content;
		newContent = newContent.replace("{clientKey}", clientKey);
		newContent = newContent.replace("{clientCrt}", clientCrt);
		newContent = newContent.replace("{caCrt}", caCrt);
		newContent = newContent.replace("{keystoreFile}", keyStoreFile);
		newContent = newContent.replace("{keyStorePwd}", keyStorePwd);
		newContent = newContent.replace("{operatorKey}", operatorKey);
		newContent = newContent.replace("{signKey}", signKey);
		newContent = newContent.replace("{secretKey}", secretKey);
		newContent = newContent.replace("{dataKey}", dataKey);
		newContent = newContent.replace("{platformId}", platformId);
		newContent = newContent.replace("{accessUrl}", accessUrl);
		newContent = newContent.replace("{operatorId}", operatorInfo.getOperatorID());
		newContent = newContent.replace("{operatorName}", operatorInfo.getOperatorName());
		return newContent;
	}

}
