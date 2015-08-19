package price.main;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import api.facebook.main.PostsInfoCrawler;
import api.facebook.util.AppContext;
import price.dao.PriceDataDao;

@Controller
public class MultiThread extends Thread
{
	int start;
	int threadNum;
	@Resource
	private PriceDataDao priceDataDao;
	
	
	public void setStart(int start) {
		this.start = start;
	}
	public void setThreadNum(int threadNum){
		this.threadNum=threadNum;
	}

	public static void main(String[] args) {
		
		System.out.println("正在创建数据库连接和缓冲池...");
	    AppContext.initAppCtx();
	    System.out.println("数据库连接已连接！缓冲池已建立");
	    
	    MultiThread th1= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    
	    AppContext.initAppCtx();
	    MultiThread th2= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    
	    AppContext.initAppCtx();
	    MultiThread th3= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    
	    AppContext.initAppCtx();
	    MultiThread th4= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    
	    AppContext.initAppCtx();
	    MultiThread th5= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    
	    AppContext.initAppCtx();
	    MultiThread th6= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    
	    AppContext.initAppCtx();
	    MultiThread th7= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    
	    AppContext.initAppCtx();
	    MultiThread th8= (MultiThread) AppContext.appCtx.getBean(MultiThread.class);
	    th1.setStart(1);
	    th1.setThreadNum(8);
	    
	    th2.setStart(2);
	    th2.setThreadNum(8);
	    
	    th3.setStart(3);
	    th3.setThreadNum(8);
	    
	    th4.setStart(4);
	    th4.setThreadNum(8);
	    
	    th5.setStart(5);
	    th5.setThreadNum(8);
	    
	    th6.setStart(6);
	    th6.setThreadNum(8);
	    
	    th7.setStart(7);
	    th7.setThreadNum(8);
	    
	    th8.setStart(8);
	    th8.setThreadNum(8);
	    
		th1.start();
		th2.start();
		th3.start();
		th4.start();
		th5.start();
		th6.start();
		th7.start();
		th8.start();

	}
	
	@Override
	public void run(){
		for(;start<=24;){
			System.out.println(start);
			start+=threadNum;
		}
		
	}

}
