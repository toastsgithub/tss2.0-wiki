/**
 * Created by zhengyunzhi on 16/7/9.
 */

function addClause() {
    // alert("judge");
    var submit = true;
    if (document.getElementById("name_input").value == "") {
        document.getElementById("nameTip").innerHTML = "请输入条目名称";
        submit = false;
    }
    if (document.getElementById("category_input").value == "") {
        document.getElementById("typeTip").innerHTML = "请选择类别";
        submit = false;
    }
    // if (document.getElementById("tags_input").value == "") {
    //     document.getElementById("tagTip").innerHTML = "请选择标签";
    //     submit = false;
    // }
    if (judge_type == false){
        submit = false;
    }
    if (submit == false) {
        return;
    } else {
        document.getElementById("nameTip").innerHTML = "";
        document.getElementById("typeTip").innerHTML = "";
        document.getElementById("tagTip").innerHTML = "";
    }
    // alert("judge done");
    make_disable('submit_btn');

    var time = getTime();
    var title = document.getElementById("name_input").value;
    var summary = document.getElementById("summary").value;
    var tags = getTags();
    var catagories = document.getElementById("category_input").value;
    var username = get_user();
    alert("current user-->"+username);
    var content = show_markdown();
    // alert("----->content--->"+content);
    // content = "###this is content";
    // alert("info");


    var data = {
        operation: "add", data: {
            time: time,
            username: "123",
            summary: summary,
            title: title,
            tags: tags,
            categories: catagories,
            content: content
        }
    };
    $.ajax({
        type: "post",
        url: "/content",
        contentType: 'application/json;charset=utf-8',
        data: JSON.stringify(data),
        success: function (data) {
            // alert(JSON.stringify(data));
            remove_disable('submit_btn');
            if(data.error==0){
                alert("create successfully!");
                window.location = '../html/Outline.html';
            }else{
                alert("尚未登录或网络不畅通");
            }
        },
        error: function (data) {
            remove_disable('submit_btn');
            alert("error");
        }
    })
}

function getTime() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    var second = date.getSeconds();
    var time = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
    return time;
}

function getTags() {
    var tags_pool = document.getElementById('tags_pool');
    var tags = [];
    var num = tags_pool.childElementCount;
    if(num==0) return null;
    for(var i =0; i<num;i++){
        tags.push(tags_pool.childNodes[i].firstChild.textContent);
    }
    return  tags;
}