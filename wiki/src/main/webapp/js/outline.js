/**
 * Created by duanzhengmou on 7/9/16.
 */

// var rawData;
var all_outline_content=[];
var current_outline_content = [];
$.get(
    "/outline",
    null,
    function (data) {
        // alert("data below----\n"+JSON.stringify(data));
        // rawData=eval("("+data+")");
        // show(rawData);
        // var json_data = {"软件":[{"一班":[{"二班":["说好的孙浩大哥呢"]},{"考拉":["考拉爸爸"]},{"浣熊":[]}]},{"三班":["瞄","好多"]},{"四班":["歪歪","就是多"]},{"怎么这么多":["好烦","编不下去了"]},"还有??"]};
        
        // alert("--->"+json_data_2);
        
        // var root_node = new Object();
        // root_node.text = "知识体系";
        // root_node.children = [];
        // all_data = root_node;
        // read_json(root_node,data);//读取到的数据为string 类型的,要转成obj (返回数据类型 可能跟请求方式和参数有关)
        
        // alert(JSON.stringify(root_node));
        $('#moutline').jstree({
            'core' : {
                // "themes" : {
                //     "dots" : true // no connecting dots between dots
                // },
                "check_callback":true,
                
                'data' : data
            },
            "types" : {
                "default" : {
                    "icon" : "glyphicon glyphicon-book"
                },
                // "demo" : {
                //     "icon" : "glyphicon glyphicon-ok"
                // }
            },
            "plugins" : ["wholerow","types",'search','unique','contextmenu']
            // "plugins" : ["dnd","contextmenu","wholerow","types",'search','unique']
        });
        $("#s").submit(function(e) {
            e.preventDefault();
            $("#moutline").jstree(true).search($("#q").val());
        });
    }
);



// var rowData={"软件":[{"一班":[{"二班":["说好的孙浩大哥呢"]},"考拉","浣熊"]},{"三班":["喵"]},{"四班":["歪歪"]},{"怎么这么多":["好烦啊","我编不下去了"]},"还有？？"]};
/**
 * 无条件读取当前的树的结构并发送到后端保存
 * @params:none
 */
function save_outline() {
    var data_obj = $('#moutline').jstree(true).get_json();
    alert(JSON.stringify(data_obj[0]));
    $.ajax({
        url:'/outline',
        type:'put',
        contentType:'application/json',
        data:JSON.stringify(data_obj[0]),
        success:function () {

            alert("success");
        },
        error:function (data) {
            alert('error'+JSON.stringify(data));
        }
    });
    
}
/**
 * 无条件存储当前的树的结构以及树节点下的条目内容
 */
function save_outline_and_entry() {
    var data_obj = $('#moutline').jstree(true).get_json('#',{flat:true,no_state:true,no_data:false});
    data_obj[0].text = '测试修改';
    data_obj[0].entries = ['title1','title2','title3'];
    alert(JSON.stringify(data_obj));
    var data_obj2 = $('#moutline').jstree(true).get_json('#',{flat:true,no_state:true,no_data:false});
    alert(JSON.stringify(data_obj2));
    // add_all_children(data_obj[0]);
    // $.ajax({
    //     url:'/outline/Test',
    //     type:'put',
    //     contentType:'application/json',
    //     data:JSON.stringify((data_obj[0])),
    //     success:function () {
    //         alert('success');
    //     },
    //     error:function () {
    //         alert('error');
    //     }
    // });
}

function add_all_children(node) {
    for(x in node.children){
        // alert(node.children[x].text);
        //TODO 添加所有的大纲下的子节点
        node.children[x].entries = [];
        add_all_children(node.children[x]);
    }
}

