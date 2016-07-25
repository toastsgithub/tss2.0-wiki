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
            var summary = data.data.summery; //
            var content = data.data.content;
            var reference = data.reference.dao;
            var title_input = decodeURI(title);
            alert("title = " + title_input);
            $("#name_input").val(title_input);
            subimit_id = data.data.id;
            if(alias==[]){
                //do nothing
            }else{
                for(var i in alias){
                    add_alias(alias[i]);
                }
            }
            if(tags == []){
                //do nothing
            }else{
                for(var i in tags){
                    add_tags(tags[i]);
                }
            }
            $("#category_input").val(category[0]);
            $("#summary").val(summary);
            $("#wiki_editor").html(content);
            if(reference == []){
                // do nothing
            }else{
                for(var i in reference){
                    add_reference_input_area();
                    $("#reference_name_"+i).val(reference[i].name);
                    $("#reference_url_"+i).val(reference[i].url);
                }
            }
        },
        error: function (data) {
            alert("error");
        }
    });
}


