/**
 * Created by duanzhengmou on 7/23/16.
 * Copyright © 2016 duanzhengmou. All rights reserved.
 */
var all_message;

function init_message_table(){
    var table = $('#message_table').DataTable({
        data:null,
        columns:[
            {data:'title'},
            {data:'fromUser'}
        ],
        columnDefs:[{
            'targets':[0],
            // 'data':'消息标题',
            'render':function (data,type,full) {
                // return 'ascii';
                return '<a href=\"http://www.baidu.com\">'+data+'</a>';
            }
        }]
    });

    load_message();
    //以下是行选择的事件
    $('#message_table tbody').on( 'click', 'tr', function () {
        table.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        var selected_row = table.row('.selected').index();
        var value = table.cell(selected_row,0).data();
        pop_test(value);
        // var url = "/html/duck_stockDetail.html"+"?code="+value;
        // window.location.href = url;
//            showStockChart(value);
//                }
    } );
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
                alert('服务器响应内容中有错误');
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

function pop_test(title) {
    var content = '';
    var user = '';
    for (x in all_message){
        if(all_message[x].title == title){
            content = all_message[x].detail;
            user = all_message[x].fromUser;
            break;
        }
    }
    document.getElementById('message_title').innerHTML = title;
    document.getElementById('message_content').innerHTML = content;
    document.getElementById('from_user').innerHTML = user;
    $('#pop_test').modal();
}