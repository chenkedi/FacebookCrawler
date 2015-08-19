package api.facebook.dao;

import java.util.List;

import api.facebook.bean.Feeds;
import api.facebook.bean.Posts;
import api.facebook.bean.Seeds;
import api.facebook.bean.To;

/**
 * 
 * @author chenkedi
 *
 */
public interface SeedsDao
{
	/**
	 * 为爬取公众人物个人信息的爬虫读取种子队列进入内存
	 * @param num
	 * @return
	 */
	public List<Seeds> readSeeds(int num); 
	
	/**
	 * 将获取的种子信息更新进入数据库
	 * @param seed
	 */
	public void addSeedsInfo(Seeds seed,int seeds_id);
	
	
	
	/**
	 * 更新post的翻页链接
	 * @param post
	 */
	public void updatePreviousPage(Posts post,int seeds_id);
	public void updateNextPage(Posts post,int seeds_id);
	public List<Seeds> readSeedsForPosts(int num,int value,String pageLink);
	
	/**
	 * 更新已经爬取过的种子的posts或feeds状态为1
	 * @param post
	 */
	public void updateCrawed(String sql,int seeds_id,int value);
	public void resetCrawed(String sql,int value);

	
	/**
	 * 更新feeds的翻页链接
	 * @param feed
	 * @param seeds_id
	 */
	public void updatePreviousPage(Feeds feed, int seeds_id);

	public void updateNextPage(Feeds feed, int seeds_id);

	public List<Seeds> readSeedsForFeeds(int num, int value);

	/**
	 * 将候选种子批量插入到seeds表中
	 * @param tos
	 * @return
	 */
	public int[] batchInsert(List<To> tos);
}
