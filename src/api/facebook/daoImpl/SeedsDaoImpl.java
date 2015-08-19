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

import api.facebook.bean.Feeds;
import api.facebook.bean.Posts;
import api.facebook.bean.Seeds;
import api.facebook.bean.To;
import api.facebook.dao.SeedsDao;
import api.facebook.util.Params;


//public class SeedsDaoImpl extends JdbcDaoSupport implements SeedsDao
@Repository
public class SeedsDaoImpl implements SeedsDao
{

	private JdbcTemplate jdbcTemplate;
	private static final Logger log =Logger.getLogger(SeedsDaoImpl.class);
	
	@Autowired
	 public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	/**
	 * Spring JdbcTemplate查询方法，query获得多行结果，queryForObject获得一行结果
	 */
	@Override
	public List<Seeds> readSeeds(int num) {
		final String SQL_SELECT_SEED=
				"SELECT seeds_id,user_name,name,facebook_id FROM seeds WHERE category IS NULL ORDER BY seeds_id ASC LIMIT "+num;
		List<Seeds> seed=jdbcTemplate.query(
				SQL_SELECT_SEED,
				new RowMapper<Seeds>(){
					public Seeds  mapRow(ResultSet rs,int rowNum) throws SQLException{
						Seeds seed=new Seeds();
						seed.setUserName(rs.getString("user_name"));
						seed.setSeedsId(rs.getInt("seeds_id"));
						seed.setName(rs.getString("name"));
						seed.setFacebookId(rs.getString("facebook_id"));
						return seed;
					}
				});
		return seed;
	}

	
	@Override
	public void addSeedsInfo(Seeds seed,int seeds_id) {
		
		if(seed.getUserName()!=null){
			final String SQL_Insert_SEED=
					"UPDATE seeds set facebook_id=?,user_name=?,about=?,bio=?,birthday=?,category=?,hometown=?,likes=?,link=?,page_name=?,website=? WHERE seeds_id=?";
			jdbcTemplate.update(
					SQL_Insert_SEED,
					seed.getFacebookId(),
					seed.getUserName(),
					seed.getAbout(),
					seed.getBio(),
					seed.getBirthday(),
					seed.getCategory(),
					seed.getHometown(),
					seed.getLikes(),
					seed.getLink(),
					seed.getPageName(),
					seed.getWebsite(),
					seeds_id);
			
		}else{
			final String SQL_Insert_SEED=
					"UPDATE seeds set facebook_id=?,about=?,bio=?,birthday=?,category=?,hometown=?,likes=?,link=?,page_name=?,website=? WHERE seeds_id=?";
			jdbcTemplate.update(
					SQL_Insert_SEED,
					seed.getFacebookId(),
					seed.getAbout(),
					seed.getBio(),
					seed.getBirthday(),
					seed.getCategory(),
					seed.getHometown(),
					seed.getLikes(),
					seed.getLink(),
					seed.getPageName(),
					seed.getWebsite(),
					seeds_id);
		}
		
		
		
	}

	/*====================以下两个方法feeds和posts可以共用============*/
	@Override
	public void updateCrawed(String sql, int seeds_id,int value) {
		jdbcTemplate.update(
				sql,
				value,				
				seeds_id);
		
	}
	
	@Override
	public void resetCrawed(String sql, int value) {
		jdbcTemplate.update(
				sql,
				value);
		
	}

	
	/*====================以下3个方法posts可用============*/
	@Override
	public void updatePreviousPage(Posts post,int seeds_id) {
		String UPDATE_SQL=
				"UPDATE seeds set posts_previous_page=? WHERE seeds_id=?";
		jdbcTemplate.update(
				UPDATE_SQL,
				post.getPostsPreviousPage(),				
				seeds_id);
		
	}
	
	@Override
	public void updateNextPage(Posts post,int seeds_id) {
		String UPDATE_SQL=
				"UPDATE seeds set posts_next_page=? WHERE seeds_id=?";
		jdbcTemplate.update(
				UPDATE_SQL,
				post.getPostsNextPage(),				
				seeds_id);
		
	}



