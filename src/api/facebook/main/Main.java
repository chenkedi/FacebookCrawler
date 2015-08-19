package api.facebook.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;






public class Main
{
	private final static String BASE_URL="https://graph.facebook.com/";

	public static void main(String[] args){
		CloseableHttpClient httpclient = null ;
		
		//设置代理主机
		HttpHost proxy = new HttpHost("127.0.0.1", 1080);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		
		
		//初始化证书容器
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//打开证书文件
         FileInputStream instream = null;
		try {
			instream = new FileInputStream(new File("certs.keystore"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}  
         try {  
             // 加载keyStore  
             trustStore.load(instream, "changeit".toCharArray());  //这里的字符串是keystore的密码，java系统自带的密码默认是changeit
         } catch (CertificateException e) {  
             e.printStackTrace();  
         } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {  
             try {  
                 instream.close();  
             } catch (Exception ignore) {  
             }  
         }  
         // 相信自己的CA和所有自签名的证书  
         try {
			SSLContext sslContext = SSLContexts.custom()
									.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
									.build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			 httpclient = HttpClients.custom()
					 .setSSLSocketFactory(sslsf)
					  .setRoutePlanner(routePlanner)
					 .build(); 
		} catch (KeyManagementException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		}
		
		
		String accessToken="1653394544897466|878b93ffea9985a0835dc9442756e05b";
		URI uri=null;
			try {
				uri = new URIBuilder()
				.setScheme("https")
				.setHost("graph.facebook.com")
				.setPath("/tsaiingwen")
				.setParameter("fields", "posts")
				.setParameter("access_token", "1628625687408087|878b93ffea9985a0835dc9442756e05b")
				.build();
			
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			HttpGet httpGet = new HttpGet(uri);

			//HttpGet httpGet = new HttpGet("https://graph.facebook.com/tsaiingwen?fields=posts&access_token=1653394544897466|878b93ffea9985a0835dc9442756e05b");
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			System.out.println(response.getStatusLine().toString());
			String entityString= EntityUtils.toString(entity, "utf-8");
//			JSONObject resultJsonObject = null;
//			if(entityString!=null && entityString!=""){
//				 resultJsonObject=new JSONObject(entityString);
//			}
//			JSONArray jsonArray=resultJsonObject.getJSONObject("posts").getJSONArray("data");
//			for(int i=0;i<jsonArray.length();i++){
//				JSONObject tempJson=jsonArray.getJSONObject(i);
//				System.out.println(tempJson.getString("message"));
//			}
			System.out.println(entityString);
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			 // 关闭连接,释放资源    
	        try {  
	            httpclient.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
		}

	}

}
