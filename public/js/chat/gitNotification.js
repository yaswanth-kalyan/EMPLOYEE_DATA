function convertGitNotificationDiv(messageObj,appuser){
	var description;
	var gitNotificationType;
	if(messageObj.gitNotification!=null){
	gitNotificationType=messageObj.gitNotification.gitNotificationType;
	}
    	if(gitNotificationType==null||gitNotificationType=='PUSH'){
    	description = messageObj.description;
    	}
    	if(gitNotificationType=='ISSUES'){
    		description = getGitIssueDiv(messageObj.gitNotification);
    	}
    	if(gitNotificationType=='CREATEBRANCH'){
    	
    		description = getGitBranchModifiedDiv(messageObj.gitNotification);
    	}
    	if(gitNotificationType=='DELETEBRANCH'){
    		
    		description = getGitBranchModifiedDiv(messageObj.gitNotification);
    	}
    	if(gitNotificationType=='COMMITCOMMENT'){
    		
    		description = getGitCommitComment(messageObj.gitNotification);
    		
    	}
    	return description;
    	
}
function getGitIssueDiv(notification){
	var issueType = notification.gitIssue.gitIssueType;
	
	var description;
	
		    	if(issueType=='OPEN'){
		    		
		    		description = gitissueCreatedDiv(notification);
		    	}
		    	if(issueType=='COMMENT'){
		    		
		    		description = gitissueCreatedDiv(notification);
			    	}
		    	if(issueType=='ASSIGNED'){
		    		description = gitIssueAssignedDiv(notification);
		    	}
		    	if(issueType=='CLOSE'){
		    		description = gitIssueAssignedDiv(notification);
		    	}
		    	if(issueType=='REOPEN'){
		    		description = gitIssueAssignedDiv(notification);
		    	}

		 return description;
	
	
}
function gitissueCreatedDiv(notification){
		    var issuetype = notification.gitIssue.gitIssueType;	
		    var issue = notification.gitIssue;
		    	if(issuetype=='OPEN'){
		    	description = '<b>['+issue.fullName+'] Issue created by <a target="_blank"  href="'+issue.IssueRaisedByUrl+'">'+issue.IssueRaisedBy+'</a></b>'+
		    	'</br><div class="git-href"> &nbsp;<a target="_blank" href="'+issue.issueUrl+'"> #'+issue.issueNumber+'&nbsp;&nbsp;'+issue.title+'</a></br>'+
		    	'<div class="git-issue-comment">'+issue.comment;
		    	}
		    	if(issuetype=='COMMENT'){
		    		description = '<b>['+issue.fullName+'] New comment on issue <a target="_blank"  href="'+
		    		issue.issueUrl+'"> &nbsp; #'+issue.issueNumber+'&nbsp;:&nbsp; '+issue.title+'</a></b></br>'+
		    		'<div class="git-href"><div class="git-issue-comment"><b> Comment by '+issue.commentedBy+'</b><br>'+
		    		issue.comment;
		    	}  	
		    	if(issue.uploadFileInfoList!=null){
		    	$.each(issue.uploadFileInfoList,function(index,obj){
		    		description = description +'<a target="_blank"  href="'+issue.uploadFileInfoList[index].fileUrl+'"><b>'
		    		+issue.uploadFileInfoList[index].uploadFileName+'</b></a><br><a target="_blank"  href="'+issue.uploadFileInfoList[index].fileUrl+'">';
		    		
		    		if(!(issue.uploadFileInfoList[index].uploadFileContentType == "pdf")){
		    			description = description+ '<div class="gitissue-img-wraper"><img src="/secure-chat/get-git-issue-image/'+issue.uploadFileInfoList[index].id+'"/></div></a>';
		    		}else{
		    			description = description+'</a>';
		    		}
		    	});
		    	}else{
		    		description = description+'</div></div>';
		    	}
		    return description;
}
function gitIssueAssignedDiv(notification){
	var description;
	var issuetype = notification.gitIssue.gitIssueType;	
	var issue = notification.gitIssue;
		    	if(issuetype=='ASSIGNED'){
		    	description = '<b>&nbsp; <a target="_blank" href="'+issue.issueUrl+'"> #'+issue.issueNumber+'&nbsp;:&nbsp;'+issue.title+'</a> assigned to '+
		    	'<a target="_blank" href="'+issue.assigneeUrl+'">'+issue.assignedTo+'</a></b>';
		    	}
		    	if(issuetype=='CLOSE'){
		    		description = '<b>['+issue.fullName+'] Issue closed : <a target="_blank" href="'+issue.issueUrl+'"> #'+issue.issueNumber+
		    		'&nbsp;:&nbsp;'+issue.title+'</a> by <a target="_blank" href="'+issue.IssueRaisedByUrl+'">'+issue.IssueRaisedBy+'</a></b>';
		    	}
		    	if(issuetype=='REOPEN'){
		    		description = '<b>['+issue.fullName+'] Issue re-opened :&nbsp;'+
		    		'<a target="_blank" href="'+issue.issueUrl+'"> #'+issue.issueNumber+'&nbsp; '+issue.title+'</a> by '+
		    		'<a target="_blank" href="'+issue.IssueRaisedByUrl+'">'+issue.IssueRaisedBy+'</a></b>';
		    	}
		    
	
	return description;
}
function getGitBranchModifiedDiv(notification){
	var notifType = notification.gitNotificationType;
	var description;

		    	if(notifType=='CREATEBRANCH'){
		    	description='<b>['+notification.repository+'] '+notification.repositoryBranch+' was branched from - "'+notification.branchedFrom+'" and pushed by '+notification.committedBy+'</b>';
		    	}
		    	if(notifType=='DELETEBRANCH'){
		    		description='<b>['+notification.repository+'] The branch "'+notification.repositoryBranch+'" was deleted by '+notification.committedBy+'</b>';
		    	}
		    

	return description;
	
	
}
function getGitCommitComment(notification){
	var comment = notification.gitCommitComment;
	var description;

		    	description = '<b>['+comment.full_name+'] New comment on commit <a target="_blank" href="'+comment.commitUrl+'"> '+comment.commitId+'</a></br>'+
		    	'<div class="git-href">&nbsp;&nbsp;Comment by '+comment.commentBy+'</b></br>&nbsp;&nbsp;'+comment.comment+'</div>';
	
	 return description;
}





