package api.facebook.dao;

import java.util.List;

import api.facebook.bean.To;

public interface ToDao
{
	public int[] batchInsert(List<To> tos);
	
	/**
	 * 读取to_other表中的候选种子
	 * @return
	 */
	public List<To> readCandidateSeeds();
	
	public void updateHasAddToSeeds(To to);
}
