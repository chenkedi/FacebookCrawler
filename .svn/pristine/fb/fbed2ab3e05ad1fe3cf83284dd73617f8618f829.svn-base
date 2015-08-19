package api.facebook.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import api.facebook.bean.Comments;
import api.facebook.bean.Likes;
import api.facebook.bean.Posts;
import api.facebook.dao.PostsDao;

@Repository
public class PostDaoImpl implements PostsDao
{
	private JdbcTemplate jdbcTemplate;
	private static final Logger log=Logger.getLogger(PostDaoImpl.class);
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public void addPosts(Posts post) {
		final String SQL_INSERT_POST=
				"INSERT INTO posts(message_id,message,created_time,seeds_id,link,picture,shares) VALUES (?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				SQL_INSERT_POST,
				post.getMessageId(),
				post.getMessage(),
				post.getCreatedTime(),
				post.getSeedsId(),
				post.getLink(),
				post.getPicture(),
				post.getShares()
				);
	}
	
	@Override
	public int[] batchInsert(final List<Posts> posts) {
		final String SQL_INSERT_POST=
				"INSERT INTO posts(message_id,message,created_time,seeds_id,link,picture,shares) VALUES (?,?,?,?,?,?,?)";
		List<Object[]> batch = new ArrayList<Object[]>();
		for (Posts post : posts) {
            Object[] values = new Object[] {
            		post.getMessageId(),
    				post.getMessage(),
    				post.getCreatedTime(),
    				post.getSeedsId(),
    				post.getLink(),
    				post.getPicture(),
    				post.getShares()};
            batch.add(values);
        }
		
		try{
		int[] updateCounts = jdbcTemplate.batchUpdate(
				SQL_INSERT_POST,
                batch);
        return updateCounts;
		}catch(Exception e){
			log.error("数据库批量插入“"+posts.get(0).getSeedsId()+"”的Posts数据出错，错误信息："+e.getMessage());
			return new int[] {1};
		}
		
	}
	
	
	
	@Override
	public List<Posts> readPostsForComments(int num, int value) {
		String SQL_SELECT_POST=
				"SELECT post_id,message_id,seeds_id,comments_previous_page,comments_next_page FROM posts WHERE crawed_comments="+value+" ORDER BY post_id ASC LIMIT "+num;
		List<Posts> post=jdbcTemplate.query(
				SQL_SELECT_POST,
				new RowMapper<Posts>(){
					public Posts mapRow(ResultSet rs,int rowNum) throws SQLException{
						Posts post=new Posts();
						post.setPostId(rs.getInt("post_id"));
						post.setMessageId(rs.getString("message_id"));
						post.setSeedsId(rs.getInt("seeds_id"));
						post.setCommentsPreviousPage(rs.getString("comments_previous_page"));
						post.setCommentsNextPage(rs.getString("comments_next_page"));
						return post;
					}
				});
		return post;
	}
	
	
	
	
	@Override
	public List<Posts> readPostsForComments(int num, int value,String pageLinkFlag) {
		//注意，与贴文不同，一条贴文可能没有评论，而一个公共主页却不可能不发帖.所以在历史数据遍历的时候要判断comments_next_page是否为空
		String SQL_SELECT_POST=
				"SELECT post_id,message_id,seeds_id,comments_previous_page,comments_next_page "
				+ "FROM posts "
				+ "WHERE crawed_comments="+value+" AND "+pageLinkFlag+" IS NOT null "
				+ "ORDER BY post_id ASC LIMIT "+num;
		//由于贴文具有时效性，所以我们在选取爬未来评论的种子贴文时，只取前三天的贴文。我们认为，只有前三天的贴文才有采集最新评论的意义
		if(pageLinkFlag.equals("comments_previous_page")){
			SQL_SELECT_POST=
					"SELECT post_id,message_id,seeds_id,comments_previous_page,comments_next_page "
					+ "FROM posts "
					+ "WHERE crawed_comments="+value+" AND "+pageLinkFlag+" IS NOT null AND DATEDIFF(NOW(),created_time)<3 "
					+ "ORDER BY post_id ASC LIMIT "+num;
		}
		List<Posts> post=jdbcTemplate.query(
				SQL_SELECT_POST,
				new RowMapper<Posts>(){
					public Posts mapRow(ResultSet rs,int rowNum) throws SQLException{
						Posts post=new Posts();
						post.setPostId(rs.getInt("post_id"));
						post.setMessageId(rs.getString("message_id"));
						post.setSeedsId(rs.getInt("seeds_id"));
						post.setCommentsPreviousPage(rs.getString("comments_previous_page"));
						post.setCommentsNextPage(rs.getString("comments_next_page"));
						return post;
					}
				});
		return post;
	}

	@Override
	public List<Posts> readPostsForLikes(int num, int value) {
		String SQL_SELECT_POST=
				"SELECT post_id,message_id,seeds_id,likes_previous_page,likes_next_page FROM posts WHERE crawed_likes="+value+" ORDER BY post_id ASC LIMIT "+num;
		List<Posts> post=jdbcTemplate.query(
				SQL_SELECT_POST,
				new RowMapper<Posts>(){
					public Posts mapRow(ResultSet rs,int rowNum) throws SQLException{
						Posts post=new Posts();
						post.setPostId(rs.getInt("post_id"));
						post.setMessageId(rs.getString("message_id"));
						post.setSeedsId(rs.getInt("seeds_id"));
						post.setCommentsPreviousPage(rs.getString("likes_previous_page"));
						post.setCommentsNextPage(rs.getString("likes_next_page"));
						return post;
					}
				});
		return post;
	}

	@Override
	public void updatePreviousPage(Comments comment, int postId) {
		String SQL_UPDATE_COMMENT=
				"UPDATE posts SET comments_previous_page=? WHERE post_id=?";
		jdbcTemplate.update(
				SQL_UPDATE_COMMENT,
				comment.getCommentsPreviousPage(),
				postId);
	}

	@Override
	public void updateNextPage(Comments comment, int postId) {
		String SQL_UPDATE_COMMENT=
				"UPDATE posts SET comments_next_page=? WHERE post_id=?";
		jdbcTemplate.update(
				SQL_UPDATE_COMMENT,
				comment.getCommentsNextPage(),
				postId);
		
	}

	@Override
	public void updatePreviousPage(Likes like, int postId) {
		String SQL_UPDATE_COMMENT=
				"UPDATE posts SET likes_previous_page=? WHERE post_id=?";
		jdbcTemplate.update(
				SQL_UPDATE_COMMENT,
				like.getLikesPreviousPage(),
				postId);
		
	}

	@Override
	public void updateNextPage(Likes like, int postId) {
		String SQL_UPDATE_COMMENT=
				"UPDATE posts SET likes_next_page=? WHERE post_id=?";
		jdbcTemplate.update(
				SQL_UPDATE_COMMENT,
				like.getLikesNextPage(),
				postId);
		
	}

	/*====================以下两个方法comments和likes可以共用============*/
	@Override
	public void updateCrawed(String sql, int postId, int value) {
		jdbcTemplate.update(
				sql,
				value,
				postId);
		
	}
	
	@Override
	public void resetCrawed(String sql, int value) {
		jdbcTemplate.update(
				sql,
				value);
		
	}

	
	
}
