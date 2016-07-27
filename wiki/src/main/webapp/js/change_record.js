/**
 * Created by wh on 2016/7/25.
 */
var all_message;
var view_message;
/**
 * 显示数据到页面中
 * */
function getRecord(){
    var table = $('#message_table').DataTable({
        data:null,
        columns:[
            {data:'title'},
            {data:'state'},
            {data:'timestamp'},
            {data:'wikiId'}
            
        ],
        columnDefs:[{
            'targets':[1],
            // 'data':'状态',
            'render':function (data,type,full) {
                // return 'ascii';
                // alert(data+" "+type+" "+JSON.stringify(full));
                if(data == 0) {
                    //待审核
                    return '<span>待审核</span>';
                }else if(data == 1){
                    //已拒绝
                    return '<span>修改失败</span>';
                }
                else{
                    //已添加
                    return '<span>修改成功</span>';
                }
            }
        },
            {
                'targets':[0],
                'render':function (data,type,full) {
                    return '<a href="../html/historyContent.html">'+data+'</a>'
                       
                }
            }]
    });
    $('#message_table tbody').on( 'click', 'tr', function () {
        table.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        var selected_row = table.row('.selected').index();
        // document.getElementById('test_part').innerHTML = JSON.stringify(test_cache);


        if (selected_row == undefined) return ;
       var state = table.cell(selected_row,1).data();
        var id = table.cell(selected_row,3).data();
        showSearchResult(state,id);
    } );

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
    if(keyword=='')return;
    var url = '../html/search_result_page.html?entry='+data;
    location.href = url;
}

/**
 * 根据页面点击从后端获取数据
 * @param state: 每条信息的状态
 * @param id: 每条信息的wikiId
 * */
function showSearchResult(state, id){
    $.ajax({
        url: '/modify/singleHistory',
        type: 'get',
        data:{state:state,id:id},
        success: function (data) {
            if (data.error == 0) {
                view_message = data.entry;

            } else {
                alert('服务器响应内容中有错误:' + data.message);
            }
        },
        error: function (data) {
            alert('error');
        }
    });
}