/**
 * Created by zhengyunzhi on 16/7/8.
 */

function verif(){
    // var xhr = new XMLHttpRequest();
    // xhr.s
    var username = document.getElementById("user").value;
    var password = document.getElementById("psw").value;
    $.ajax({
        url:"/user/login",
        type:"get",
        data:{username:username,password:password},
        success:function (data) {
            alert(data);
            if(data==""){
                alert("user not exits");
            } else if (data.included==false){
               alert("username or password is incorrect");
           } else if (data.user.type==0){
                alert("welcome, user!");
            }else if(data.user.type == 1){
                alert("welcome, admin!");
            }
        },
        error:function (data) {
            alert("error!");
        }
    });
}