function show(rowData) {
    var outline = document.getElementById("moutline");
    var data = "<ol>";
    display(rowData);
    data += "</ol>";
    outline.innerHTML = data;

    function display(iterms) {

        //显示节点（可能是数组）
        var child;
        if (iterms.length > 0) {
            //为数组
            for (var i = 0; i < iterms.length; i++) {
                if ((typeof iterms[i]) != "string") {
                    for (var jsnkey in iterms[i]) {
//					alert("array = " + jsnkey);
                        data += "<li>" + jsnkey + "</li>";

                        child = iterms[i][jsnkey];//表示键值(即子节点)
                        //如果子节点没有： 即没有json格式的东西就不处理啦

                        if (child.length > 0) {
//							alert("我是分割线1");
                            data += "<ol>";
                            display(child);	//处理子节点（）
//							alert("我是分割线2");
                            data += "</ol>";
                        }
                    }
                } else {
//				alert("我也是子节点！= " + iterms[i]);	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    data += "<li>" + iterms[i] + "</li>";
                }
            }
        } else {
            //为单个json值
            for (var jsnkey in iterms) {
//				alert("single " + jsnkey);	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                data += "<li>" + jsnkey + "</li>";
                child = iterms[jsnkey];//表示键值(即子节点)
//				alert("我是分割线1");
                data += "<ol>";
                display(child);	//处理子节点（）
//				alert("我是分割线2");
                data += "</ol>";

            }
        }


    }
}

function getNum(data){
    var count=0;
    for(var item in data){
        count++;
    }
    return count;
}
/**
 * 递归遍历节点
 * @param father 父节点json对象
 * @param data 当前节点对象
 * @status 暂时无用
 */
function read_json(father,data) {
    // alert("data-->"+data);
    //    alert("current father:--?>"+JSON.stringify(data));

    if(!is_array(data)) {
        var current_node = new Object();
//            alert(" is a json obj");
        for (key in data){
            current_node.text = key;
            father.children[father.children.length] = current_node;
            current_node.children=[];
            read_json(current_node,data[key]);
//                alert("datakey: "+is_array(data[key]));
        }

    }else{
//            alert(" is a Array");
//            alert(data+" is an array and length is "+data.length);
//              alert(JSON.stringify(data));
        for (x in data){
//                alert("data type is json obj? ->"+(typeof data[x] == "object"));
            if(typeof data[x] == 'object'){
                //该数组元素是json对象
//                    alert("!!!这是一个json对象->"+JSON.stringify(data[x]));
                read_json(father,data[x]);
            }else{
                //该数组元素是单个数组值
//                    alert("!!!这是单个数值->"+data[x]);
                var current_node = new Object();
                current_node.text = data[x];
                father.children[father.children.length] = current_node;
            }
        }
    }
}
/**
 * 判断参数是否为array
 * @param data 任意对象类型
 * @status work well
 * @returns {boolean}
 */
function is_array(data) {
//        alert ("type test-->"+Object.prototype.toString.call(data));
    return Object.prototype.toString.call(data)=='[object Array]';//!!!!!!!<<-----此处有精华

}
/**
 * 根据搜索的条目执行相关跳转,如果传入字符串不合法(如为空)则do nothing,若有其他判断条件则在此方法中扩展
 * @status work well
 * @param entry_key 条目关键字,字符串类型
 */
function search_entry(entry_key) {
    if(entry_key == "")return ;
    window.location.href="../html/search_result_page.html?entry="+entry_key;
}

/**
 * 根据大纲节点的名称获取该节点下的所有条目,并构造对象,存储到全局数组中
 * @param outline_key 大纲的名称
 * @status work well
 * @returns {array}
 */
function get_entry_of_outline(outline_key) {
    var result = [];
    $.ajax({
        url:'/content/searchByCategories',
        // async:false,
        type:'get',
        data:{categories:outline_key},
        success:function (data){
            if(data.exist==0){
            //    do nothing
            }else{
                for (x in data.titlelist){
                    result.push(data.titlelist[x]);
                }
                var temp_obj = new Object();
                temp_obj.name = outline_key;
                temp_obj.content = result;
                // alert(JSON.stringify(temp_obj));
                all_outline_content.push(temp_obj);
            }

        },
        error:function(data){
        //    do nothing
        }
    });
    // return result;
}

/**
 * 在页面一加载的时候同时启动该方法,请求所有大纲的内容,并保存到全局数组中
 * @status work well
 * @params none
 */
