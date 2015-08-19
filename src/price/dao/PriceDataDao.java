package price.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import price.bean.PriceData;

@Repository
public class PriceDataDao
{
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSourceLocal(DataSource dataSourceLocal){
		jdbcTemplate=new JdbcTemplate(dataSourceLocal);
	}
	
	public int[] batchInsert(List<PriceData> priceDatas){
		String SQL_INSERT_PRICE=
				"INSERT INTO price_data (date,type,market,lowest_price,highest_price,avg_price,unit) VALUES (?,?,?,?,?,?,?)";
		
		List<Object[]> batch=new ArrayList<Object[]>();
		for(PriceData priceData : priceDatas){
			Object[] values=new Object[]{
					priceData.getDate(),
					priceData.getType(),
					priceData.getMarket(),
					priceData.getLowestPrice(),
					priceData.getHighestPrice(),
					priceData.getAvgPrice(),
					priceData.getUnit()
			};
			batch.add(values);
		}
		
		try{
			int[] updateCounts = jdbcTemplate.batchUpdate(
					SQL_INSERT_PRICE,
	                batch);
	        return updateCounts;
		}catch(Exception e){
				System.out.println("数据库批量插入价格信息数据出错，错误信息："+e.getMessage());
				return new int[] {0};
		}
	}
}