	@Override
	public List<Seeds> readSeedsForPosts(int num,int value,String pageLink) {
		String SQL_SELECT_SEED=
				"SELECT seeds_id,facebook_id,user_name,name,posts_previous_page,posts_next_page FROM seeds WHERE crawed_posts="+value+" ORDER BY seeds_id ASC LIMIT "+num;
		if(pageLink!=null){
			SQL_SELECT_SEED=
					"SELECT seeds_id,facebook_id,user_name,name,posts_previous_page,posts_next_page FROM seeds WHERE crawed_posts="+value+" AND "+pageLink+" IS NOT null ORDER BY seeds_id ASC LIMIT "+num;
		}
		List<Seeds> seed=jdbcTemplate.query(
				SQL_SELECT_SEED,
				new RowMapper<Seeds>(){
					public Seeds  mapRow(ResultSet rs,int rowNum) throws SQLException{
						Seeds seed=new Seeds();
						seed.setUserName(rs.getString("user_name"));
						seed.setSeedsId(rs.getInt("seeds_id"));
						seed.setFacebookId(rs.getString("facebook_id"));
						seed.setName(rs.getString("name"));
						seed.setPostsPreviousPage(rs.getString("posts_previous_page"));
						seed.setPostsNextPage(rs.getString("posts_next_page"));
						return seed;
					}
				});
		return seed;
	}

	
	/*====================以下3个方法feeds可用============*/
	@Override
	public void updatePreviousPage(Feeds feed,int seeds_id) {
		String UPDATE_SQL=
				"UPDATE seeds set feeds_previous_page=? WHERE seeds_id=?";
		jdbcTemplate.update(
				UPDATE_SQL,
				feed.getFeedsPreviousPage(),				
				seeds_id);
		
	}
	
	@Override
	public void updateNextPage(Feeds feed,int seeds_id) {
		String UPDATE_SQL=
				"UPDATE seeds set feeds_next_page=? WHERE seeds_id=?";
		jdbcTemplate.update(
				UPDATE_SQL,
				feed.getFeedsNextPage(),				
				seeds_id);
		
	}
	
	@Override
	public List<Seeds> readSeedsForFeeds(int num,int value) {
		//除了根据crawed_feeds 判断要读那些种子外，还有看该种子是否有feeds这个功能
		String SQL_SELECT_SEED=
				"SELECT seeds_id,facebook_id,user_name,name,feeds_previous_page,feeds_next_page FROM seeds WHERE crawed_feeds="+value+" AND has_feed=1 ORDER BY seeds_id ASC LIMIT "+num;
		List<Seeds> seed=jdbcTemplate.query(
				SQL_SELECT_SEED,
				new RowMapper<Seeds>(){
					public Seeds  mapRow(ResultSet rs,int rowNum) throws SQLException{
						Seeds seed=new Seeds();
						seed.setUserName(rs.getString("user_name"));
						seed.setSeedsId(rs.getInt("seeds_id"));
						seed.setFacebookId(rs.getString("facebook_id"));
						seed.setName(rs.getString("name"));
						seed.setFeedsPreviousPage(rs.getString("feeds_previous_page"));
						seed.setFeedsNextPage(rs.getString("feeds_next_page"));
						return seed;
					}
				});
		return seed;
	}

	@Override
	public int[] batchInsert(List<To> tos) {
		String SQL_INSERT_SEED=
				"INSERT INTO seeds (facebook_id,page_name) VALUES (?,?)";
		List<Object[]> batch = new ArrayList<Object[]>();
		for (To to : tos) {
            Object[] values = new Object[] {
            		to.getFacebookId(),
            		to.getPageName()
    		};
            batch.add(values);
        }
		
		try{
			int[] updateCounts = jdbcTemplate.batchUpdate(
					SQL_INSERT_SEED,
	                batch);
	        return updateCounts;
		}catch(Exception e){
				log.error("批量插入候选种子人物失败！"+e.getMessage());
				return new int[] {0};
		}
	}
	
	
}
