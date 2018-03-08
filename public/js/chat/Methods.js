$.ajaxSetup({
  cache: true
});
function sendMessage(){
	firstMessageAndDateUnderline();
		if($('#msgText').val().trim()== '' || $('#msgText').val().trim() == null){
			return false;
		}else{
			if(isLogin==false){
				window.location.reload();
				//alert("please login");
				return false;
			}else{
				msgdescreplaced = String($('#msgText').val().trim()).replace(/<br ?\/?>/g, "#$%^*");
				messageObj = new Object();
				messageObj.toUserId =clientId;
				messageObj.role=role;
				messageObj.messagePersistentStatus="saving";
				messageObj.messageContentType="TEXT";
			messageObj.description= replaceString('\n','<br>',msgdescreplaced);
				messageObj=prepareMessageObj(messageObj);
					messageObj.isUserNameChange=isuserNameChange();
					if(Object.keys(allMsgsMap[channelName]).length <= 2){
						$('#messages').html('');	
					}
					pushMessageToCache(messageObj,channelName);
					$(displayMessage(messageObj,appuser,messageObj.isUserNameChange)).appendTo('#messages').hide().fadeIn(300);
					replaceAll('new');
					saveLinkPreview(messageObj.randomId,'false');
					sendMessageOn(messageObj);
					$('#underLine'+appuser+'').css('display','none');
					scrollBottom();	
					//var newTxtArea = $('<textarea id="msgText" class="form-control inputcustom input-dsgn comment js-auto-size" placeholder="Type your message here..." data-toggle="popover" style="border: 0px solid !important;" rows="1" data-emojiable="true" autofocus></textarea>');
					//$('#msgText').replaceWith(newTxtArea);
					$('.js-auto-size').textareaAutoSize();
					$('#msgText').val('').focus();
			} //else 
		  }// outer else
	
	 var text = document.getElementById('msgText');
	 text.style.height = '32px';
	 }//sendMessage
 
function isuserNameChange(){
	var result='';
	if($('.msg_description').last().attr('data-id') == messageObj.messageBy.id){
		result = false;
		}
		else{
			result = true;
		}
	
	var d = new Date();
	var curr_date = d.getDate();
	var curr_month = d.getMonth();
	var curr_year = d.getFullYear();
	var formattedDate = curr_year + "-" + (curr_month+1) + "-" + curr_date;
	var formattedDate2 = moment(d).format('DD-MMM-YYYY');
	if(!$('#messages').text() || $('.time-chat').last().attr('data-date') != formattedDate2){
		result=true;
	}
	return result;
} 

 
 
 var readURL2 = function(inputFile) {
 	var file = inputFile; 
 	var fileName=file.name;
 	var fileType=file.type;

 	
     var reader = new FileReader();
         reader.onload = function (e) {
             $('#uploadImage').attr('src', e.target.result);
         }
      reader.readAsDataURL(file);
 }//readURL
 function uploadFile(event){
    /* var formData = new FormData($('#save-form')[0]);
 	formData.append('image', $('input[type=file]')[0].files[0]);*/
   $('#progressOuter').removeClass('hidden');
   $('#uploadFileForm').submit();
  }
