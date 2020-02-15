package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.db.model.User;

public class SqlStatement {
    public static String allSubscriptSql = "select ANY_VALUE(u.name) as username,GROUP_CONCAT(k.key_name) as keyname,s.hash_key from subscription as s " +
            "left join keywords as k on k.id = s.keyword_id left join `user` as u on u.id=s.user_id GROUP by s.hash_key";
    public static String userSubscriptSql = "select ANY_VALUE(u.name) as username,GROUP_CONCAT(k.key_name) as keyname,s.hash_key " +
            "from subscription as s left join keywords as k on k.id = s.keyword_id left join `user` as u " +
            "on u.id=s.user_id where s.user_id=\"%d\" GROUP by s.hash_key";

    public String getAllSubscriptSql() {
        return allSubscriptSql;
    }

    public String getUserSubscriptSql(User user) {
        return String.format(userSubscriptSql, user.getId());
    }
}
