
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
	$.get("/secure-chat/get-user-groups/"+appuser, function(result) {
		$.each(result,function(i,obj){
			grpIdarray.push(obj);
			});
		});
    updateOnlineStatus();
    
});

$('textarea.js-auto-size').textareaAutoSize();