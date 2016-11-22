package com.example.prmgclient;

public interface ConstantValue {
	// class :public static final
	String ENCONDING="utf-8";
	/**
	 * 代理的id
	 */
	String AGENTERID="889931";
//	<source>ivr</source>
	/**
	 * 信息来源
	 */
	String SOURCE="ivr";
	//<compress>DES</compress>
	/**
	 * body里面的数据加密算法
	 */
	String COMPRESS="DES";
	
	/**
	 * 子代理商的密钥(.so) JNI
	 */
	String AGENTER_PASSWORD = "9ab62a694d8bf6ced1fab6acd48d02f8";
	
	/**
	 * des加密用密钥
	 */
	String DES_PASSWORD = "9b2648fcdfbad80f";
	/**
	 * 服务器地址
	 */
	String LOTTERY_URI = "http://10.0.2.2:8080/ZCWService/Entrance";// 10.0.2.2模拟器如果需要跟PC机通信127.0.0.1
	// String LOTTERY_URI = "http://192.168.1.100:8080/ZCWService/Entrance";// 10.0.2.2模拟器如果需要跟PC机通信127.0.0.1
	String COMMON="http://192.168.191.1:8080/prmgserver";

	String PARKDETAIL="/parkdetail";

	String PARKLIST="/parklist";

	String LOGIN="/login";

	String REGISTER="/register";

	String RECORD="/record";

	String FINDUSERBYNAME="/finduerbyname";

	String FINDPARKBYWIFI="/findParkByWifiName";

	String UPDATEPARKIN="/updatein";

	String UPDATEPARKOUT="/updateout";

	String GETINTIME="/getIntime";

	String GETINRECORD="/getin";

	String  GETOUTRECORD="/updatefee";

	String USERPAY="/uerPay";

	//定义wifi密码
	String WIFIPSD="1234567890";

	//检查服务器返回数据错误
	String ERROR="error";
	/**
	XmlPullParser parser = Xml.newPullParser();
			try {
				parser.setInput(is, ConstantValue.ENCONDING);

				int eventType = parser.getEventType();
				String name;

				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						name = parser.getName();
						if ("".equals(name)) {

						}
						break;
					}
					eventType = parser.next();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
	 */

}
