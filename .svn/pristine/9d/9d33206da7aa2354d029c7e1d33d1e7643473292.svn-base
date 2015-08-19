package api.facebook.dao;

import java.util.List;

import api.facebook.bean.Comments;
import api.facebook.bean.Likes;
import api.facebook.bean.Posts;

public interface PostsDao
{
	/**
	 * 单条插入post
	 * @param post
	 */
	public void addPosts(Posts post);
	
	/**
	 * 批量插入List<Posts>
	 */
	public int[] batchInsert(final List<Posts> posts);
	
	/**
	 * 为comments读取postid,首轮遍历
	 * @param num 读取的posts种子的条数
	 * @param value 读取的where条件值
	 * @return
	 */
	public List<Posts> readPostsForComments(int num,int value);
	/**
	 * 为comments读取postId，历史数据遍历或者未来数据遍历
	 * @param num
	 * @param value
	 * @param pageLinkFlag comments_next_page或 comments_previous_page
	 * @return
	 */
	public List<Posts> readPostsForComments(int num,int value,String pageLinkFlag);
	public void updatePreviousPage(Comments comment,int postId);
	public void updateNextPage(Comments comment,int postId);
	
	/**
	 * 为likes读取postid等
	 * @param num 读取的posts种子的条数
	 * @param value 读取的where条件值
	 * @return
	 */
	public List<Posts> readPostsForLikes(int num,int value);
	public void updatePreviousPage(Likes like,int postId);
	public void updateNextPage(Likes like,int postId);
	

	public void updateCrawed(String sql,int postId,int value);
	public void resetCrawed(String sql, int value);
}
