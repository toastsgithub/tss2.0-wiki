package tss2.wiki.control.service;

/**
 * Created by Administrator on 2016/7/9.
 */
public interface OutLineModifyService {

    /**
     *
     * @param father 所需要添加的大纲节点所在地的父节点
     * @param addString 所需要添加的大纲节点
     * @return 修改后大纲的json格式String
     */
    public String add(String father,String addString);

    /**
     *
     * @param father 所需要删除的大纲节点所在地的父节点
     * @param deleteString 所需要删除的大纲节点
     * @return 修改后大纲的json格式String
     */
    public String delete(String father,String deleteString);

    /**
     *
     * @param father 所需要修改的大纲节点所在地的父节点
     * @param beforeChange 修改前的节点
     * @param afterChange 修改后的节点
     * @return 修改后大纲的json格式String
     */
    public String modify(String father,String beforeChange, String afterChange);
}
