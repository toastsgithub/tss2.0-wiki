/**
 * Created by wh on 2016/7/25.
 */
function clearLogin(){
    $.ajax({
        url: '/user/logout',
        type: 'get',
        async: false,
        success: function (data) {},
        error: function (data) {
            alert('error!');
        }
    });
    deleteCookie("login");
    deleteCookie("username");


}
