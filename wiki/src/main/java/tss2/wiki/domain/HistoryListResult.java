package tss2.wiki.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */
public class HistoryListResult {
    private int error = 0;
    private ArrayList<ModifyHistory> modifyHistoryArrayList;
    private String message = "";

    public HistoryListResult(int error,String message){
        this.setError(error);
        this.setMessage(message);
    }

    public HistoryListResult(int error,ArrayList<ModifyHistory> modifyHistoryArrayList){
        this.setError(error);
        this.setModifyHistoryArrayList(modifyHistoryArrayList);
    }

    public int getError() {
        return error;
    }

    public ArrayList<ModifyHistory> getModifyHistoryArrayList() {
        return modifyHistoryArrayList;
    }

    public String getMessage() {
        return message;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setModifyHistoryArrayList(ArrayList<ModifyHistory> modifyHistoryArrayList) {
        this.modifyHistoryArrayList = modifyHistoryArrayList;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
