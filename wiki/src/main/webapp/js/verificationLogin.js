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
                // alert("user not exits");
                show_not_exist_tip();
            } else if (data.included==false){
               // alert("username or password is incorrect");
                show_illegal_tip();
           } else {
                location.href = "../html/Outline.html";
            }
            // document.getElementById('login_btn').removeAttribute('disabled');
            remove_disable('login_btn');
        },
        error:function (data) {
            alert("error!");
            // document.getElementById('login_btn').removeAttribute('disabled');
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