var username;
var role;
var socket;
var cur_chat = "#currentChat";
var cur_chat_id;
var message_html = '<div class="chat-message %RIGHT%">'+
	                   '<img class="message-avatar" src="images/profile.png" alt="" >'+
                           '<div class="message %EMERGENCY%">'+
				'<a class="message-author" href="#">%SENDER%</a>'+
                                '<span class="message-date">%DATE%</span>'+
                                '<span class="message-content">%TEXT%</span>'+
                           '</div>'+
                    '</div>';
var room_chat_html = '<div class="chat-user room-chat" data-chat = "%CHATID%">'+
                     		'<div class="chat-user-name">'+
                                 	'<a href="#">%ROOMNAME% </a><span class = "label label-warning new-msgs">0</span>'+
                                '</div>'+
                     '</div>';
    $("#logOut").click(function (event) {
        $.ajax({
            type: 'POST',
            url: '/Project/rest/users/auth',
            success: function () {
                window.location.replace("index.html");
            }
        });
    });
$(document).ready(function(){
        
	$.ajax({
		type: 'GET',
		url: '/Project/rest/users/auth',
		success: function (data) {
                        console.log("dffasd")
			if (!data) {
				alert("noSession");
				window.location.replace("index.html");
			} else {
				//$("#username").append(data);
				username = data;
				webSocket();
			}
		}
	});
});
function createRoomChatHTML(data) {
       	$("#rooms-list").append(room_chat_html.replace(/%CHATID%/g, data.chatId).replace(/%ROOMNAME%/g, "Room "+data.roomNumber));
}
function createPrivateChatHTML(data) {
        var usr_str = "";
        if (data.users.constructor === Array) {
            data.users.forEach(function (usr) {
                usr_str += usr + ", ";
            })
            usr_str = usr_str.substring(0, usr_str.length - 2);
        } else {
            usr_str = data.users;
        }
       	$("#rooms-list").append(room_chat_html.replace(/%CHATID%/g, data.chatId).replace(/%ROOMNAME%/g, usr_str));
}
function makeMessage(text, sender, chat, flag, date){
	var em_class = "";
        
        if(sender === username)  em_class  = "left"; else em_class  = "right";
	var right_class = "";
        if (flag == 1)  right_class = "emergency-message";
	$(chat).append(message_html.replace(/%SENDER%/g, sender).replace(/%TEXT%/g, text).replace(/%EMERGENCY%/g, right_class).replace(/%RIGHT%/g, em_class).replace(/%DATE%/g, date));
}
function webSocket() {
	var wsUri = "ws://" + document.location.host + "/Project/endpoint";
	socket = new WebSocket(wsUri);
	socket.onopen = function (msg) {
		console.log('Connection successfully opened');
		
	};
	socket.onmessage = function (msg) {
		    // Handle received data
		    // flag!!!!!!!!!!!!!!!!!!!!!!!!
		    // 0-standard message
		    // 1-emergency message
		    // 2-assignment inside room message
		    // 3-file
		    // 4-new assignment (assignmentId inside chatId)
		    // 5-assignment accepted (assignmentId inside chatId)
		    // 6-assignment done (assignmentId inside chatId)
		    //orderly should only see new assignments and the ones that he has accepted or completed
		    //doctor or nurse should see all assignments that he or she has created(all : new, accpeted, completed)
		   // console.log(msg);
		var json = JSON.parse(msg.data);
                console.log("Message!!")
		makeMessage(json.message, json.username, cur_chat, json.flag, json.timestamp);
	};
	socket.onclose = function () {
		console.log('Connection was closed.');
		window.location.replace("index.html");
	};
	$(window).unload(function () {
		socket.close();
	});
	socket.error = function (err) {
		console.log(err);
		window.location.replace("index.html");
	};
};
	function webSocketSend(chatId, flag, message) {
		var json = JSON.stringify({'chatId': ""+chatId,
			'flag': flag,
			'message': message});
		socket.send(json);
                console.log("MSG snt "+chatId)
	}
	function getHistory(chatId){
		$.ajax({
			type: 'GET',
			url: '/Project/rest/chats/history/'+chatId,
			success: function (data) {
				var x2js = new X2JS();
				var hist = x2js.xml2json(data);
				$(cur_chat).html("");
				if(hist.historyEntries.historyEntry){
					if(hist.historyEntries.historyEntry.constructor === Array){
						hist.historyEntries.historyEntry.forEach(function(ent){
							makeMessage(ent.message, ent.username, cur_chat, ent.flag, ent.timestamp);
						});
			    		}
				    	if(typeof hist.historyEntries.historyEntry === "object"){
						makeMessage(hist.historyEntries.historyEntry.message, hist.historyEntries.historyEntry.username, cur_chat, hist.historyEntries.historyEntry.flag, hist.historyEntries.historyEntry.timestamp);
						
				    	}
				}
                                 var wtf    = $(cur_chat);
                                var height = wtf[0].scrollHeight;
                                wtf.scrollTop(height);
			}
		});
	};
