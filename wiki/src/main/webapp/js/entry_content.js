/**
 * Created by duanzhengmou on 7/14/16.
 */

/**
 * 加载页面时请求并加载词条内容
 * @params title
 */
function load_content(title){
    // alert("we are going  to load:"+title);
    $.ajax({
        url:'/content',
        type:'get',
        data:{title:title},
        success:function (data) {
            alert(JSON.stringify(data));
            var markdown_data = data.data.content;
            var converter = new showdown.Converter(),
                // text      = '#hello, markdown!\n\n|name|age|\n|--|--|\n|Toast|20|',
                text      = markdown_data,
                html      = converter.makeHtml(text);
            // alert(html);
            document.getElementById('right_part').innerHTML = html;
        },
        error:function (data) {
            alert("error");
        }
    });
}