function prepareMessageObj(){
	
	//general properties for all type messages
	
	messageObj.randomId=generateRandomId();
	messageObj.createdOn= new Date();
	messageObj.messageTo=clientId;
	messageObj.messageBy= appuserJson;
	
	messageObj.attachmentList = [];
	messageObj.gitIssue = '';
	messageObj.gitNotification = '';
	messageObj.isAttachment=false;
	//only for file uplaod
	messageObj.uploadFileId ='';
	messageObj.uploadFile={
			uploadFileName:'',
			id:'',
			commentList:[{
				comment:'',
				commentBy: appuserJson,
				createdOn:new Date()
			}],
			snippetMap:{
				noOfLines: '',
				snippetCode: '',
				likesCount : '',
				isLiked : '',
				lastLiked :{},
				eventBy : '',
				byUserName :'',
				commentsCount : ''
			},
			likeList:[{
				id:'',
				likeBy: ''
			}],
	};
	messageObj.comments="";
	messageObj.gitNotificationId="";
	
	return messageObj;
	 
}
 function showConfirmationPopup(deleteRandomId1,title,header,body) {
 	var modal = '<div id="deleteModal" class="modal fade" role="dialog">'+
 	'<div class="modal-dialog">'+
 	'<!-- Modal content-->'+
 	'<div class="modal-content">'+
 	'<div class="modal-header">'+
 	'<button type="button" class="close" data-dismiss="modal">&times;</button>'+
 	'<h4 class="modal-title">' + title + '</h4>'+
 	'</div>'+
 	'<div class="modal-body">'+
 	 body +
 	'</div>'+
 	'<div class="modal-footer">'+
 	'<button type="button" class="btn btn-primary  lv-ch-confirmed" data-dismiss="modal" data-id=" '+ deleteRandomId1 +'" >Delete this Message</button>'+
 	'<button type="button" class="btn btn-default"  data-dismiss="modal">No</button>'+
 	'</div>'+
 	'</div>'+
 	'</div>'+
 	'</div>';
 	return modal;
 }

 function deleteMain(deleteRandomId){
 	var title;
 	var header;
 	var description;
 	var appUserName;
 	var messageContentType;
 	var modalHtml="";
 	var modalBody ='';
 	
 	
 	$.ajax({
 		type : 'GET',
 	    url: '/secure-chat/getMessage-details/'+deleteRandomId+'',
 	    contentType: false,
 	    processData: false,
 	    success:function(result){
 	    	 description=result.description;
 	    	 appUserName=result.appUserName;
 	    	 messageContentType=result.messageContentType;
 	    	 if(messageContentType =='TEXT' ||  messageContentType == 'GROUPSTATUS' ||  messageContentType=='URL'){
 	    		 header='Delete Message';
 	    		 title='Are you sure you want to delete this message?';
 	    	}else{
 	    		 header='Delete File';
 	    		 title='Are you sure you want to delete this file?';
 	    	}
 	    	    modalBody='<b>'+appUserName+'</b><br>'+description;

 	    	   bootbox.dialog({
 	    	     message: modalBody,
 	    	     title: title,
 	    	     buttons: {
 	    	       success: {
 	    	         label: "Delete this Message",
 	    	         className: "btn-success",
 	    	         callback: function(result) {
 	    	        	if(result){
 	   	    			 deleteMessage(deleteRandomId)
 	   	    		  }
 	    	         }
 	    	       },
 	    	       danger: {
 	    	         label: "No",
 	    	         className: "btn-danger",
 	    	         callback: function() {
 	    	         }
 	    	       }
 	    	     }
 	    	   });
 	    	 	/*modalHtml = showConfirmationPopup(deleteRandomId,title,header,modalBody);
 	    	 	   $('#deleteModal').remove();
     	 		   $('body').append(modalHtml);
     	 	   	   $('#deleteModal').modal('show');*/
 	    		
 	    },//success
 	  error: function(jqXHR, textStatus, errorThrown) {
 		 
 	  }
 	
 	});//ajax
 }

 function deleteAllFileComments(fileId){
	 if(isRightWindowOpend == true && currentFileId == fileId){
		 HideRightPanel();
	 }
	 deleteMessageComments(channelName,fileId);
	 $('span[data-fileid='+fileId+']').closest('.mainMsg').remove();
 }
 
 
 function deleteMessage(deleteRandomId){
	 var fileId = $('span[data-msgid='+deleteRandomId+']').attr('data-fileid');
	 deleteAllFileComments(fileId);
	 deleteCacheMessage(deleteRandomId,channelName);
 	if($('#message'+deleteRandomId).closest('.allUserMsgs').find('.edit-msg').length ==1 ){
 		$('#message'+deleteRandomId).closest('.allUserMsgs').remove();
 	}else{
 		if(!$('#message'+deleteRandomId).find('.firstMsgcontainer').length){
 			
 			$('#message'+deleteRandomId).remove().end();
 		}
 		else{
 			$('#message'+deleteRandomId).remove();
 		}
 	}
 
 	messageObj = new Object();
	messageObj.description= "";
    messageObj.toUserId="1";
	messageObj.role="";
	messageObj.messageContentType="TEXT";
	messageObj.uploadFileId="";
	messageObj.comments="";
	messageObj.messagePersistentStatus="delete";
    messageObj.randomId=deleteRandomId;
    messageObj.gitNotificationId="";
 	sendMessageOn(messageObj);
 		
 }



 function spawnNotification(theBody,theIcon,theTitle,jsonData) {
 	var isFirefox = typeof InstallTrigger !== 'undefined';   // Firefox 1.0+

 		
 		 if(isFirefox==true){
 			//  var self = require("sdk/self");
 			//  var myIconURL = self.data.url('/assets/images/favicon1.png');
 			  var options = {
 					  body: theBody,
 				      iconUrl: theIcon
 			  }
 		  }else{
 			  var options = {
 			  body: theBody,
 			  icon: theIcon
 			  }
 		  }
 		//console.log(options);
 		var instance  = new Notification(theTitle,options);
 		instance.onclick = function () {
 		 notificationClickd(jsonData);
 		};
 		instance.onerror = function () {
 			// Something to do
 		};
 		instance.onshow = function () {
 		};
 		instance.onclose = function () {
 			// Something to do
 		};
 		
 		setTimeout(instance.close.bind(instance),3000);
 }
 
 function notificationClickd(jsonData){
	 roleL=jsonData.role.toLowerCase(); 
	 if(roleL=='user'){
		getChatData11(jsonData.fromUserId,roleL,jsonData.channelName);
	 }else if(roleL=='group'){
		 getChatData11(jsonData.groupId,roleL,jsonData.groupName);
	 }
 }
 


 function getCaret(el) { 
     if (el.selectionStart) { 
         return el.selectionStart; 
     } else if (document.selection) { 
         el.focus();
         var r = document.selection.createRange(); 
         if (r == null) { 
             return 0;
         }
         var re = el.createTextRange(), rc = re.duplicate();
         re.moveToBookmark(r.getBookmark());
         rc.setEndPoint('EndToStart', re);
         return rc.text.length;
     }  
     return 0; 
 }

 function replaceAll(isNew){
	 $('.edit-file-upld,.edit-image-upld,.edit-messageAdded').hide();
	 mentionsCode('&nbsp;');
 	 $('p').linkify({
 		 target: "_blank",
 		 handleLinks: function (links) {
 			 links.addClass('anchorlink');
           }
 	 });
 	
 	 
 	 $('p:contains("<br>")').each(function(){
 		 var x = $(this).html();
 		 var m = x.split('&lt;br&gt;');
 		 //console.log(m);
 		 var string = m.join(' <br> ');
 		 $(this).html(string);
 	 });
 	 
 	$('p:contains(":")').each(function(){
		    var x = $(this).html();
		    var m = x.split('&nbsp;');
		    var j =0;
		    m.forEach(function(i){
		    	var c = i.trim();
		    	var lnt = c.length;
		    	if(c[0] == ':' && c[lnt-1] == ':'){
		    		var objt = EmojiDataByName[c];
		    		//console.log(objt);
		    		//objt.find('.label').remove();
		    		//objt.find('img').attr('title',c);
		    		if(objt){
		    			m[j] = createEmoji(c,objt);
		    		}
		    		//console.log(m[j]);
		    	}
		    	j++;
		    })
		    var d = m.join(" ");
		    $(this).html(d);
		    
	 });
 	
 	 $('p:contains("~!")').each(function(){
 		 //console.log($(this).text());
 		 var x = $(this).html();
 		 var m = x.split("~!");
 		 //console.log(m);
 		 var string = m.join(' &nbsp; ');
 		 $(this).html(string);
 	 });
 	 $('p:contains(":")').each(function(){
 		    var x = $(this).html();
 		    var m = x.split(' ');
 		    var j =0;
 		    m.forEach(function(i){
 		    	var c = i.trim();
 		    	var lnt = c.length;
 		    	if(c[0] == ':' && c[lnt-1] == ':'){
 		    		var objt = EmojiDataByName[c];
 		    		//console.log(objt);
 		    		/*objt.find('.label').remove();
 		    		objt.find('img').attr('title',c);*/
 		    		if(objt){
 		    			m[j] = createEmoji(c,objt);
 		    		}
 		    		//console.log(m[j]);
 		    	}
 		    	j++;
 		    })
 		    var d = m.join(" ");
 		    $(this).html(d);
 		    
 	 });
 	 CodeMirror.modeURL = "/assets/snippet/mode/%N/%N.js";
 	 $('#messages .coder').each(function(){
 		 addCodeMirror($(this));
	  });
 	mentionsCode(',');
 	mentionsCode(' ');
 }
 function mentionsCode(char){
		$('.msg_description:contains("@")').each(function(){
	 		var m = [];
	 		var x = $(this).html();
	 	    m = x.split(char);
	 	    var j =0;
	 	    m.forEach(function(i){
	 	    		if(i[0] == '@'){
	     			var id = i.split('@')[1];
	     			if($.inArray(id,appUsersArray) != -1){
	     				m[j] = '<a href="#" class="profile-vw-click anchorlink" data-id="' + id + '" data-username="' + id + '">' + i + '</a>';
	     			}else if($.inArray(id,urlMatchingKeys) != -1){ 
	     				m[j] = '<a href="' + i + 'target="_blank" class="anchorlink">' + i + '</a>';
	     			}
	 	        }
	 	        j++;
	 	    })
	 	    var d= m.join(' ');
	 	    $(this).html(d);
	 	});
	}
 
 function createEmoji(k,v){
	return `<img src="/assets/blank.gif" class="img emoji-images" style="display:inline-block;width:25px;height:25px;background:${v.background};background-size:${v.backgroundSize};" alt="${k}">`;
 }
 function displayMessage(messageObj, appuser,isUserNameChange){
	 //console.log(messageObj);
	// console.log(JSON.stringify(messageObj));
	 //console.log("MessageBy "+messageObj.messageBy);
 	var message = '',msgcontent = '',msgtype = '',userimage='',attachmentContent = '';
 	var messageContentType =messageObj.messageContentType;
 	
 	 
 		userimage = '<img src="/secure-chat/get-appuser-image/' +messageObj.messageBy.id+ '" onError="this.onerror=null;makeNoImage(this);" class="img-circle chat-image"/>';
 		
 		if(messageContentType == 'TEXT'){
 		msgtype = convertTextDiv(messageObj,appuser);
 			if(messageObj.isAttachment){
 				attachmentContent=addAttachmentDiv(messageObj.attachmentList);
 			}
 		}
 		else if(messageContentType == 'IMAGE'){
 			msgtype = convertImageDiv(messageObj,appuser);
 		}
 		else if(messageContentType == 'FILE'){
 			msgtype = convertFileDiv(messageObj,appuser);
 		}
 		else if(messageContentType=='LEFTGROUP' || messageContentType=='CREATEGROUP' || messageContentType=='ADDTOGROUP' ||messageContentType=='DELETEGROUP' || messageContentType=='RENAMEGROUP'){
 			msgtype = convertGroupStatusDiv(messageObj,appuser);
 		}
 		else if(messageContentType=='SNIPPET'){
 			msgtype = convertSnippetDiv(messageObj,appuser);
 		}
 		else if(messageContentType == 'GITNOTIFICATION'){
 			isUserNameChange = true;
 			msgtype = convertGitNotificationDiv(messageObj,appuser);
 			userimage = '<i class="fa fa-github-square fa-3x "></i>';
 		}
 		else if(messageContentType=='COMMENT'){
 			userimage = '<img src="/secure-chat/get-appuser-image/' +messageObj.messageBy.id+ '"  onError="this.onerror=null;makeNoImage(this);" class="img-circle chat-image"/>';
 			isUserNameChange = true;
 			msgtype = convertCommentDiv(messageObj,appuser);
 		}else if(messageContentType=='BIRTHDAY'){
 			msgtype = convertBirthdayDiv(messageObj,appuser);
 			isUserNameChange = true;
 		}else if(messageContentType=='LEAVESTATUS'){
 			msgtype = convertLeaveStatusDiv(messageObj,appuser);
 			isUserNameChange = true;
 		}
 		//alert("MessageBy "+ messageObj.messageBy.id);
 		
 		if(isUserNameChange){
 			//alert(messageObj.description + "--" + messageObj.messageBy.userName)
 			msgcontent = '<div class="allUserMsgs newAppendedMsg">' +
 			'<div id="message'+ messageObj.randomId +'" class="mainMsg message-wrapper">' +
 	  		'<div class="edit-chat-time edit-chat" >' +
 	  			'<div class="user-image" id="message_star_holder' + messageObj.randomId + '">' +
					userimage +  messageObj.messageBy.userName +			
				'</div>'+
 			    '<div class="messageAdded chat-wrp user-message" id="'+ messageObj.randomId +'">' +
 			    	'<div id="profileWrapper' + message.randomId + '" class="profileWrapper">' + 	
 				    	'<h6><a href="#" class="profile-vw-click" data-userName='+ messageObj.messageBy.userName +' data-id='+ messageObj.randomId + '>' + messageObj.messageBy.userName + '</a>'+
 				    	'<span class="time time-chat time-main firts-msg-time" data-date="' + convertTimestampToSpecificFormat(messageObj,appuser).date + '">'+ convertTimestampToSpecificFormat(messageObj,appuser).time + '</span>' +
 				    	'<div id="profile-vw-wrapper'+ messageObj.messageBy.userName + '" class="profile-vw-wrapper"></div>'+
 			    	'</div>' + msgtype ;
 		}
 		else{
 			//alert(messageObj.description + "--" + messageObj.messageBy.userName)
 			msgcontent = '<div id="message' + messageObj.randomId +'" class="mainMsg message-wrapper">' +
 		 	'<div class="edit-chat-time edit-chat" >' +
 		 	'<div class="msgTime">'+
 	 		  '<span class="time time-chat pst-time" data-date="' + convertTimestampToSpecificFormatSubMsg(messageObj,appuser).date + '">' + convertTimestampToSpecificFormatSubMsg(messageObj,appuser).time +'</span>'+
 	 	     '</div>'+
 			   '<div class="user-message chat-wrp" id="message_body'+ messageObj.randomId + '">'+
 			   msgtype;
 		}
 		 
 	message  = msgcontent + attachmentContent +
 	 	'</div> '+
 	 	'</div> '+
 	 	'</div></div>';
 	//console.log(message);
 	return  message;
	 }
   
   function htmlEntities(str) {
	 var stng = String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g,'&gt;').replace(/"/g, '&quot;');
	 var arr = stng.split('&lt;br&gt;');
	 var content = arr.join("<br>");
	 var arr2 = content.split(' ');
	 var content2 = arr2.join('&nbsp;');
	 var arr3 =  content2.split('#$%^*');
	 var content3 = arr3.join('&lt;br&gt;');
	 var arr4 = content3.split(':&nbsp;');
	 var content4 = arr4.join(': ');
	 return content4;
	 }
 	
 	

 //appsuer means appsuerid(long value)

 function convertTextDiv(messageObj, appuser){
	 var editDeletelink='';
		if(messageObj.messageBy.id==appuser || ($.inArray('Admin',appuserJson.appUserRoleList) != -1) ){
			editDeletelink='<div class="edit-msg-hover edit-messageAdded">'+
	  			'<a href="#" class="edit_link modifylinks" data-container="body" data-placement="top" data-toggle="tooltip" title="Edit message"  id="'+ messageObj.randomId + '" data-role="edit"><i class="fa fa-pencil"></i></a>'+
	  			'<a href="#" data-placement="top" data-container="body" data-toggle="tooltip" title="Delete message" class="delete_link" id="'+ messageObj.randomId +'" data-role="file"><i class="fa fa-trash-o"></i></a>'+
	  	  		'</div>';
		}
		var  result='<div class="messageAdded edit-msg edit-msg'+ messageObj.randomId + ' firstMsgcontainer" id="message_body_content' + messageObj.randomId + '">'+
 	  	  			'<p class="content-pargh msg_description" data-id="'+ messageObj.messageBy.id +'">' + htmlEntities(messageObj.description) + '</p>'+
 	  	  				editDeletelink+
 	  	  			'</div>';	
		return result;
 }

 function convertImageDiv(messageObj,appuser){
	 var fileId= getUploadFileId(messageObj);
	 var editDeletelink='';
		if(messageObj.messageBy.id==appuser || ($.inArray('Admin',appuserJson.appUserRoleList) != -1)){
			editDeletelink='<div class="edit-image-upld edit-msg-hover"> '+
			'<span class="edit-image-upld-btnWrap"> '+
			'<a href="#" class="delete_link" id="'+ messageObj.randomId +'" data-role="file" data-toggle="tooltip" title="Delete Image"><i class="fa fa-trash-o"></i></a>'+
			'</span>'+
			'</div>';
		}
	 
 	var  result=	'<div class="upload-wrap edit-msg edit-msg' + messageObj.randomId + '" id="message_body_content' + messageObj.randomId + '"> '+
 					'<span class="upload-name" data-msgId="'+ messageObj.randomId +'" data-fileId="'+fileId+'">File Name:'+ messageObj.description + '</span>'+
 					'<div class="img-upld msg_description" data-id="'+ messageObj.messageBy.id +'">'+
 			     	  '<p class="content-pargh">' + getComment(messageObj) + '</p>'+
	 			     	 '<div class="image-upld">'+
	 				     	 	'<a href="/secure-chat/get-upload-image/' + fileId + '" target="_blank"><img src="/secure-chat/get-upload-image/' + fileId+ '" class="displayImage img-responsive"/></a>'+
	 				     	 	editDeletelink+
	 				     '</div>'+
	 				    getImageLikesAndCommentsCountInfo(messageObj,appuser)+
 			     	 '<div class="clearfix"></div>'+
 		     	'</div> '+
 		    '</div>';
 	return result;
 }
 function getImageLikesAndCommentsCountInfo(messageObj,appuser){
	 var likeObj = {};
	 var uploadFile = messageObj.uploadFile;
	 var snippetMap = uploadFile !='' ? uploadFile.snippetMap : '';
	// console.log(JSON.stringify(snippetMap));
	 if(uploadFile && snippetMap ){
		 likeObj.likesCount = snippetMap.likesCount;
		 likeObj.commentsCount = snippetMap.commentsCount;
		 likeObj.lastLiked = snippetMap.lastLiked;
		 likeObj.isLiked = snippetMap.isLiked;
	 }
	 likeObj.eventBy = messageObj.messageBy.id;
	 var fileId = uploadFile.id;
	 
	 var  result = `${getImageLikesDetailsAndCommentsCount(fileId,likeObj)}`;
	
	 return result;
 }
 
 
 
 
 function convertFileDiv(messageObj, appuser){
	 var fileId= getUploadFileId(messageObj);
	 var editDeletelink='';
			if(messageObj.messageBy.id==appuser || ($.inArray('Admin',appuserJson.appUserRoleList) != -1)){
			editDeletelink='<div class="edit-file-upld download-link-div-hvr">'+
	     		'<span class="edit-file-upld-btnWrap">'+
	     				'<a href="/secure-chat/download-upload-image/'+fileId+'" data-role="file" data-toggle="tooltip" title="Download File"><i class="fa fa-download"></i></a>'+
	     				'<a href="#" class="delete_link" id="'+ messageObj.randomId +'" data-role="file" data-toggle="tooltip" title="Delete File"><i class="fa fa-trash-o"></i></a>'+
	     		'</span>'+
	     	'</div>';
	  		}else{
	  			editDeletelink=`<div class="edit-file-upld download-link-div-hvr">
									<span class="edit-file-upld-btnWrap">
										<a href="/secure-chat/download-upload-image/${messageObj.uploadFile.id}" data-toggle="tooltip" title="Download File">
										<i class="fa fa-download"></i></a>
									</span>
								</div>`;
	  		}
			
			
			
 		    var  result='<div class="upload-wrap">' +
 		    	'<div class="download-link-div" id="message_body_content' + messageObj.randomId + '"> '+
 		    	'<span class="upload-name" data-msgId="'+ messageObj.randomId +'" data-fileId="'+fileId+'">File name : ' + messageObj.description + '</span>'+
 		    	'<div class="file-upld msg_description" data-id="'+ messageObj.messageBy.id +'">'+
 		    	    '<span class="file-type">'+
						'<i class="fa fa-file-archive-o"></i>'+
					'</span>'+
					'<span class="file-name">'+
					'<h5>' + messageObj.description + '</h5>'+
					'<h6 class="margin-0">' + getComment(messageObj) + '</h6>'+
				    '</span>'+
				    editDeletelink+
 		    	'</div>'+
 		    '</div>'+
 		   '</div>';
 		    return result;
 	
 }
 	 
 function convertGroupStatusDiv(messageObj, appuser){
	 var   result='';
			result =  '<div class="edit-msg edit-msg' + messageObj.randomId + '" id="message_body_content' + messageObj.randomId + '"> '+
 			  '<p class="content-pargh msg_description">' + messageObj.description + '</p>'+
 		  	'</div>	';
			
 			return result;
 }
 
 function convertSnippetDiv(messageObj, appuser){
	
	 var fileId= getUploadFileId(messageObj);
	 var snippetCode = messageObj.uploadFile.snippetMap.snippetCode;
	 var   result='';
	 var textArea = '<textarea cols="10" data-lines="' +messageObj.uploadFile.snippetMap.noOfLines+ '" data-mode="'+messageObj.uploadFile.uploadFileContentType+'" id="code-'+messageObj.randomId+'" class="coder">'+messageObj.uploadFile.snippetMap.snippetCode+'</textarea><br>';
	 		
	 var editDeletelink='';
		if(messageObj.messageBy.id==appuser || ($.inArray('Admin',appuserJson.appUserRoleList) != -1) ){
			editDeletelink='<div class="edit-msg-hover edit-messageAdded">'+
			'<a href="#" class="delete_link" id="'+ messageObj.randomId +'" data-role="file" data-toggle="tooltip" title="Delete Snippet"><i class="fa fa-trash-o"></i></a>'+
	  	  	'</div>';
		}
		   
		 result='<div class="messageAdded edit-msg edit-msg'+ messageObj.randomId + ' firstMsgcontainer" id="message_body_content' + messageObj.randomId + '">'+
		 		'<span class="upload-name" data-msgId="'+ messageObj.randomId +'" data-fileId="'+fileId+'"> Snippet Name: ' + messageObj.description + '</span>'+
	  			'<p class="content-pargh msg_description" data-id="'+ messageObj.messageBy.id +'">' +
	  			'<p class="content-pargh">' + getComment(messageObj) + '</p>'+
	  			textArea + '</p>'+
	  			editDeletelink+
	  			'</div>';
		   
		return result;
 }
 function convertBirthdayDiv(messageObj, appuser){
	/* var editDeletelink='';
		if(messageObj.messageBy.id==appuser || ($.inArray('Admin',appuserJson.appUserRoleList) != -1) ){
			editDeletelink='<div class="edit-msg-hover edit-messageAdded">'+
	  			'<a href="#" class="edit_link modifylinks" data-container="body" data-placement="top" data-toggle="tooltip" title="Edit message"  id="'+ messageObj.randomId + '" data-role="edit"><i class="fa fa-pencil"></i></a>'+
	  			'<a href="#" data-placement="top" data-container="body" data-toggle="tooltip" title="Delete message" class="delete_link" id="'+ messageObj.randomId +'" data-role="file"><i class="fa fa-trash-o"></i></a>'+
	  	  		'</div>';
		}*/
		var  result='<div class="messageAdded edit-msg edit-msg'+ messageObj.randomId + ' firstMsgcontainer" id="message_body_content' + messageObj.randomId + '">'+
		  		'<img src="/assets/images/bday/bday1.jpg" class=""/>'+
	  	  			'<p class="content-pargh msg_description" data-id="'+ messageObj.messageBy.id +'">Thrymr Wishes you a Very Happy Birthday: ' + messageObj.description + '</p>'+
	  	  			'</div>';
		
		return result;
 }
 function convertLeaveStatusDiv(messageObj, appuser){
	
	/*	 var editDeletelink='';
	if(messageObj.messageBy.id==appuser || ($.inArray('Admin',appuserJson.appUserRoleList) != -1) ){
		editDeletelink='<div class="edit-msg-hover edit-messageAdded">'+
			'<a href="#" class="edit_link modifylinks" data-container="body" data-placement="top" data-toggle="tooltip" title="Edit message"  id="'+ messageObj.randomId + '" data-role="edit"><i class="fa fa-pencil"></i></a>'+
			'<a href="#" data-placement="top" data-container="body" data-toggle="tooltip" title="Delete message" class="delete_link" id="'+ messageObj.randomId +'" data-role="file"><i class="fa fa-trash-o"></i></a>'+
	  		'</div>';
	}*/
	var  result='<div class="messageAdded edit-msg edit-msg'+ messageObj.randomId + ' firstMsgcontainer" id="message_body_content' + messageObj.randomId + '">'+
	  			'<p class="content-pargh msg_description" data-id="'+ messageObj.messageBy.id +'"> Employees on leave Today: ' + messageObj.description + '</p>'+
	  			'</div>';
	
	return result;
 }

 function convertCommentDiv(messageObj, appuser){
	 
	 var byUserName =getFileBYUserName(messageObj);
	// alert(byUserName);
	 var  result='<div class="messageAdded edit-msg edit-msg'+ messageObj.randomId + ' firstMsgcontainer" id="message_body_content' + messageObj.randomId + '">'+
			'<p class="commentColor">commented on <a class="profile-vw-click" data-username="'+ byUserName +'">'+ byUserName +'\'s</a> file <span class="upload-name" data-fileName=' + messageObj.uploadFile.uploadFileName + ' data-msgid="'+messageObj.randomId+'" data-fileid="'+ messageObj.uploadFile.id + '">'+ messageObj.uploadFile.uploadFileName+'</span></p>'+
			'<p class="content-pargh msg_description" data-id="'+ messageObj.messageBy.id +'">' + messageObj.description + '</p>'+
			'</div>';
	 return result;
 }
 
 
 
 function getFileBYUserName(messageObj){
	 //console.log("comment"+messageObj);
	 var byUserName = '';
	 if(messageObj.uploadFile  &&  messageObj.uploadFile.snippetMap ){
		 byUserName = messageObj.uploadFile.snippetMap.byUserName;
	 }
	 return byUserName;
 }
 
 function getComment(messageObj){
	 var comment='';
	 var commentList = messageObj.uploadFile != null ? messageObj.uploadFile.commentList : '';
		if(commentList.length){
			 comment=messageObj.uploadFile.commentList[0].comment;
		}
	 return comment;
 }
 	 
 function getUploadFileId(messageObj){
	 var fileId = '';
	// console.log("messageObj--------------------------------->"+messageObj.uploadFile);
	  if(messageObj.uploadFile){
		  fileId = messageObj.uploadFile.id;
	  }
	  return fileId;
	 
 }
 
 function convertTimestampToSpecificFormat( messageObj, appuser){
	 var date = new Date(messageObj.createdOn)
	 var formatteddatestr = {};
	 formatteddatestr.time = moment(date).format('hh:mm a');
	 formatteddatestr.date = moment(date).format('DD-MMM-YYYY');
 	 return formatteddatestr;
 }
 
 function convertTimestampToSpecificFormatSubMsg( messageObj, appuser){
	 var date = new Date(messageObj.createdOn)
	 var formatteddatestr = {};
	 formatteddatestr.time = moment(date).format('hh:mm');
	 formatteddatestr.date = moment(date).format('DD-MMM-YYYY');
 	 return formatteddatestr;
 }
 
 function convertTimestampToSpecificFormatComment(date, appuser){
	 var date = new Date(date)
	 var formatteddatestr = {};
	 formatteddatestr.time = moment(date).format('hh:mm a');
	 formatteddatestr.date = moment(date).format('DD-MMM-YYYY');
 	 return formatteddatestr;
 }
 
 function getDateUnderLine(date){
	  var date_Date = date.split('-')[2];
	  var date_Month = date.split('-')[1];
	  var date_Year = date.split('-')[0];
	  var dateObj = new Date(date_Year,date_Month-1,date_Date,0,0,0);
	  var formattedDate = moment(dateObj).format('DD-MMM-YYYY');
	  var today = new Date();
	  var yesterday = new Date();
	  yesterday.setDate(yesterday.getDate()-1);
	  var formatTodayDate = new Date(today.getFullYear(),(today.getMonth()+1),today.getDate(),0,0,0);
	  var newDate = moment(today).format('DD-MMM-YYYY');
	  var YesterdayDate = moment(yesterday).format('DD-MMM-YYYY');
	  result = '';
	  var returnedDate = '';
	 if(formattedDate == newDate){
		  returnedDate = "Today";
	  }
	  else if(formattedDate == YesterdayDate){
		  //alert(((dateObj.getTime()-formatTodayDate.getTime()) == 86400000));
		  returnedDate = "Yesterday"
	  }
	  else{
		  returnedDate = formattedDate;
	  }
	 //console.log("formatted Date" + returnedDate);
	  result = '<div class="row">'
		  + '<div class="col-md-12 text-center daytitle">'
		  + '<h4 style="border-bottom: 1px solid #E4E4E4;">'
		  + returnedDate
		  + '</h4>'
		  + '</div>'	
		  + '</div>';
	  $('h4:contains('+returnedDate+')').hide();
	  $('h4:contains('+returnedDate+')').eq(0).hide();
	  return result;
}
	 
 function changeDocumentTitleAndStar(){
	 document.title = '* BB8 | Connect';    
	 $("#ficon").attr('href','/assets/images/notification.ico');
 }
 function getAppUserDate(){
 	$.get("/secure-chat/get-appuser-json-Details/"+appuser, function(result) {
 		appuserJson = result.appUser;
 		//console.log(appuserJson)
 		//console.log("login appuser Id  :"+appuserJson.id);
 		//console.log("login appuser name  :"+appuserJson.userName);
 		//console.log("login appuser roles :"+appuserJson.appUserRoleList);
 		var chatAppuserSettings = result.appUserSettings;
 		if(chatAppuserSettings){
 	 		isEnableDesktopNotfication = chatAppuserSettings.isEnableDesktopNotfication,
 	   	    leftPanelColor = chatAppuserSettings.leftPanelColor;
 	 		//alert(leftPanelColor);
 			//console.log("login appuser settings :"+chatAppuserSettings);
 		}
 	 });
 }
 
 function changeOnlineStatus(jsonData){
	    console.log("Online Status");
		console.log(jsonData);
	 	if(jsonData.onlineStatus=='join'){
	 		//console.log("Joined");
	 		//console.log(jsonData.userId);
	 		  $('.online-status'+jsonData.userId).addClass('activeUser');
			  $('.online-status'+jsonData.userId).addClass('fa fa-circle');
	 	}else if(jsonData.onlineStatus=='quit'){
	 		  $('.online-status'+jsonData.userId).removeClass('activeUser');
	 	}
 }
 
 function appendCommentsData(data){
	 jsonData = JSON.parse(data.message);
	 //console.log("comment......*******.."+JSON.stringify(jsonData));
	 var fileId = jsonData.uploadFile ? jsonData.uploadFile.id : '';
	 var commentsCount = jsonData.uploadFile && jsonData.uploadFile.snippetMap ?  jsonData.uploadFile.snippetMap.commentsCount : '';
	 var isCommentAdded =false;
	 if(isRightWindowOpend == true && currentFileId == fileId && appuser != jsonData.messageBy.id){
		$('.commentsList').append(getFileCommentListDiv(jsonData.uploadFile.commentList));
		scrollBottom();
		scrollCommentsBottom();
		updateCommentsCount(fileId,commentsCount);
		isCommentAdded = true;
	 }
	 if(appuser != jsonData.messageBy.id && isCommentAdded == false){
		 updateCommentsCount(fileId,commentsCount);
	 }
		
	 
	 
 }
 
 function changeFileLikeStatus(jsonData){
	 var likeObj = JSON.stringify(jsonData);
	 var fileId= jsonData.fileId;
	
	 jsonData.isLiked = $('#like'+fileId).attr('data-rel');
	 var likedyByData = getFileLikesDiv(fileId,jsonData,'receive');
		 $('.likedByContainer'+fileId).html(likedyByData);
		 $('.likedByContainer'+fileId).attr('data-id',fileId);
		 createToolTip();
	// console.log("changeFileStaus"+likeObj);
 }
 
 function receiveEventOn(event){
		var data = JSON.parse(event.data)
		// Handle errors
		if(data.error) {
			alert("There is a websocket error please reload the page");
			return false;
		} else {
					 changeOnlineStatus(data);
					 
					 if(data.likeStatus){
						 changeFileLikeStatus(data);
					 }
					 
					  //get global notifications
					 //this is global notification Code Snippet  
					 // if(tabNo!=10){ //toUser is login through WebSocket, tab is other then connect
						  var totalMessages;
						  if(data.role == 'USER' && data.messagePersistentStatus=='saving'){
						   $.get("/secure-chat/get-global-message-notification-count/"+data.toUserId, function(result) {
							   if(tabNo!=10){
								   var count=parseInt(result, 10);
								   if(count>=1){
									   $('.messageNotificationConnectTo'+data.toUserId+'').html('<span class=""><i class="fa fa-asterisk "></i></span>');
									   //  $('.messageNotificationHeader'+appuser+'').html("")
							   		}
							   
								   if(prevType!='focus'){
										  if(Notification.permission == "granted"){
											   notificationTitle=data.userName;
											   spawnNotification(data.messageBody,notificationIcon,notificationTitle,data);
										   }
										 
									}	
							   }//tabNo!=10
							   
							   if(tabNo==10){
								      jsonData = JSON.parse(data.message);
										  if(Notification.permission == "granted" &&  prevType!='focus' && appuser != jsonData.messageBy.id){
											   notificationTitle=data.userName;
											   spawnNotification(data.messageBody,notificationIcon,notificationTitle,data);
										   }
										  changeDocumentTitleAndStar();
							   }
							   
							   
							  
							   
						  });
						 }
						 
						 if(data.role == 'GROUP' && data.messagePersistentStatus=='saving'){
							 $.get("/secure-chat/get-group-global-message-notification-count/"+data.groupId, function(result) {
									if(tabNo!=10){ // enable otherthen connnectab notification
										   var result1="*"
											   $.each(result.groupAppUserIdList, function(index, value ) {
												   //console.log( $('.messageNotificationConnectTo'+value+''));
												  // $('.messageNotificationConnectTo'+value+'').html('<span class="notifiastic">'+result1+'</span>');
												   $('.messageNotificationConnectTo'+value+'').html('<span class=""><i class="fa fa-asterisk "></i></span>');
												});
										   if(result.isValidMember==true && Notification.permission == "granted" && prevType!='focus' ){
											   notificationTitle=data.groupName;
											   spawnNotification(data.messageBody,notificationIcon,notificationTitle,data);
											   changeDocumentTitleAndStar();
										   }
								   }//tabNo!=10
									
							   if(tabNo==10){
								   
								   if(result.isValidMember==true && Notification.permission == "granted" && prevType!='focus' &&  appuser != jsonData.messageBy.id){
									   notificationTitle=data.groupName;
									   spawnNotification(data.messageBody,notificationIcon,notificationTitle,data);
									   changeDocumentTitleAndStar();
								   }
							   }
							   });//get
						 }
						 
						 
					 // }//tabNo if
						
					  //  ********************************** server side user messages**********************************************//
						
						 if(data.role == 'USER'){
							//console.log("data.role messageTo"+data.role);
								if(data.messagePersistentStatus == 'saving'){
										
									pushMessageToCache(JSON.parse(data.message),data.userName);
									//if messageToId and active tabId is equal  changeMessageViewStatus as true(tabNo==1 means active connect tab)
									   if(clientId==data.fromUserId && tabNo==10 && (role=='user'|| role=='USER')){
										   $('#messageType-'+data.toUserId+'').html('');
										   jsonData = JSON.parse(data.message);
										   firstMessageAndDateUnderline();	
										   var content='';
										   content += displayMessage(jsonData,appuser,jsonData.isUserNameChange);
										   $('#messages').append(content);
										   
										   replaceAll();
										   saveLinkPreview(data.randomId,'true');
										   scrollBottom();
						                   $('#underLine'+appuser+'').css('display','none');
						                  
										   var messageId=data.messageId;
										    $.post("/secure-chat/change-message-view-status/"+messageId, function() {
											});
									   }else{
										   //means touser login but not active tab,add Notification to touser non active tab
										   //get notificationNo calling ajax call in chatController messageCount() method  
										   $.get("/secure-chat/get-message-notification-count/"+data.fromUserId, function(result) {
											   var count=parseInt(result, 10);
											   if(count>=1){
												   $('.messageNotification'+data.fromUserId+'').html('<span class="badge label-danger">'+result+'</span>');
												   //sort listitems(notifiactions)
												  $('.messageNotification'+data.fromUserId+'').addClass('active');
												  $('.search-users').prepend($('.messageNotification'+data.fromUserId+'').closest('.client'));
											   }
										   });
									   }
									  // end 
									   if(data.messageContentType=='COMMENT'){
											appendCommentsData(data);
									   }
								}else if(data.messagePersistentStatus == 'edit'){
									 updateCacheMessage(data.randomId,data.userName,data.messageBody);
									 $('#message_body_content'+data.randomId+'').find('.msg_description').html(data.messageBody);
								}else if(data.messagePersistentStatus == 'delete'){
									 deleteCacheMessage(data.randomId,data.userName);
									 $('#message'+data.randomId+'').hide();
									 deleteServerFileComments(data);
									
									 
								}else if(data.messagePersistentStatus == 'typing'){
									  if(clientId==data.fromUserId && (role=='user'|| role=='USER')){
										  if(data.typingUserName!=""){
											// alert(data.typingUserName);
											  $('#messageType-'+data.toUserId+'').html(data.typingUserName+' is typing...');
										  }else{
											   $('#messageType-'+data.toUserId+'').html("");
										  }
											  
									  }
								}//else if
					 }else if(data.role == 'GROUP'){    //  ********************************** server side group messages **********************************************//
						 if(data.messagePersistentStatus == 'saving' && tabNo == 10 ){
							 			jsonData = JSON.parse(data.message);
										  if(clientId==data.groupId  && (role=='GROUP' || role=='group') && tabNo==10){ //active
											  // console.log('active group tab');
											   if(data.fromUserId!=appuser){
												   var content='';
												   firstMessageAndDateUnderline();												   
												   content = displayMessage(jsonData,appuser,jsonData.isUserNameChange);
												   $('#messages').append(content);
												   
												   replaceAll();
												   saveLinkPreview(data.randomId,'true');
												   scrollBottom();
												   $('#underLine'+appuser+'').css('display','none');
												   var messageId=data.messageId;
												   var groupId = data.groupId;
												    $.post("/secure-chat/change-message-notification-status/"+groupId, function() {
													});
											  }
										   }else{
											   //console.log('group notification');
											  // console.log("groupnotification role "+role);
											//   alert("else notification group Id"+data.groupId);
											   //means touser login but not active tab,add Notification to touser non active tab
											   //get notificationNo calling ajax call in chatController messageCount() method  
											   $.get("/secure-chat/get-group-message-notification-count/"+data.groupId, function(result) {
												   var count=parseInt(result, 10);
												   if(count>=1){
													   $('.messageNotificationGroup'+data.groupId).html('<span class="label badge label-danger">'+result+'</span>');
												   }
											   });
										   }
										  if(data.messageContentType == 'CREATEGROUP'){
								 				 addGroup(data.groupId);
								 				 //and add to cache allMsgsMap[groupName]
								 				allMsgsMap[data.groupName] = {};
								 		  }else if(data.messageContentType=='COMMENT' &&  appuser != jsonData.messageBy.id){
								 		 		appendCommentsData(data);
								 				pushMessageToCache(JSON.parse(data.message),data.groupName);
								 		  }else{
								 				 pushMessageToCache(JSON.parse(data.message),data.groupName);
								 		  }
							 }else if(data.messagePersistentStatus == 'edit'){
								 updateCacheMessage(data.randomId,data.groupName,data.messageBody);
								 $('#message_body_content'+data.randomId+' > .msg_description').html(data.messageBody);
							}else if(data.messagePersistentStatus == 'delete'){
								 deleteCacheMessage(data.randomId,data.groupName);
								if($('#message'+data.randomId).closest('.allUserMsgs').find('.edit-msg').length ==1 ){
									$('#message'+data.randomId).closest('.allUserMsgs').remove();
								}else{
									if(!$('#message'+data.randomId).find('.firstMsgcontainer').length){
										$('#message'+data.randomId).remove().end();
									}
									else{
									 $('#message'+data.randomId).find('.firstMsgcontainer').remove();
									}
								}
								 deleteServerFileComments(data);
							}else if(data.messagePersistentStatus == 'typing'){
								  if(clientId==data.groupId && tabNo==10 &&  data.fromUserId!=appuser && (role=='GROUP' || role=='group')  ){
									  $.get("/secure-chat/isvalidgroupmeber/"+clientId, function(result) {
										  $.each(result.appUserIdList, function(index, value ) {
											  //console.log("group appuser id"+value);
											  if(data.typingUserName!=""){
													// alert(data.typingUserName);
												  $('#messageType-'+value+'').html(data.typingUserName+' is typing...');
												  }else{
													  $('#messageType-'+value+'').html("");
												  }
												 
											});
										  
									  });//get  
								  }
							}//else if
						 
					 }//else if
					 

		}
	}
 
 function deleteServerFileComments(data){
	 jsonData =JSON.parse(data.message);
	// console.log(jsonData);
	 if(data.messageContentType == 'FILE' || data.messageContentType == 'SNIPPET' ||  data.messageContentType == 'IMAGE'){
		 var fileId='';
		 if(jsonData.uploadFile){
			 fileId=jsonData.uploadFile.id;
			 deleteMessageComments(data.channelName,fileId);
			 deleteAllFileComments(fileId);
		 }else{
			// console.log('upload file id is nulll.........'+jsonData);
		 }
		 
	 }
 }
 
 
 //Calling Only When we Click on Load more
 function getChannelMessageData(jsonData,mode){
	 totalMessage = 0;
	  var chatData = '';
	  var lastreadMessage =''
		
	//console.log("jsonData............."+JSON.stringify(jsonData));
	  
	  $.each(jsonData.messagePage,function(date,messageList){
		  chatData+=getDateUnderLine(date);
		  EachchatData='';
	       $.each(messageList,function(k,v){
	    	   EachchatData += displayMessage(v,appuser,v.isUserNameChange);
	    	   totalMessage++;
	       });
	       chatData += EachchatData;
	       EachchatData='';
	       
	  });

	 
	  // 1  means on load more....
	  if(mode == '1'){
		  $('#messages').prepend(chatData);
		  createToolTip();
		  replaceAll();
	  }else{
		  $('#messages').html(chatData);
		  createToolTip();
		  replaceAll();
		  scrollBottom();
	  }
	 
	  if(jsonData.lastUnReadRandomId){
		  lastreadMessage = "#message"+jsonData['lastUnReadRandomId'];
	  }
	  $('<div class="row" id=underLine' + appuser + '>'
			  + '<div class="col-md-12 text-center daytitle">'
			  + '<h4  class="header-newmsg" style="border-bottom: 1px solid #E4E4E4;">'
			  + 'New Messages'
			  + '</h4>'
			  + '</div>'	
			  + '</div>').insertBefore($(lastreadMessage));
	  if(isLastRow || !totalMessage){
		  $('#loadMore').hide();
	  }else{
		  $('#loadMore').show();
	  }
	  $('[data-toggle="tooltip"]').tooltip(); 
 }
 
