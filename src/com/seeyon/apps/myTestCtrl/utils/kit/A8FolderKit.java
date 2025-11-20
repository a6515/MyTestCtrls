package com.seeyon.apps.myTestCtrl.utils.kit;

import com.seeyon.ctp.common.SystemEnvironment;

/**
 * Description
 * <pre>获取A8产品下面的文件夹路径</pre>
 * Date 2019年10月11日 上午9:42:49<br>
 * Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class A8FolderKit {
	
	private static String A8_APPLICAION_FLODER = "";
	

	/**
	 * Description:
	 * <pre>获取到A8的安装模目录，ApacheJetspeed目录的父目录</pre>
	 * @return
	 */
	public static String getApplicationFolder() {
		if("".equals(A8_APPLICAION_FLODER)) {
			String installPath = SystemEnvironment.getApplicationFolder();
			A8_APPLICAION_FLODER = installPath.split("ApacheJetspeed")[0].replaceAll("\\\\", "/");
		}
		return A8_APPLICAION_FLODER;
	}
	
	/**
	 * Description:
	 * <pre>获取配置文件的地址</pre>
	 * @return
	 */
	public static String getCodePropertiesPath() {
		String path = getApplicationFolder() + "ApacheJetspeed/webapps/seeyon/WEB-INF/classes/code.properties";
		return path;
	}

	
	public static String getPropertiesPath(String fileName) {
		String path = getApplicationFolder() + "ApacheJetspeed/webapps/seeyon/WEB-INF/classes/" + fileName;
		return path;
	}

	public static String getZSJCodePropertiesPath() {
		String path = getApplicationFolder() + "ApacheJetspeed/webapps/seeyon/WEB-INF/classes/ZSJcode.properties";
		return path;
	}
}
