/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var message_html = "<div class = 'message %CLASS%'><h5>%SENDER% : %TEXT%</h5><h6>%DATE%</h6></div>";
var cur_usr;
$(document).ready(function () {
    var username;
    var role;
    var socket;
    $.ajax({
        type: 'GET',
        url: '/Project/rest/users/auth',
        success: function (data) {
            if (data === undefined) {
                alert("noSession");
                window.location.replace("index.html");
            } else {
                $("#username").append(data);
                username = data;
                cur_usr = data;
                webSocket();
            }
        }
    });
    $("#getGlbChat").trigger("click")
    $("#getUsers").click(function () {
        $(".global-chat").hide();
        $("#roomChats").hide();
        $("#")
        $(".active").removeClass("active");
        $(this).addClass("active");
    });

    function webSocket() {
        var wsUri = "ws://" + document.location.host + "/Project/endpoint";
        socket = new WebSocket(wsUri);

        socket.onopen = function (msg) {
            // Logic for opened connection
            console.log('Connection successfully opened');

            webSocketSend(0, 0, 'test', '');
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
            console.log(json);
            /*if (json.chat === "global") {
             $("#globalMessages").append(json.username + ':' + json.message + '\n');
             } else if (json.chat === "room") {*/
            var id = "#" + json.chatId + "chat";
            $(id).append(json.username + ':' + json.message + '\n');
            //}

        };

        socket.onclose = function () {
            // Logic for closed connection
            console.log('Connection was closed.');
            window.location.replace("index.html");
        };

        $(window).unload(function () {
            socket.close();
        });

        socket.error = function (err) {
            console.log(err);
            window.location.replace("index.html");
            // Write errors to console
        };
    }
    ;

    function webSocketSend(chatId, flag, message) {
        var json = JSON.stringify({'chatId': chatId,
            'flag': flag,
            'message': message});
        socket.send(json);
    }

    $(document.body).on('click', '.sendMessage', function () {
        var chatId = $(this).data('chat');
        var message = $(this).prev(".msg-input").val();
        webSocketSend(chatId, 0, message);
    });

    $("#logOut").click(function (event) {
        $.ajax({
            type: 'POST',
            url: '/Project/rest/users/auth',
            success: function () {
                window.location.replace("index.html");
            }
        });
    });

    $("#uploadFile").click(function (event) {
        var file = $('input[name="file"').get(0).files[0];

        var formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/Project/rest/file/upload', //Server script to process data
            type: 'POST',
            data: formData,
            cache: false,
            contentType: false,
            dataType: false,
            processData: false,
            enctype: 'multipart/form-data',
            success: function (data, status) {
                console.log(data);
                console.log(status);
                //$("#imgTest").append("<img src='images/" + data +"'>");
                if (status === "success") {
                    $("#imgTest").append("<a href='/Project/rest/file/" + data + "' target='_blank' download>Download</a>");
                    //$("#imgTest").append("<a href='/Project/rest/file/" + data +"'>Download</a>");
                    webSocketSend(chatId, 4, data);
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);

            }
        });
    });

    $("#getUsers").click(function (event) {
        $.ajax({
            type: 'GET',
            url: '/Project/rest/users',
            success: function (data) {
                var x2js = new X2JS();
                var users = x2js.xml2json(data)['users'];
                console.log(users);
                console.log(data);
                if (users.hasOwnProperty("user")) {
                    var arrayOfUsers = users.user;
                    if (arrayOfUsers instanceof Array) {
                        $.each(arrayOfUsers, function () {
                            writeUsersToView(this['username']);
                        });
                    } else
                        writeUsersToView(arrayOfUsers['username']);
                }
            }
        });
    });

    function writeUsersToView(data) {
        $("#users").append("<button  class='user' type='button' id=" + data + "> " + data + "</button><br>");
    }
    function prepareRoomsNav(data){
        return "<li role='presentation' class ='room-nav' data-room = '"+data.chatId+"'><a href='#'>Room "+data.chatId+"</a></li>";
    }
    $(document.body).on("click", ".room-nav", function(){
        $(".room-chat").hide();
        $("#"+$(this).data("room")+"chat").show();
    })
    function createRoomChatHTML(data) {
        $("#roomChatContainer").append("<div id = '"+data.chatId+"chat' class = 'room-chat' style = 'display : none'></div>");
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
        $("#privateChats").append("<h3>" + usr_str + "</h3>" +
                "<button class = 'btn btn-danger btn-md ajax-button' data-action = '/Project/rest/chats/privateChat/' data-add-to-url ='" + data.chatId + "' data-callback ='leaveChat' data-type ='DELETE'>Leave chat</button>" +
                "<button class = 'btn btn-success btn-md add-user'  data-chat = '" + data.chatId + "'>Add user</button>" +
                "<textarea class= 'form-control' id = '" + data.chatId + "chat' readonly = 'readonly' rows = '10' cols = '45'></textarea><br>" +
                "<input class= 'form-control msg-input'  type = 'text' placeholder = 'Message' / >" +
                "<button style = 'margin : 15px' type = 'button' data-chat='" + data.chatId + "' class = 'btn btn-md btn-success sendMessage' > Send </button><br>");
    }
    function createGlobalChatHTML(data) {
        $("#globalChatContainer").prepend("<div id = '"+data.chatId+"chat'> </div>");
        $("#sendGlobal").data("chat", data.chatId);
    }
    var makeRoomChats = function (data) {
        $("#globalChatContainer").hide();
        $("#roomChatsContainer").show();
        $("#privateChatsContainer").hide();
        $(".active").removeClass("active");
        $("#getRoomChats").addClass("active");
        console.log(data);
        var x2js = new X2JS();
        var chats = x2js.xml2json(data);
        console.log(chats);
        $("#roomChats").html("");
        var nav = "<ul class='nav nav-tabs'>";
        if(chats.roomChats === "") return;
        chats.roomChats.roomChat.forEach(function (cht) {
            nav += prepareRoomsNav(cht);
            createRoomChatHTML(cht);
            $("#getHistory").data("add-to-url", cht.chatId);
            $("#getHistory").trigger("click");
            
        });
        nav += "</ul>";
        console.log(nav);
        $(nav).insertBefore($("#roomChatContainer"));
        $(".room-nav").first().trigger("click");
    }
    var makeGlobalChat = function (data) {
        $("#globalChat").show();
        $("#roomChats").hide();
        $("#privateChatsContainer").hide();
        $(".active").removeClass("active");
        $("#getGlobalChat").addClass("active");
        var x2js = new X2JS();
        var chat = x2js.xml2json(data);
        console.log(data)
        createGlobalChatHTML(chat.globalChat);
        $("#globalChat").html("");
        $("#getHistory").data("add-to-url", chat.globalChat.chatId);
        $("#getHistory").trigger("click");
        
    }
    var getHistory = function (data) {
        var x2js = new X2JS();
        var hist = x2js.xml2json(data);
        if (hist.historyEntries.historyEntry) {
            if (hist.historyEntries.historyEntry.constructor === Array) {
                hist.historyEntries.historyEntry.forEach(function (ent) {
                    var id = "#" + ent.chatId + "chat";
                    var classs = "message-left";
                    if(cur_usr ===ent.username) classs = "message-right";
                    //$(id).append(ent.username + ':' + ent.message + '\n');
                    $(id).append(message_html.replace(/%TEXT%/g, ent.message).replace(/%SENDER%/g, ent.username).replace(/%DATE%/g, ent.timestamp).replace(/%CLASS%/g, classs));
                });
            }
            if (typeof hist.historyEntries.historyEntry === "object") {
                var id = "#" + hist.historyEntries.historyEntry.chatId + "chat";
                var classs = "message-left";
                if(cur_usr ===hist.historyEntries.historyEntry.username) classs = "message-right";
                //$(id).append(hist.historyEntries.historyEntry.username + ':' + hist.historyEntries.historyEntry.message + '\n');
                $(id).append(message_html.replace(/%CLASS%/g, classs).replace(/%TEXT%/g, hist.historyEntries.historyEntry.message).replace(/%SENDER%/g, hist.historyEntries.historyEntry.username).replace(/%DATE%/g, hist.historyEntries.historyEntry.timestamp));
            }
        }
    }

    var getPrivateChats = function (data) {
        $("#globalChat").hide();
        $("#roomChats").hide();
        $("#privateChatsContainer").show();
        $(".active").removeClass("active");
        $("#getPrivateChats").addClass("active");
        //console.log(data);
        var x2js = new X2JS();
        var chats = x2js.xml2json(data);
        console.log(chats);
        $("#privateChats").html("");
        if (chats.privateChats === "")
            return;
        if (chats.privateChats.privateChat.constructor === Array) {
            chats.privateChats.privateChat.forEach(function (cht) {
                $("#getHistory").data("add-to-url", cht.chatId);
                $("#getHistory").trigger("click");
                createPrivateChatHTML(cht);
            });
        } else {
            $("#getHistory").data("add-to-url", chats.privateChats.privateChat.chatId);
            $("#getHistory").trigger("click");
            createPrivateChatHTML(chats.privateChats.privateChat);
        }
    }

    var createPrivateChat = function () {
        $("#getPrivateChats").trigger("click");
    }
    var leaveChat = function () {
        $("#getPrivateChats").trigger("click");
    }
    var addUserToChat = function(){
        $("#usrss").modal("hide");
        $("#getPrivateChats").trigger("click");
    }
    var callbacks = {
        justLog: function (data) {
            console.log(data);
        },
        createRoomChats: makeRoomChats,
        createGlobalChat: makeGlobalChat,
        getPrivate: getPrivateChats,
        getHistory: getHistory,
        createPrivate: createPrivateChat,
        leaveChat: leaveChat,
        addUser : addUserToChat

    };
    var chat_to_add_user_to = 0;
    function makeUsersSelect(users){
        $("#pickUser").html("");
        $("#pickUser").append("<option value = ''></option>")
        if(users === "") return;
        if (users.user.constructor === Array) {
            users.user.forEach(function (usr) {
                $("#pickUser").append("<option value = '"+usr.username+"'>"+ usr.role + " " +usr.firstName + " " + usr.lastName+"</option>")
            });
        }else{
            $("#pickUser").append("<option value = '"+users.user.username+"'>"+ users.user.role + " " +users.user.firstName + " " + users.user.lastName+"</option>")
        }
    }
    $(document.body).on("click", ".add-user", function () {
        chat_to_add_user_to = $(this).data("chat");
        $.ajax({
            url: "/Project/rest/users",
            type: "GET",
            success: function (data) {
                var x2js = new X2JS();
                var users = x2js.xml2json(data);
                console.log(users)
                makeUsersSelect(users.users);
                $("#usrss").modal("show");
            }
        });
    });
    $(document.body).on("change", "#pickUser", function () {
        $("#addUserToChat").data("add-to-url", "/"+chat_to_add_user_to+"/"+$(this).val());
    });
    $(document.body).on("click", ".ajax-button", function () {
        var add_to_url = $(this).data("add-to-url");
        if (!add_to_url)
            add_to_url = "";
        var act = $(this).data("action");
        var callback = callbacks[$(this).data("callback")];
        var type = $(this).data("type");
        console.log(act)
        $.ajax({
            url: act + add_to_url,
            type: type,
            success: function (data) {
                console.log(data);
                callback(data);
            }
        });
    });
    $("#getGlbChat").trigger("click");
});