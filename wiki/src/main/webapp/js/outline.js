/**
 * Created by duanzhengmou on 7/9/16.
 */

var rawData;
$.get(
    "/content/outline",
    null,
    function (data) {
        rawData=eval("("+data+")");

        show(rawData);
    }
)


// var rowData={"软件":[{"一班":[{"二班":["说好的孙浩大哥呢"]},"考拉","浣熊"]},{"三班":["喵"]},{"四班":["歪歪"]},{"怎么这么多":["好烦啊","我编不下去了"]},"还有？？"]};

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



