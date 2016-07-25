/**
 * Created by zhengyunzhi on 16/7/24.
 */



function get_entry_data(){
   var title = window.location.search.split("&")[1].split("=")[1];
    $.ajax({
        url:'/content/wiki/'+title,
        type: 'get',
        data: null,
        async: false,
        success: function (data) {
            if(data.exist == 0){
                alert("the entry does not exist!");
                return;
            }
            var alias = data.data.aliasByTitle;// may be null
            var category = data.data.categories;
            var tags = data.data.tags; // [""]??
            var summary = data.data.summary; //
            var content = data.data.content;
            var reference = data.reference.dao;
            var title_input = decodeURI(title);
            alert("title = " + title_input);
            $("#name_input").val(title_input);
            if(alias==[]){
                //do nothing
            }else{
                
            }
            $("#category_input").val(category[0]);
            // if(tags==[])
        },
        error: function (data) {
            alert("error");
        }
    });
}


