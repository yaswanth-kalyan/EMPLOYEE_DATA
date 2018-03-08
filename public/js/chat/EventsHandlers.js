var setTimeout3 = '';
var setTimeout2 = '';
var setTimeout1 = '';
var Load_id = "" ;
var Load_role = "";
//Address bar location URL Change Plugin 
////////////////////////////////////////////////////////////
  			var init = true, 
                state = window.history.pushState !== undefined;
            
            // Handles response
            var handler = function(data) {
                $('title').html($('title', data).html());
                $('.content').html($('.content', data).html());
                $('.page').show();
                $.address.title(/>([^<]*)<\/title/.exec(data)[1]);
            };
            
            $.address.state('/chat/connect').init(function() {

                // Initializes the plugin
                $('.client a').address();
                
            }).change(function(event) {

                // Selects the proper navigation link
                $('.client a').each(function() {
                    if ($(this).attr('href') == ($.address.state() + event.path)) {
                        $(this).addClass('selected').focus();
                    } else {
                        $(this).removeClass('selected');
                    }
                });
                
                if (state && init) {
                
                    init = false;
                
                } else {
                	var item = $('.client a[href="'+$.address.state() + event.path+'"]');
                	Load_id = item.parent().attr('id');
                	Load_role = item.parent().attr('data-role').toLowerCase();
                	getChatData(Load_id,Load_role);
                }

            });

            if (!state) {
            
                // Hides the page during initialization
                document.write('<style type="text/css"> .page { display: none; } </style>');
            }
//////////////////////////////////////////
//End of Address bar location URL Change Plugin
            
            
//Start of INitJS
/////////////////////////////////////

			   var appUsers=[];
			   var appUserNames={};
			   var appUsersArray = [];
			  $.get("/secure-chat/getallappuserlist", function(result) {
				 
				  $.each(result, function(listindex,map ) {
					  var object={};
					  $.each(map, function(key,mapValue ) {
						  if(key=='username'){
						    appUsersArray.push(mapValue);
							object.username=mapValue;
						  }
						  if(key=='fullname'){
							  object.fullname=mapValue;
						  }
					});
					 // console.log(object.username+"object"+object.fullname);
					  appUserNames[object.username] = object.fullname;
					  var temp={'username':object.username,'fullname':object.fullname}
					  appUsers.push(temp);
					});
				  
				 
				  $('.comment').suggest('@', {
		              data: appUsers,
		              position: 'top',
		              map: function(user) {
		                return {
		                  value: user.username,
		                  text: '<strong>'+user.username+'</strong> <small>'+user.fullname+'</small>'
		                }
		              }
		            });
			  });

$(window).on("blur focus", function(e) {
    prevType = e.type;
});

window.addEventListener('load', function () {
	//alert(Notification.permission);
	  if (window.Notification && Notification.permission !== "granted") {
	    Notification.requestPermission(function (status) {
	      if (Notification.permission !== status) {
	        Notification.permission = status;
	      }
	    });
	  }
});


$(window).load(function() {
	$('.right-panel').scrollTop($('.right-panel')[0].scrollHeight);
});


$(function(){
	$('.chosen-container-single,.chosen.mylist').css({'width':'100%'});
	var newColor = '';
	$(document).on('click','.color-picker li a',function(){
		newColor = $(this).attr('data-color');
		$('.left-panel').removeClass().addClass('left-panel').addClass(newColor);
		$('.color-picker-wrapper').removeClass( "show-picker" );
	    $('.first-message').removeClass().addClass('first-message').addClass(newColor +'-color');
	    $('.logged-user h4').removeClass(newColor +'-color').addClass(newColor +'-color');
	});
	$('.color-pick').click(function() {
	  $('.color-picker-wrapper').toggleClass( "show-picker" );
	});
	$(document).on('click','.users li a,.channels li a',function(){
		$('.message').focus().val('');
		$(this).parent().addClass('active').addClass(newColor);	
	});
	
	$('.file-upload').css('display','none');
	$('.uploadicon').click(function(){
		$('.file-upload').trigger('click');
	});
	$('.right-panel').scrollTop($('.right-panel')[0].scrollHeight);
	var newMessage = ''; 
	$('.message').keyup(function(e){
		newMessage = $(this).val();
		var code = (e.keyCode ? e.keyCode : e.which);
	     if(code == 13) { //Enter keycode
	       $('.chat-wrapper').append(
	       	'<div class="message-wrapper">'+
				'<div class="user-image">'+
					'<img src="/assets/images/user.png" alt="" />'+
				'</div>'+
				'<div class="user-message">'+
					'<p class="user-input">'+newMessage+'</p>'+
				'</div>'+
				'<div class="clearfix"></div>'+
			'</div>');
	       $('.message').val('');
	     }
		
	});
	
	var channelLength = $('.channels ul li').length;
	if( channelLength <= 5 ){
		//$('.channels').css('max-height','190px');
	}else if( channelLength > 5 && channelLength <= 8 ){
		//$('.channels').css('max-height','280px');
	}else if( channelLength >= 9 && channelLength > 10){
		//$('.channels').css({'max-height':'340px','overflow':'auto'});
	}
	$(document).on('click','.smiley-wrapper #my-tab-content a',function(){
		var emojiTag = $(this).attr('title');
		var existMsg = $('.message').val();
		$('.message').val(existMsg+''+emojiTag);
	});
	
	$(document).on('click','.btn-emoji',function(){
		$('.smiley-wrapper').toggleClass('hidden');
		$('.smiley-wrapper #my-tab-content a').click(function(){
			$('.smiley-wrapper').addClass('hidden');
			$('.message').focus();
		});
	});
	
	$(document).keydown(function(e) {
        if ( e.ctrlKey && e.keyCode == 75 ) {
        	e.preventDefault();
			$('#searchuser').modal('show');
        }
    });
	$('.chosen-user').change(function(){
		var clientId = $(this).val();
		var channelName = $(this).find('option:selected').attr('data-username');
		 //getChatData(clientId,role);
		 $('li.client[data-username=\"'+channelName+'\"] a').eq(0).click();
		 //$('#'+clientId+'.client a[data-role='+role+']').eq(0).click();
		 $('#searchuser').modal('hide');
		 $('.search-users').prepend($('.messageNotification'+clientId+'').closest('.client'));
	});
	
    $(document).on('change','#search_usr',function(){
    	var optionSelected = $('#search_usr option:selected').val();
    	$('.users li a').each(function(){
    		if($(this).attr('data-user') == optionSelected){
    			$('#searchuser').modal('hide');
    			$('.users li,.channels li').removeClass('active');
    			$(this).parent().addClass('active');
    		}
    	});
    });
    
    $(document).on('click','.search-newUser',function(){
    	$('#searchuser').modal('show');
    });

    //search
    $.extend($.expr[":"], {
		"containsIN": function(elem, i, match, array) {
		return (elem.textContent || elem.innerText || "").toLowerCase().indexOf((match[3] || "").toLowerCase()) >= 0;
		}
	});
	//$.get("/secure-chat/get-user-groups/"+appuser, function(result) {
		//$.each(result,function(i,obj){
			//grpIdarray.push(obj);
			//});
		//});
    updateOnlineStatus();
    
});