/* function getChannelMessageOnload(jsonData){
	 totalMessage = 0;
	 var chatData = '';
	 $.each(jsonData,function(date,msgsList){
		 var EachchatData ='';
		 chatData += getDateUnderLine(date);
		 $.each(msgsList,function(k,v){
			 EachchatData += displayMessage(v,appuser,v.isUserNameChange);
			 totalMessage++;
		 });
		 chatData += EachchatData;
		 EachchatData='';
	 });
	 $('#messages').html(chatData);
	 scrollBottom();
	 replaceAll();
	 console.log("isLast--->2"+isLastRow);
	 if(isLastRow || !totalMessage){
		 $('#loadMore').hide();
	 }else{
		 $('#loadMore').show();
	 }
 }*/
 
 
 //Calling on Page load and when we switch from one channel to another displaying from cache
 function getChannelMessageDataCached(jsonData,mode){
	 //console.log("Inside getChannelMessageDataCached");
	 totalMessage = 0;
	  var chatData = '';
	  var lastreadMessage =''
	  var previousDate ='';
	  $.each(jsonData,function(k,v){
		       if(k != 'firstRow'  && k != 'isLastRow'){
			       var createdOn = timeConverter(v.createdOn);
			       if(createdOn != previousDate){
			  	   chatData+=getDateUnderLine(createdOn);
			       }
			  	   chatData += displayMessage(v,appuser,v.isUserNameChange);
		    	   totalMessage++;
		    	   previousDate = timeConverter(v.createdOn);
		       }
	  });

	  if(jsonData.lastUnReadRandomId){
		  lastreadMessage = "#message"+jsonData['lastUnReadRandomId'];
	  }
	  // 1  means on load more....
	  if(mode == '1'){
			  	$('#messages').prepend(chatData);
			  	createToolTip();
			  	replaceAll();
	  }else{
		  if(Object.keys(jsonData).length > 2){
			  	$('#messages').html(chatData);
			  	replaceAll();
			  	createToolTip();
			  	$('#messages').load(function(){
			  		
			  	})
			  		
			  	
		  }
		  else{
			  $('#messages').html(ShowDefaultMessage());
		  }
	  }
	  
	  $('<div class="row" id=underLine' + appuser + '>'
			  + '<div class="col-md-12 text-center daytitle">'
			  + '<h4  class="header-newmsg" style="border-bottom: 1px solid #E4E4E4;">'
			  + 'New Messages'
			  + '</h4>'
			  + '</div>'	
			  + '</div>').insertBefore($(lastreadMessage));
	 // console.log("isLast--->1"+isLastRow);
	  if(isLastRow || !totalMessage){
		  $('#loadMore').hide();
	  }else{
		  $('#loadMore').show();
	  }
	  scrollBottom();
	  $('[data-toggle="tooltip"]').tooltip(); 
 }
 
 
 function generateRandomId(){
	return  moment(new Date()).format('DDMMYYYYhhmmssSSSS');
 }
 
 
 function entersavedata(event){
	 var randomId = event.attr('id');
		//var actionRole = $(this).attr('data-role');
		var description;
		messageObj = new Object();
		var thisMsg = event;
		var changedText = event.closest('.editTextArea').find('.editable-txt').val();
		$.post("/secure-chat/edit-message/"+randomId,$('#editForm').serialize(),function(result) {
			$.each(result, function(index, value ) {
				 if(index=='description'){
					 messageObj.description= value;
				 }else  if(index=='randomId'){
					 messageObj.randomId=value;
				 }
				
			});
			//update cache message
			updateCacheMessage(messageObj.randomId,channelName,messageObj.description);
			thisMsg.closest('.editTextArea').hide();
			thisMsg.closest('.edit-msg').find('.msg_description').text(messageObj.description).show();
			
			
			messageObj.toUserId="0";
			messageObj.role="Admin";
			messageObj.uploadFile="";
			messageObj.comments="";
			messageObj.gitNotificationId="";
			messageObj.messageContentType="TEXT";
			messageObj.messagePersistentStatus="edit";
			
			sendMessageOn(messageObj);
			replaceAll();
		 });//post
 }
