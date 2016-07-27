/**
 * Created by duanzhengmou on 7/14/16.
 */





/**
 * 加载页面时请求并加载词条内容
 * @params title
 */
function load_content(title){
    show_tips(0,'内容加载中,请稍候');
    var url = '/content/wiki/'+title;
    // alert('url'+url);
    $.ajax({
        url:url,
        type:'get',
        // data:{title:title},
        success:function (data) {
            show_tips(1,'内容加载完毕');
            // alert(JSON.stringify(data));
            var markdown_data;
            if (data.data) {
                markdown_data = data.data.content;
                var converter = new showdown.Converter(),
                    // text      = '#hello, markdown!\n\n|name|age|\n|--|--|\n|Toast|20|',
                    text      = markdown_data,
                    html      = converter.makeHtml(text);
                // alert(data.visits);
                document.getElementById('access_times').innerHTML+=data.data.visits;
                document.getElementById('last_update_time').innerHTML+=data.data.date;
            } else {
                markdown_data = '当前不存在条目*' + title + '* 的详细内容。你可以[创建这个条目](http://localhost:8080/html/Entry_editor.html?title=' + title + ')。' +
                                '或者你也[请求新增这个条目](/message/wiki?title=' + title + ')。 你也可以将这个条目创建为已存在条目的别名)。';
                document.getElementById('drop_menu').removeChild(document.getElementById('edit_old').parentNode);
                document.getElementById('drop_menu').removeChild(document.getElementById('delete_entry').parentNode);
            
            }

            // alert(html);
                //----------以下是内容的渲染-----------------
                var testEditormdView;

                var markdown = html;
                testEditormdView = editormd.markdownToHTML("test-editormd-view", {
                    markdown        : markdown_data ,//+ "\r\n" + $("#append-test").text(),
                    //htmlDecode      : true,       // 开启 HTML 标签解析，为了安全性，默认不开启
                    htmlDecode      : "style,script,iframe",  // you can filter tags decode
                    //toc             : false,
                    tocm            : true,    // Using [TOCM]
                    tocContainer    : "#left_part", // 自定义 ToC 容器层
                    //gfm             : false,
                    //tocDropdown     : true,
                    // markdownSourceCode : true, // 是否保留 Markdown 源码，即是否删除保存源码的 Textarea 标签
                    emoji           : true,
                    taskList        : true,
                    tex             : true,  // 默认不解析
                    flowChart       : true,  // 默认不解析
                    sequenceDiagram : true  // 默认不解析
                });

            var display_panel = document.getElementById('reference');
            var tmp;
            
            try{ 
                tmp = data.reference.dao;
            }
            catch (e) {
                tmp = "";
            }
            if(tmp.length == 0){
                display_panel.innerHTML = "<p>无</p>";
            }
            else{
                for(var i in tmp){
                    var name = tmp[i].name;
                    var url = tmp[i].url;
                    var link = document.createElement('a');
                    link.href = url;
                    link.innerHTML = name;
                    
                    var br = document.createElement('br');
                    display_panel.appendChild(link);
                    display_panel.appendChild(br);
                }
            }
        },
        error:function (data) {
            alert("error"+JSON.stringify(data));
        }
    });
}

/**
 * 读取搜索框内的内容并跳转到搜索结果页面
 */
function search_entry() {
    var keyword = document.getElementById('search_input').value;
    if(keyword=='')return;
    var url = '../html/search_result_page.html?entry='+keyword;
    location.href = url;
}

function init_content(){
    document.getElementById('search_input').addEventListener('keydown',function (e) {
        if(e.keyCode == 13){
            search_entry();
        }
    });
    document.getElementById('search_btn').addEventListener('click',function () {
        search_entry();
    })
}

function delete_entry() {
    var title = location.href.split('?')[1].split('=')[1];
    var url = '/content/wiki/'+title;
    $.ajax({
        url:url,
        type:'delete',
        success:function (data) {
            if(data.error == 0){
                alert('delete [success]');
                location.href = '../html/Outline.html';
            }else{
                alert('delete[fail] and reason is '+data.message);
            }
            
        },
        error:function (data) {
            alert('error:'+JSON.stringify(data));
        }
    });
}

