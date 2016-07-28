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

                document.getElementById('category').innerHTML+=data.data.categories;
                // document.getElementById('tags').innerHTML+=data.data.tags;
                //加载tag
                for(x in data.data.tags){
                // <span class="label label-primary">Primary</span>
                    var new_tag = document.createElement('span');
                    new_tag.className = 'label label-primary';
                    new_tag.innerHTML = data.data.tags[x];
                    new_tag.style.marginRight = '5px';
                    document.getElementById('tags').appendChild(new_tag);
                }
                document.getElementById('last_editor').innerHTML+=data.data.editor;
                // load the question
                
                load_relative_question_and_ppt(data.data.id);
       
                var question = document.createElement('div');
            // <div style="margin-left: 20px">
            //         <div id="question_title">
            //     <h3>对此条目仍有问题?</h3>
            //     </div>
            //     <div id="question"></div>
            //         </div>


                $.ajax({
                    url: 'http://110.173.17.140:8080/api/ask?wikiId='+data.data.id,
                    type: 'get',
                    success: function (data0) {
                        // alert("data = " + data0);
                        question.style.marginLeft = "20px";
                        question.innerHTML = "<div id='question_title' style='margin-top: 30px;'><h4>对此条目仍有疑问?</h4></div><div id='question'><a href = 'http://" + data0+ "'>去这里提问</a></div>"
                        document.getElementById('right_part').appendChild(question);
                    },
                    error: function (data) {
                        alert('error');
                    }
                })
                
            } else {
                markdown_data = '当前不存在条目*' + title + '* 的详细内容。你可以[创建这个条目](http://localhost:8080/html/Entry_editor.html?title=' + title + ')。' +
                                '或者你也[请求新增这个条目](/message/wiki?title=' + title + ')。 你也可以将这个条目创建为已存在条目的别名。';
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

function load_relative_question_and_ppt(wiki_id) {
    wiki_id = 177;
    var url = 'http://110.173.17.140:8080/api/relation/wiki/'+wiki_id;
    // alert(url);
    $.ajax({
        url:url,
        type:'get',
        // dataType:'JPSON',
        success:function (data) {
            // alert('mix data:'+JSON.stringify(data));
            // alert('question:'+data.questionIds);
            // alert('[0]:'+data.questionIds[0]);
            // alert('length'+data.questionIds);
            for (var x=0;x<data.questionIds.length;x++){
                // alert('he'+data.questionIds[x]);
                load_question(data.questionIds[x]);
            }
            // for(n in data.qustionIds){
            //     alert('load item:');
            //     // load_question(data.questionIds[n]);    
            // }
            
            // alert(JSON.stringify(data));
        },
        error:function (data) {
            alert(JSON.stringify(data));
            alert('error');
        }
    });
}

function load_question(question_id) {
    var url = 'http://110.173.17.140:8080/api/question?id='+question_id;
    // alert('question '+url);
    $.ajax({
        url:url,
        type:'get',
        // dataType:'JPSON',
        success:function (data) {
            
            // alert(JSON.stringify(data));
            create_question_item(data);
        },
        error:function (data) {
            alert('error '+JSON.stringify(data));
        }
    });
}

function create_question_item(data) {
    // alert('data :'+JSON.stringify(data));
    var question_item = document.createElement('button');
    // question_item.style.border = 'solid 1px black';
    // question_item.href = data.questionUrl;
    // question_item.onclick = 'location.href="'+data+'"';
    // question_item.onclick = function () {
    //     alert('click');
    // }
    question_item.addEventListener('click',function () {
       window.location.href = 'http://'+data.questionUrl; 
    });
    question_item.className = 'list-group-item';
    var media = document.createElement('div');
    media.className = 'media';
    var media_body = document.createElement('div');
    media_body.className = 'media-body';
    var title = document.createElement('h4');
    title.className = 'media-heading';
    title.innerHTML = data.title;
    var questioner = document.createElement('div');
    var questioner_icon = document.createElement('span');
    questioner_icon.className = 'glyphicon glyphicon-user';
    var questioner_value = document.createElement('span');
    questioner_value.innerHTML = data.userName;
    var question_time = document.createElement('div');
    var question_time_icon = document.createElement('span');
    question_time_icon.className = 'glyphicon glyphicon-time';
    var question_time_value = document.createElement('span');
    question_time_value.innerHTML = data.createAt;
    var content_icon = document.createElement('span');
    content_icon.className = 'glyphicon glyphicon-file';
    var content_value = document.createElement('span');
    content_value.innerHTML = data.content;
    var content = document.createElement('div');
    

    document.getElementById('question_panel').appendChild(question_item);
    question_item.appendChild(media);
    media.appendChild(media_body);
    questioner.appendChild(questioner_icon);
    questioner.appendChild(questioner_value);
    question_time.appendChild(question_time_icon);
    question_time.appendChild(question_time_value);
    content.appendChild(content_icon);
    content.appendChild(content_value);
    media_body.appendChild(title);
    media_body.appendChild(questioner);
    media_body.appendChild(question_time);

    media_body.appendChild(content);
    
}