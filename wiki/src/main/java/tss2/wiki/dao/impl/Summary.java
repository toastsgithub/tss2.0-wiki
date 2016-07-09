package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public class Summary extends DAOBase {
    public String summaryJO;

    public static Summary query() {
        return new Summary();
    }

    public static void main(String[] args) {
        Summary s = new Summary();
        s.summaryJO =
                "{\n" +
                "    \"软件\": [\n" +
                "        {\n" +
                "            \"一班\": [\n" +
                "                {\n" +
                "                    \"二班\": [\n" +
                "                        \"段正谋X崔忠诚\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                \"考拉\",\n" +
                "                \"浣熊\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"三班\": [\n" +
                "                \"喵\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"四班\": [\n" +
                "                \"歪歪\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"怎么这么多\": [\n" +
                "                \"好烦啊\",\n" +
                "                \"我编不下去了\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"还有？？\"\n" +
                "    ]\n" +
                "}";
        s.save();
    }
}
