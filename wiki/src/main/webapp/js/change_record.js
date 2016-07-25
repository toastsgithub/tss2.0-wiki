/**
 * Created by wh on 2016/7/25.
 */
function getRecord(){
    $.ajax({
        url:'/modify/list',
        type:'get',
        success:function (data) {
            if (data.error==0){
                var ID = 0;
                for (x in data.data){
                    if(data.data[x].isread == 0){
                        ID++;
                    }
                }
                document.getElementById('message_num').innerHTML = ID;
            }else{
                alert('http返回的内容存在错误');
            }
        },
        error:function(){
            alert('error');
        }
    });


}