function load_all_outline_content(){
    //--------------------革命的分界线------------------------
    // $.ajax({
    //     url:'/outline/list',
    //     // async:false,
    //     type:'get',
    //     success:function (data) {
    //         for (x in data.data){
    //             var list = get_entry_of_outline(data.data[x]);
    //
    //             // var temp_obj = new Object();
    //             // temp_obj.name = data.data[x];
    //             // temp_obj.content = list;
    //             // all_outline_content.push(temp_obj);
    //         }
    //
    //     },
    //     error:function(data){
    //         //    do nothing
    //     }
    // });
    //--------------------革命的分界线------------------------
    $.ajax({
        url:'/content/getAllTitleAndSummery',
        type:'get',
        success:function (data) {
            all_outline_content = data;
        },
        error:function (data) {
            alert('error'+JSON.stringify(data));
        }
    });
}
// /**
//  * 根据大纲的名称从全局数组中查询该大纲节点下存在的条目,并加到右侧div中,该方法通常
//  * 在大纲树的点击事件中触发
//  * @param outline_key 大纲节点名称
//  */
// function display_outline_content(outline_key) {
//     remove_all_child('article_board');
//     for (x in all_outline_content){
//       
//         if(all_outline_content[x].name == outline_key){
//             // alert(JSON.stringify(all_outline_content[x]));
//             for (y in all_outline_content[x].content){
//                 var content_title = all_outline_content[x].content[y];
//                 // alert(content_title+"]]]");
//                 var right_part = document.getElementById('article_board');
//                 var new_node = document.createElement('div');
//                 document.getElementById('article_board').style.backgroundColor='red';
//                 var the_link = document.createElement('a');
//                 var url = '../html/entry_content.html?entry='+content_title;
//                 the_link.href = url;
//                 the_link.innerHTML = content_title;
//                 new_node.appendChild(the_link);
//                 right_part.appendChild(new_node);
//             }
//         }
//     }
//
//     // var right_part = document.getElementById('article_board');
//     // var new_node = document.createElement('div');
//     // var the_link = document.createElement('a');
//     // the_link.href = 'http://www.baidu.com';
//     // the_link.innerHTML = outline_key;
//     // new_node.appendChild(the_link);
//     // right_part.appendChild(new_node);
//
// }

/**
 * 根据大纲的名称从全局数组中查询该大纲节点下存在的条目及其内容摘要,并加到右侧div中,该方法通常
 * 在大纲树的点击事件中触发
 * @param outline_key 大纲节点名称
 */
//var zhaiyao=["\n  摘要：123321，上山打老虎怎么样SDHCUSIC会发生丢还没看到妇女金额共和国viwegrjhvoigerhvoiejrpvjrepojvifdjvjleq 呢抗日女哦ieqrjfqpoewojgnfkdvfre分部日哈佛"];
function display_outline_content(outline_key) {
    remove_all_child('search_result_content');
    remove_all_child('nav_part');
    var has_detected = false;

    for (x in all_outline_content){

        if(all_outline_content[x].name == outline_key){
            // alert(JSON.stringify(all_outline_content[x]));
            has_detected = true;
            // alert("keyyyyyyyyyy = " + JSON.stringify(all_outline_content[x]));
            var content_num = showSearchResult(all_outline_content[x].content, 5);
            load_page_btns(content_num);
            
            // for (y in all_outline_content[x].content){
            //
            //         var content_title = all_outline_content[x].content[y].title;
            //         //这里是内容摘要
            //         var content_abstract = all_outline_content[x].content[y].summary;
            //         var temp_obj = new Object();
            //         temp_obj.title = content_title;
            //         temp_obj.summary = content_abstract;
            //         current_outline_content.push(temp_obj);
            //
            //         // alert(content_title+"]]]");
            //         var right_part = document.getElementById('article_content');
            //         var new_node = document.createElement('div');
            //         // document.getElementById('article_board').style.backgroundColor='red';
            //         var the_node = document.createElement('div');
            //         var the_link = document.createElement('a');
            //         var the_abstract = document.createElement('div');
            //         var url = '../html/entry_content.html?entry='+content_title;
            //         the_link.href = url;
            //         the_link.innerHTML = content_title;
            //         the_link.style.fontSize = '20px';
            //         the_abstract.innerHTML = content_abstract;
            //         the_node.appendChild(the_link);
            //     the_node.appendChild(the_abstract);
            //     the_node.style.marginTop='10px';
            //     new_node.style.backgroundColor='#afd9ee';
            //     new_node.style.height = '70px';
            //     new_node.appendChild(the_node);
            //     new_node.style.marginBottom='10px';
            //     new_node.onclick = Function('temp("hello");');
            //     right_part.appendChild(new_node);
            // }

        }
    }
    if(!has_detected){
        load_empty_tip();
    }else{
       
    }

    // var right_part = document.getElementById('article_board');
    // var new_node = document.createElement('div');
    // var the_link = document.createElement('a');
    // the_link.href = 'http://www.baidu.com';
    // the_link.innerHTML = outline_key;
    // new_node.appendChild(the_link);
    // right_part.appendChild(new_node);

}