$('textarea.js-auto-size').textareaAutoSize();

////////////////////////////////////
//End of INIT JS

//Event for displaying Messages on clicking on clients
$(document).on("click",".client",function() {
	/*
			var item = $(this);
	        $("#msgText").focus();
	        $("#msgText").val(''); 
	    	$('#messages').val('');
	        loadMoreIndex=-1;
	        document.title ='BB8 | Connect';    
	        $("#ficon").attr('href','/assets/images/faviconnew.ico');
	        $('.users li,.channels li').removeClass('active');
			clientId = $(this).attr('id');
			role = $(this).attr('data-role');
			//alert(clientId);
			var appuserId=$('#appUser').val();
		
			if(role=='user'){
				$('.messageNotification'+clientId+'').html('');
				if(item.hasClass('active')){
					return false;
				}
				channelName=$('#name'+clientId+'').val();
				
				$('.active-client'+clientId+'').addClass('active');
				$('.toUserName').html(topChannelName(channelName));
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
				if(item.hasClass('active')){
					return false;
				}
				channelName=$('#group'+clientId+'').val();
				if(channelName=='General'){
					$('#leave-li').hide();		
					$('#remove-li').hide();
					$('#rename-li').hide();
				}else{
					$('#leave-li').show();	
					isGroupAdmin();
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
							//$('#remove-li').hide();
						}else{
							$('#leave-li').show();
							//$('#remove-li').show();
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
			});*/
 });
/*
$('#msgText').keypress(function(){
clearTimeout(timer);
  var tyingMessage=$('#msgText').val();
  if(tyingMessage.trim().length>=1){
	  console.log(tyingMessage.length);
	  	messageObj = new Object();
	  	
	    messageObj.description = tyingMessage;
		messageObj.toUserId =clientId;
		messageObj.role=role;
		messageObj.uploadFile="";
		messageObj.randomId="";
		messageObj.uploadFileContentType="";
		messageObj.comments="";
		messageObj.messageContentType="";
		messageObj.messagePersistentStatus="typing";
		sendMessageOn(messageObj);
		
		timer = setTimeout(function(){
			messageObj = new Object();
    	    messageObj.description = "";
    		messageObj.randomId="";
			messageObj.toUserId =clientId
			messageObj.role=role;
			messageObj.uploadFile="";
			messageObj.uploadFileContentType="";
			messageObj.comments="";
			messageObj.messageContentType="";
			messageObj.messagePersistentStatus="typing";
			sendMessageOn(messageObj);
			
		},timeout);
	  
  }
  
   
}); */

$(".file-upload").on('change', function(){
	  
	var file = this.files[0]; 
	 fileName=file.name;
	 fileType=file.type;
	 var size=file.size;
	 
	$('#imageTitle').val(fileName);
	
	if(imageExtensions.indexOf(fileType)!=-1){
		 readURL2(file);
		 messageContentType='IMAGE';
		 $('.hideforfile').show();
		 $('#fileComments').val('');
		 $('#uploadimage-modal').modal('show');
	}else if(fileExtensions.indexOf(fileType)!=-1){
		messageContentType='FILE';
		$('.hideforfile').hide();
		$('#fileComments').val('');
		$('#uploadimage-modal').modal('show');
		
	}else{
		messageContentType='FILE';
		$('.hideforfile').hide();
		$('#fileComments').val('');
		$('#uploadimage-modal').modal('show');
		
	}
	var roleLower = role.toString().toLowerCase(); 
	var clientLower = clientId.toString().toLowerCase(); 
	var current = $('.mylist').find('option[data-role="'+roleLower+'"]').filter(function(){
		 if($(this).val() == clientLower){
			 return $(this);
		}
	});
	//console.log(current);
	$('.mylist').find('option:selected').removeAttr('selected');
	$('.mylist').find('option:selected').prop('selected',false);
	current.attr('selected',true);
	current.prop('selected',true);
	$('.mylist').trigger('chosen:updated');
	
	/*var reader = new FileReader();
    reader.onload = function (e) {
    	var binaryString = e.target.result;
    	dataURL = btoa(binaryString);
    }
    reader.readAsBinaryString(file);*/
    
}); //filUpload


$(document).on('click','.edit_link',function(){
	//alert();
	var randomId = $(this).attr('id');
	var actionRole = $(this).attr('data-role');
	var txtArea = $(this).closest('.edit-msg').find('.editTextArea');
	var msg = $(this).closest('.edit-msg'+randomId).find('.msg_description');
	$('.editTextArea').hide();
	msg.find('.emoji-images').each(function(){
		$(this).replaceWith($(this).attr('alt'));
	});
	msg.find('.profile-vw-click').each(function(){
		$(this).replaceWith($(this).text());
	});
	var arr = msg.html().split('<br>');
	var existMsg = arr.join('\n');
	if(txtArea.length){
		txtArea.show();
		msg.hide();
	}else{
		var newMsg = '<div class="editTextArea">'
				+'<form id="editForm"><textarea class="editable-txt form-control" style="height:40px;" name="msgTextArea">'+existMsg+'</textarea>'
				+'<button type="button" class="btn btn-xs btn-success save-para" id="'+randomId+'">Save</button">&nbsp;&nbsp;<button type="button" close-id="'+randomId+'" class="btn-xs btn btn-default close-para">Close</button">'
				+'</form>'
				+'</div>';
		$(this).closest('.edit-msg'+randomId).append(newMsg);
		msg.hide();
	}
});


