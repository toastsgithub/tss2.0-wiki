/**
 * Created by duanzhengmou on 7/23/16.
 * Copyright © 2016 duanzhengmou. All rights reserved.
 */
var all_message;

function init_message_table(){
    var table = $('#message_table').DataTable({
        data:null,
        order:[[2,'desc']],
        dom: 'rtip',
        columns:[
            {data:'isread'},
            {data:'title'},
            {data:'timestamp'},
            {data:'fromUser'},
            {data:'messageID'}
        ],
        columnDefs:[{
            'targets':[0],
            // 'data':'消息标题',
            'render':function (data,type,full) {
                // return 'ascii';
                // alert(data+" "+type+" "+JSON.stringify(full));
                if(data == 1) {
                    //已读
                    return '<span class=\"glyphicon glyphicon-comment\" style=\"\"></span><span>已读</span>';
                }else{
                    //未读
                    return '<span class=\"glyphicon glyphicon-comment\" style=\"color: #3b81c7\"></span><span>未读</span>';
                }
            }
        }]
    });
    //以下关于自定义搜索框
    $('#search_table_input').on('keyup',function () {
        table.search(this.value).draw();
    });
    load_message();
    // hide id column
    table.column(4).visible(false);
    //以下是行选择的事件
    $('#message_table tbody').on( 'click', 'tr', function () {
        table.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        var selected_row = table.row('.selected').index();
        // document.getElementById('test_part').innerHTML = JSON.stringify(test_cache);


        if (selected_row == undefined) return ;
        var value = table.cell(selected_row,1).data();
        table.cell(selected_row,0).data(1).draw();
        var id = table.cell(selected_row,4).data();
        read_msg(id);
        pop_test(id);
        // var url = "/html/duck_stockDetail.html"+"?code="+value;
        // window.location.href = url;
//            showStockChart(value);
//                }
    } );
}

function read_msg(id) {
    // alert('reading:'+id);
    var url = '/message/'+id;
    $.ajax({
        url:url,
        type:'get',
        success:function (data) {
            // alert('bingo');
        },
        error:function (data) {
            alert("errorrrrrrr");
        }
    });
}
function load_message() {
    var table = $('#message_table').DataTable();
    $.ajax({
        url:'/message',
        type:'get',
        success:function(data){
            if(data.error==0){
                all_message = data.data;
                table.rows.add(data.data).draw();
            }else{
                alert('服务器响应内容中有错误:'+data.message);
            }
        },
        error:function (data) {
            alert('error');
        }
    });
 
}

function pop_test(id) {
    var content = '';
    var user = '';
    var title = '';
    for (x in all_message){
        if(all_message[x].messageID == id){
            content = all_message[x].detail;
            user = all_message[x].fromUser;
            title = all_message[x].title;
            break;
        }
    }
    document.getElementById('message_title').innerHTML = title;
    document.getElementById('message_content').innerHTML = content;
    document.getElementById('from_user').innerHTML = user;
    $('#pop_test').modal();
}

function load_message_num() {
    //todo 后期改为只加载未读取的数目,并考虑用新接口只返回未读取的数目以提高性能
    $.ajax({
        url:'/message',
        type:'get',
        success:function (data) {
            if (data.error==0){
                var unread_num = 0;
                for (x in data.data){
                    if(data.data[x].isread == 0){
                        unread_num++;
                    }
                }
                document.getElementById('message_num').innerHTML = unread_num;
            }else{
                alert('http返回的内容存在错误');
            }
        },
        error:function(){
            alert('error');
        }
    });
}