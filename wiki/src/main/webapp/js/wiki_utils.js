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
    $.ajax({
        url:'/user/info',
        type:'post',
        success:function (data) {
            if(data.login){
                return data.data;
            }else{
                return null
            }
        },
        error:function (data) {
            // alert('error');
            return null
        }
    });
}