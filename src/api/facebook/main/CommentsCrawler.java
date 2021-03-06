package api.facebook.main;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import api.facebook.bean.Comments;
import api.facebook.bean.Posts;
import api.facebook.bean.To;
import api.facebook.dao.CommentsDao;
import api.facebook.dao.PostsDao;
import api.facebook.dao.ToDao;
import api.facebook.method.GetComments;
import api.facebook.util.AppContext;
import api.facebook.util.Params;

@Controller
public class CommentsCrawler
{
	@Resource
	private PostsDao postsDao;
	@Resource
	private CommentsDao commentsDao;
	@Resource
	private ToDao toDao;
	@Resource
	private Params params;
	@Resource
	GetComments getComments;
	
	private int cycle=1;//记录爬虫启动以来轮询的次数
	private static final Logger log = Logger.getLogger(CommentsCrawler.class);
	
	public static void main(String[] args){
		log.info("正在创建数据库连接和缓冲池...");
	    AppContext.initAppCtx();
	    log.info("数据库连接已连接！缓冲池已建立");
	    
	    CommentsCrawler crawler= (CommentsCrawler) AppContext.appCtx.getBean(CommentsCrawler.class);
	    crawler.run();
	}
	
	/**
	 * 爬行器主方法
	 */
	public void run(){
		while(true){
		
				//查询Crawed_Comments为0的seed，表示还未经过第一次遍历，需要首次获取PageLink
				List<Posts> posts=postsDao.readPostsForComments(params.getCommentsLength(),0);//获得需要爬取的种子队列
				
				if(posts.size()!=0){
					for(Posts temp : posts){

		    			
		    			//调用api得到To的json数据
		    			JSONObject toJsonObjct=getComments.callAPI(temp.getMessageId(), "to");
		    			//此处处理的是To的数据,如果json中有to这个键，则解析数据，否则，输出错误原因
		    			if(toJsonObjct.has("to")){
		    				List<To> toList=getComments.toDataExtract(toJsonObjct.getJSONObject("to"), temp.getPostId(), temp.getSeedsId());
		    				toDao.batchInsert(toList);
		    				log.info(temp.getMessageId()+"号贴文的To信息插入成功!");
		    				
		    			}else{
		    				List<To> toList=getComments.toDataExtract(toJsonObjct, temp.getPostId(), temp.getSeedsId());
		    			}
		    			
		    			//调用api得到Comments的json数据
		    			JSONObject jsonObject=getComments.callAPI(temp.getMessageId(),"comments");
		    			//数据抽取，将json转换为bean的格式
		    			//上一句返回的json对象有可能是含有错误信息的json对象，所以用三元运算符判断，然后交给dataExtract处理
		    			//此处处理的是Comments的数据，如果一条贴文没有评论，则不含有comments键
		    			
		    			List<Comments> commentsList=getComments.commentsDataExtract(jsonObject.has("comments")?jsonObject.getJSONObject("comments"):jsonObject,
		    					temp.getPostId(),temp.getMessageId());
		    			
		    			if(!commentsList.get(0).getStatus().equals("error")){
		    				//即便是第一次初始化遍历，某些贴文也可能没有评论，即为”empty“的情况，这与posts和feeds第一次遍历不肯能为空的情况不同
		    				if(!commentsList.get(0).getStatus().equals("empty")){
			    				commentsDao.batchInsert(commentsList);
			    				log.info(temp.getMessageId()+"号贴文的Comments批量插入成功!");
			    				
			    				//将翻页的链接写入posts,此处第一次遍历，previous和next都要更新
			    				postsDao.updatePreviousPage(commentsList.get(0),temp.getPostId());
			    				postsDao.updateNextPage(commentsList.get(0),temp.getPostId());
			    				log.info(temp.getMessageId()+"号贴文的翻页链接更新成功!");
			    				
			    				//将已经爬取过的种子标记值更新为1
			    				String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
			    				postsDao.updateCrawed(sql,temp.getPostId(),1);
			    				log.info(temp.getMessageId()+"号贴文的Crawed_Comments爬取状态更新为1成功!\n\n");
		    				}else{
		    					log.info(temp.getMessageId()+"号贴文下面没有评论，继续采集下一个种子！");
		    					//将已经爬取过的种子标记值更新为-1，因为它下面没有评论，不需要继续爬行历史评论
			    				String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
			    				postsDao.updateCrawed(sql,temp.getPostId(),-1);
			    				log.info(temp.getMessageId()+"号贴文的Crawed_Comments爬取状态更新为-1成功!\n\n");
		    				}
		    			}
		    			else{
		    				
		    				log.error(temp.getMessageId()+"号贴文的Comments获取失败，继续采集下一个种子！");
		    				if(commentsList.get(0).getCodeMessage().equals("100")){
		    					log.error(temp.getMessageId()+"号贴文不符合目前GraphAPI请求方式，放弃采集！设置为-3");
		    					String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
			    				postsDao.updateCrawed(sql,temp.getPostId(),-3);
			    				log.info(temp.getMessageId()+"号贴文的Crawed_Comments爬取状态更新为-3成功!\n\n");
		    				}
		    				log.info("为避免系统问题，睡眠10秒钟\n\n");
		    				try {
								Thread.sleep(10*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
		    			}
					}
				}else{
					//当种子都经过第一次爬取后，就要切换到翻页链接的爬取，使用深度优先。
					//使用facebook提供的分页链接爬取，返回的json对象外面没有包裹说明变量（如Comments等）
					//使用这个翻页链接可以避免数据重复
					
					//查询CrawedComments为1的值，该值表示种子还未经过第二轮，即爬取历史数据
					posts=postsDao.readPostsForComments(params.getCommentsLength(),1,"comments_next_page");//获得需要爬取的Crawed_Comments为1的种子队列
					if(posts.size()!=0){
						for(Posts temp : posts){
							
			    			//调用api得到Comments的json数据
			    			JSONObject jsonObject=getComments.callAPI(temp.getMessageId(),"comments",temp.getCommentsNextPage());
			    			
			    			//数据抽取，将json转换为bean的格式
			    			//上一句返回的json对象有可能是含有错误信息的json对象，但由于正确和错误的类型的json对象都是顶层对象，不需要做判断
			    			List<Comments> commentsList=getComments.commentsDataExtract(jsonObject,temp.getPostId(),temp.getMessageId());
			    			
			    			//开始深度优先爬取历史feed
			    			if(!commentsList.get(0).getStatus().equals("error")){
			    				if(!commentsList.get(0).getStatus().equals("empty")){
			    					
			    					commentsDao.batchInsert(commentsList);
				    				log.info(temp.getMessageId()+"号贴文的Comments批量插入成功!");
				    				
				    				//将最新的翻页的链接写入posts，注意，此处只应该更新nextpage链接
				    				postsDao.updateNextPage(commentsList.get(0),temp.getPostId());
				    				log.info(temp.getMessageId()+"号贴文的Next翻页链接更新成功!\n\n");
				    				
			    				}else{
			    					log.info(temp.getMessageId()+"号贴文的历史Comment为空，已采集完毕，将Crawed_Comment状态更新为-1（历史feed枯竭）继续采集下一个种子！");
			    					
			    					//将已经爬取过的种子标记值更新为-1,表示历史数据枯竭
				    				String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
				    				postsDao.updateCrawed(sql,temp.getPostId(),-1);
				    				log.info("种子的Crawed_Comments爬取状态更新为-1成功!\n\n");
			    				}		
			    			}else{
			    				log.error(temp.getMessageId()+"号贴文的Commet获取失败，继续采集下一个种子！");
			    				//此处可能会出现”(#1) An unknown error has occurred.“错误
			    				//这表示爬取到一定深度，facebook给出的翻页链接出现自己的API不能识别的情况（一般是因为时间过于久远，导致api链接与当前api不兼容）
			    				//如果出现此错误，说明这个种子不可能遍历到历史数据为空。由于这是facebook给出的翻页链接本身有问题，所以跳过并标记此种子，继续下一个
			    				if(commentsList.get(0).getCodeMessage().equals("1")){
			    					log.info(temp.getMessageId()+"号贴文的历史Comment出现Facebook自身链接的错误(code："+commentsList.get(0).getCodeMessage()+"），绝大部分数据已采集完毕，将Crawed_post状态更新为-1，继续采集下一个种子！");
			    					String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
				    				postsDao.updateCrawed(sql,temp.getPostId(),-1);
				    				log.info("种子的Crawed_Comments爬取状态更新为-1成功!\n\n");
			    				}
			    				
			    				log.info("为避免系统问题，睡眠10秒钟\n\n");
			    				try {
									Thread.sleep(10*1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
			    				
			    			}
						}
						
					}else{//为1的种子全部爬完，历史数据采集完毕，crawed_Comments现在应该被全部置为-1可以开始向未来数据在线爬取了
						
						//由于贴文具有时效性，所以我们取种子贴文时，只取前三天的贴文。我们认为，只有前三天的贴文才有采集最新评论的意义
						posts=postsDao.readPostsForComments(params.getCommentsLength(),-1,"comments_previous_page");//获得需要爬取的Crawed_Comments为1的种子队列
						if(posts.size()!=0){
							for(Posts temp : posts){	
				    			
				    			//调用api得到Comments的json数据
				    			JSONObject jsonObject=getComments.callAPI(temp.getMessageId(),"feed",temp.getCommentsPreviousPage());
				    			
				    			//数据抽取，将json转换为bean的格式
				    			//上一句返回的json对象有可能是含有错误信息的json对象，但由于正确和错误的类型的json对象都是顶层对象，不需要做判断
				    			List<Comments> commentsList=getComments.commentsDataExtract(jsonObject,temp.getPostId(),temp.getMessageId());
				    			
				    			//开始在线爬取历史数据已采集完毕的Comments的未来数据
				    			if(!commentsList.get(0).getStatus().equals("error")){
				    				if(!commentsList.get(0).getStatus().equals("empty")){
				    					
				    					commentsDao.batchInsert(commentsList);
					    				log.info(temp.getMessageId()+"号贴文的Comments批量插入成功!");
					    				
					    				//将最新的翻页的链接写入posts，注意，此处只应该更新nextpage链接
					    				postsDao.updatePreviousPage(commentsList.get(0),temp.getPostId());
					    				log.info(temp.getMessageId()+"号贴文的previous翻页链接更新成功!");
					    				
					    				String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
					    				postsDao.updateCrawed(sql,temp.getPostId(),-2);
					    				log.info("种子的Crawed_Comments爬取状态更新为-2（已经经过至少一轮未来数据采集）成功!\n\n");
					    				
				    				}else{
				    					log.info(temp.getMessageId()+"号贴文暂时没有新评论，将crawed_Comments置为-2，继续采集下一个种子！");
				    					//将已经爬取过的种子标记值更新为-2,即使它没有采到数据，防止其很久没有发帖，影响其他活跃种子的采集，-2表示经过了一次未来数据的遍历
					    				String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
					    				postsDao.updateCrawed(sql,temp.getPostId(),-2);
					    				log.info("种子的Crawed_Comments爬取状态更新为-2（已经经过至少一轮未来数据采集）成功!\n\n");
					    
				    				}		
				    			}else{
				    				log.error(temp.getMessageId()+"号贴文的Comments获取失败，继续采集下一个种子！");
				    				//此处可能会出现”(#1) An unknown error has occurred.“错误
				    				//这表示爬取到一定深度，facebook给出的翻页链接出现自己的API不能识别的情况（一般是因为时间过于久远，导致api链接与当前api不兼容）
				    				//如果出现此错误，说明这个种子不可能遍历到历史数据为空。由于这是facebook给出的翻页链接本身有问题，所以跳过并标记此种子，继续下一个
				    				if(commentsList.get(0).getCodeMessage().equals("1")){
				    					log.info(temp.getMessageId()+"号贴文的历史Comment出现Facebook自身链接的错误(code："+commentsList.get(0).getCodeMessage()+"），绝大部分数据已采集完毕，将Crawed_post状态更新为-2，继续采集下一个种子！");
				    					String sql="UPDATE posts set crawed_comments=? WHERE post_id=?";
					    				postsDao.updateCrawed(sql,temp.getPostId(),-2);
					    				log.info("种子的Crawed_Comments爬取状态更新为-2成功!\n\n");
				    				}
				    				
				    				log.info("为避免系统问题，睡眠15秒钟\n\n");
				    				try {
										Thread.sleep(15*1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
				    				
				    			}
						}
					}else{//状态为-1的种子已经全部遍历一遍，现在为状态为-2，可以开始重置种子为-1，继续进行未来爬行
						String sql="UPDATE posts set crawed_comments=?";
						postsDao.resetCrawed(sql,-1);
	    				log.info("自爬虫启动以来，第” "+cycle+" “次的轮询已完成，Crawed_Comments重置为-1成功!\n\n");
	    				log.info("在开始下一轮在线轮询时，考虑到用户涂鸦的频繁度，睡眠15分钟，也可以防止请求过于频繁");
	    				
	    				cycle++;
	    				
					}
					
				}
			}		
		}	
	}
}