$(document).on('click','.close-para',function(){
	var randomId = $(this).attr('close-id');
	var txtArea = $(this).closest('.edit-msg').find('.editTextArea');
	var msg = $(this).closest('.edit-msg'+randomId).find('.msg_description');
	txtArea.hide();	
	msg.show();
});

$(document).on('keypress','.editable-txt',function (e) {
	var key = e.which;
	if(key == 13){
		$(this).closest('#editForm').find('.save-para').trigger('click');
	};
});

$(document).on('click','.delete_link',function(){
	deleteMain($(this).attr('id'));
})

$(document).on('click','.save-para',function(){
	
	var txtVal = $(this).closest('#editForm').find('.editable-txt').val();
	if(txtVal.trim() == ''){
	 var id = $(this).attr('id');
		 deleteMain(id);	 
	 }else{
		var event = $(this);
		entersavedata(event);
	 };
});

$(document).on('click','.close-para',function(){
	$('.editTextArea').remove();	
	$('.msg_description').show();
});


var mySelect = $('.chosen-user');
mySelect.chosen();
$(document).keydown(function(e) {
    if ((e.keyCode == 75 && e.ctrlKey) || (e.keyCode == 75 && e.metaKey)) {
    	var roleLower = role.toString().toLowerCase(); 
    	var clientLower = clientId.toString().toLowerCase();
    	//console.log("Client Role -->"+roleLower)
    	//console.log("Client Id -->"+clientLower)
    	var current = $('.chosen-user').find('option[data-role="'+roleLower+'"]').filter(function(){
    		if($(this).val() == clientLower){
    			return $(this);
    		}
    	});
    	//console.log(current);
    	$('.chosen-user').find('option:selected').removeAttr('selected');
    	$('.chosen-user').find('option:selected').prop('selected',false);
    	current.attr('selected',true);
    	current.prop('selected',true);
    	mySelect.trigger('chosen:updated');
    	mySelect.trigger('chosen:open');
    	$('#searchuser').modal('show');
        return false;
    }
});

$('#searchuser').on('shown.bs.modal', function (e) {
	mySelect.trigger('chosen:open');
});

//profile view js
var completeProfile ="";
var profileObject={};
var UserProfiles = {};
	$(document).on('click','.profile-vw-click',function(x){
		profileObject = {}
		var userName=$(this).attr('data-userName');
		//console.log(userName);
		var e = x;
		var item = $(this);
		profileObject.userName = userName;
		//console.log(item.attr('popover-loaded'));
		if(!item.attr('popover-loaded')){
			$.get("/secure-chat/get-appuser-profile/"+userName, function(result) {
				
				$.each(result, function(key,mapValue ){
					profileObject[key]=mapValue;
					});//for
				
		    	 completeProfile ='<div class="profile-vw col-md-12">'+
		          '<div class="artist-collage row">'+
		          '<div class="img-prf"><div class="profilepreviewImage"><img src="/imgProfile/'+ profileObject.id +'" onError="this.onerror=null;makeNoImage(this);"><span class="hidden">'+ profileObject.userName +'</span></div></div>'+
		          '</div>'+
		          '<hr class="profileHr">' +
		          '<div class="listing-tab row">'+
		          '<div class="tab-content col-md-12">'+
		          '<div role="tabpanel" class="tab-pane active" id="track">'+
		          '<ul>'+
		          '<li class="text-center"><label class="viewProfileLink" data-username="'+profileObject.userName+'">View Profile</label></li>'+
		          '<li><label>Full Name </label>&nbsp;&nbsp;<span>'+profileObject.fullName+'</span></li>'+
		          '<li><label>Job Title  </label>&nbsp;&nbsp; <span>'+profileObject.jobTitle+'</span></li>'+
		          '<li><label>Phone Number </label>&nbsp;&nbsp;<span>'+profileObject.mobileNo+'</span></li>'+
		         '<li><label>Email Id </label>&nbsp;&nbsp;<span>' +profileObject.email + '</span></li>'+
		          '</ul>'+
		          '</div>'+
		          '</div>'+
		          '</div>'+
		          '</div>';
		    	 
		          //$('.profile-vw-click').not(this).popover('hide');
	             item.attr('popover-loaded',true);
		    	 item.popover({
		    		 title : profileObject.userName,
		    		 content : completeProfile,
		    		 html : true,
		    		 container : 'body'
		    	 });
		    	 UserProfiles[profileObject.userName] = profileObject;
		    	 $('.profile-vw-click').not(this).popover('hide');
		    	 item.popover('show');
			});//get
      }else{
    	        $('.profile-vw-click').not(this).popover('hide');
    	  		item.popover('show');
      }
	});//close profile-vw-click
	var windowOpened = false;
	$(document).on('click','.viewProfileLink',function(){
		getRightPanel('profile',$(this));
	});
	$(document).on('click','.upload-name',function(){
		getRightPanel('file',$(this));
		
	})
$(document).on('keypress input','#msgText',function (e) {
	//$('.chat-wrapper').css('margin-bottom',($(this).height()-16) +'px' );
	var curItem = $(this)
	var curItem2 = this;
	var totalLines = $(this).val().split('\n').length;
	var trimmedVal = curItem.val().trim();
	 var key = e.which;
//	 console.log(key);
	 if(key == 13 && e.shiftKey){
		 e.preventDefault();
		 if(trimmedVal){
		     $('#msgText').val($('#msgText').val()+ '\n');
		 }
	   return false;
	 }else if(key == 13){  
		 e.preventDefault();
		 if(totalLines > 25){
			  return false;
		 }else{
			 if(trimmedVal){
				 sendMessage();
			 }
		 }
	  }
	 if(totalLines > 25){
		 if(!$('.create-code-exceed').length){
			 $('<div class="col-md-12"><a href="javascript:void(0)" class="pull-right create-code-exceed">Create Snippet</a></div>').insertAfter($('.input-with-upload'));
		 }
		 $('.create-code-exceed').show();
	 }else{
		 $('.create-code-exceed').hide();
	 }
 });