/* search groups and users  */
 function groupmembersSearch(){
	 
	  $(".searchinput-new").keyup(function(){ 
			var newSrchgrp = $(this).val();	
			//console.log(newSrchgrp)
			if(newSrchgrp){
				$('.searchgrpname-rst').hide();
				$('.searchgrpname-rst:containsIN("'+newSrchgrp+'")').show();
				$('.searchgrpname-rst:containsIN("'+newSrchgrp+'")');
			}else{ 
				
				$('.searchgrpname-rst').show();
				$('.searchgrpname-rst:containsIN("'+newSrchgrp+'")').css("background","none");
			}
		});
	  }
 
 function topChannelName(channelName){
	  var status = $('.search-users li[data-username="'+channelName+'"] .fa-circle').hasClass('activeUser')  ? "activeUser" : '';
	  var id = $('.search-users li[data-username="'+channelName+'"]').attr('id');
	  var result=`<span class="dots"> <i class="fa fa-circle online-status${id} ${status}"></i> </span><span class="profile-vw-click" data-username="${channelName}">@${channelName}</span>`;
	  
	  return result;
 }
function makeactiveChannel(role,clientId){
	$('li.client').removeClass('active');
	if(role=='user'){
		$('.active-client'+clientId).addClass('active');
	}else if(role=='group'){
		$('.active-group'+clientId).addClass('active');
	}
}
var channelInterval ='';
 function getChatData(LClientId,LRole){
	 console.log("LClientId--"+LClientId);
	 console.log("LRole--"+LRole);
	$('#loadMore').hide();
	clearInterval(channelInterval);
	clientId=LClientId;
	role=LRole;
	makeactiveChannel(LRole,LClientId);
	channelInterval = setInterval(function(){
	      if(channelsLoaded){
	 		$('.profile-vw-click').popover('hide');
	        $("#msgText").focus();
	        $("#msgText").val(''); 
	        $('.users li,.channels li').removeClass('active');
	 		role=LRole;
	 			document.title ='BB8 | Connect';    
		        $("#ficon").attr('href','/assets/images/faviconnew.ico');
				if(role=='user'){
					$('.messageNotification'+clientId+'').html('');
					channelName=$('#name'+clientId).val();
					$('.active-client'+clientId).addClass('active');
					$('.toUserName').html(topChannelName(channelName));
					$(".group-drop").hide();
					$(".user-drop").show();
			        if(allMsgsMap[channelName]){
			          if(Object.keys(allMsgsMap[channelName]).length <= 2){
						$('#loadMore').hide();	
					  }
			        	loadMoreIndex=allMsgsMap[channelName].firstRow;
						isLastRow=allMsgsMap[channelName].isLastRow;
						getChannelMessageDataCached(allMsgsMap[channelName],'0');
			        }
					//update MessageNotification status
					$.post("/secure-chat/change-message-notification/"+clientId, function(result) {
					});
					
				}else if(role=='group'){
					$('.messageNotificationGroup'+clientId+'').html('');
					$(".group-drop").show();
					$(".user-drop").hide();
					channelName=$('#group'+clientId+'').val();
					if(channelName=='General'){
						$('#leave-li').hide();			
					}else{
						$('#leave-li').show();	
					}
					$('.active-group'+clientId+'').addClass('active');
					$('.toUserName').html('#'+channelName+'');
					
					if(allMsgsMap[channelName]){
			        	loadMoreIndex=allMsgsMap[channelName].firstRow;
						isLastRow=allMsgsMap[channelName].isLastRow;
						getChannelMessageDataCached(allMsgsMap[channelName],'0');
						$.post("/secure-chat/change-group-message-notification/"+clientId, function() {
						});	
			        }else{
				        	channelName=$('#group'+clientId+'').val();
							if(channelName=='General'){
								$('#leave-li').hide();			
							}else{
								$('#leave-li').show();	
							}
							$('.active-group'+clientId+'').addClass('active');
							$('.toUserName').html('#'+channelName+'');
							$.post('/secure-chat/get-new-channel-jsondata/' + clientId,
									function(data) {
										pushChannelToMap(data);
										getChannelMessageDataCached(allMsgsMap[channelName],'0');
							});//get
							$.post("/secure-chat/change-group-message-notification/"+clientId, function() {
							});	
						}
			      }else if(clientId!=='firstTime' && role!=='firstTime' && (typeof clientId !== 'undefined') && (typeof role !== 'undefined')){
					     $('#msgtxbox').show();
			      }
				// save or update ChatAppUser lastSeentab id
				$.post("/secure-chat/update-lastseentab/"+clientId+"/"+role, function() {
				});
				clearInterval(channelInterval);
	      }else{
	    	  $('#messages').html(`<div class="text-center">
				<img src="/assets/images/loading-s.gif" ><br>Please Wait Loading Messagess...
				</div>`);
	      }
		},5);
 }
 

 function getChatData11(LClientId,LRole,channelName){
	 	    $('.profile-vw-click').popover('hide');
	        $("#msgText").focus();
	        $("#msgText").val(''); 
	        $('.users li,.channels li').removeClass('active');
	 		clientId=LClientId;
	 		role=LRole;
	 		channelName=channelName;
	 			document.title ='BB8 | Connect';    
		        $("#ficon").attr('href','/assets/images/faviconnew.ico');
				if(role=='user'){
					$('.messageNotification'+clientId+'').html('');
					$('.active-client'+clientId).addClass('active');
					$('.toUserName').html('@'+channelName+'');
					$(".group-drop").hide();
					$(".user-drop").show();
			        if(allMsgsMap[channelName]){
			        	loadMoreIndex=allMsgsMap[channelName].firstRow;
						isLastRow=allMsgsMap[channelName].isLastRow;
						getChannelMessageDataCached(allMsgsMap[channelName],'0');
			        }
					//update MessageNotification status
					$.post("/secure-chat/change-message-notification/"+clientId, function(result) {
					});
				}else if(role=='group'){
					$('.messageNotificationGroup'+clientId+'').html('');
					$(".group-drop").show();
					$(".user-drop").hide();
					if(channelName=='General'){
						$('#leave-li').hide();			
					}else{
						$('#leave-li').show();	
					}
					$('.active-group'+clientId+'').addClass('active');
					$('.toUserName').html('#'+channelName+'');
					
					if(allMsgsMap[channelName]){
			        	loadMoreIndex=allMsgsMap[channelName].firstRow;
						isLastRow=allMsgsMap[channelName].isLastRow;
						getChannelMessageDataCached(allMsgsMap[channelName],'0');
						$.post("/secure-chat/change-group-message-notification/"+clientId, function() {
						});	
			        }
			      }
				// save or update ChatAppUser lastSeentab id
				$.post("/secure-chat/update-lastseentab/"+clientId+"/"+role, function() {
				});
				
 }
