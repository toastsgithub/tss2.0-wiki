/**
 * Created by zhengyunzhi on 16/7/8.
 */

function verif(){
    // var xhr = new XMLHttpRequest();
    // xhr.s
    alert(21345678);
    var username = document.getElementById("user").value;
    alert("name"+username);
    var password = document.getElementById("psw").value;
    alert("pass"+password);
    $.ajax({
        url:"/user/login",
        type:"get",
        data:{username:username,password:password},
        success:function (data) {
            var veri = eval("(" + data + ")");
            // var info = data[1];
            alert(veri);
        },
        error:function (data) {
            alert("error!");
        }
    });
}