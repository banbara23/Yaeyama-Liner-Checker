
package com.ikmr.banbara23.yaeyama_liner_checker;

/**
 * 会社のenum
 */
public enum Company {
    ANNEI("annei", "安栄観光"), YKF("ykf", "八重山観光フェリー");

    /** フィールド変数 */
    private String company;
    private String companyName;

    Company(String company, String companyName) {
        this.company = company;
        this.companyName = companyName;
    }

    public String getCompany() {
        return company;
    }

    public String getCompanyName() {
        return companyName;
    }
}
