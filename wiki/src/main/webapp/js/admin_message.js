/**
 * Created by duanzhengmou on 7/23/16.
 * Copyright © 2016 duanzhengmou. All rights reserved.
 */
var all_message;



function init_message_table(){
    var table = $('#message_table').DataTable({
        data:null,
        order:[[2,'desc']],
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
        var id = table.cell(selected_row,4).data();
        table.cell(selected_row,0).data(1).draw();
        read_msg(id);
        pop_test(id);
        
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
            var table = $('#message_table').DataTable();
            table.draw();
            load_message_num();
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
    // table.rows.add( [ {
    //         "title":       "Tiger Nixon",
    //         "source":   "System Architect",
    //         "salary":     "$3,120",
    //         "start_date": "2011/04/25",
    //         "office":     "Edinburgh",
    //         "extn":       "5421"
    //     }, {
    //         "sdsad": "Garrett Winters",
    //         "title": "Director",
    //         "source": "$5,300",
    //         "start_date": "2011/07/25",
    //         "office": "Edinburgh",
    //         "extn": "8422"
    //     } ] )
    //     .draw();
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

function test_data_set() {
    var table = $('#message_table').DataTable();
    table.cell(0,0).data('??').draw();
    // alert(JSON.stringify(table.cell(0,0)));
}