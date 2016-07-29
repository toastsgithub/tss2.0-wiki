package tss2.wiki.script;

import tss2.wiki.model.WikiMessage;

/**
 * Created by zhengyunzhi on 16/7/28.
 */
public class SendMessage {
    public static void main(String[] args) {
        for (int i = 0; i < 200; ++i) {
            WikiMessage message = new WikiMessage("智障" + i + "號", "wh14", "吵死了", "跟你說了" + i + "次了,你是智障");
            System.out.println("已發送第" + i + "條消息");
            message.send();
        }
    }
}