/**
 * 移除该元素下的所有子元素
 * @param element_id
 */
function remove_all_child(element_id) {
    var div = document.getElementById(element_id);
    while(div.hasChildNodes()) //当div下还存在子节点时 循环继续
    {
        div.removeChild(div.firstChild);
    }
}

/**
 * 返回jstree选中的节点的名称
 * @returns selected_node 选中的节点的名称
 */
function get_selected_node() {
    var selected_node = $('#moutline').jstree(true).get_selected(true)[0].text;
    alert('selected:'+selected_node);
    return selected_node;
}

/**
 * 加载目录下内容为空的插画提示
 */
function load_empty_tip(){
    var embarrassing_div = document.createElement('div');
    var image = document.createElement('img');
    var embarrasing_text = document.createElement('div');
    embarrassing_div.appendChild(image);
    embarrassing_div.appendChild(embarrasing_text);
    image.src = '../resource/image/embarrassing_gray.png';
    embarrassing_div.style.textAlign = "center";
    image.style.width = '350px';
    embarrasing_text.innerHTML = '这非常的尴尬,但是这个条目下没有内容';
    embarrasing_text.style.fontSize = '20px';
    embarrasing_text.style.color = '#d9d9d9';
    document.getElementById('search_result_content').appendChild(embarrassing_div);
}

/**
 * 根据内容数目加载页面的按钮数
 */
function load_page_btns(num){
    
    var pages = num/5;
    

    var nav_part = document.getElementById('nav_part');
    var nav = document.createElement('nav');
    nav_part.appendChild(nav);

    var ul = document.createElement('ul');
    ul.className = 'pagination nav ';
    var previous = document.createElement('li');
    previous.innerHTML = "<a href='#' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a>";
    ul.appendChild(previous);
    for(var i=1;i<pages+1;i++) {
        var li = document.createElement('li');
        if(i==1){
            li.className = "active";
        }
        li.id="page_"+i;
        li.innerHTML="<a href='#page_content_"+i+"' data-toggle='tab'>"+i+"</a>";
        ul.appendChild(li);
    }
    var after = document.createElement('li');
    after.innerHTML = "<a href='#' aria-label='Next'><span aria-hidden='true'>&raquo;</span></a>";
    ul.appendChild(after);
    nav.appendChild(ul);
    return pages;
  
}
/**
 *
 * @param page_num
 */
function jump_on_page(page_num) {

}

/**
 * 根据页面nav和点击的大纲内容生成搜索结果
 * @param content: 所有搜索结果
 * @param pages: 页数
 * @param num: 每页显示的条目数
 * */
