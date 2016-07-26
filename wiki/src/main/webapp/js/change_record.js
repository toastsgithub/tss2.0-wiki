/**
 * Created by wh on 2016/7/25.
 */
var all_message;
function getRecord(){
    var table = $('#message_table').DataTable({
        data:null,
        columns:[
            {data:'title'},
            {data:'state'},
            {data:'timestamp'}
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
                    return '<a href="http://www.baidu.com">百度一下</a>'
                }
            }]
    });
load_message();
}

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
    var url = '../html/search_result_page.html?entry='+keyword;
    location.href = url;
}