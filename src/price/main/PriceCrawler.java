package price.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.springframework.stereotype.Controller;

import api.facebook.http.ClientFactory;
import api.facebook.http.NormalClient;
import api.facebook.main.FeedsInfoCrawler;
import api.facebook.method.GetSeeds;
import api.facebook.method.GraphAPI;
import api.facebook.util.AppContext;
import price.bean.PriceData;
import price.dao.PriceDataDao;

@Controller
public class PriceCrawler extends Thread
{
	int start;
	int threadNum;
	@Resource
	private PriceDataDao priceDataDao;
	
	
	
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public static void main(String[] args) {
		System.out.println("正在创建数据库连接和缓冲池...");
	    AppContext.initAppCtx();
	    System.out.println("数据库连接已连接！缓冲池已建立");
	    
	    PriceCrawler th1= (PriceCrawler) AppContext.appCtx.getBean(PriceCrawler.class);
	    
	    AppContext.initAppCtx();
	    PriceCrawler th2= (PriceCrawler) AppContext.appCtx.getBean(PriceCrawler.class);
	    
	    AppContext.initAppCtx();
	    PriceCrawler th3= (PriceCrawler) AppContext.appCtx.getBean(PriceCrawler.class);
	    
	    AppContext.initAppCtx();
	    PriceCrawler th4= (PriceCrawler) AppContext.appCtx.getBean(PriceCrawler.class);
	    
	    th1.setStart(2221);
	    th1.setThreadNum(4);
	    
	    th2.setStart(2222);
	    th2.setThreadNum(4);
	    
	    th3.setStart(2223);
	    th3.setThreadNum(4);
	    
	    th4.setStart(2224);
	    th4.setThreadNum(4);

	    
		th1.start();
		th2.start();
		th3.start();
		th4.start();


	}
	
	public void run(){
		
		//设置代理主机
		HttpHost proxy = new HttpHost("127.0.0.1", 1080);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		CloseableHttpClient httpClient=HttpClients.custom()
				.setRoutePlanner(routePlanner)
				.build();
		
		GraphAPI api=new GetSeeds();
		URI uri=null;
		for(;start<=10000;){
			
			String url="http://www.vegnet.com.cn/Price/List_ar110000_p"+start+".html";
			
			try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//请求URL并获取response
			CloseableHttpResponse response= api.getResponse(httpClient, uri);
			
			//获得请求的实体
			HttpEntity entity = response.getEntity();
			
			String entityString=api.getEntityString(entity);
			
			//create an instance of HtmlCleaner
			HtmlCleaner cleaner = new HtmlCleaner();
			 
			TagNode node = cleaner.clean(entityString);
			//optionally find parts of the DOM or modify some nodes
			//TagNode[] myNodes = node.getElementsByAttValue("class", "pri_k", false, false);
			// and/or
			Object[] myNodes=null;
			try {
				myNodes = node.evaluateXPath("//div[@class=\"pri_k\"]/p");
			} catch (XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// and/or
//			aNode.removeFromTree();
//			// and/or
//			aNode.addAttribute(attName, attValue);
//			// and/or
//			aNode.removeAttribute(attName, attValue);
//			// and/or
//			cleaner.setInnerHtml(aNode, htmlContent);
			// and/or do some other tree manipulation/traversal
			 
			// serialize a node to a file, output stream, DOM, JDom...
//			new XXXSerializer(props).writeXmlXXX(aNode, ...);
//			myJDom = new JDomSerializer(props, true).createJDom(aNode);
//			myDom = new DomSerializer(props, true).createDOM(aNode);
			List<PriceData> priceDataList=new ArrayList<PriceData>();
			for(Object nodes:myNodes){
				TagNode n = (TagNode) nodes;
				Object[] spans=null;
				try {
					spans=n.evaluateXPath("/span");
					
				} catch (XPatherException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PriceData priceData=new PriceData();

				
				TagNode date = (TagNode) spans[0];
				priceData.setDate(date.getText().toString());
				
				
				TagNode type = (TagNode) spans[1];
				priceData.setType(type.getText().toString());
				
				TagNode Market = (TagNode) spans[2];
				priceData.setMarket(Market.getText().toString());
				
				TagNode LowestPrice = (TagNode) spans[3];
				priceData.setLowestPrice(LowestPrice.getText().toString());
				
				TagNode HighestPrice = (TagNode) spans[4];
				priceData.setHighestPrice(HighestPrice.getText().toString());
				
				TagNode AvgPrice = (TagNode) spans[5];
				priceData.setAvgPrice(AvgPrice.getText().toString());
				
				TagNode Unit = (TagNode) spans[6];
				priceData.setUnit(Unit.getText().toString());
			
				priceDataList.add(priceData);
				
			}
			
			priceDataDao.batchInsert(priceDataList);
			System.out.println("插入成功！！");
			start=start+threadNum;
		}

		
		
	}
}
