package tss2.wiki.control.service;

import tss2.wiki.domain.ResultMessage;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public interface ContentService {
    ArrayList<String> getTags();

    ResultMessage process(String sessionID, Map map);
}
