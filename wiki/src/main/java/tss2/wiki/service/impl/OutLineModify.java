package tss2.wiki.service.impl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import tss2.wiki.service.OutLineModifyService;

/**
 * Created by Administrator on 2016/7/9.
 */

public class OutLineModify implements OutLineModifyService{
    //{"ruanjian":[{"a":[{"e":["q"]},"f"]},{"b":["h"]},{"c":["i"]},{"d":["j","k"]},"m"]}
    private static String JsonString = "{\"ruanjian\":[{\"a\":[{\"e\":[\"q\"]},\"f\"]},{\"b\":[\"h\"]},{\"c\":[\"i\"]},{\"d\":[\"j\",\"k\"]},\"m\"]}";

    public static void main(String[] args){
        OutLineModify tree =new OutLineModify();
        System.out.println(tree.add(JsonString, "a", "l"));
    }

    /**
     *
     * @param father 所需要添加的大纲节点所在地的父节点
     * @param addString 所需要添加的大纲节点
     * @return 修改后大纲的json格式String
     */
    public String add(String father,String addString){
        return this.add(JsonString,father,addString);
    }

    /**
     *
     * @param father 所需要删除的大纲节点所在地的父节点
     * @param deleteString 所需要删除的大纲节点
     * @return 修改后大纲的json格式String
     */
    public String delete(String father,String deleteString){
        return this.delete(JsonString,father,deleteString);
    }

    /**
     *
     * @param father 所需要修改的大纲节点所在地的父节点
     * @param beforeChange 修改前的节点
     * @param afterChange 修改后的节点
     * @return 修改后大纲的json格式String
     */
    public String modify(String father, String beforeChange, String afterChange){
        return this.modify(JsonString,father,beforeChange,afterChange);
    }

    /**
     *
     * @param jsonString 表示大纲的json格式String
     * @param father 所需要添加的大纲节点所在地的父节点
     * @param addString 所需要添加的大纲节点
     * @return 修改后大纲的json格式String
     */
    private String add(String jsonString,String father,String addString){
        JSONObject jsonObject;
        jsonObject = JSONObject.fromObject(jsonString);
        String[] arr = jsonString.split(":");
        if(arr.length>2){
            String temp = arr[0].substring(2, arr[0].length()-1);
            System.out.println(temp);
            if(temp.equals(father)){
                JSONArray jsonarray = JSONArray.fromObject(jsonObject.get(temp));
                jsonarray.add(addString);
                //System.out.println(jsonarray);
                int l = jsonarray.size();
                for(int i = 0;i<l;i++){
                    System.out.println(jsonarray.get(i).toString());
                }
                String result = "{\""+temp+"\":"+jsonarray.toString()+"}";
                //System.out.println(result);
                return result;
            }else{
                JSONArray jsonarray = JSONArray.fromObject(jsonObject.get(temp));
                int l = jsonarray.size();
                String two = "";
                String result = "";
                for(int i = 0;i<l;i++){
                    try{
                        jsonObject = JSONObject.fromObject(jsonarray.getString(i));
                        //System.out.println(jsonarray.getString(i));
                        two = this.add(jsonObject.toString(), father, addString);
                        if(result == ""){
                            if(two == ""){
                            }else{
                                result = result+two;
                            }
                        }else{
                            result = result+","+two;
                        }
                        //System.out.println(result);
                    }catch(Exception e){
                        System.out.println(jsonarray.getString(i));
                        //System.out.println(jsonarray);
                        if(result == ""){
                            result= result+jsonarray.getString(i);
                        }else{
                            result = result+","+"\""+jsonarray.getString(i)+"\"";
                        }
                        //System.out.println(result);
                    }
                }
                result = "{\""+temp+"\":["+result+"]}";
                //System.out.println(result);
                return result;
            }
        }else if(arr.length==2){
            String temp = arr[0].substring(2, arr[0].length()-1);
            System.out.println(temp);
            if(temp.equals(father)){
                JSONArray jsonarray = JSONArray.fromObject(jsonObject.get(temp));
                jsonarray.add(addString);
                int l = jsonarray.size();
                for(int i = 0;i<l;i++){
                    System.out.println(jsonarray.get(i).toString());
                }
                String result = "{\""+temp+"\":"+jsonarray.toString()+"}";
                //System.out.println(result);
                return result;
            }else{
                JSONArray jsonarray = JSONArray.fromObject(jsonObject.get(temp));
                int l = jsonarray.size();
                for(int i = 0;i<l;i++){
                    System.out.println(jsonarray.get(i).toString());
                }
            }
            //System.out.println(jsonString);
            return jsonString;
        }
        return "";
    }

    private String delete(String jsonString,String father,String addString){
        return null;
    }

    private String modify(String jsonString,String father,String beforeChange, String afterChange){
        return null;
    }
}