function updateOnlineStatus() {
		$.get("/secure-chat/onlineAppusers", function(result) {
			$.each(result.onlineIdsList, function(index, value ) {
				   $('.online-status'+value).addClass('activeUser');
			});
			$.each(result.offlineIdsList, function(index, value ) {
				  $('.online-status'+value).removeClass('activeUser');
			});
			
		})//get
}//updateOnlineStatus
 

function scrollBottom(){
	var scollamount = parseInt($('.chat-wrapper')[0].scrollHeight);
	 $('.chat-wrapper').scrollTop(scollamount);
	 $(".chat-wrapper").animate({ scrollTop: scollamount+999 }, "fast");
}
function scrollCommentsBottom(){
	var scollamount = parseInt($('.msg-wrap')[0].scrollHeight);
	 $('.msg-wrap').scrollTop(scollamount);
	 $(".msg-wrap").animate({ scrollTop: scollamount+999 }, "fast");
	 $('.commentText').textareaAutoSize();
}

function firstMessageAndDateUnderline(){
	var d = new Date();
	var curr_date = d.getDate();
	var curr_month = d.getMonth();
	var curr_year = d.getFullYear();
	var formattedDate = curr_year + "-" + (curr_month+1) + "-" + curr_date;
	var formattedDate2 = moment(d).format('DD-MMM-YYYY');
	//console.log("onpageDate--> " + $('.time-chat').last().attr('data-date') + " TodayDaat-->"+formattedDate2);
	if(!$('#messages').text() || $('.time-chat').last().attr('data-date') != formattedDate2){
		
		   var dateUnderline = getDateUnderLine(formattedDate);
		   $('#messages').append(dateUnderline);
	}
}

function isMessageEdited (createdOn ,lastUpdate){
	var message=('edited');
	
	 var createdOnDate = new Date(createdOn);
	 var lastUpdateDate = new Date(lastUpdate);
	 //console.log(createdOnDate); 
	 //console.log(lastUpdateDate); 
}
function timeConverter(UNIX_timestamp){
  var a = new Date(UNIX_timestamp);
  var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  var year = a.getFullYear();
  var month = a.getMonth()+1;
  var date = a.getDate();
  var hour = a.getHours();
  var min = a.getMinutes();
  var sec = a.getSeconds();
  var time = year + '-' + month + '-' + date;
  return time;
}


