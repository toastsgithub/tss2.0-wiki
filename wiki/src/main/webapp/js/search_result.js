/**
 * Created by duanzhengmou on 7/19/16.
 */

/**
 * (模糊)搜索词条结果,并在页面加载词条列表
 * @param keyword
 */
function search_and_load(keyword) {
    show_tips(1,'searching result...');
    // for (var n=0;n<10;n++){
    //     load_on_search_page("title",'sumary--------------------------------------------------------------------');
    // }
    $.ajax({
        url:'/content/fuzzysearch',
        type:'get',
        data:{search:keyword},
        success:function (data) {
            
            if(data.exist==0){
                document.getElementById('main_content').innerHTML = 'sorry we found 0 result match';
            }else{
                for (x in data.list){
                    var title = data.list[x].title;
                    var summary = data.list[x].summery;
                    load_on_search_page(title,summary);
                    
                }
                show_tips(1,"load finished at "+get_current_time());
            }
        },
        error:function (data) {
            show_tips(0,'some thing goes wrong');

        }
    });
}

function load_on_search_page(title,summary) {
    // show_tips(1,title+" "+summary);
    var single_entry = document.createElement('div');
    single_entry.style.maxHeight = '80px';
    single_entry.style.width = '100%';
    // single_entry.style.backgroundColor = 'blue';
    // single_entry.style.backgroundColor = 'chocolate';
    single_entry.style.marginTop = '20px';
    var entry_title = document.createElement('a');
    entry_title.href = '../html/entry_content.html?entry='+title;
    entry_title.innerHTML = title;
    entry_title.style.fontSize = '25px';
    
    var entry_summary = document.createElement('div');
    entry_summary.innerHTML = summary;
    entry_summary.style.color = 'rgb(151,151,151)';
    // entry_summary.style.backgroundColor ='red';
    entry_summary.style.maxHeight = '60px';
    entry_summary.style.overflow ='hidden';
    entry_summary.style.textOverflow = 'ellipsis';
    single_entry.appendChild(entry_title);
    single_entry.appendChild(entry_summary);
    
    document.getElementById('main_content').appendChild(single_entry);
}
/**
 * 从编辑框读取内容并跳入相关搜索页面
 */
function search_on_input() {
    var keyword_input = document.getElementById('search_input').value;
    // alert(keyword);
    if(keyword_input=='')return;
    var url = '../html/search_result_page.html?entry='+keyword_input;
    // alert(url);
    location.href = url;
}