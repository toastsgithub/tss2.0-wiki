/**
 * Created by zhengyunzhi on 16/7/9.
 */

function addClause(){
    alert("judge");
    var submit=true;
    if(document.getElementById("name_input").value == ""){
        document.getElementById("nameTip").innerHTML="请输入条目名称";
        submit=false;
    }
    if(document.getElementById("category_input").value == ""){
        document.getElementById("typeTip").innerHTML="请选择类别";
        submit=false;
    }
    if(document.getElementById("tags_input").value == ""){
        document.getElementById("tagTip").innerHTML="请选择标签";
        submit=false;
    }
    if(submit==false){
        return;
    }else {
        document.getElementById("nameTip").innerHTML="";
        document.getElementById("typeTip").innerHTML="";
        document.getElementById("tagTip").innerHTML="";
    }
    alert("judge done");
    

    var time = getTime();
    var title = document.getElementById("name_input").value;
    var summary = document.getElementById("summary").value;
    var tags = document.getElementById("tags_input").value;
    var catagories = document.getElementById("category_input").value;
    var content = show_markdown();
    content = "###this is content";
    alert("info");


    var data = {
                   operation: "add",data:
                   {
                       time: time,
                       username: "123",
                       summary: summary,
                       title: title,
                       tags: tags,
                       categories: catagories,
                       content: content
                   }
                   };
    // alert("content:"+content);
    alert("data:" + JSON.stringify(data));
    // var data = {operations:'add',data:{time:time, username:'123', summary:summary,title: title, tags:tags,
    //     categories:catagories,content:content}};
    $.ajax({
        type:"post",
        url:"/content/add",
        contentType:'application/json;charset=utf-8',
        data:JSON.stringify(data),
        success:function(data){
            alert("create successfully!");
        },
        error:function(data){
            alert("error");
        },
    })
}

function getTime(){
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth()+1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    var second = date.getSeconds();
    var time = year+'-'+month+'-'+day+' '+hour+':'+minute+':'+second;
    return time;
}