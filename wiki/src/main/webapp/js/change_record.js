/**
 * Created by wh on 2016/7/25.
 */
var all_message;
var view_message;
/**
 * 显示数据到页面中
 * */
function getRecord() {
    var table = $('#message_table').DataTable({
        data: null,
        dom: 'rtip',
        columns: [
            {data: 'title'},
            {data: 'state'},
            {data: 'timestamp'},
            {data: 'wikiId'}

        ],
        columnDefs: [{
            'targets': [1],
            // 'data':'状态',
            'render': function (data, type, full) {
                // return 'ascii';
                // alert(data+" "+type+" "+JSON.stringify(full));
                if (data == 0) {
                    //待审核
                    return '<span>待审核</span>';
                } else if (data == 1) {
                    //已拒绝
                    return '<span>修改失败</span>';
                }
                else {
                    //已添加
                    return '<span>修改成功</span>';
                }
            }
        },
            ]
    });
    //以下关于自定义搜索框
    $('#search_table_input').on('keyup',function () {
        table.search(this.value).draw();
    });
    
    $('#message_table tbody').on('click', 'tr', function () {
        table.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        var selected_row = table.row('.selected').index();
        // document.getElementById('test_part').innerHTML = JSON.stringify(test_cache);


        if (selected_row == undefined) return;
        var state = table.cell(selected_row, 1).data();
        var id = table.cell(selected_row, 3).data();
        location.href = '../html/historyContent.html?state=' + state + '&id=' + id;
    });

    load_message();
    table.column(3).visible(false);
}

/**
 * 从后端获取数据
 * */
function load_message() {
    var table = $('#message_table').DataTable();
    // table.rows.add([{'title':'标题','state':1,'timestamp':2016}]);
    $.ajax({
        url: '/modify/list',
        type: 'get',
        success: function (data) {
            if (data.error == 0) {
                all_message = data.modifyHistoryArrayList;
                table.rows.add(data.modifyHistoryArrayList).draw();
            } else {
                alert('服务器响应内容中有错误:' + data.message);
            }
        },
        error: function (data) {
            alert('error');
        }
    });
}

function click_entry() {
    var keyword = document.getElementById('search_input').value;
    if (keyword == '')return;
    var url = '../html/search_result_page.html?entry=' + data;
    location.href = url;
}

/**
 * 根据页面点击从后端获取数据
 * @param state: 每条信息的状态
 * @param id: 每条信息的wikiId
 * */
function showSearchResult(state, id) {
    alert(state + "" + id);
    $.ajax({
        url: '/modify/singleHistory?state=' + state + '&id=' + id,
        type: 'get',
        //data:{state:state,id:id},
        success: function (data) {
            if (data.error == 0) {
                view_message = data.entry;
                fill(view_message);
            } else {
                alert('服务器响应内容中有错误:' + data.message);
            }
            var testEditormdView;
            var markdown_data = view_message.content;
            alert(markdown_data);
            testEditormdView = editormd.markdownToHTML("test-editormd-view", {
                markdown: markdown_data,//+ "\r\n" + $("#append-test").text(),
                //htmlDecode      : true,       // 开启 HTML 标签解析，为了安全性，默认不开启
                htmlDecode: "style,script,iframe",  // you can filter tags decode
                //toc             : false,
                tocm: true,    // Using [TOCM]
                tocContainer: "#left_part", // 自定义 ToC 容器层
                //gfm             : false,
                //tocDropdown     : true,
                // markdownSourceCode : true, // 是否保留 Markdown 源码，即是否删除保存源码的 Textarea 标签
                emoji: true,
                taskList: true,
                tex: true,  // 默认不解析
                flowChart: true,  // 默认不解析
                sequenceDiagram: true  // 默认不解析
            });
            var tags = document.getElementById("tags");
            for (var tagid in view_message.tags) {
                var tag = document.createElement("span");
                tag.className = "label label-primary";
                tag.innerHTML = view_message.tags[tagid];
                tag.setAttribute("style", "margin-right: 5px;");
                tags.appendChild(tag);
            }
        },
        error: function (data) {
            alert('error');
        }
    });
}

function fill(data) {
    var title = document.getElementById("article_title");
    title.innerHTML = data.title;
    var update_time = document.getElementById("last_update_time");
    update_time.innerHTML = data.timestamp;
}