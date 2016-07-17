/**
 * Created by duanzhengmou on 7/14/16.
 */

/**
 * 加载页面时请求并加载词条内容
 * @params title
 */
function load_content(title){
    var url = '/content/wiki/'+title;
    // alert('url'+url);
    $.ajax({
        url:url,
        type:'get',
        // data:{title:title},
        success:function (data) {
            // alert(JSON.stringify(data));
            var markdown_data = data.data.content;
            var converter = new showdown.Converter(),
                // text      = '#hello, markdown!\n\n|name|age|\n|--|--|\n|Toast|20|',
                text      = markdown_data,
                html      = converter.makeHtml(text);
            // alert(data.visits);
            document.getElementById('access_times').innerHTML+=data.data.visits;
            document.getElementById('last_update_time').innerHTML+=data.data.date;
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
            
        },
        error:function (data) {
            alert("error"+JSON.stringify(data));
        }
    });
}


