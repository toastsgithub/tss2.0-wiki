/**
 * Created by zhengyunzhi on 16/7/20.
 */

var all_outline_content=[];
var count = 0;
$.get(
    "/outline",
    null,
    function (data) {
        getEntry(data);
        show(data);
        
        $("#s").submit(function(e) {
            e.preventDefault();
            $("#outline").jstree(true).search($("#query").val());
        });
    }
);




function getEntry(rawData) {
    //解析该数据的id
    
    if(rawData.type == "default") {
        $.ajax({
            url: '/content/searchByCategories',
            async: false,
            type: 'get',
            data: {categories: rawData.text},
            success: function (data) {
                if (data.exist == 0) {
                    //do nothing
                } else {
                    for (var i in data.titlelist) {
                        count++;
                        alert("count = " + count);
                        var obj = {
                            "id": "entry_" + count,
                            "text": data.titlelist[i].title,
                            "li_attr": {"id": "entry_" + count},
                            "a_attr": {"href": "#", "id": "entry_" + count + "_anchor"},
                            "state": {
                                "loaded": true,
                                "opened": true,
                                "selected": false,
                                "disabled": false
                            },
                            "data": {},
                            "children": [],
                            "type": "entry"
                        };
                        rawData.children.push(obj);
                        // alert(JSON.stringify(data.titlelist[i]));
                    }
                }
            },
            error:function (data) {
                alert("error = " + rawData.text);
            }
        });

        //循环遍历解析children里面的内容
        for (var j in rawData.children) {
            getEntry(rawData.children[j]);
        }
    }
}

function show(data){
    alert("exm??");
    $('#outline').jstree({
        'core' : {
            "check_callback":true,

            'data' : data
        },
        "types" : {
            "default" : {
                "icon" : "glyphicon glyphicon-book"
            },
            "entry" :{
                "icon" : "glyphicon glyphicon-search"
            }
        },
        "plugins" : ["wholerow","types",'search','unique','contextmenu']
    });
}

