package tss2.wiki.control.service;

import tss2.wiki.domain.ResultMessage;
import tss2.wiki.vo.WikiEntryVO;

import java.util.ArrayList;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public interface ContentService {
    ArrayList<String> getTags();

    ResultMessage process(WikiEntryVO wikiEntry);
}
