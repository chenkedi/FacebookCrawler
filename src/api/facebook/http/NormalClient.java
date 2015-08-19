package api.facebook.http;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 生成常规的Client，以适用于使用VPN的情况
 * @author chenkedi
 *
 */
public class NormalClient extends ClientFactory
{
	@Override
	public CloseableHttpClient createClient(){
		
		SSLConnectionSocketFactory sslsf=buildSSLSocket();
		
		httpClient = HttpClients.custom()
				 .setSSLSocketFactory(sslsf)
				 .build(); 
		
		return httpClient;
	}
}
