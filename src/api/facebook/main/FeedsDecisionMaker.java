package api.facebook.main;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import api.facebook.bean.Feeds;
import api.facebook.bean.Posts;
import api.facebook.bean.Seeds;
import api.facebook.dao.FeedsDao;
import api.facebook.dao.PostsDao;
import api.facebook.dao.SeedsDao;
import api.facebook.method.GetFeeds;
import api.facebook.method.GetPosts;
import api.facebook.util.AppContext;

/**
 * 决定扩展的用户是否具有feeds，而后更新seeds表中的has_feeds列
 * @author chenkedi
 *
 */
@Controller
public class FeedsDecisionMaker{
	
	@Resource
	private SeedsDao seedsDao;
	@Resource
	private PostsDao postsDao;
	@Resource
	private FeedsDao feedsDao;
	@Resource
	private GetPosts getPosts;
	@Resource
	private GetFeeds getFeeds;
	
	private static final Logger log = Logger.getLogger(ExpandSeeds.class);

	
	public static void main(String[] args){
		
		log.info("正在创建数据库连接和缓冲池...");
	    AppContext.initAppCtx();
	    log.info("数据库连接已连接！缓冲池已建立");
	    
	    FeedsDecisionMaker crawler= (FeedsDecisionMaker) AppContext.appCtx.getBean(FeedsDecisionMaker.class);
	    crawler.run();
	}

	public void run(){
		while(true){
			//读取所有的seeds记录
			List<Seeds> seeds = seedsDao.readSeedsForFeedDecisionMaker();
			
			for(Seeds seed : seeds){
				
				//设置累计变量
				int feedZeroTagAccumulate=0;
				String firstPost=null;
				String secondPost=null;
				String firstFeed=null;
				String secondFeed=null;
				
				//为防止posts和feeds请求间隔用户发了一条贴文这种小概率事件发生，每个种子比较两次，取计数达一次及以上的结果写入数据库
				for(int i=1;i<=2;i++){
					
					//====================请求posts，获取第一条贴文===============================
					//调用api得到posts的json数据
	    			JSONObject jsonObject=getPosts.callAPI((seed.getFacebookId()!=null)?seed.getFacebookId():seed.getUserName(),"posts");
	    			//数据抽取，将json转换为bean的格式
	    			//上一句返回的json对象有可能不含有post数据，所以用三元运算符判断，然后交给dataExtract处理
	    			List<Posts> postsList=getPosts.dataExtract(jsonObject.has("posts")?jsonObject.getJSONObject("posts"):jsonObject,seed.getSeedsId());//如果出现请求错误，seed可能为空，需要做处理
	    			if(!postsList.get(0).getStatus().equals("error")){
	    				if(postsList.size()>=1)
	    					firstPost=postsList.get(0).getMessage();
	    				if(postsList.size()>=2)
	    					secondPost=postsList.get(1).getMessage();
	    				
	    				log.info("第  "+i+" 次对比，第一条Post："+firstPost);
	    				log.info("第  "+i+" 次对比，第二条Post："+secondPost);
	    				
	    				//==================post请求成功，则请求feeds，获取第一条涂鸦======================================
	    				//调用api得到Feeds的json数据
		    			jsonObject=getFeeds.callAPI((seed.getFacebookId()!=null)?seed.getFacebookId():seed.getUserName(),"feed");
		    			//数据抽取，将json转换为bean的格式
		    			//上一句返回的json对象有可能没有feed键，所以用三元运算符判断，然后交给dataExtract处理(是错误或者空json)
		    			List<Feeds> feedsList=getFeeds.dataExtract(jsonObject.has("feed")?jsonObject.getJSONObject("feed"):jsonObject,seed.getSeedsId());
		    			if(!feedsList.get(0).getStatus().equals("error")){
		    				if(feedsList.size()>=1)
		    					firstFeed=feedsList.get(0).getMessage();
		    				if(feedsList.size()>=2)
		    					secondFeed=feedsList.get(1).getMessage();
		    				log.info("第  "+i+" 次对比，第一条Feed："+firstFeed);
		    				log.info("第  "+i+" 次对比，第二条Feed："+secondFeed);
		    			}
		    			else{
		    				log.error(seed.getName()+"的feed获取失败，i-1，重试本次对比！");
		    				i=i-1;
		    				log.info("为避免系统问题，睡眠10秒钟\n\n");
		    				try {
								Thread.sleep(10*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
		    			}	    				
	    			}
	    			else{
	    				log.error(seed.getName()+"的贴文获取失败，i-1，重试本次对比！");
	    				i=i-1;
	    				log.info("为避免系统问题，睡眠10秒钟\n\n");
	    				try {
							Thread.sleep(10*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    			}					
					
					//比较两者是否是同一条贴文，如果是，则表示该种子没有涂鸦墙，则将feedZeroTagAccumulate加1。
	    			if(firstFeed!=null && secondFeed!=null ){
	    				if(firstFeed.equals(firstPost) && secondFeed.equals(secondPost)){
	    					feedZeroTagAccumulate++;
		    				log.info("============第  "+i+" 次对比，feedZeroTagAccumulate加一。===========");
	    				}		
	    			}else{
	    				if(firstFeed==null && firstPost==null){
	    					feedZeroTagAccumulate++;
	    					log.info("============第  "+i+" 次对比，feedZeroTagAccumulate加一。===========");
	    				}
	    				if(secondFeed==null && secondFeed==null){
	    					feedZeroTagAccumulate++;
	    					log.info("============第  "+i+" 次对比，feedZeroTagAccumulate加一。===========");
	    				}
	    					
	    			}
				}
				
				log.info(seed.getName()+"======对比结果为，无feed计数次数："+feedZeroTagAccumulate+"=========\n");
				//如果feedZeroTagAccumulate>=1,则表明三次对比中，至少有两次对比说明该种子无涂鸦墙，将has_feed置为0。该项默认为1
				if(feedZeroTagAccumulate>=1){
					seedsDao.updateHasFeed(0, seed.getSeedsId());
					log.info("更新hasFeed项目成功!");
				}
				
			}
			
			log.info("本次has_feed扫描完成，睡眠一个月");
			long time=30*24*3600*1000;
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