function addGroup(groupId){
	//console.log("groupCreated.....");
	$.get("/secure-chat/isvalidgroupmeber1/"+groupId, function(result) {
		//alert(result.isMember);
		if(result.isMember){
			var groupUser=result;
			var groupContent = '<li class="active-group'+groupId+' client link" id="'+groupId+'" data-username="'+groupUser.groupName+'" data-role="group" data-id='+groupId+'>'
			    + '<input id="group'+groupId +'" class="hidden"  value=' + groupUser.groupName +'>'
				+ '<a href="/chat/connect/'+groupUser.groupName+'"> <span class="group-name">#'+groupUser.groupName+'</span>'
				+ '<span class="messageNotificationGroup'+groupId+'">'
		           + '<span class="label badge label-danger"></span>'
		           + '</span>'
		           + '</a>'
		           + '</li>';
			//console.log("groupCreated....."+groupUser.groupName);
			$('.lefttop-channelname').prepend(groupContent);
			$('a[href="/chat/connect/'+groupUser.groupName+'"]').click();
			$('.messageNotificationGroup'+groupId).html('<span class="label badge label-danger">1</span>');
		}
	});						
	
}

function saveLinkPreview(randomId,recieve){
	 	$("#message"+ randomId +" .msg_description a").each(function(){
	 		if(!($(this).hasClass("visited")) && !($(this).hasClass("boldText"))){
	 			$(this).addClass('visited');
	 			var previewId = '#message_body_content' + randomId
	 			$(this).attr('recive',recieve);
	 			$(this).urlive({
	 			    imageSize: 'small',
	 			   container : previewId
	 			});
	 		}
	 	});
}
function addAttachmentDiv(attacmentlist,appuser){
	
	var attachmentContent ='',desc='';
	$.each(attacmentlist,function(i,attachment){
		imgDiv = '';
		//console.log("All Attachments");
		if(attachment.imageUrl && attachment.imageUrl != 'null' && attachment.imageUrl.indexOf('mailto:') == -1){
			imgDiv = '<div class="span4"><img src="' + attachment.imageUrl + '"></div>';
		}
		if(attachment.description && attachment.description !='null'){
		 desc =	attachment.description;
		}
		attachmentContent += '<div class="link-preview row-fluid">'
			+ imgDiv
			+ '<div class="span8">'
			+ '<a class="boldText" href="' + attachment.url+ '" target="_blank">' + attachment.title+ '</a>'
			+ '<p>' + desc + '</p>'
			+ '</div>'
			+ '</div>';
	});
return attachmentContent;
}


function replaceString(by,to,String){
	var splittedDesc = String.split(by)
	newString = splittedDesc.join(to);
	return newString; 
}

/*function saveSnippet(){
	 var fileName, fileType, size,code,snippetComments,snippetName;
	 var toUserRole= $('.mylist option:selected').attr('data-role');
	 var toUserId=$('.mylist').val();
	 
	fileName = $('#fileName').val() != '' ? $('#fileName').val() : $('#mode option:selected').text()+" Snippet";
	snippetName=$('#snippetName').val(fileName);
	fileType = $('#fileType').val();
	code = $('.CodeMirror textarea').val();
	var text = editor.getValue();
	$('#code').val(text);
	snippetComments = $('#snippetComments').val();
	
 	messageObj = new Object();
 	messageObj=prepareMessageObj(messageObj);
 	messageObj.uploadFile.commentList[0].comment=snippetComments;
 	var messageContentType='SNIPPET';
 	
 	$.ajax({
 		type : 'POST',
 	    url: '/secure-chat/save-code-snippet',
 	    data: $('#save-snippet-form').serialize(),
 	    success:function(result){
 	    	console.log("uploadImageId---------------------");
 	    		messageObj.uploadFileId= result.uploadFile.id;
 	    		messageObj.uploadFile.id= result.uploadFile.id;
 	    		messageObj.uploadFile.snippetMap= result.uploadFile.snippetMap;
 	    		console.log(messageObj)
	 	    		messageObj.toUserId=toUserId;
	 	    		messageObj.role=toUserRole;
					messageObj.description=result.uploadFile.uploadFileName;
					var snippetComments=$('#snippetComments').val();
					if(snippetComments){
						messageObj.comments=snippetComments;
					}else{
						messageObj.comments="";	
					}
					messageObj.gitNotificationId="";
					messageObj.messagePersistentStatus="saving";
					messageObj.messageContentType=messageContentType;
					console.log("messageObj"+messageObj);
					var lowerRole = role.toLowerCase();
					console.log()
					if(toUserId==clientId && toUserRole==lowerRole){
						if($('.msg_description').last().attr('data-id') == messageObj.messageBy.id){
				 			messageObj.isUserNameChange = false;
				 		}
				 		else{
				 			messageObj.isUserNameChange = true;
				 		}
						var appendHtml = displayMessage(messageObj,appuser,messageObj.isUserNameChange);
						$('#messages').append(appendHtml);
						
						replaceAll();
						scrollBottom();
					}
					console.log(result.uploadFile.snippetMap)
					pushMessageToCache(messageObj,channelName);
					sendMessageOn(messageObj);
					//messageObj.uploadFile.snippetMap='';
					
					$('#msgText').val('');
					$('.create-code-exceed').hide();
					$('textarea.js-auto-size').css('height','auto');
					$('.chat-wrapper').css('margin-bottom','0px');
					$('#save-snippet-form')[0].reset();
 	    },
 	  error: function(jqXHR, textStatus, errorThrown) {
 		$('#fileUploadModel').modal('show');
 	    //console.log(jqXHR.status);
 	    //console.log(textStatus);
 	    //console.log(errorThrown);
 	  }
 	
 	});
  }*/

function addCodeMirror(x){
	 if(!x.data('codemirrorInstance')){
	  var fileType = x.attr('data-mode') ? x.attr('data-mode') : 'text/html';
	  var editor = CodeMirror.fromTextArea(document.getElementById(x.attr('id')), {
		  lineNumbers: true,
		  readOnly : 'nocursor',
		  mode : fileType
	  });
	   setMode(editor, fileType);
	  //change(fileType)
	  x.data('codemirrorInstance',editor);
	  editor.setSize('100%', '100%');
	  if(x.attr('data-lines') >= 5){
	  $($('#message_body_content'+x.attr('id').split('-')[1])).append('<div style="position:absolute;bottom:65px;z-index:9999;width:100%;text-align:center;"><h3><span class="badge loadMoreCode hidden" data-id="'+
		x.attr('id').split('-')[1]+'">+ Click to see more (' + x.attr('data-lines') + ' lines)</span></h3></div>')
	  }
	 }
	}
function setMode(cm, mode) {
	mode = mode.toString();
    if(mode != 'text') {
    	//console.log(mode)
    	mode = $('#mode option[value="'+  mode +'"]').attr('data-js') ? $('#mode option[value="'+  mode +'"]').attr('data-js') : '';
    	//console.log(mode)
    	if(mode != ''){
    	var script = '/assets/snippet/mode/'+mode+'/'+mode+'.js';
        $.getScript(script, function(data, success) {
            if(success) cm.setOption('mode', mode);
            else cm.setOption('mode', 'clike');
        });
    	}
    }
    else cm.setOption('mode', 'clike');
}
function isGroupAdmin(){
	var cId = parseInt(clientId);
	//alert(cId);
	//alert(grpIdarray);
	if($.inArray(cId,grpIdarray) != -1){
		$('#remove-li').show();
		$('#rename-li').show();
	}else{
		$('#remove-li').hide();
		//$('#rename-li').hide();
	}

}
function renderProfile(profileObject){
	$('.profile-vw-click').popover('hide');
	currentFileId = '';
	var editLink = ( appuser == profileObject.id || ($.inArray('Admin',appuserJson.appUserRoleList) != -1) ) ? '<a href="/editUser/'+profileObject.id+'" target="_blank" data-action="edit_member_profile_modal" class="btn btn-default">Edit</a>&nbsp;' : '';
	var msgLink =  appuser != profileObject.id ?  '<a href="javascript:void(0)" onclick="getChatData('+profileObject.id+',\'user\')" target="new_1461216294560" class="btn btn-default">Message</a>': '';
	var profileContent = '<center><div class="profileMainImage"><img src="/imgProfile/'+profileObject.id+'" class="profile-large-image img-responsive" onError="this.onerror=null;makeNoImage(this);"><span class="hidden">'+ profileObject.userName +'</span></div></center>'
						
	
						+ '<div class="col-md-6 col-md-offset-3 text-center">'
						+ '<h4 id="profUserName">'+profileObject.userName+'</h4>'
						+ '<h5 class="user-role">'+profileObject.jobTitle +'</h5>'
						+ '<div class="col-md-12 padding-left0">'
						+ '<div class="member_action_bar">'
						//+ editLink
						+ msgLink
						+ '</div></div>'
						
						+ '<a class="member_preview_menu_target btn btn_outline"><i class="ts_icon ts_icon_chevron_large_down"></i></a>'
						+ '</div>'
						+ '<hr>'
						+ '<table class="table borderless">'
						+ '<tbody><tr>'
						+ '<td><span class="small_right_padding old_petunia_grey" title="Username">Full Name</span></td>'
						+ '<td><span>' +profileObject.fullName + '</span></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td><span class="small_right_padding old_petunia_grey" title="Phone Number">Phone Number</span></td>'
						+ '<td>'+profileObject.mobileNo+'</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td><span class="small_right_padding old_petunia_grey" title="Email">Email Id</span></td>'
						+ '<td>' +profileObject.email + '</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td><span class="small_right_padding old_petunia_grey" title="Git Id">Employee Id</span></td>'
						+ '<td>' +profileObject.employeeId + '</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td><span class="small_right_padding old_petunia_grey" title="">Git Id</span></td>'
						+ '<td>' +profileObject.gitId + '</td>'
						+ '</tr>'
						+ '</tbody>'
						+ '</table>'
						+ '</div>';
	$('#right-window-body').hide();
	$('#right-window-body').html(profileContent);
	$('#right-window-body').fadeIn('slow');
	isRightWindowOpend = true;
}

function renderComments(fileId){
	$('.profile-vw-click').popover('hide');
	var openflag = false;
	if(isRightWindowOpend == false && currentFileId == fileId){
		openflag = true;
	}else if(currentFileId != fileId){
		openflag = true;
	}
	likeObj = '';
	if(openflag){
	var messageContentType = '';
	var content = '';
	var fileMsgObj = '';
	var isLiked = '';
	$.ajax({
	    type : 'GET',
		url : "/secure-chat/get-file-details/"+fileId, 
		async : false,
		success : function(result) {
			likeObj = result;
			fileMsgObj = result.message;
			messageContentType = result.message.messageContentType;
			isRightWindowOpend=true;
			currentFileId = fileId
			fileMsgObj.uploadFile.likeList = result.likeList;
			isLiked =result.isLiked;
		}
	});
	if(messageContentType== 'FILE'){
		content = `<div class="file-upld msg_description" style="width:100% !important" data-id="3">
 				   <span class="file-type"><i class="fa fa-file-archive-o"></i></span>
 				   <span class="file-name"><h5>${fileMsgObj.description}</h5><h6 class="margin-0"></h6></span>
 				   <div style="display: none;" class="edit-file-upld download-link-div-hvr">
 				   </div>
 				   </div>
 				  <span><span> File name : </span>  <b><small class="fileName">${fileMsgObj.description}</small></b></span></br>
 				  ${getFileDetails(fileMsgObj)} `;
	}else if(messageContentType== 'IMAGE'){
		content = `<center><img src="/secure-chat/get-upload-image/+${fileId}" class="profile-large-image img-responsive"></center>
				   <span><span> Image name : </span>  <b><small class="fileName">${fileMsgObj.description}</small></b></span>
				   <br>
				   ${getFileDetails(fileMsgObj)}
				   &nbsp;&nbsp;
				   ${getImageLikesDetailsAndCommentsCount(fileId,likeObj)}
				   `;
	}else if(messageContentType== 'SNIPPET'){
		content = `<textarea cols="10" data-mode="${fileMsgObj.uploadFile.uploadFileContentType}" id="code-${fileMsgObj.randomId}-${fileId}" class="coder2" ></textarea>
				   <span><span> Snippet Name : </span>  <b><small class="fileName">${fileMsgObj.description}</small></b></span></br>
				   ${getFileDetails(fileMsgObj)} `;
	}
	
	$('#right-window-body').html('<br><br><br><br><br><center>Please Wait Fetching Comments ....</center>');
	
	commentContent = `${getprofileName(fileMsgObj)} ${content} 
				   	  <div class="msg-wrap">
				      <div class="media conversation">
				      <div class="commentsList">
				      ${getCommentsList(fileMsgObj)}
				      </div>
				      </div>
				      </div>
				      <div class="col-md-12">
				      <div class="row">
				      <textarea data-randomId="${fileMsgObj.randomId}"  data-fileId="${fileId}" data-fileName="${fileMsgObj.description}"  rows="1" class="form-control input-sm commentText" placeholder="Leave a comment..."></textarea>
                      </div>
				      <div class="col-md-12 text-right">`;
			          //<button type="button" data-randomId="${msgObj.randomId}" data-fileId="${fileId}" data-fileName="${msgObj.uploadFile.uploadFileName}"  class="addComment btn btn-warning btn-sm">Add Comment</button>`;
    $('#right-window-body').hide();			      
	$('#right-window-body').html(commentContent);
	$('#right-window-body').fadeIn('slow');
	createToolTip();
	//adding code mirror to text area for snippet
	if(messageContentType== 'SNIPPET'){
	 //console.log(fileMsgObj.uploadFile.snippetMap.snippetCode)
	 $('#right-window-body .coder2').val(fileMsgObj.uploadFile.snippetMap.snippetCode);
	 addCodeMirror($('#right-window-body .coder2'));
	}
	
	scrollCommentsBottom();
	}
	isRightWindowOpend = true;
}