$('.groupcheck').click(function() {
	   if ($(this).is(':checked')) {
		   $('#group-type-id').val("on");
		   $("#public-private-heading").html("Private ")
	      $(".publicprivate-content").html("Restricted to invited members")					   
	      $(".privatecontetnonly").html("A private channel is only visible to its members, and only members of a private channel can read or search its contents.")
	      $(".mychanelname").attr("placeholder","# Enter name here")
	   } if (!$(this).is(':checked')) {
		   $('#group-type-id').val("off");
	       $("#public-private-heading").html("Public ")
	       $(".publicprivate-content").html("Anyone on your team can join")
	       $(".privatecontetnonly").html("")
	       $(".mychanelname").attr("placeholder","# Enter name here")
	   }
});


$('#invite-add').click(function(){
	  $.get('/secure-chat/invite-others-to-channel/'+clientId,function(data){
		$('#invite-other-main').html(data);
	  });
});


$('.add-channel-btn').click(function(){
	
	 $('.addchannel-cchannel').attr('disabled','disabled'); 
	 $('.addchannel-cchannel:disabled').css("background-color","#4CAF50");
	 $('.create-channel-sel').on('change', function (e) {
		 var length = $('.create-channel-sel > option:selected').length;
		 if(length == 0){
			$('.addchannel-cchannel').attr('disabled','disabled');
			$('.addchannel-cchannel:disabled').css("background-color","#4CAF50");
			}
		 if(length>0){
			 $('.addchannel-cchannel').prop("disabled", false);
	    }
	 }); 
	 
	 
});
$('.addchannel-cchannel').click(function(){
	   /**/
	
});


$('.git-init-modal').click(function(){
	// alert(clientId);
		$.get('/secure-chat/git-integration-confirm/'+clientId, function(result) {
					$('.git-conf-verify-wrapper').hide();
			if(result.configure){
					//alert();
					$('.git-confgtn').html('configured').prop('disabled',true).css('background','#bfbfbf');
					$('.git-conf-check').removeClass( "hidden" );
				}else{
					$('.git-confgtn').html('configure').prop('disabled',false).css('background','#cccccc');
					$('.git-conf-check').addClass("hidden");
					}
		});

	 
	 $('#git-intig-modal').modal({
			show:true,	
		});	
 });



$('.git-confgtn').click(function(){
	$.get('/secure-chat/git-integration-confirm/'+clientId, function(result) {
		if(!result.configure){
		
			$.get('/secure-chat/git-configure-url/'+clientId, function(data) {
				$('.git-conf-url').html(data.url);
				$('.git-conf-verify-wrapper').show();
				});
			}
		});

	});
/*   ***************emoji ************************/

/*$('#emoji').one('click',function(){
	  $('#emoji').popover('show');
});*/


$('html').on('click', function(e) {
	  if (typeof $(e.target).data('original-title') == 'undefined' &&
	     !$(e.target).parents().is('.popover.in')) {
	    $('[data-original-title]').popover('hide');
	  }
	});
	
$(document).on('click','.emojiTabs .tab-pane a',function(){
	  var emojiTitle = $(this).attr('title');
	  var messageText = $('#msgText').val();
	  $('#msgText').val(messageText+" "+emojiTitle);
	  });
$('.get-group-mm').click(function(){
	$.get('/secure-chat/get-group-users/'+clientId, function(result) {
		$('.groupbody-modal').html(result);
		groupmembersSearch();
		$('#groupmembers-modal').modal({
			show:true	
		});	
	});
});


/*****hover events for text,image,file ---edit of text ****/
$(document).on({
		mouseenter: function(){
			$(this).find('.edit-messageAdded').show();
		},
		mouseleave: function(){
			$(this).find('.edit-messageAdded').hide();
		}
}, '.messageAdded');
//image hover
$(document).on({
	mouseenter: function(){
		$(this).find('.edit-image-upld').show();
	},
	mouseleave: function(){
		$(this).find('.edit-image-upld').hide();
	}
}, '.image-upld');
//file hover
$(document).on({
	mouseenter: function(){
		$(this).find('.edit-file-upld').show();
	},
	mouseleave: function(){
		$(this).find('.edit-file-upld').hide();
	}
}, '.file-upld');

//msg time on hover
$(document).on({
	mouseenter: function(){
		$(this).find('.msgTime').css('opacity','1');
	},
	mouseleave: function(){
		$(this).find('.msgTime').css('opacity','0');
	}
}, '.mainMsg');

$(document).on('each','.image-upld img', function () {
  /*  var imgCtrl = $(this).height();
    if(imgCtrl <= 50){
    	$(this).css('height','auto');
    }else if(imgCtrl > 50 && imgCtrl <= 100){
    	$(this).css('height','75px');
    }else if(imgCtrl > 100 && imgCtrl <= 150){
    	$(this).css('height','125px');
    }else{
    	$(this).css('height','200px');
    }
    */
});  
/*****end of hover events for text,image,file ---edit of text ****/


