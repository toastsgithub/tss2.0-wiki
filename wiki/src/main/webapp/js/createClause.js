/**
 * Created by zhengyunzhi on 16/7/9.
 */



var countries = [
    { value: 'Andorra', data: 'AD' },
    {value: 'Boston'},
    {value:'China'},
    {value:'Devon'},
    {value:'England'},
    { value: 'Zimbabwe', data: 'ZZ' }
];

var types = [];

var names = [];

var name = false, type = false, tag = false;




function addClause() {
    // alert("judge");
   
    if (!name && type && tag) {
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


function getAllNames() {
    $.get('/content/getTitleAndAlias',null, function (data) {
        for(var i=0;i<data.length;i++){
            names.push(data[i]);
        }
    })
}

function testName() {
    var name_tip = document.getElementById('nameTip');
    document.getElementById('name_input').onblur = function () {
    if(names.indexOf(this.value)!=-1){
        name_tip.innerHTML="该条目名称已存在";
        name = false;
    }else if(this.value == ""){
        name_tip.innerHTML = "名称不能为空";
        name = false;
    }else{
        name_tip.innerHTML = "";
        name =  true;
    }
    }
}


function testAll(){
    testName();
    testType();
    testTag();
    
}

function initialData(){
//提前获得所有的数据
    $(document).ready(function () {

        document.getElementById('wiki_editor').innerHTML ='#Title\n\nContent';
        $('#tags_input').autocomplete({
            lookup: countries
//            serviceUrl: '/content/tags'
        });




        $.get('/outline/list',null,function(data){
            var sourceData = data.data;
            for(var i=0;i<sourceData.length;i++){
                types.push(sourceData[i]);
            }
            $('#category_input').autocomplete({
                lookup: types
            });

        });

        $.ajax({
            type: 'get',
            url:'/content/tags',
            success:function (data) {
                for (var i=0;i<data.data.length;i++){
                    countries.push({value:data.data[i]});
                }
//                countries.push({value:"hello"});
//                alert("append done");
            },
            error:function () {
                alert("网络连接不畅通");
            }
        });


    });

    getAllNames();
}

function testTag() {
    //about tags
    $("#tags_input").keydown(function (e) {
        if(e.keyCode==13){
            var new_tag_to_add = document.getElementById('tags_input').value;
            if(document.getElementById("tags_pool").childElementCount>0){
                if(getTags()!=null && $.inArray(new_tag_to_add, getTags())>=0){
                    tag = false;
                    document.getElementById("tagTip").innerHTML = "请勿选择重复标签!";
                    return;
                }
            }
            if(document.getElementById("tags_pool").childElementCount>=5){
                document.getElementById("tagTip").innerHTML = "最多添加5个标签";
                tag = true;
                return;
            }
            document.getElementById("tagTip").innerHTML = "";
            add_tags(new_tag_to_add);
            tag = true;
        }
    })


    function add_tags(tag) {
//        alert("painting:"+tag);
        if(tag=="")return;

        var tag_pool = document.getElementById('tags_pool');
        var new_tag = document.createElement("span");
        var cancel_btn = document.createElement("label");

//        cancel_btn
        cancel_btn.innerHTML = "×";
        cancel_btn.style.marginLeft = '3px';
        cancel_btn.style.color = "#F5F5DC";
        cancel_btn.style.cursor = 'pointer';

        new_tag.innerHTML = tag;
        new_tag.style.borderRadius = '3px';
        new_tag.appendChild(cancel_btn);
        new_tag.id = tag;
        new_tag.style.color = "white";
        new_tag.style.backgroundColor = "#3B81C7";
        new_tag.style.padding = '5px';
        new_tag.style.marginRight = "20px";


//        alert("create done");
        tag_pool.appendChild(new_tag);
//        alert("append done");
        document.getElementById('tags_input').value = "";
        cancel_btn.onclick = function(){tag_pool.removeChild(document.getElementById(tag));}

    }
}

function testType(){
    //验证类别是否存在

    document.getElementById('category_input').onblur=function () {
        if(this.value!=""){
            if($.inArray(this.value, types)==-1){
                document.getElementById('typeTip').innerHTML = "该类别不存在!";
                type = false;
            }else{
                type = true;
                document.getElementById('typeTip').innerHTML = "";
            }
        }else{
            type = false;
            document.getElementById('typeTip').innerHTML = "请填写类别";
        }
    }
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