package api.facebook.method;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import api.facebook.bean.Comments;
import api.facebook.bean.To;
import api.facebook.bean.Likes;

/**
 * 此类对应于API中获取贴文的评论、点赞的人、以及贴文中提到的人的信息的方法
 * @author chenkedi
 *
 */
public class GetComments extends GraphAPI
{
	/**
	 * 负责对请求某条postId后获得的顶层json对象解析出所有的评论
	 * @param jsonObject
	 * @param postId
	 * @return
	 */
	public List<Comments> commentsDataExtract(JSONObject jsonObject,int postId,String postMessageId){
		
		List<Comments> commentList=new ArrayList<Comments>();
		//如果请求没有成功，则返回错误信息和错误code
		if(json.isErrorJson(jsonObject)){
			Map<String,String> map=null;
			map=json.jsonErrorMessage(jsonObject);
			log.error("错误代码："+map.get("code")+"，错误信息："+map.get("message"));
			Comments comment=new Comments();
			comment.setStatus("error");
			comment.setCodeMessage(map.get("code"));
			commentList.add(comment);
		}else{
			
			//检测当前的顶层json对象是否还有paging这个键，没有的话说明这个json是空的
			if(jsonObject.has("paging")){

				//获取翻页的api链接
				JSONObject pageLinkJson=jsonObject.getJSONObject("paging");
				JSONArray jsonArray=jsonObject.getJSONArray("data");
				for(int i=0;i<jsonArray.length();i++){
					Comments comment= new Comments();
					JSONObject commentJsonObj=jsonArray.getJSONObject(i);
					
					//先处理comment本表内部对应json对象中的一级键值
					if(commentJsonObj.has("id")){
						comment.setMessageId(commentJsonObj.getString("id"));
					}else{
						comment.setMessageId(null);
					}
					
					if(commentJsonObj.has("message")){
						comment.setMessage(commentJsonObj.getString("message"));
					}else{
						comment.setMessage(null);
					}
					
					if(commentJsonObj.has("from")){
						comment.setFromUserId(commentJsonObj.getJSONObject("from").getString("id"));
						comment.setFromUserName(commentJsonObj.getJSONObject("from").has("name")?
								commentJsonObj.getJSONObject("from").getString("name"):null);
					}else{
						comment.setFromUserId(null);
						comment.setFromUserName(null);
					}
					
					comment.setPostId(postId);
					
					if(commentJsonObj.has("like_count")){
						comment.setLikeCount(commentJsonObj.getInt("like_count"));
					}else{
						comment.setLikeCount(null);
					}
					
					if(commentJsonObj.has("user_likes")){
						comment.setUserLikes(String.valueOf(commentJsonObj.getBoolean("user_likes")));
					}else{
						comment.setUserLikes(null);
					}
					
					if(commentJsonObj.has("created_time")){
						comment.setCreatedTime(string2Timestamp( commentJsonObj.getString("created_time"), null));
					}else{
						comment.setCreatedTime(null);
					}
					
					/*===============为了防止每个comment对象都存储PageLink，仅在循环第一次时写入PageLink进入comment对象，取出时只需要取出index索引为0即可=====================*/
					if(i==0){
						if(pageLinkJson.has("cursors")){
							if(pageLinkJson.getJSONObject("cursors").has("before"))
							{
								String before=pageLinkJson.getJSONObject("cursors").getString("before");
								comment.setCommentsPreviousPage(previousPageLinkComments(postMessageId,before));
							}
							
						}else{
							comment.setCommentsPreviousPage(null);
						}
						
						if(pageLinkJson.has("next")){
							comment.setCommentsNextPage(pageLinkJson.getString("next"));
						}else{
							comment.setCommentsNextPage(null);
						}
					}
					
					
					commentList.add(comment);

					if(jsonArray.length()-1==i){
						log.info("获得ID为\""+postMessageId+"\"贴文的评论信息成功！准备写入数据库！");
					}
				}
			
			}else{//Comments 顶层不含有paging这个key，说明Comments为空，爬取走到尽头
				
				Comments comment =new Comments();
				comment.setStatus("empty");
				commentList.add(comment);
			}
			
		}
		return commentList;		
	}
	
