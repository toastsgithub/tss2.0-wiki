/**
 * Created by duanzhengmou on 7/12/16.
 * 所有页面公用的工具
 */


/**
 * description:   disable一个元素
 * params:        要disable的元素的id
 */
function make_disable(element_id) {
    
    document.getElementById(element_id).setAttribute('disabled',"disabled");
}
/**
 * description:   移除一个元素的disabled属性
 * params:        要移除disable属性的元素的id
 */
function remove_disable(element_id) {
    document.getElementById(element_id).removeAttribute('disabled');
}
