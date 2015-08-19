package api.facebook.daoImpl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import api.facebook.bean.Comments;
import api.facebook.dao.CommentsDao;
import api.facebook.util.RollTableName;

@Repository
public class CommentsDaoImply implements CommentsDao
{
	private JdbcTemplate jdbcTemplate;
	private static final Logger log=Logger.getLogger(PostDaoImpl.class);
	
	@Autowired
	public void setDataSource(DataSource dataSource){
		jdbcTemplate=new JdbcTemplate(dataSource);
	}

	@Override
	public int[] batchInsert(List<Comments> comments) {
		
		//根据日期构建表名
		RollTableName Roll=new RollTableName("comments");
		String tableName=Roll.construct();
		
		String SQL_INSERT_COMMENTS=
				"INSERT INTO "+tableName+" (message_id,message,from_user_id,from_user_name,post_id,like_count,user_likes,created_time) VALUES (?,?,?,?,?,?,?,?)";
		List<Object[]> batch = new ArrayList<Object[]>();
		for(Comments comment : comments){
			 Object[] values = new Object[] {
				comment.getMessageId(),
				comment.getMessage(),
				comment.getFromUserId(),
				comment.getFromUserName(),
				comment.getPostId(),
				comment.getLikeCount(),
				comment.getUserLikes(),
				comment.getCreatedTime()
			};
			batch.add(values);
		}
		
		try{
			int[] updateCounts = jdbcTemplate.batchUpdate(
					SQL_INSERT_COMMENTS,
	                batch);
	        return updateCounts;
		}catch(Exception e){
				log.error("数据库批量插入贴文“"+comments.get(0).getMessageId()+"号”的Comment数据出错，错误信息："+e.getMessage());
				return new int[] {1};
		}
	}

}
