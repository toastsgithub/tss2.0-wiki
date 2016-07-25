/**
 * Created by duanzhengmou on 7/23/16.
 * Copyright © 2016 duanzhengmou. All rights reserved.
 */
function init_admin_common(){
    load_message_num();
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