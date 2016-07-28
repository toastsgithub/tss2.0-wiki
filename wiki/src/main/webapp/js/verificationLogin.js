/**
 * Created by zhengyunzhi on 16/7/8.
 */


function verif(){
    // var xhr = new XMLHttpRequest();
    // xhr.s

    var username = document.getElementById("user").value;
    var password = document.getElementById("psw").value;
    if(username==""||password==""){
        // alert("please fill the blanks")
        show_unfinished_tip();
        return;
    }
    // document.getElementById('login_btn').setAttribute('disabled',"disabled");
    make_disable('login_btn');
    $.ajax({
        url:"/user/login",
        type:"get",
        data:{username:username,password:password},
        success:function (data) {
            if(data==""){
                show_not_exist_tip();
            } else if (data.included==false){
                show_illegal_tip();
           } else {
                if(location.search.split("=")[1]=="editor"){
                    location.href = "../html/New_entry_editor.html";
                }else {
                    location.href = "../html/Outline.html";
                }
            }
            remove_disable('login_btn');
        },
        error:function (data) {
            alert("error!");
            remove_disable('login_btn');
        }
    });
    
    // alert("end");
}

function show_unfinished_tip() {
    document.getElementById("tooltips").style.borderColor = "#f6ebcb";
    document.getElementById("tooltips").style.color = "rgb(139,109,61)";
    document.getElementById("tooltips").style.backgroundColor = "#fbf8e2";
    document.getElementById("tooltips").innerHTML = "请输入完整的账户名和密码";
    show_tooltips();
}

function show_illegal_tip() {
    document.getElementById("tooltips").style.borderColor = "rgb(226,203,207)";
    document.getElementById("tooltips").style.color = "rgb(149,69,66)";
    document.getElementById("tooltips").style.backgroundColor = "rgb(236,221,221)";
    document.getElementById("tooltips").innerHTML = "账户名或密码错误";
    show_tooltips();
}

function show_not_exist_tip() {
    document.getElementById("tooltips").style.borderColor = "rgb(226,203,207)";
    document.getElementById("tooltips").style.color = "rgb(149,69,66)";
    document.getElementById("tooltips").style.backgroundColor = "rgb(236,221,221)";
    document.getElementById("tooltips").innerHTML = "账户不存在";
    show_tooltips();
}

function show_tooltips() {
    document.getElementById("tooltips").style.visibility = "visible";
    setTimeout(function () {
        document.getElementById("tooltips").style.visibility = "hidden";
    },2000);
}


function get_welcome() {

    var login = false;
    var user_name;
    var user_type;

        $.ajax({
            url: '/user/info',
            type: 'get',
            async: false,
            data: null,
            success: function (data) {
                if(data.login == true){
                    login = true;
                    user_name = data.data.username;
                    user_type = data.data.type;
                    if(user_type==0){
                        try {
                            document.getElementById('console_button').parentNode.removeChild(document.getElementById('console_button'));
                        }catch (e){
                            // do nothing
                        }
                    }
                }
            },
            error: function (data) {
                alert("error!");
            }
        })
    if(login == false){
        document.getElementById("login_button").innerHTML="<a href='../html/login.html'>登录</a>";
        document.getElementById("welcome_tip").innerHTML="";
        document.getElementById("welcome_user").style.visibility="hidden";
        alert("hidden");
        return false;
    }
    else{
        var date = new Date();
        var hours = date.getHours();
        var welcome;
        if(4<=hours&&hours<11){
            welcome = "早上好, " + user_name;
        }else if(11<=hours&&hours<14){
            welcome =  "中午好, " + user_name;
        }else if(14<=hours&&hours<19){
            welcome =  "下午好, " + user_name;
        }else if(19<=hours && hours<24){
            welcome =  "晚上好, " + user_name;
        }else{
            welcome =  "已经是深夜了,早点休息, " + user_name;
        }
        user_message(user_type);
        document.getElementById("login_button").innerHTML="";
        document.getElementById("welcome_tip").innerHTML=welcome+document.getElementById("welcome_tip").innerHTML;
        
        return true;
    }
}

function getCookie(name){
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
    if(arr != null){
        return unescape(arr[2]);
    }else{
        return null;
    }
}

function deleteCookie(name){
    var date=new Date();
    date.setTime(date.getTime()-999999999999); //设定一个过去的时间即可
    document.cookie=name+"=v; expires="+date.toGMTString();
}

function clearLogin(){
    
    $.ajax({
        url: '/user/logout',
        type: 'get',
        async: false,
        success: function (data) {
            
        },
        error: function (data) {
            alert('error!');
        }
    });
}

function user_message(user_type){
    var parent = document.getElementById('welcome_user').parentNode;
    var li = document.createElement('li');
    li.style.marginTop = "13px";
    var image;
    $.ajax({
        url: '/message',
        type: 'get',
        success: function(data){
            if("error" == 1){
                alert("message pass error!");
                return;
            }else{
                if(data==[]){
                    //显示没有消息
                    image = document.createElement('img');
                    image.src = "../resource/icon/message.png";
                    image.style.height = '20px';
                    image.style.width = '20px';
                }else{
                    var hasNotRead = false;
                    for(var i in data.data){
                        if(data.data[i].isread == 0){
                            hasNotRead = true;
                        }
                    }
                    if(hasNotRead){
                        //显示有未读消息
                        image = document.createElement('img');
                        image.src = "../resource/icon/new_message.png";
                        image.style.height = '20px';
                        image.style.width = '20px';
                    }else{
                        //显示没有未读消息
                        image = document.createElement('img');
                        image.src = "../resource/icon/message.png";
                        image.style.height = '20px';
                        image.style.width = '20px';
                    }
                }
            }
            image.onclick = function(){
                if(user_type==1) {
                    window.location = "../html/admin_message_page.html";
                }else if(user_type == 0){
                    window.location = "../html/user_message_page.html";
                }
            }
            image.style.cursor = "pointer";
            li.appendChild(image);
            parent.appendChild(li);
        }
    })
}

