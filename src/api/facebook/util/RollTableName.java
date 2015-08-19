package api.facebook.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 此类用于构建数据量较大的表的分月表名
 * 格式tableName_yyyyMM
 * @author chenkedi
 *
 */
public class RollTableName
{
	private String tableName;
	
	public RollTableName(String tableName){
		this.tableName=tableName;
	}
	
	public String construct(){
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
		String currentMonth=sdf.format(new Date());
		
		return tableName+"_"+currentMonth;
		
	}
}
