/**
 * Created by zhengyunzhi on 16/7/9.
 */

function addClause(){
    var time = getTime();
    var title = document.getElementById("name").value;
    var summary = document.getElementById("summary").value;
    var tags = document.getElementById("tag").value;
    var catagories = document.getElementById("type").value;
    var content = show_markdown();

    var data0 = {
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
    // var data = {operations:'add',data:{time:time, username:'123', summary:summary,title: title, tags:tags,
    //     categories:catagories,content:content}};
    $.ajax({
        type:"post",
        url:"/content/add",
        contentType:'application/json;charset=utf-8',
        data:JSON.stringify(data0),
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