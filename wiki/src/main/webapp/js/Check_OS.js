/**
 * Created by duanzhengmou on 7/12/16.
 */
function check_os() {
    alert("??");
//        alert(navigator.userAgent.indexOf("Mac"));
    var windows = (navigator.userAgent.indexOf("Windows",0) != -1)?1:0;
    var mac = (navigator.userAgent.indexOf("Mac",0) != -1)?1:0;
    var linux = (navigator.userAgent.indexOf("Linux",0) != -1)?1:0;
    var unix = (navigator.userAgent.indexOf("X11",0) != -1)?1:0;

    if (mac) alert("⚠️尊敬的用户您好,检测到您正在使用 windows 登陆我们的页面,我们的页面即将拒绝支持windows,如果希望继续使用,请更换mac 或 linux登陆我们的系统\nDear user, we detected that you are still using Microsoft Windows to access our page, Unfortunately, we are not going to support this OS anymore,you should change a OS if you wish to access our page in the future");
    else if (mac) alert("👽welcome Mac user");
    else if (linux) alert("welcome linux user");
    else if (unix) alert("welcome unix");

//        return os_type;
}
