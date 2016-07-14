/**
 * Created by duanzhengmou on 7/9/16.
 */

// var rawData;
// var all_data = new Object();
$.get(
    "/outline/show",
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
            "plugins" : ["wholerow","types",'search','unique']
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
    $.ajax({
        url:'/outline/setSummary',
        type:'post',
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
 * @returns {boolean}
 */
function is_array(data) {
//        alert ("type test-->"+Object.prototype.toString.call(data));
    return Object.prototype.toString.call(data)=='[object Array]';//!!!!!!!<<-----此处有精华

}
/**
 * 根据搜索的条目执行相关跳转,如果传入字符串不合法(如为空)则do nothing,若有其他判断条件则在此方法中扩展
 * @param entry_key 条目关键字,字符串类型
 */
function search_entry(entry_key) {
    if(entry_key == "")return ;
    alert("you enter the:"+entry_key);
    window.location.href="../html/entry_content_prototype.html";
}