function getImageLikesDetailsAndCommentsCount(fileId,likeObj){
	 result = `<p><span class="likesStatusContainer${fileId}" data-id="${fileId}">
	   ${likesStatusUpdate(fileId,likeObj)}
	   </span>&nbsp;&nbsp;&nbsp;
	   <span class="totalLikes likedByContainer${fileId}" data-id="${fileId}">
	   ${getFileLikesDiv(fileId,likeObj)}
	   </span>
	   &nbsp;&nbsp;&nbsp;&nbsp;
	   ${getFileCommentsCountSpan(fileId,likeObj)}
	   </p>`;
	   return result;
}

function getFileCommentsCountSpan(fileId,likeObj){
	var  result =  `<span class="upload-name commentsCount${fileId}" data-fileid="${fileId}">${getCommentCount(likeObj)+ ' comments'}` ;
	return result;
}
function getCommentCount(likeObj){
	var commentsCount = likeObj.commentsCount;
	var  result = '<span>'+ commentsCount +'</span>';
	return result;
}

function updateCommentsCount(fileId,count){
	$('.commentsCount'+fileId).html('<span>'+ count +'</span>'+' comments');
}

function getFileDetails(fileMsgObj){
	var filDetails = '';
	var description = '';
	var channelName= fileMsgObj.uploadFile != '' && fileMsgObj.uploadFile.snippetMap != '' ? fileMsgObj.uploadFile.snippetMap.groupName : '' ;
	var fileSize= fileMsgObj.uploadFile != '' && fileMsgObj.uploadFile.snippetMap != '' ? fileMsgObj.uploadFile.snippetMap.fileSize : '' ;
	if(fileMsgObj){
		if(fileMsgObj.role == 'USER'){
			description = 'Private file shared with you';
		}else if(fileMsgObj.role == 'GROUP'){
			description = `Shared in <span class="fileName"><b>#${channelName}</b></span>`;
		}
	}
	filDetails = `<small style="color:gray">${convertMonthDateformate(fileMsgObj) } • ${bytesToSize(fileSize)} • ${description}</small>`;
	
	return filDetails;
}



function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    if (i == 0) return bytes + ' ' + sizes[i];
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
}
function convertMonthDateformate(fileMsgObj){
	 var date = new Date(fileMsgObj.createdOn)
	 var result='';
	 result = moment(date).format('MMM DD ddd  hh:mm a');
	 return result;
}

function getRightPanel(linkType,el){
	var fileId = el.attr('data-fileid'),randomId = el.attr('data-msgid');
	var panelHeader = "";
	if(!isRightWindowOpend){
		if($('#profUserName').text() == el.attr('data-username')){
			isRightWindowOpend = true;
		}
		$('.contentWIndow').removeClass('hidden');
		$('.logged-user,.chat-input,#msgText').css('width',rightPanelWidth-450 + 'px');
		$('.right-panel').css('width',rightPanelWidth-400 + 'px');
		$('.emojisSpan').css('right','405px');
	}else{
		$('.contentWIndow').removeClass('hidden');
	}
	if(linkType == "profile"){			
		renderProfile(UserProfiles[el.attr('data-username')]);
		panelHeader = "Team Directory";
	}
	else if(linkType == "file"){
		$('.contentWIndow').removeClass('hidden');
		renderComments(fileId);
		panelHeader = "File";
	}
	$('#right-window-heading').html('' + panelHeader +' <span class="closeleftWindow pull-right"><i class="fa fa-times" aria-hidden="true"></i></span>');
}

function HideRightPanel(){
	$('.logged-user,.chat-input,#msgText,.right-panel').css('width',rightPanelWidth-50 + 'px');
	$('.right-panel').css('width',rightPanelWidth + 'px');
	$('.contentWIndow').addClass('hidden');
	$('.emojisSpan').css('right','15px');
	isRightWindowOpend = false;
}
var liked = false;
 function likesStatusUpdate(fileId,likeObj){
	 //console.log(".......likeObj...>"+JSON.stringify(likeObj));
	 var statusContent = '';
	 if(likeObj.isLiked){
		 liked = true;
		 statusContent = `<a href="#" class="like" data-id=${fileId} id="like${fileId}" data-status="unLike" title="unLike" data-rel="true"><i class="fa fa-thumbs-o-down" aria-hidden="true"></i> Unlike </a>`;
	 }else{
		 liked = false;
		 statusContent = ` <a href="#" class="like" data-id=${fileId} id="like${fileId}" data-status="like" title="Like" data-rel="false"><i class="fa fa-thumbs-o-up" aria-hidden="true"></i> Like </a>`;
	 }
	 return statusContent; 
 }
 function getFileLikesDiv(fileId,likeObj,event){
	 var content = '';
	 if(event == 'receive'){
		// alert('recieve');
		 content = getFileLikesContentServer(likeObj,fileId)
	 }else{
		 content = getFileLikesContent(likeObj);
	 }
	 var fileDiv =`
	 <span>
	  <a>${content}</a>
	 </span>`;
	 return fileDiv;
 }
 
 function getFileLikesContent(likeObj){
	 var likesCount = likeObj.likesCount,lastLiked= likeObj.lastLiked ? likeObj.lastLiked : '' ,isLiked= likeObj.isLiked,content="";
	 if(likesCount && lastLiked){
		 if(likesCount == 1 ){
			 if(isLiked){
				 content +='You liked';
			 }else{
				 content +=`${lastLiked.likeBy.userName} liked`;
			 }
		 }
		 else if(likesCount == 2){
			 if(isLiked){
				 content +=` You, ${lastLiked.likeBy.userName}`;
			 }else{
				 content +=`${lastLiked.likeBy.userName} and ${likesCount-1} Others`;
			 }
		 }
		 else if(likesCount > 2){
			 if(isLiked){
				 content +=` You, ${lastLiked.likeBy.userName} and ${likesCount-2} others`;
			 }else{
				 content +=`${lastLiked.likeBy.userName} and ${likesCount-2} Others`;
			 }
		 }
	//return content +=` You, Ashok Nanda and 230 others`;
	 }
	 return content;
	}
 
 function getFileLikesContentServer(likeObj,fileId){
	 liked = ($('#like'+fileId).attr('data-rel') == 'true');
	 //alert(liked);
	 var likesCount = likeObj.likesCount,lastLiked= likeObj.lastLiked,isLiked= likeObj.isLiked,content="",userId = likeObj.eventBy ;
	 if(likesCount){
		 if(likesCount == 1 ){
			 //alert(lastLiked.likeBy.userName)
			 if(liked){
					 content +='You liked';
			 }else{
				 content +=`${lastLiked.likeBy.userName} liked`;
			 }
		 }
		 else if(likesCount == 2){
			 //alert(lastLiked.likeBy.userName)
			 if(liked){
				 content +=` You, ${lastLiked.likeBy.userName}`;
			 }else{
				 content +=`${lastLiked.likeBy.userName} and ${likesCount-1} Others`;
			 }
		 }
		 else if(likesCount > 2){
			 //alert(lastLiked.likeBy.userName)
			 if(liked){
				 content +=` You, ${lastLiked.likeBy.userName} and ${likesCount-2} others`;
			 }else{
				 content +=`${lastLiked.likeBy.userName} and ${likesCount-2} Others`;
			 }
		 }
	//return content +=` You, Ashok Nanda and 230 others`;
	 }
	 return content;
	}
 function updateFileLike(likeStatus,fileId,likeBy){
	var  routesUrl = '/secure-chat/connect/update-file-like'
	 $.ajax({
         url: routesUrl,
         type: 'POST',
         contentType: 'application/json',
         data: JSON.stringify({
        	 fileId : fileId,
        	 likeBy : likeBy,
        	 likeStatus : likeStatus
         }),
         dataType: 'json',
         success : function(result){
        	 var likesStatusData = likesStatusUpdate(result.fileId,result);
        	 var likedyByData = getFileLikesDiv(result.fileId,result,"");
        	 $('.likesStatusContainer'+result.fileId).html(likesStatusData);
        	 $('.likedByContainer'+result.fileId).html(likedyByData);
        	 $('.likedByContainer'+result.fileId).attr('data-id',fileId);
        	 createToolTip();
         }
     });
	 
 }
 function getAllLikesTooltip(fileId){
	 var  routesUrl = '/secure-chat/connect/get-all-image-likes'
	 var userNames = [];
		 $.ajax({
	         url: routesUrl,
	         type: 'POST',
	         async : false,
	         contentType: 'application/json',
	         data: JSON.stringify({
	        	 fileId : fileId
	         }),
	         dataType: 'json',
	         success : function(result){
	        	 userNames = result;
	         }
	     });
		 return userNames;
 }
 
 function createToolTip(){
		var timeout="";
		$(document).off('mouseover','.totalLikes').on('mouseover','.totalLikes',function(){
			var el = $(this);
			if(el.data("tooltipset")) {
				el.tooltip('destroy');
			}
			var fileId = el.attr('data-id');
			var list = getAllLikesTooltip(fileId);
			//console.log(list);
			var userList = '<ul>';
			for (i = 0; i < list.length; i++) {
				userList += `<li>${list[i]}<li>`;
			}
				el.tooltip({
					trigger : 'hover focus',
					html : true,
					animation : true,
					container : 'body',
					title : userList + '</ul>'
				});
				el.tooltip('show');
		});
		
		$(document).on('mouseleave','.totalLikes',function(){
			var el = $(this);
			el.tooltip('destroy');
		});
	}
 
function setActiveChannel(){
	 var roleLower = role.toString().toLowerCase(); 
	 var clientLower = clientId.toString().toLowerCase(); 
	 var current = $('.mylist').find('option[data-role="'+roleLower+'"]').filter(function(){
		 if($(this).val() == clientLower){
			 return $(this);
		}
	});
	$('.mylist').find('option:selected').removeAttr('selected');
	$('.mylist').find('option:selected').prop('selected',false);
	current.attr('selected',true);
	current.prop('selected',true);
	$('.mylist').trigger('chosen:updated');
	
}
function ShowDefaultMessage(){
	//console.log(appUserNames);
	var content = '<div ><div>'
				 + '<div class="user-image"><img src="/secure-chat/get-appuser-image/'+clientId+'" class="img-circle chat-image" onError="this.onerror=null;makeNoImage(this);"><span class="hidden">' + channelName +'</span></div>'
				 + '<p class="dm_badge_meta">'
				 + '<span >'+ appUserNames[channelName] +'</span>'
				 + '<span  title="away"><i class="ts_icon ts_icon_presence presence_icon"></i></span>'
				 + '<span >'
				 +  ' <a href="#" class="profile-vw-click" data-username="'+channelName+'" >@'+channelName+'</a>'
				 + '</span>'
				 + '<br>'
				 + '</p>'
				 + '</div>'
 			 	 + '<p class="dm_explanation margin_auto align_left clear_both">'
 			 	 + 'There are no messages with ' 
 			 	 + '<strong>'+channelName+'</strong>.'
 			 	 + '</p>'
 			 	 + '</div>';
	return content;
}
function getprofileName(msgObj){
	var content = `<div class="user-image" id="message_star_holder${msgObj.messageBy.userName}" >
				<img src="/secure-chat/get-appuser-image/${msgObj.messageBy.id}" class="img-circle chat-image" onError="this.onerror=null;makeNoImage(this);">${msgObj.messageBy.userName}</div>
				<div id="profileWrapper" class="profileWrapper" >
				<h6><a href="#" class="profile-vw-click" data-username=${msgObj.messageBy.userName} data-id="${msgObj.randomId}">${msgObj.messageBy.userName}</a>
				<div id="profile-vw-wrapper${msgObj.userName}" class="profile-vw-wrapper">
				</div></h6></div><br>`;
	return content;
}
function getCommentsList(fileObj){
	var comments = '';
	var commentData = fileObj.uploadFile.commentList;
		comments=getFileCommentListDiv(commentData);
	return comments;
}

