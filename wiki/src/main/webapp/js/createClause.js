/**
 * Created by zhengyunzhi on 16/7/9.
 */



var countries = [
    {value: 'Andorra', data: 'AD' },
    {value: 'Boston'},
    {value:'China'},
    {value:'Devon'},
    {value:'England'},
    { value: 'Zimbabwe', data: 'ZZ' }
];

var types = [];

var names = [];

var name = false, type = false, tag = false;

var origin_width_of_input;

var origin_width_of_alias_input;

var reference_count = 0;


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
    //这里摘要的提取还需要精细化

    if(summary==''){
        //如果没有输入摘要
        if(content.length>100) {
            summary = content.substring(0, 100);
        }else{
            summary = content;
        }
    }else{
        //nothing
    }
    // alert("----->content--->"+content);
    // content = "###this is content";
    // alert("info");

    //mock data
    var mock_obj = new Object();
    mock_obj.name = '这是一条新闻';
    mock_obj.url = 'www.baidu.com';
    mock_obj.websiteName = '凤凰网';
    
    var mock_obj2 = new Object();
    mock_obj2.name = '这是一条谷歌记录';
    mock_obj2.url = 'www.google.com';
    mock_obj2.websiteName = '谷歌';
    var all_alias = getAlias();
    alert(all_alias);
    var data = {
        operation: "add", data: {
            id:0,
            alias:all_alias,
            time: time,
            username: username,
            summary: summary,
            title: title,
            tags: tags,
            categories: catagories,
            content: content,
            reference:[mock_obj,mock_obj2]
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
                alert("添加过程中出现错误,原因:"+data.message);
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

function get_all_reference() {
    var reference = document.getElementById('reference');
    var result = [];
    
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
    testAlias();
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
            if(document.getElementById("tag_bar").childElementCount>0){
                if(getTags()!=null && $.inArray(new_tag_to_add, getTags())>=0){
                    tag = false;
                    document.getElementById("tagTip").innerHTML = "请勿选择重复标签!";
                    return;
                }
            }
            if(document.getElementById("tag_bar").childElementCount>=5){
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
        if(tag=="")return;
        if(tag.length>15){
            tag = tag.substring(0,14);
        }
        var tag_pool = document.getElementById('tag_bar');
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
        new_tag.style.padding = '3px';
        new_tag.style.marginRight = "10px";


//        alert("create done");
        tag_pool.appendChild(new_tag);
//        alert("append done");
        var tags_input = document.getElementById('tags_input');
        tags_input.value = '';
        // var tags_width = tag_pool.offsetWidth;
        // alert('width '+tags_width);
        // tags_input.style.width = (origin_width_of_input - tags_width) + 'px';
        adjust_tag_input_width();
        cancel_btn.onclick = function(){
            tag_pool.removeChild(document.getElementById(tag));
            // tags_input.style.width = (origin_width_of_input - tags_width) + 'px';
            adjust_tag_input_width();
        }

    }
}

function adjust_tag_input_width() {
    var tags_input = document.getElementById('tags_input');
    var tag_pool = document.getElementById('tag_bar');
    var tags_width = tag_pool.offsetWidth;
    tags_input.style.width = (origin_width_of_input - tags_width) + 'px';
}

function adjust_alias_input_width() {
    var tags_input = document.getElementById('alias_input');
    var alias_pool = document.getElementById('alias_bar');
    var alias_width = alias_pool.offsetWidth;
    tags_input.style.width = (origin_width_of_alias_input - alias_width) + 'px';
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

function testAlias() {
    $('#alias_input').keydown(function (e) {
        if(e.keyCode==13){
            var new_alias_to_add = document.getElementById('alias_input').value;
            if(new_alias_to_add=='')return;
            // alert(new_alias_to_add);

            //测试条件

            //通过则增加↓
            add_alias(new_alias_to_add);
        }
        
    })
}

function add_alias(alias) {
    var alias_pool = document.getElementById('alias_bar');
    var alias_input = document.getElementById('alias_input');
    var new_alias = document.createElement("span");
    var cancel_btn = document.createElement("label");

    
    
    // alert(alias_pool.children[0]);
        if(alias_pool.childElementCount>0) {
            for (var x=0;x<alias_pool.childElementCount;x++) {
                // alert(alias_pool.childNodes[x].firstChild.textContent);
                if(alias_pool.childNodes[x].firstChild.textContent ==alias){
                    alias_input.value = '';
                    return;
                }
            }
    }
    // all_alias.push(alias);


    

//        cancel_btn
    cancel_btn.innerHTML = "×";
    cancel_btn.style.marginLeft = '3px';
    cancel_btn.style.color = "#F5F5DC";
    cancel_btn.style.cursor = 'pointer';

    new_alias.innerHTML = alias;
    new_alias.style.borderRadius = '3px';
    new_alias.appendChild(cancel_btn);
    new_alias.id = alias;
    new_alias.style.color = "white";
    new_alias.style.backgroundColor = "#3B81C7";
    new_alias.style.padding = '3px';
    new_alias.style.marginRight = "10px";
    alias_input.value = '';
    alias_pool.appendChild(new_alias);
    
    cancel_btn.onclick = function(){
        alias_pool.removeChild(document.getElementById(alias));
        // tags_input.style.width = (origin_width_of_input - tags_width) + 'px';
        adjust_tag_input_width();
    };
    adjust_alias_input_width();
}

function getAlias(){
    var alias_pool = document.getElementById('alias_bar');
    var all_alias = [];
    var len = alias_pool.childElementCount;
    for(var x=0;x<len;x++){
        all_alias.push(alias_pool.childNodes[x].firstChild.textContent);
    }
    return all_alias;
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
    var tags_pool = document.getElementById('tag_bar');
    var tags = [];
    var num = tags_pool.childElementCount;
    if(num==0) return null;
    for(var i =0; i<num;i++){
        tags.push(tags_pool.childNodes[i].firstChild.textContent);
    }
    return  tags;
}

function add_reference_input_area() {
    var reference = document.createElement('div');
    reference.id = 'ref_item'+reference_count;
    reference.style.marginTop = '10px';
    var name_label = document.createElement('span');
    name_label.style.padding = '2px';
    name_label.innerHTML = '名称';
    var name_input = document.createElement('input');
    name_input.style.width = '100px';
    name_input.id = 'reference_name_' + reference_count;
    var url_label = document.createElement('span');
    url_label.innerHTML = 'url';
    url_label.style.padding = '4px';
    var url_input = document.createElement('input');
    url_input.style.width = '200px';
    url_input.id = 'reference_url_' + reference_count;
    
    var reference_delete = document.createElement('label');
    reference_delete.innerHTML = "x";
    reference_delete.id = "reference_delete_" + reference_count;
    reference_delete.style.marginLeft = '20px';
    reference_delete.style.cursor = 'pointer';
    
    // var source_label =document.createElement('span');
    // source_label.innerHTML = '来源名称';
    // source_label.style.padding = '4px';
    // var source_input = document.createElement('input');
    // source_input.style.width = '100px';
    
    // var cancel_btn
    reference.appendChild(name_label);
    reference.appendChild(name_input);
    reference.appendChild(url_label);
    reference.appendChild(url_input);
    reference.appendChild(reference_delete);
    // reference.appendChild(source_label);
    // reference.appendChild(source_input);
    document.getElementById('reference').appendChild(reference);
    reference_delete.onclick = delete_reference;
    reference_count++;
}

function get_reference(){
    var reference = document.getElementById('reference');
    var child = reference.childNodes;
    var reference_result = [];
    for(var i = 0; i < child.length; i++){
        var reference_name = child[i].childNodes[1].value;
        var reference_link = child[i].childNodes[3].value;
        if("reference_name"=="" || reference_link == ""){
            // do not push
        }else {
            var obj = {"reference_name": reference_name, "reference_link": reference_link};
            reference_result.push(obj);
        }
    }
    alert(JSON.stringify(reference_result));
}

function delete_reference(){
    var tmp = this.id.split("_");
    var count = tmp[2];
    // alert("count = " + count);
    var refer = document.getElementById('reference');
    refer.removeChild(document.getElementById('ref_item'+count));
    
}