function showSearchResult(content, num){
    var sr_content = document.getElementById("search_result_content");
    var content_count = 0;
    var page_count = 0;
    var sr_pages = [];
    for(var x in content){
        // alert("x = "+ JSON.stringify(content[x].title));
        if(content_count%num==0){   /* 每满一页 */
            var new_toggle = document.createElement('div');
            new_toggle.id = "page_content_" + (page_count+1);
            if(page_count==0){
                new_toggle.className = 'tab-pane active';
            }else{
                new_toggle.className = 'tab-pane';
            }
            sr_content.appendChild(new_toggle);
            sr_pages.push(new_toggle);
            page_count++;
        }
        /* 获得一个新条目的信息 */
        var content_title = content[x].title;
        var content_abstract = content[x].summary;
        var temp_obj = new Object();
        temp_obj.title = content_title;
        temp_obj.summary = content_abstract;
        content_count++;

        /* 为一个条目创建一个div */
        // <new_entry_div>
        //     <new_entry_content_div>
        //         <new_entry_link></new_entry_link>
        //         <new_entry_abstract_di></new_entry_abstract_di>
        //     </new_entry_content_div>
        // </new_entry_div>
        //-----------------------革命的开始--------------------------
        // var new_entry_div = document.createElement('div');
        // new_entry_div.style.backgroundColor = "rgb(175, 217, 238)";
        // new_entry_div.style.height = "70px";
        // new_entry_div.style.marginBottom = "10px";
        // var new_entry_content_div = document.createElement('div');
        // new_entry_content_div.style.marginTop = "10px";
        // var new_entry_link = document.createElement('a');
        // new_entry_link.href = '../html/entry_content.html?entry='+content_title;
        // new_entry_link.style.fontSize = "20px";
        // new_entry_link.innerHTML = content_title;
        // var new_entry_abstract_div = document.createElement('div');
        // new_entry_abstract_div.innerHTML = content_abstract;
        //
        // new_entry_div.appendChild(new_entry_content_div);
        // new_entry_content_div.appendChild(new_entry_link);
        // new_entry_content_div.appendChild(new_entry_abstract_div);
        // sr_pages[page_count-1].appendChild(new_entry_div);
        //-----------------------革命的分界线--------------------------
        var new_entry_div = document.createElement('div');
        new_entry_div.className = 'panel panel-primary';
        // new_entry_div.style.marginBottom = "10px";
        var new_entry_head_div = document.createElement('div');
        new_entry_head_div.className = 'panel-heading';
        new_entry_head_div.style.padding = '2px';
        new_entry_head_div.style.paddingLeft = '10px';
        
        // var new_entry_content_div = document.createElement('div');
        // new_entry_content_div.className = 'panel-body';
        
        var new_entry_link = document.createElement('a');
        new_entry_link.href = '../html/entry_content.html?entry='+content_title;
        new_entry_link.style.fontSize = "20px";
        new_entry_link.style.fontWeight = '200';
        new_entry_link.style.color = 'white';
        new_entry_link.innerHTML = content_title;
        
        var new_entry_abstract_div = document.createElement('div');
        new_entry_abstract_div.className = 'panel-body';
        new_entry_abstract_div.innerHTML = content_abstract;

        new_entry_head_div.appendChild(new_entry_link);
        new_entry_div.appendChild(new_entry_head_div);
        new_entry_div.appendChild(new_entry_abstract_div);
        
        // new_entry_content_div.appendChild(new_entry_link);
        // new_entry_content_div.appendChild(new_entry_abstract_div);
        sr_pages[page_count-1].appendChild(new_entry_div);
    }
    for(var tmp in sr_pages){
        sr_content.appendChild(sr_pages[tmp]);
    }
    return content_count;
}

function show_all_buffer() {
    alert('buffer '+JSON.stringify(all_outline_content));
}
/**
 * 初始化部分事件监听等
 */
function init_outline_page() {
    document.getElementById('search_input').addEventListener('keydown',function (e) {
        if(e.keyCode == 13){
            var keyword = document.getElementById('search_input').value;
            search_entry(keyword);
        }
    });
    document.getElementById('search_btn').addEventListener('click',function () {
        alert('done');
        var keyword = document.getElementById('search_input').value;
        search_entry(keyword);
    })
}