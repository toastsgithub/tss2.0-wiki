package tss2.wiki.control.impl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import tss2.wiki.control.service.OutLineModifyService;
import tss2.wiki.model.WikiSunmary;

/**
 * Created by Administrator on 2016/7/9.
 */

public class OutLineModifyImpl implements OutLineModifyService{
    //{"ruanjian":[{"a":[{"e":["q"]},"f"]},{"b":["h"]},{"c":["i"]},{"d":["j","k"]},"m"]}
    private static String JsonString = "{\"ruanjian\":[{\"a\":[{\"e\":[\"q\"]},\"f\"]},{\"b\":[\"h\"]},{\"c\":[\"i\"]},{\"d\":[\"j\",\"k\"]},\"m\"]}";
    private WikiSunmary wikiSunmary = new WikiSunmary();

    public static void main(String[] args){
        OutLineModifyService tree =new OutLineModifyImpl();
        System.out.println(tree.add("考拉", "考拉爸爸"));
    }

    /**
     *
     * @param father 所需要添加的大纲节点所在地的父节点
     * @param addString 所需要添加的大纲节点
     * @return 修改后大纲的json格式String
     */
    public String add(String father,String addString){
        String jsonString = wikiSunmary.getSummary();
        String result = this.add(jsonString,father,addString);
        //wikiSunmary.setSummary(result);
        return result;
    }

    /**
     *
     * @param father 所需要删除的大纲节点所在地的父节点
     * @param deleteString 所需要删除的大纲节点
     * @return 修改后大纲的json格式String
     */
    public String delete(String father,String deleteString){
        String jsonString = wikiSunmary.getSummary();
        String result = this.delete(JsonString,father,deleteString);
        wikiSunmary.setSummary(result);
        return result;
    }

    /**
     *
     * @param father 所需要修改的大纲节点所在地的父节点
     * @param beforeChange 修改前的节点
     * @param afterChange 修改后的节点
     * @return 修改后大纲的json格式String
     */
    public String modify(String father, String beforeChange, String afterChange){
        String jsonString = wikiSunmary.getSummary();
        String result = this.modify(JsonString,father,beforeChange,afterChange);
        wikiSunmary.setSummary(result);
        return result;
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
                String nothing;
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
                    //System.out.println(jsonarray.getString(i));
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
                        System.out.println(i);
                        if(jsonarray.getString(i).equals(father)){
                            JSONObject a = new JSONObject();
                            String[] t = new String[1];
                            t[0] = addString;
                            a.accumulate(father,t);
                            //jsonarray.add(i-1,a);
                            jsonarray.add(a);
                            jsonarray.remove(i);
                            System.out.println(jsonarray);
                            System.out.println(i);
                            System.out.println(result);
                            //jsonObject.replace(temp,jsonarray);

                            for(int j = i; j < l; j++){
                                if(result == ""){
                                    try{
                                        JSONObject Object = JSONObject.fromObject(jsonarray.getString(j));
                                        result= result+jsonarray.getString(j);
                                    }catch(Exception el){
                                        result = result+"\""+jsonarray.getString(j)+"\"";
                                    }
                                }else{
                                    try{
                                        JSONObject Object = JSONObject.fromObject(jsonarray.getString(j));
                                        result= result+","+jsonarray.getString(j);
                                    }catch(Exception el){
                                        result = result+","+"\""+jsonarray.getString(j)+"\"";
                                    }
                                    //result = result+","+"\""+jsonarray.getString(i)+"\"";
                                    //result = result+","+jsonarray.getString(j);
                                }

                            }
                            break;
                        }else{
                            if(result == ""){
                                try{
                                    JSONObject Object = JSONObject.fromObject(jsonarray.getString(i));
                                    result= result+jsonarray.getString(i);
                                }catch(Exception e1){
                                    result= result+"\""+jsonarray.getString(i)+"\"";
                                }

                            }else{
                                result = result+","+"\""+jsonarray.getString(i)+"\"";
                            }
                        }
                        //System.out.println(jsonarray);

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
                    if(jsonarray.get(i).toString().equals(father)){
                        JSONObject a = new JSONObject();
                        String[] t = new String[1];
                        t[0] = addString;
                        a.accumulate(father,t);
                        jsonarray.add(a);
                        jsonarray.remove(i);
                        jsonObject.replace(temp,jsonarray);
                    }
                }
            }

            //System.out.println(jsonObject);
            return jsonObject.toString();
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

