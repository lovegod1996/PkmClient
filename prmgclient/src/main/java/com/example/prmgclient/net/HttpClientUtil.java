package com.example.prmgclient.net;


import com.example.prmgclient.ConstantValue;
import com.example.prmgclient.GlobalParams;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
	private HttpClient client;

	private HttpPost post;
	private HttpGet get;
    private static Header[] headers;
	static {
        headers=new BasicHeader[2];
		headers[0]=new BasicHeader("Appkey","12343");
		headers[1]=new BasicHeader("Userid","");
	}


	public HttpClientUtil() {
		client = new DefaultHttpClient();
		// 判断是否需要设置代理信息
		if (StringUtils.isNotBlank(GlobalParams.PROXY)) {
			// 设置代理信息
			HttpHost host = new HttpHost(GlobalParams.PROXY, GlobalParams.PORT);
			client.getParams()
					.setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
		}
	}

	/**
	 * 向指定的链接发送xml文件
	 * 
	 * @param uri
	 * @param xml
	 */
	public InputStream sendXml(String uri, String xml) {
		post = new HttpPost(uri);

		try {
			StringEntity entity = new StringEntity(xml, ConstantValue.ENCONDING);
			post.setEntity(entity);

			HttpResponse response = client.execute(post);

			// 200
			if (response.getStatusLine().getStatusCode() == 200) {
				return response.getEntity().getContent();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	//发送post和get请求

	/**
	 * 发送post请求
	 * @param uri
	 * @param params
     * @return
     */
	public String sendPost(String uri, Map<String ,Object> params){
      post=new HttpPost(uri);

		post.setHeaders(headers);

		if(params!=null&&params.size()>0) {
			//编码
			List<BasicNameValuePair> parameters=new ArrayList<BasicNameValuePair>();
			for(Map.Entry<String,Object> item:params.entrySet()){
				BasicNameValuePair pair=new BasicNameValuePair(item.getKey(),item.getValue().toString());
				parameters.add(pair);
			}
			UrlEncodedFormEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(parameters, ConstantValue.ENCONDING);
				post.setEntity(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		HttpResponse response= null;
		try {
			response = client.execute(post);
			//200
			if(response.getStatusLine().getStatusCode()==200){
			return 	EntityUtils.toString(response.getEntity(),ConstantValue.ENCONDING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	return  null;
	}
	/**
	 * 发送Get请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public String sendGet(String uri, Map<String ,Object> params){
		uri+="?";
		return  "";
	}
}