$(document).on('click','#loadMore',function(){
	$(this).hide();
	var url='';
	if(role == 'group' || role=='GROUP'){
		url='/secure-chat/group-messages/' + clientId+'/' + loadMoreIndex;
	}else if(role=='user' || role=='USER'){
		url='/secure-chat/messages/' + clientId+'/' + loadMoreIndex;
	}
	
	  $.ajax({
		  'url': url,
		  type : 'GET',
		  success : function(response){
			  var height = $('.chat-wrapper').prop('scrollHeight')
			  loadMoreIndex = response.firstRow;
			  isLastRow=response.isLastRow;
			  getChannelMessageData(response,'1');
			  //console.log("isLast--->"+isLastRow);
			  var height2 = $('.chat-wrapper').prop('scrollHeight')
			  $('.chat-wrapper').scrollTop(height2-height);
			  $(".chat-wrapper").animate({ scrollTop: height2-height }, "fast");
		  }
	  })
});
$(function(){
	EmojiDataByTab = {"0":{},"1":{},"2":{},"3":{},"4":{},"5":{},"6":{}};
	EmojiDataByName = {};
	var i = 0;
	$('.emojiTabs .tab-pane').each(function(){
	$(this).find('.emoji-items a').each(function(){
		var backgroundPos = $(this).find('img').css('backgroundPosition').split(" ");
		//now contains an array like ["0%", "50px"]
		var xPos = backgroundPos[0],
		    yPos = backgroundPos[1];
		//console.log(xPos + '---'+ yPos);
		EmojiDataByTab[i][$(this).attr('title')] = {
				background : $(this).find('img').css('background-image') + "  " + xPos + "  " + yPos+ " no-repeat" ,
				backgroundSize : $(this).find('img').css('background-size'),
				tabPane : i
		};
		EmojiDataByName[$(this).attr('title')] = {
				background : $(this).find('img').css('background-image'),
				backgroundSize : $(this).find('img').css('background-size'),
				tabPane : i
		};
	});
	i++;
	});
	//console.log(JSON.stringify(EmojiDataByTab));
	//console.log(JSON.stringify(EmojiDataByName));
	
});
focused = false;
$(window).focus(function() {
	if(!focused){
	$.get("/secure-chat/islogin", function(result) {
		isLogin= result.isLogin;
		//console.log("isLogin Called"+isLogin);
		//console.log(isLogin)
		if(isLogin == "false"){
			location.reload();
		}
	});
	focused = true;
	}
});
$(window).focus(function() {
	focused = false;
	//console.log("isLogin Ended"+isLogin);
});


	$('.showleftpannel').click(function(){
		$('.overlay').toggleClass('hidden');
		$('.left-panel').toggleClass('hidden-xs');
	});
	$('.overlay').on('click',function(){
		$('.left-panel').toggleClass('hidden-xs');
		$('.overlay').toggleClass('hidden');
	});
		
	//upload image and docs
	$('.fileinput').hide();
	$('.uploadicon').click(function(){
			$('.fileinput').trigger('click');
		});
		
	    // Chosenify every multiple select DOM elements with class 'chosen'
	    $('select.chosen').chosen();
				
			  $('#save-form').bootstrapValidator({
				    framework: 'bootstrap',
				    // This option will not ignore invisible fields which belong to inactive panels
				    excluded: ':disabled',
				    fields: {
				    	  name: {
				              validators: {
				                  notEmpty: {
				                      message: 'Please fill this field'
				                  },
			                 	remote: {
			                      	  message: 'Group Name Already Existed',
			                          type: 'POST',
			                          url: '/secure-chat/isExistGroup',
			                          data: function(validator) {
			                              return {
			                                  FullName: validator.getFieldElements('name').val()
			                              }
			                          }
				                  },
				                    regexp: {
				                        regexp: /^[a-zA-Z0-9_\.]+$/,
				                        message: 'The Group name can only consist of alphabetical, number, dot and underscore'
				                    }
				              }
				          }
				    },
				    
			  }).on('success.form.bv', function(e) {
				  e.preventDefault();
				  $.ajax({
				        url: '/secure-chat/save-chat-group',
				        type: 'post',
				        dataType: 'json',
				        data: $('.create-group-modal').serialize(),
				        success: function(data) {
				        	$('#mychannel').modal('hide');
				        }
				    });
			  });
				
			    
			  $('#leave-channel').click(function(){
				var grpName = $(".edit-group-dropdown .group-drop").find(".toUserName").text();
				$('.leave-channelmd-body').html('<p>Would you like to leave '+grpName+' group?</p>');
					$("#leave-ch-modal").modal('show');
				});
				
				$('.lv-ch-confirmed').click(function(){
					$.get('/secure-chat/leave-channel/'+clientId,function(data){
						if(data.status){
							$("#leave-ch-modal").modal('hide');
							$('.active-group'+ data.groupId +'').remove();
							channelName=data.defaultGroupName;
							var role='GROUP';
							 $.post("/secure-chat/update-lastseentab/"+data.defaultgroup+"/"+role, function() {
								 window.location.href='/chat/connect';
							 });
						}else{
							alert('error while leaving channel');
						}
					});
				});
				 $('#delete-channel').click(function(){
						var grpName = $(".edit-group-dropdown .group-drop").find(".toUserName").text();
						$('.delete-channelmd-body').html('<p>Would you like to delete '+grpName+' group?</p>');
							$("#delete-ch-modal").modal('show');
						});
				 $('.dl-ch-confirmed').click(function(){
						$.get('/secure-chat/remove-channel/'+clientId,function(data){
							if(data.status){
								$("#delete-ch-modal").modal('hide');
								$('.active-group'+ data.groupId +'').remove();
								channelName=data.defaultGroupName;
								var role='GROUP';
								 $.post("/secure-chat/update-lastseentab/"+data.defaultgroup+"/"+role, function() {
								 });
							}else{
								alert('error while leaving channel');
							}
						});

					});
				
				    
	$('#mychannel').on('show.bs.modal', function () {
		$("#save-form")[0].reset();
		$('.create-channel-sel').trigger('chosen:updated');
	})
	
/*	
		var observe;
		if (window.attachEvent) {
		    observe = function (element, event, handler) {
		        element.attachEvent('on'+event, handler);
		    };
		}
		else {
		    observe = function (element, event, handler) {
		        element.addEventListener(event, handler, false);
		    };
		}*/
	
	
