package api.facebook.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.log4j.Logger;

/**
 * 生成使用代理的SSL HttpClient，适用于使用代理的环境
 * 
 * 后期改进需要将代理IP和代理端口号写进配置文件
 * 
 * @author chenkedi
 *
 */
public class ProxyClient extends ClientFactory
{
	private static final Logger log=Logger.getLogger(ProxyClient.class);
	private String proxyIp;
	private int proxyPort;

	@Override
	public CloseableHttpClient createClient() {
		
		SSLConnectionSocketFactory sslsf=buildSSLSocket();
		
		
		Properties prop = new Properties();
		
		InputStream is = ProxyClient.class.getResourceAsStream("/params.properties");
		try{
			if(is!=null){
				prop.load(is);
			}
			
			if(prop.getProperty("params.proxyIp")!=null && prop.getProperty("params.proxyPort")!=null){
				 proxyIp=prop.getProperty("params.proxyIp");
				 proxyPort=Integer.valueOf(prop.getProperty("params.proxyPort"));
			}else{
				log.error("params.properties 文件中的proxyIp 或 proxyPort字段为空！读取失败");
			}
			
		}catch(IOException e){
			e.printStackTrace();
			log.error("params useProxy 读取出现错误！"+e.getMessage());
		}finally{
			if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                	
                }
            }
		}
		
		
		//设置代理主机
		HttpHost proxy = new HttpHost(proxyIp, proxyPort);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		
		httpClient = HttpClients.custom()
				 .setSSLSocketFactory(sslsf)
				  .setRoutePlanner(routePlanner)
				 .build(); 
		
		return httpClient;
	}

}
