@(loginUser:models.AppUser)
var systemDate='@System.currentTimeMillis';
var createdGroupId;
var createdGroupName;
var timer;
var timeout = 3000;
//global variables
var clientId;
var messageObj;
var role;
var notificationTitle;
var Notification = window.Notification || window.mozNotification || window.webkitNotification;
var notificationIcon='/assets/images/bb8_logo_thrymr.png';
var isValidGroupMember;
var sendMessageOn;
var status;
var appuser='@loginUser.id';
var appuserJson = '';
var rightmessage ;
var prevType;
var dataURL;
var messageContentType;
var fileName;
var fileType;
var urlMatchingKeys=['https://','http://'];
var messageQueue = [];
var imageExtensions =['image/bmp', 'image/gif', 'image/jpeg','image/pjpeg','image/jpeg','image/pjpeg','image/jpeg', 'image/pjpeg','image/png','image/x-png','image/tiff','image/tiff','image/vnd.microsoft.icon'];
var fileExtensions=[
                    'application/mac-binhex40','application/mac-compactpro','text/x-comma-separated-values', 'text/comma-separated-values', 'application/octet-stream', 'application/vnd.ms-excel',
                    'application/macbinary', 'application/octet-stream','application/x-photoshop','application/oda','application/pdf','application/x-download','application/postscript','application/smil',
                    'application/excel','message/rfc822', 'application/vnd.mif','application/excel', 'application/vnd.ms-excel',
                    'application/powerpoint','application/wbxml', 'application/wmlc','application/x-director','application/x-dvi','application/x-gzip','application/x-httpd-php','application/x-httpd-php-source',
                    'application/x-javascript','application/x-shockwave-flash','application/x-tar','application/x-tar','application/xhtml+xml',
                    'application/x-zip', 'application/zip', 'application/x-zip-compressed',
                    'audio/x-wav', 'audio/midi', 'audio/midi', 'audio/mpeg', 'audio/x-aiff','audio/x-pn-realaudio', 'audio/x-pn-realaudio','audio/x-pn-realaudio', 'audio/x-realaudio','audio/x-wav','video/vnd.rn-realvideo',
                    'text/css','text/html','text/plain','text/plain','text/x-log','text/rtf','text/xml',
                    'video/mpeg', 'video/quicktime','video/quicktime','video/x-msvideo','video/x-sgi-movie',
                    'application/rtf','application/vnd.ms-excel','ppt','application/vnd.ms-powerpoint',
                    'application/msword','application/msword', 'application/octet-stream','application/excel','message/rfc822','application/msword'
                    ];
var isLogin=true;
var messageQue = {};
var loadMoreIndex = 0;
var totalMessage = 0;
var isLastRow = false;
var messageData = {};
var allMsgsMap = {};
var channelName = '';
var grpIdarray = []; 
var commentsMap = {};
var currentFileId='';
var isRightWindowOpend=false;
var EmojiDataByName = {};
var EmojiDataByTab = {};
var isEnableDesktopNotfication = true;

var metro_colors = ['rgba(104, 14, 14, 0.78)',
                    'rgba(12, 126,131,0.95)',
                    'rgba(83, 146, 68, 0.95)',
                    'rgba(66, 70, 74, 0.95)',
                    'rgba(90, 53, 112, 0.95)',
                    'rgba(131, 80, 0, 0.95)',
                    'rgba(12, 62, 77, 0.95)',
                    'rgba(12, 60, 108, 0.95)'
                    ];
var secondary_colors = ['#F17C03',
                        '#46B6AE',
                        'rgba(94, 203, 19, 0.8)',
                        'rgb(241, 100, 0)',
                        'rgb(153, 70, 255)',
                        '#E49300',
                        '#007294',
                        '#0583E0'];
var leftPanelColor = '';
var active_color = '';
var channelsLoaded = false;

function Init(){
	if(tabNo==10){
		chatSocket.onmessage = receiveEventOn;
	}
	chatSocket.onclose=function(e){
		clearTimeout(setTimeout1);
		$('#msgText').prop('disabled',true)
		$('#msgText').attr('disabled');
		$('#progressSocket').removeClass("hidden");
		$('#progressSocket .percent').html("Trying to Re-connect in a minute.....");
		setTimeout1 = setTimeout(function(){
			  reconnect();
			  $('#progressSocket').addClass("hidden");
			  $('#msgText').prop('disabled',false)
		},60000);
	}
	chatSocket.onerror = function(evt){
		clearTimeout(setTimeout2);
		$('#msgText').prop('disabled',true)
		$('#progressSocket').removeClass("hidden");
		$('#progressSocket .percent').html("Trying to Re-connect in a minute.....");
		setTimeout2 = setTimeout(function(){
			  reconnect();
			  $('#progressSocket').addClass("hidden");
			  $('#msgText').prop('disabled',false)
		},60000);
	}
	var foo = document.cookie;
	
}
function reconnect(){
	var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
	        var chatSocketUrl = "@routes.ChatController.chatRoom(Application.getLoggedInUser.id).webSocketURL(request)";
	        chatSocketUrl = chatSocketUrl.replace("ws", "@utils.Constants.SOCKET_ADSRESS");
		    chatSocket = new ReconnectingWebSocket(chatSocketUrl,null, {debug: true});
		    //chatSocket.refresh();
		    if(tabNo==10){
		    	chatSocket.onmessage = receiveEventOn;
			    $.ajax({
					url : "/secure-chat/get-all-channels-jsondata",
					type: 'POST',
					async:false,
					 success : function(result){
					 pushChannelToMap(result);
					 channelsLoaded = true;
					 getChatData(Load_id,Load_role);
				}
				});
		    }
		    chatSocket.onclose=function(e){
		    	clearTimeout(setTimeout3);
		    	$('#msgText').prop('disabled',true)
				$('#progressSocket').removeClass("hidden");
				$('#progressSocket .percent').html("Trying to Re-connect in a minute.....");
				setTimeout3 =  setTimeout(function(){
					  reconnect();
					  $('#progressSocket').addClass("hidden");
					  $('#msgText').prop('disabled',false)
				},60000);
			}
}

	sendMessageOn = function(messageObj){
		//alert("chatSocket.send() method"+JSON.stringify(messageObj));
		messageQue[messageObj.randomId]= messageObj;
		chatSocket.send(JSON.stringify(messageObj));
		//console.log(messageQue)
			setTimeout(function(){
				$.ajax({
					url: '/secure-chat/ismessage-saved/'+messageObj.randomId,
					type : 'GET',
					success : function(result){
						//console.log("Status");
						//console.log(result);
						if(!result.status){
							$('#message_body_content'+result.randomId).append('<small>message sending failed....</small>');
						}
						else{
							console.log(messageQue);
							if(messageQue[result.randomId]){
								delete messageQue[result.randomId];
								//console.log(messageQue);
							}
						}
						
					}
				});
			},100);
}
$(function(){
Init();

if(tabNo==10){
	getAppUserDate();
}
});

window.onbeforeunload = function() {
	chatSocket.onclose = function () {}; // disable onclose handler first
	chatSocket.close()
};


