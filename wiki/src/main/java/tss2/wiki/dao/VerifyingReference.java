package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * Created by coral on 16-7-24.
 */
public class VerifyingReference extends DAOBase {
    public int verifyId;
    public String name;
    public String url;

    public static VerifyingReference query() {
        return new VerifyingReference();
    }
}
