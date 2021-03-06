package api.facebook.bean;

/**
 * Likes entity. @author MyEclipse Persistence Tools
 */

public class Likes
{

	// Fields
	private String status="";
	private String codeMessage="";
	
	private Integer likeId;
	private Integer	postId;
	private String userId;
	private String userName;

	private String likesPreviousPage;
	private String likesNextPage;
	// Constructors

	/** default constructor */
	public Likes() {
	}

	/** full constructor */
	public Likes( String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}

	// Property accessors

	public Integer getLikeId() {
		return this.likeId;
	}

	public void setLikeId(Integer likeId) {
		this.likeId = likeId;
	}


	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCodeMessage() {
		return codeMessage;
	}

	public void setCodeMessage(String codeMessage) {
		this.codeMessage = codeMessage;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public String getLikesPreviousPage() {
		return likesPreviousPage;
	}

	public void setLikesPreviousPage(String likesPreviousPage) {
		this.likesPreviousPage = likesPreviousPage;
	}

	public String getLikesNextPage() {
		return likesNextPage;
	}

	public void setLikesNextPage(String likesNextPage) {
		this.likesNextPage = likesNextPage;
	}
	
	
}