$(".nameSrch-list").keyup(function(){
		var newSrch = $(this).val();	
		//console.log(newSrch)
		if(newSrch){
			$('.search-members li').hide();
			$('.search-members li:containsIN("'+newSrch+'")').show();
			$('.search-members li:containsIN("'+newSrch+'")');
		}else{ 
			$('.search-members li').show();
			$('.search-members li:containsIN("'+newSrch+'")').css("background","none");
		}
	});
	
$('.createSnippet').on('click',function(){
	editor.getDoc().setValue('');
	editor.refresh();
	change($('#mode').val());
	$('#code-Snippet').modal('show');
	setActiveChannel();
})



CodeMirror.modeURL = "/assets/snippet/mode/%N/%N.js";
var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
  lineNumbers: true
});
var modeInput = document.getElementById("mode");
CodeMirror.on(modeInput, "change", function(e) {
  change(modeInput.value);
});
function change(modeInput) {
  var val = modeInput, m, mode, spec;
  if (m = /.+\.([^.]+)$/.exec(val)) {
    var info = CodeMirror.findModeByExtension(m[1]);
    if (info) {
      mode = info.mode;
      spec = info.mime;
    }
  } else if (/\//.test(val)) {
    var info = CodeMirror.findModeByMIME(val);
    if (info) {
      mode = info.mode;
      spec = val;
    }
  } else {
    mode = spec = val;
  }
  if (mode) {
    editor.setOption("mode", spec);
    CodeMirror.autoLoadMode(editor, mode);
    document.getElementById("modeinfo").textContent = spec;
  } else {
    alert("Could not find a mode corresponding to " + val);
  }
}

