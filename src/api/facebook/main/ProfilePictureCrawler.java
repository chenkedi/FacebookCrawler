package api.facebook.main;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import api.facebook.dao.SeedsDao;

/**
 * 爬取公共主页人物头像
 * @author chenkedi
 *
 */
@Controller
public class ProfilePictureCrawler
{
	@Resource
	private SeedsDao seedsDao;
	
}
