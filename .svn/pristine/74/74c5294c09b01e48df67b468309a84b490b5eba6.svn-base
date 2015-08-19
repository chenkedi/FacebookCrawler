package api.facebook.bean;

import java.sql.Timestamp;

/**
 * Comments entity. @author MyEclipse Persistence Tools
 */

public class Comments
{

	// Fields
	private String status="";
	private String codeMessage="";

	private Integer commentId;
	private String messageId;
	private String message;
	private String fromUserId;
	private String fromUserName;
	private Integer postId;
	private Integer likeCount;
	private String userLikes;
	private Timestamp createdTime;
	private Timestamp insertTime;
	
	private String commentsPreviousPage;
	private String commentsNextPage;

	// Constructors

	/** default constructor */
	public Comments() {
	}

	/** minimal constructor */
	public Comments(String messageId, String fromUserId,
			String fromUserName, Timestamp createdTime, Timestamp insertTime) {
		this.messageId = messageId;
		this.fromUserId = fromUserId;
		this.fromUserName = fromUserName;
		this.createdTime = createdTime;
		this.insertTime = insertTime;
	}

	/** full constructor */
	public Comments(String messageId, String message,
			String fromUserId, String fromUserName, Timestamp createdTime,
			Timestamp insertTime, Integer likeCount, String userLike) {
		this.messageId = messageId;
		this.message = message;
		this.fromUserId = fromUserId;
		this.fromUserName = fromUserName;
		this.createdTime = createdTime;
		this.insertTime = insertTime;
		this.likeCount = likeCount;
	}

	// Property accessors

	public Integer getCommentId() {
		return this.commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}


	public String getMessageId() {
		return this.messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFromUserId() {
		return this.fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserName() {
		return this.fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public Timestamp getCreatedTime() {
		return this.createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Timestamp getInsertTime() {
		return this.insertTime;
	}

	public void setInsertTime(Timestamp insertTime) {
		this.insertTime = insertTime;
	}

	public Integer getLikeCount() {
		return this.likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}


	public String getUserLikes() {
		return userLikes;
	}

	public void setUserLikes(String userLikes) {
		this.userLikes = userLikes;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
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

	public String getCommentsPreviousPage() {
		return commentsPreviousPage;
	}

	public void setCommentsPreviousPage(String commentsPreviousPage) {
		this.commentsPreviousPage = commentsPreviousPage;
	}

	public String getCommentsNextPage() {
		return commentsNextPage;
	}

	public void setCommentsNextPage(String commentsNextPage) {
		this.commentsNextPage = commentsNextPage;
	}

	
}