function getFileCommentListDiv(commentList){
	//console.log(commentList);
	var content = '';
	$.each(commentList,function(i,v){
		if(v.comment.trim()){
        content +=`<div class="user-image" id="message_star_holder210420160108158100">
					<img src="/secure-chat/get-appuser-image/${v.commentBy.id}" class="img-circle chat-image"onError="this.onerror=null;makeNoImage(this);">${v.commentBy.userName}</div>
					<div id="profileWrapper" class="profileWrapper">
					<h6><a href="#" class="profile-vw-click" data-username='${v.commentBy.userName}' data-id="210420160108158100">${v.commentBy.userName}</a>
					<span class="time time-chat time-main firts-msg-time" data-date="${convertTimestampToSpecificFormatComment(v.createdOn,"").date}">${convertTimestampToSpecificFormatComment(v.createdOn,"").time}</span>
					<div id="profile-vw-wrapper" class="profile-vw-wrapper">
					</div></h6>
					${v.comment}
					</div><br>`;
		}
	});
	return content;
	
}

function  sendCommentMessage(event){
	var comment=$('.commentText').val();
	if(comment){
		var data =$(event);
		var uploadFileId= data.attr('data-fileId');
		var uploadFileName= data.attr('data-fileName');
		var description= comment;
		var commentsCount = '';
		var byUserName
		
		var fileObj = {};
		var message = {};
		
		$.ajax({
		    type : 'POST',
			url : "/secure-chat/get-file-to-details/"+uploadFileId, 
			async : false,
			success : function(result) {
				fileObj=result;
				message=result.message;
				commentsCount = result.commentsCount;
				byUserName = result.byUserName;
	     }
	   });
		
		
		messageObj = new Object();
		messageObj=prepareMessageObj(messageObj);
		messageObj.description=description;
		messageObj.uploadFileId=uploadFileId;
		messageObj.uploadFile.id=uploadFileId;
		messageObj.uploadFile.uploadFileName=uploadFileName;
		messageObj.uploadFile.snippetMap.byUserName=byUserName;
		
		messageObj.toUserId = fileObj.messageTo;
		
		var commentObj=[{}];
		commentObj[0].comment=description;
		commentObj[0].commentBy=appuserJson;
		commentObj[0].createdOn=new Date();
		messageObj.uploadFile.commentList[0]= commentObj;
		
		// to userId = fileid ->  -> messageBy
		// to role = fileid -> message-> role-
		
		messageObj.role = message.role;
		messageObj.messagePersistentStatus="saving";
		messageObj.messageContentType='COMMENT';
	 	messageObj.comments=comment;
	 	
	 	$('.commentText').val('').focus();
		

	 	// to channelName equals active channel name append messages div
		if(message.messageBy.userName ==  fileObj.channelName && fileObj.channelName == channelName){
			$('#messages').append(displayMessage(messageObj,appuser,false));
			
		}
		
		if(message.role == 'USER'){ 
			if(fileObj.channelName == channelName){
				$('#messages').append(displayMessage(messageObj,appuser,false));
				
			}
			if(message.messageBy.userName == channelName){
				$('#messages').append(displayMessage(messageObj,appuser,false));
				
			}				
		}else if(message.role == 'GROUP'){
			if(fileObj.channelName == channelName){
				$('#messages').append(displayMessage(messageObj,appuser,false));
				
			}
		}

		
		$('.commentsList').append(getFileCommentListDiv(commentObj));
		//alert(fileObj.channelName);
		//pushing message and comment objects to caching
		if(message.role == 'USER'){ 
			if(messageObj.messageBy.userName == fileObj.channelName){
				//alert('ashok commented.. admin pushed..');
				messageObj.toUserId = message.messageBy.id;
			pushMessageToCache(messageObj,message.messageBy.userName); //admin 
			}else{
				//alert('admin commented..ashok channel name pushed');
				messageObj.toUserId = fileObj.messageTo;
				pushMessageToCache(messageObj,fileObj.channelName); //ashok
			}
			messageObj.role = 'USER';
		}else if(message.role == 'GROUP'){
			pushMessageToCache(messageObj,fileObj.channelName);
			messageObj.toUserId = fileObj.messageTo;
			messageObj.role = 'GROUP';
		}
		updateCommentsCount(uploadFileId,commentsCount+1);
		sendMessageOn(messageObj);
		
		if(commentsMap[uploadFileId]){
			commentsMap[uploadFileId].push(commentObj[0]);
		}else{
			commentsMap[uploadFileId] = [];
			commentsMap[uploadFileId].push(commentObj[0]);
		}
		
		
		replaceAll('new');
		scrollBottom();
		scrollCommentsBottom();
	}
	
}

function getEmojiTab(tabId){
	var content = `<div style="right: -15px;" tabindex="-1" class="emoji-items nano-content">`;
	$.each(EmojiDataByTab[tabId],function(k,v){
		content += `<a href="javascript:void(0)" title="${k}"><img src="/assets/blank.gif" class="img" style="display:inline-block;width:25px;height:25px;background:${v.background};background-size:${v.backgroundSize};" alt="${k}"><span class="label">${k}</span>
	    </a>`;
	});
	content += "</div>";
	return content;
}




//Caching Methods 


function pushChannelToMap(result){
	$.each(result,function(group,messageList){
		eachGroup = {}
	       $.each(messageList.messagePage,function(k,v){
	    	   $.each(v,function(k2,v2){
	    		   //console.log(v2)
	    	   eachGroup[v2.randomId] = v2;
	    	   });
	       });
		allMsgsMap[group] = eachGroup;
		allMsgsMap[group].firstRow =messageList.firstRow;
		allMsgsMap[group].isLastRow =messageList.isLastRow;
		//console.log(allMsgsMap[group]);
	  });
}


function deleteCacheMessage(deleteRandomId,channelName){
	 //console.log('daleteCacheMessage() method channelName :'+allMsgsMap[channelName][deleteRandomId]);
	 delete allMsgsMap[channelName][deleteRandomId]
	 //console.log(allMsgsMap[channelName][deleteRandomId]);	
}
function pushMessageToCache(messageObj,channelName){
	//console.log('pushMessageToCache() method channelName :'+ channelName + '-----'+messageObj);
	//console.log("Hiiiiiiiii"+channelName)
	//console.log(allMsgsMap[channelName])
	allMsgsMap[channelName][messageObj.randomId]=messageObj;
	//console.log(allMsgsMap);
}

function updateCacheMessage(randomId,channelName,description){
	//console.log('updateCacheMessage() method channelName :'+ channelName + '-----'+messageObj);
	allMsgsMap[channelName][randomId].description = description;
}

/*****push Attachment to messageObj ***/

function pushAttachmentToCache(messageObj,channelName){
	//console.log('pushAttachmentToCache() method channelName :'+ channelName + '-----'+messageObj);
	allMsgsMap[channelName][messageObj.randomId].attachmentList=messageObj;
	//console.log(allMsgsMap);
}

function pushCommentsToCache(randomId,messageObj,channelName){
	//console.log('pushCommentsToCache() method  :');
	allMsgsMap[channelName][randomId].uploadFile.commentList.push( messageObj.uploadFile.commentList[0] );
	//console.log(allMsgsMap);
}

/*****delete  file comments  ***/
function deleteMessageComments(channelName,fileId){
	 $.each(allMsgsMap[channelName],function(k,v){
		 if(v.uploadFile){
			 if(v.uploadFile.id == fileId){
				 deleteCacheMessage(k,channelName);
			 }
		 }
  	 });
	
	
}



//GIT NOtifications Methods

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


function makeNoImage(nameBadegeEl){
		$(nameBadegeEl).parent().addClass('noImage');
		$(nameBadegeEl).parent().nameBadge({
  			border: {
  				width: 3
  			}
  		});
		$(nameBadegeEl).parent().fadeIn('slow');
}

function getNameBadgeFromUserName(userName){
	//console.log("Username -----"+userName.length);
	userName = userName.trim();
	var fullName = appUserNames[userName] ? appUserNames[userName] : userName;
	//console.log("Fullname -----"+fullName);
	var namesArray = fullName.split(' ');
	var nameBadge = '';
	//console.log(namesArray + '--------namesArray');
	if(namesArray.length >=2){
		nameBadge = namesArray[0][0] + namesArray[1][0];
	}else{
		nameBadge = namesArray[0][0];
		//console.log("else----"+nameBadge);
	}
	return nameBadge;
}
var alphabet = ["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"];
function getNameBadgeColor(userName){
	var colorPos ;
	var namesBadge = getNameBadgeFromUserName(userName);
	//console.log("userName--------" + userName)
	var s1 = namesBadge[0].toLowerCase();
	//console.log('namesBadge[0]------'+namesBadge[0]);
	var s2 = namesBadge[1] ? namesBadge[1].toLowerCase() : "";
	var n1 = alphabet.indexOf(s1)+1;
	var n2 = (alphabet.indexOf(s2) != -1)  ? alphabet.indexOf(s2)+1 : 1;
	//console.log(namesBadge+ "------------nameBadge");
	//console.log(n1 + "!------!" + n2)
	//console.log("Names ----" + s1 + "------" + s2)
	var total = n1 + n2;
	//console.log("total------" + total)
	var colorIndex = (total != 1) ? total%10 : 1;
	//console.log("colorIndex------" + colorIndex)
	return parseInt(colorIndex);
	//console.log(String.fromCharCode(97 + n));
}

function saveAppUserSettings(isEnableDesktopNotfication,leftPanelColor){
	 var  routesUrl = '/secure-chat/connect/save-chat-appuser-settings'
	 var appUserSettings = {};
		 $.ajax({
	         url: routesUrl,
	         type: 'POST',
	         async : false,
	         contentType: 'application/json',
	         data: JSON.stringify({
	        	 isEnableDesktopNotfication : isEnableDesktopNotfication,
	        	 leftPanelColor : leftPanelColor
	         }),
	         dataType: 'json',
	         success : function(result){
	        	 	appUserSettings = result;
	        	    $('#preferences-modal').modal('hide');
	        	    $('.left-panel').css('background-color',leftPanelColor);
	        		$('.style').remove();
	        		var active_color = secondary_colors[metro_colors.indexOf(leftPanelColor) ? metro_colors.indexOf(leftPanelColor) : 0];
	        		$('body').append(`<style class="style">li.client.active a{background-color : ${active_color} !important; }`);
	         }
	     });
		 return appUserSettings;
}

function setLeftPanelColor(){
	//$('li.client.active a').css('background-color','transparent');
	//$('li.client.active a').css('background','transparent');
	if(leftPanelColor){
		//var active_color = secondary_colors[(metro_colors.indexOf(leftPanelColor) != -1) ? metro_colors.indexOf(leftPanelColor) : 0];
		var index = metro_colors.indexOf(leftPanelColor);
		//alert(index);
		$('.left-panel').css('background-color', leftPanelColor);
		$('body').append(`<style class="style">li.client.active a{background-color : ${secondary_colors[index]} !important; }`);
		//$('li.client.active a').css('background',secondary_colors[index]);
		active_color = secondary_colors[index];
	}else{
		var index = Math.floor(Math.random() * 7);
		$('.left-panel').css('background-color', metro_colors[index]);
		$('body').append(`<style class="style">li.client.active a{background-color : ${secondary_colors[index]} !important; }`);
		active_color = secondary_colors[index];
		//$('li.client.active a').css('background',secondary_colors[index]);
	}
}

