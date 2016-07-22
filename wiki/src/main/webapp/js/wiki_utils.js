/**
 * Created by duanzhengmou on 7/12/16.
 * 所有页面公用的工具
 */


/**
 * description:   disable一个元素
 * params:        要disable的元素的id
 * reuturn: none
 */
function make_disable(element_id) {
    
    document.getElementById(element_id).setAttribute('disabled',"disabled");
}
/**
 * description:   移除一个元素的disabled属性
 * params:        要移除disable属性的元素的id
 * return: none
 */
function remove_disable(element_id) {
    document.getElementById(element_id).removeAttribute('disabled');
}

/**
 * description:   获取当前登录的用户,没有用户登录则返回null
 * params: none
 * return
 */
function get_user() {
    var username = 'nobody';
    $.ajax({
        url:'/user/info',
        async:false,
        type:'get',
        success:function (data) {
            alert('got:'+JSON.stringify(data));
            var status = data.login;
            if(status==1){
                username = data.data.username;
            }else{
                username = null;
            }
        },
        error:function (data) {
            alert('error');
            username = null;
        }
    });
    return username;
}
/**
 * 右下角显示消息提示的方法
 * @param type 消息提示的基本类型 0为警告类消息提示  1为成功类消息提示
 * @param message 消息提示内容
 */
function show_tips(type,message) {
    if(type) {
        Messenger().post({
            message:message,
            type:'success'
        });
    }else{
        Messenger().post({
            message:message,
            type:'error'
        });
    }
}
/**
 * 获取当前日期和时间的方法 待扩充
 */
function get_current_time() {
    var date = new Date();
    // alert(date.getDate());
    var time = date.getHours() +":"+ date.getMinutes()+":"+date.getSeconds();
    // alert(time);
    return time;
}