	/**
	 * 负责对请求某条postId后获得的顶层json对象解析出所有的likes
	 * @param jsonObject
	 * @param postId
	 * @return
	 */
	public List<Likes> likesDataExtract(JSONObject jsonObject,int postId,String postMessageId){
		List<Likes> likeList=new ArrayList<Likes>();	
		//如果请求没有成功，则返回错误信息和错误code
		if(json.isErrorJson(jsonObject)){
			Map<String,String> map=null;
			map=json.jsonErrorMessage(jsonObject);
			log.error("错误代码："+map.get("code")+"，错误信息："+map.get("message"));
			Likes like=new Likes();
			like.setStatus("error");
			like.setCodeMessage("出现系统错误: code: "+map.get("code")+" message: "+map.get("message"));
			likeList.add(like);
		}else{
			
			//检测当前的顶层json对象是否还有paging这个键，没有的话说明这个json是空的
			if(jsonObject.has("paging")){

				//获取翻页的api链接
				JSONObject pageLinkJson=jsonObject.getJSONObject("paging");
				JSONArray jsonArray=jsonObject.getJSONArray("data");
				for(int i=0;i<jsonArray.length();i++){
					Likes like= new Likes();
					JSONObject likeJsonObj=jsonArray.getJSONObject(i);
					
					//先处理like本表内部对应json对象中的一级键值
					if(likeJsonObj.has("id")){
						like.setUserId(likeJsonObj.getString("id"));
					}else{
						like.setUserId(null);
					}
					
					if(likeJsonObj.has("name")){
						like.setUserName(likeJsonObj.getString("name"));
					}else{
						like.setUserName(null);
					}
					
					like.setPostId(postId);	
					
					
					/*===============为了防止每个comment对象都存储PageLink，仅在循环第一次时写入PageLink进入comment对象，取出时只需要取出index索引为0即可=====================*/
					if(i==0){
						if(pageLinkJson.has("cursors")){
							if(pageLinkJson.getJSONObject("cursors").has("before"))
							{
								String before=pageLinkJson.getJSONObject("cursors").getString("before");
								like.setLikesPreviousPage(previousPageLinkLikes(postMessageId,before));
							}
							
						}else{
							like.setLikesPreviousPage(null);
						}
						
						if(pageLinkJson.has("next")){
							like.setLikesNextPage(pageLinkJson.getString("next"));
						}else{
							like.setLikesNextPage(null);
						}
					}
					
					
					likeList.add(like);

					if(jsonArray.length()-1==i){
						log.info("获得ID为\""+postMessageId+"\"贴文的点赞用户信息成功！准备写入数据库！");
					}
				}
			
			}else{//Likes 顶层不含有paging这个key，说明Likes为空，爬取走到尽头
				
				Likes like =new Likes();
				like.setStatus("empty");
				likeList.add(like);
			}
			
		}
		return likeList;		
	}
	
	/**
	 * 负责对请求某条postId后获得的顶层json对象解析出所有的TO,这是关键信息，能构造政治人物关系网和阵营网
	 * @param jsonObject
	 * @param postId
	 * @param seedsId
	 * @return
	 */
	public List<To> toDataExtract(JSONObject jsonObject,int postId,int seedsId){
		List<To> toList=new ArrayList<To>();	
		//如果请求没有成功，则返回错误信息和错误code
		if(json.isErrorJson(jsonObject)){
			Map<String,String> map=null;
			map=json.jsonErrorMessage(jsonObject);
			log.error("错误代码："+map.get("code")+"，错误信息："+map.get("message"));
			To to=new To();
			to.setStatus("error");
			to.setCodeMessage("出现系统错误: code: "+map.get("code")+" message: "+map.get("message"));
			toList.add(to);
		}else{
			if(jsonObject.has("data")){
				//获取翻页的api链接
				JSONArray jsonArray=jsonObject.getJSONArray("data");
				for(int i=0;i<jsonArray.length();i++){
					To to= new To();
					JSONObject toJsonObj=jsonArray.getJSONObject(i);
					
					//先处理to本表内部对应json对象中的一级键值
					if(toJsonObj.has("id")){
						to.setFacebookId(toJsonObj.getString("id"));
					}else{
						to.setFacebookId(null);
					}
					
					if(toJsonObj.has("name")){
						to.setPageName(toJsonObj.getString("name"));
					}else{
						to.setPageName(null);
					}
					
					to.setSeedsId(seedsId);	
					to.setPostId(postId);
					
					if(toJsonObj.has("category")){
						to.setCategory(toJsonObj.getString("category"));
					}else{
						to.setCategory(null);
					}
					
					toList.add(to);

					if(jsonArray.length()-1==i){
							log.info("“"+seedsId+"”的ID为”"+postId+"“号的贴文的to信息爬取成功，准备写入数据库！");
					}
				}
			}else{
				log.info("“"+seedsId+"”的ID为”"+postId+"“号的贴文的to信息为空。");
			}

		}
		return toList;		
	}
	
	
	
	/**
	 * 通过给定的历史数据爬取链接和未来数据的游标，构建爬取未来数据的链接
	 * 由于facebook在comments里面没有给定previousPageLink,所以该方法仅评论使用
	 * @param NextPageLink
	 * @param beforeCursor
	 * @return
	 */
	public String previousPageLinkComments(String postMessageId,String beforeCursor){
		URI uri=null;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setHost(BASE_URL)
			.setPath("/"+postMessageId+"/comments")
			.setParameter("limit", "25")
			.setParameter("order", "chronological")
			.setParameter("access_token", clientId+"|"+clientSecret)
			.setParameter("before", beforeCursor)
			.build();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return uri.toString();
	}
	
	/**
	 * 通过给定的历史数据爬取链接和未来数据的游标，构建爬取未来数据的链接
	 * 由于facebook在comments里面没有给定previousPageLink,所以该方法仅评论使用
	 * @param NextPageLink
	 * @param beforeCursor
	 * @return
	 */
	public String previousPageLinkLikes(String postMessageId,String beforeCursor){
		URI uri=null;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setHost(BASE_URL)
			.setPath("/"+postMessageId+"/likes")
			.setParameter("limit", "25")
			.setParameter("access_token", clientId+"|"+clientSecret)
			.setParameter("before", beforeCursor)
			.build();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return uri.toString();
	}
}