var pending;
editor.on("change", function() {
    clearTimeout(pending);
    pending = setTimeout(update, 400);
  });
  function looksLikeScheme(code) {
    return !/^\s*\(\s*function\b/.test(code) && /^\s*[;\(]/.test(code);
  }
  function update() {
    editor.setOption("mode", looksLikeScheme(editor.getValue()) ? "scheme" : "javascript");
  }

$(document).on('mouseover','.user-message',function(){
	$(this).find('.loadMoreCode').removeClass('hidden');
});
$(document).on('mouseleave','.user-message',function(){
	$(this).find('.loadMoreCode').addClass('hidden');
});

$(document).on('click','.loadMoreCode',function(){
	var item = $(this);
	var status = $('#code-'+item.attr('data-id')).attr('data-status') ? 'loaded' : '';
	var txtArea = $('#code-'+item.attr('data-id'));
	if(!status){
	item.html('<img src="/assets/images/ring.gif" style="width:20px">');
	if(!item.attr('status')){
		$.post('/secure-chat/load-more-lines/'+$(this).attr('data-id'),function(data){
			txtArea.text(data.allLines);
			var instance = txtArea.data('codemirrorInstance');
			instance.setValue(data.allLines);
		});
	}	
		item.html('Collapse &#8593; ');
		txtArea.next().css('height','100%');
		txtArea.attr('data-status','loaded');
		item.attr('status',true);
	}else{
		txtArea.next().css('height','95px');
		txtArea.attr('data-status','');
		item.text('+ Click to see more (' + txtArea.attr('data-lines') + ' lines)');
	}
});
// rename group keyup 
	$('#rn-grp-nm').keyup(function(){
		
		var changedGpName = $(this).val();
		if(changedGpName.length<1){
			$('#emptychaneelMessage').show();
			$('.rn-ch-confirmed').attr('disabled');
			$('.rn-ch-confirmed').prop('disabled',true);
		}else{
			$('#emptychaneelMessage').hide();
			$('.rn-ch-confirmed').removeAttr('disabled');
			$('.rn-ch-confirmed').prop('disabled',false);
			
		
		var rnGrpName = $('.prv-grp-name').val();
		if(!(rnGrpName==changedGpName)){
			$.ajax({
			type:'POST',
			url:"/secure-chat/isExistGroup",
			dataType: 'json',
   	        data: $('#rename-group-form').serialize(),
			success:function(data){
					if(!data.valid){
						$('#chaneelMessage').show();
						$('.rn-ch-confirmed').attr('disabled');
						$('.rn-ch-confirmed').prop('disabled',true);
					}else{
						$('#chaneelMessage').hide();
						$('.rn-ch-confirmed').removeAttr('disabled');
						$('.rn-ch-confirmed').prop('disabled',false);
						}
				}
				});
		}
			
	}
		  });
	//end rename group keyup
//rename group function
	$('#rename-channel').click(function(){
		var grpName = $(".edit-group-dropdown .group-drop").find(".toUserName").text().split("#").join("");
		$('.rename-ch-input,.prv-grp-name').val(grpName);
		$('.rn-ch-grpId').val(clientId);
		$('#rename-ch-modal').modal('show');
		$('.rename-ch-input').focus();
		});
	 $('.rn-ch-confirmed').click(function(){
			$.ajax({
				type:'POST',
				url:"/secure-chat/rename-channel",
				dataType: 'json',
	   	        data: $('#rename-group-form').serialize(),
				success:function(data){
						if(data.status){
							 window.location.href='/chat/connect';	
					}
				}

		 });

	 });
//end of rename group function

$(document).on('click','.closeleftWindow',function(){
	HideRightPanel();
	isRightWindowOpend = false;
});
$(document).on('click','.create-code-exceed',function(){
	$('#code').val($('#msgText').val());
	editor.getDoc().setValue($('#msgText').val());
	$('#code-Snippet').modal('show');
	setActiveChannel();
	change($('#mode').val());
	editor.refresh();
});

/*$(window).bind('hashchange', function() {
	var hash = window.location.hash.split('#')[1];
	if($('li[data-username="'+hash+'"]').length){
		var userId = $('li[data-username="'+hash+'"]').eq(0).attr('id')
		var role = $('li[data-username="'+hash+'"]').eq(0).attr('data-role')
		if(hash){
			getChatData(userId,role);
		}
	}
});*/


$(document).on('keypress input','.commentText',function (e) {
	var curItem = $(this)
	var curItem2 = this;
	var trimmedVal = curItem.val().trim();
	if(trimmedVal.length){
	 var key = e.which;
	 if(key == 13 && e.shiftKey){
		 e.preventDefault();
		     $('.commentText').val($('.commentText').val()+ '\n');
	   return false;
	 }else if(key == 13){  
		 e.preventDefault();
		 var uploadFileId= $(this).attr('data-fileId');
		var uploadFileName= $(this).attr('data-fileName');
		 sendCommentMessage(this);
	  }
	}
 });

$(document).on('click','.addComment',function(){
 
		 sendCommentMessage(this);
	
});



$(document).on('click','.emoji-menu-tabs',function(){
	var id = parseInt($(this).attr('data-id'))-1;
	tabId = id +1;
	var content = getEmojiTab(id);
	$('.emojiTabs #tab'+tabId+'default').html(content);
})


var bar = $('.bar');
var percent = $('.percent');
var status = $('#status');
var xhrdata = '';
$('#uploadFileForm').ajaxForm({
	    beforeSend: function(xhr, opts) {
	    	$('#cancelFileUpload').show();
	        var percentVal = '0%';
	        percent.html(percentVal);
	        $('.progess2').show();
	        $('#progressOuter').removeClass('hidden');
	        xhrdata = xhr;
	    },
	    uploadProgress: function(event, position, total, percentComplete) {
	    	var fileName = $('.file-upload').val();
	        var percentText = "Uploading file <b>"+fileName+"</b> "+percentComplete + '%';
	        if(percentComplete == '100'){
	        	percentText = "Processing your file <b>"+fileName+"</b>";
	        }
	        var percentVal = percentComplete + '%';
	        percent.html(percentText);
	        $('.progress2').width(percentVal)
	        //console.log(percentVal);
	    },
	    success: function(result) {
	    	percent.html('100%');
	    	$('.progress2').width('100%');
	    	setTimeout(function(){
	    	$('#cancelFileUpload').hide();
	    	},1000);
	        var percentVal = '100%';
	        percent.html(percentVal);
	        
	        var toUserRole= $('.mylist option:selected').attr('data-role');
	     	var toUserId=$('.mylist').val();
	     	
	     		messageObj = new Object();
	     		messageOb=prepareMessageObj(messageObj);
	    		messageObj.uploadFileId= result.id;
	    		messageObj.uploadFile.id= result.id;
	    		messageObj.uploadFile.snippetMap = result.snippetMap;
 	    	if(messageObj.uploadFileId != 'undefined'){
 	    		messageObj.toUserId=toUserId;
 	    		messageObj.role=toUserRole;
				messageObj.description= fileName;
				var filecomments=$('#fileComments').val();
				if(filecomments){
					messageObj.uploadFile.commentList[0].comment=filecomments;
					messageObj.comments=filecomments;
					messageObj.uploadFile.snippetMap.commentsCount = 1;
					//alert(messageObj.uploadFile.commentList[0].comment);
				}else{
					messageObj.comments="";
					messageObj.uploadFile.commentList[0].comment="";
					//alert(messageObj.uploadFile.commentList[0].comment);
				}
				messageObj.gitNotificationId="";
				messageObj.messagePersistentStatus="saving";
				messageObj.messageContentType=messageContentType;
				
				if(role=='USER' || role=='user'){
					sendMessageOn(messageObj);
				}else if(role=='GROUP' || role=='group'){
 	    		sendMessageOn(messageObj);
				}
				var lowerRole = role.toLowerCase();
				//alert(toUserId+""+toUserRole);
				//alert(clientId+""+role);
				if(toUserId==clientId && toUserRole==lowerRole){
					if($('.msg_description').last().attr('data-id') == messageObj.messageBy.id){
			 			messageObj.isUserNameChange = false;
			 		}
			 		else{
			 			messageObj.isUserNameChange = true;
			 		}
					$('#messages').append(displayMessage(messageObj,appuser,messageObj.isUserNameChange));
					pushMessageToCache(messageObj,channelName);
					scrollBottom();
				}
 	    }
		setTimeout(function(){
			  //$('#progressOuter').fadeOut('slow');
			  $('#progressOuter').addClass('hidden');
			},200);

	    },
		complete: function(xhr) {
			percent.html('Successfully Uploaded...');
		},
	    error: function(jqXHR, textStatus, errorThrown) {
	    	//console.log("textStatus"+textStatus);
	    	//console.log("errorThrown"+errorThrown);
	    	
	 		$('#fileUploadModel').modal('show');
	 		percent.html('Unable to upload your file');
	 		$('.progess2').hide();
	 		$('#progressOuter').addClass('hidden');
	 	}
	}); 


/*$("#saveSnippet").on('click', function(){
	var snippetCode= $('#snippetCode').val();
	if(snippetCode  == '' || snippetCode == null ){
		alert('Pleas add snippet code.');
		return false;
	}else{
		saveSnippet();		
	}

	
}); //saveSnippet
*/

$(document).on('click','#saveSnippet',function(){
	var snippetCode= $('#snippetComments').val();
	if(snippetCode  == '' || snippetCode == null ){
		alert('Please add snippet code.');
		return false;
	}else{
		var fileName, fileType, size,code,snippetName;
    	fileName = $('#fileName').val() != '' ? $('#fileName').val() : $('#mode option:selected').text()+" Snippet";
    	snippetName=$('#snippetName').val(fileName);
    	fileType = $('#fileType').val();
    	code = $('.CodeMirror textarea').val();
    	var text = editor.getValue();
    	$('#code').val(text);
		$('#save-snippet-form').submit();
	}
});

$('#save-snippet-form').ajaxForm({
    beforeSend: function(xhr, opts) {
    	$('#cancelFileUpload').show();
        var percentVal = '0%';
        percent.html(percentVal);
        $('#progressOuter').removeClass('hidden');
        xhrdata = xhr;
    },
    uploadProgress: function(event, position, total, percentComplete) {
    	var fileName = "Snippet";
    	var percentText = "Uploading file <b>"+fileName+"</b> "+percentComplete + '%';
    	if(percentComplete == '100'){
        	percentText = "Processing your file <b>"+fileName+"</b>";
        }
        var percentVal = percentComplete + '%';
        percent.html(percentText);
       // console.log(percentVal);
    },
    
    success:function(result){
    	percent.html('100%');
    	$('.progress2').width('100%');
    	percent.html('Successfully Uploaded...');
    	setTimeout(function(){
			  //$('#progressOuter').fadeOut('slow');
			  $('#progressOuter').addClass('hidden');
		},200);
    	var snippetComments;
    	var toUserId=$('.mylist').val();
    	var toUserRole= $('.mylist option:selected').attr('data-role');
    	snippetComments = $('#snippetComments').val();
     	messageObj = new Object();
     	messageObj=prepareMessageObj(messageObj);
     	messageObj.uploadFile.commentList[0].comment=snippetComments;
     	var messageContentType='SNIPPET';
     	
	    		messageObj.uploadFileId= result.uploadFile.id;
	    		messageObj.uploadFile.id= result.uploadFile.id;
	    		messageObj.uploadFile.snippetMap= result.uploadFile.snippetMap;
	    		//console.log(messageObj)
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
				//console.log("messageObj"+messageObj);
				var lowerRole = role.toLowerCase();
				//console.log()
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
				//console.log(result.uploadFile.snippetMap)
				pushMessageToCache(messageObj,channelName);
				sendMessageOn(messageObj);
				//messageObj.uploadFile.snippetMap='';
				
				$('#msgText').val('');
				$('.create-code-exceed').hide();
				$('textarea.js-auto-size').css('height','auto');
				$('.chat-wrapper').css('margin-bottom','0px');
				$('#save-snippet-form')[0].reset();
				
	    },
	complete: function(xhr) {
		
	},
    error: function(jqXHR, textStatus, errorThrown) {
 		$('#fileUploadModel').modal('show');
 	    $('#progressOuter').addClass('hidden');
 	}
}); 


$('#cancelFileUpload').on('click', function(){
	xhrdata.abort();
	$('#progressOuter').addClass('hidden');
});



$(document).on('click','.like',function(){
	var likeDiv= $(this);
	var fileId = likeDiv.attr('data-id');
	if(likeDiv.attr('data-status') == "like"){
		if(fileId != '' && appuser != ''){
			updateFileLike('like',fileId,appuser);
		}
	}else if(likeDiv.attr('data-status') == "unLike"){
		if(fileId != '' && appuser != ''){
			updateFileLike('unLike',fileId,appuser);	
		}
	}
});
///////////////////////Code For Name Badge////////////////////////
(function ($) {
	$.fn.nameBadge = function (options) {
		var settings = $.extend({
			border: {
				color: '#ddd',
				width: 3
			},
			colors: ['#a3a948', '#edb92e', '#C71585', '#ce1836', '#009989','#f3f3f3','#FF9516','#82D855','#1ABC9C','#f85931'],
			text: '#fff',
			size: 35,
			margin: 5,
			middlename: true,
			uppercase: true,
			capitalize:true
		}, options);
		return this.each(function () {
			var elementText = $(this).text();
			//var initialLetters = elementText.match(settings.middlename ? /\b(\w)/g : /^\w|\b\w(?=\S+$)/g);
			//var initials = initialLetters.join('');
			var initials = getNameBadgeFromUserName(elementText);
			$(this).text(initials);
			$(this).css({
				'color': settings.text,
				'background-color': settings.colors[getNameBadgeColor(elementText)],
				'border': settings.border.width + 'px solid ' + settings.border.color,
				'display': 'inline-block',
				'font-weight':'400',
				'font-family': 'Arial, \'Helvetica Neue\', Helvetica, sans-serif',
				'font-size': settings.size * 0.4,
				'border-radius': '3px',
				'width': settings.size + 'px',
				'height': settings.size + 'px',
				'line-height': settings.size + 'px',
				'margin': settings.margin + 'px',
				'text-align': 'center',
				'text-transform' : settings.uppercase ? 'uppercase' : ''
			});
		});
	};
}(jQuery));



var prevPanelColor = '';
var prevactiveColor = '';
$("#preferences-modal").on("shown.bs.modal", function () {
	prevPanelColor = leftPanelColor;
	prevactiveColor = active_color;
	var content = '';
	metro_colors.forEach(function(i){
		content +=`<div class="col-md-3 "><label class="colorTheme radio" style="background-color:${i}"><input type="radio" value="${i}" name="colorTheme" class="radio-theme"></label></div>`;
	});
	content += '<div class="clearfix"></div>'
	$('#Themes').html(content);
	$('.modal-backdrop').css('margin-left','230px');
});
$("#preferences-modal").on("hidden.bs.modal", function () {
	$('.left-panel').css('background-color',prevPanelColor);
	$('.style').remove();
	$('body').append(`<style class="style">li.client.active a{background-color : ${prevactiveColor} !important; }`);
});
$(document).on('click','.radio-theme',function(){
		$('.left-panel').css('background-color',$(this).val());
		active_color = secondary_colors[metro_colors.indexOf($(this).val()) ? metro_colors.indexOf($(this).val()) : 0];
		 //$('.client.active a').css('background-color',active_color);
		$('.style').remove();
		$('body').append(`<style class="style">li.client.active a{background-color : ${active_color} !important; }`);
});

$(document).on('click','.saveSettings',function(){
	isEnableDesktopNotfication = $('.desktopNotification').is(':checked') ? true : false;
	leftPanelColor = $('input[name="colorTheme"]:checked').val();
	saveAppUserSettings(isEnableDesktopNotfication,leftPanelColor);
});
$(document).on('keydown','.emojifilter',function(){
	if($(this).val().length > 0){
	$('.emojiTabs .tab-pane').removeClass('in active');
	$('.emojiTabs  .tab0default').addClass('in active');
	//$('.emoji-filter-items').html('');
	var content = $('<span>');
	$('.emoji-items a:contains('+$(this).val()+')').each(function(){
		content.append($(this).clone());
	});
	$('.emojiTabs  .tab0default').html(content);
	$('.emojiTabs  .tab0default').find('.label').hide();
	}
});
