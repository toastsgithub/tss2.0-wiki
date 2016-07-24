/**
 * Created by zhengyunzhi on 16/7/24.
 */



function load_entry_data(){
   var title = window.location.search.split("&")[1].split("=")[1];
    $.ajax({
        url:'/content/wiki/'+title,
        type: 'get',
        data: null,
        success: function (data) {
            if(data.exist == 0) alert("the entry does not exist!");
            var alias = data.data.aliasByTitle;// may be null
            var category = data.data.categories;
            var tags = data.data.tags; // [""]??
            var summary = data.data.summary; //
            var contennt = data.data.content;
            var reference = data.data.reference.dao;
        },
        error: function (data) {
            alert("error");
        }
    });
}

function asd(title, alias, category, tags, summary, content, reference){}
