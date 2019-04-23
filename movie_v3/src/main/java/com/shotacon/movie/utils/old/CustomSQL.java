package com.shotacon.movie.utils.old;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Joker on 14-8-27. Edited by Joker on 14-8-28.
 */
public class CustomSQL {
    private String[] substrings = null;
    private String[] nodestrings = null;

    /**
     * 构造方法，传入待填充值的SQL语句
     *
     * @param sql
     */
    public CustomSQL(String sql) {
        if (sql.lastIndexOf("?") == (sql.length() - 1)) {
            sql = sql + "  ";
        }
        substrings = sql.split("\\?");
        nodestrings = new String[substrings.length - 1];
        for (int i = 0; i < nodestrings.length; i++) {
            nodestrings[i] = "?";
        }
    }

    /**
     * 设定参数
     *
     * @param index   参数序号，从1开始
     * @param content 值
     */
    public void setString(int index, String content) {
        if (content != null && !content.equals("")) {
            content = content.replace("'", "''");
        }
        if (content != null && content.endsWith("\\")) {
            content = new StringBuffer(content).append(" ").toString();
        }
        nodestrings[index - 1] = new StringBuffer("'").append(content).append("'").toString();
    }

    public void setSQL(int index, String content) {
        nodestrings[index - 1] = content;
    }

    /**
     * 设定参数
     *
     * @param index   参数序号，从1开始
     * @param content 值
     */
    public void setInt(int index, int content) {
        nodestrings[index - 1] = String.valueOf(content);
    }

    /**
     * 设定参数
     *
     * @param index   参数序号，从1开始
     * @param content 值
     */
    public void setDate(int index, Date content) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dt = sdf.format(content);
        nodestrings[index - 1] = new StringBuffer("'").append(dt).append("'").toString();
    }

    /**
     * 设定参数
     *
     * @param index   参数序号，从1开始
     * @param content 值
     */
    public void setDateTime(int index, Date content) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt = sdf.format(content);
        nodestrings[index - 1] = new StringBuffer("'").append(dt).append("'").toString();
    }

    /**
     * 设定参数
     *
     * @param index   参数序号，从1开始
     * @param content 值
     */
    public void setLong(int index, long content) {
        nodestrings[index - 1] = String.valueOf(content);
    }

    public void setDouble(int index, double content) {
        nodestrings[index - 1] = String.valueOf(content);
    }

    /**
     * 获取填充好的SQL语句
     *
     * @return 填充好的SQL语句
     */
    public String getSql() {
        StringBuffer newSql = new StringBuffer("");
        for (int i = 0; i < nodestrings.length; i++) {
            newSql = newSql.append(substrings[i]).append(nodestrings[i]);
        }
        newSql = newSql.append(substrings[substrings.length - 1]);
        return newSql.toString();
    }

    /**
     * 打印SQL语句
     */
    public void print() {
        for (int i = 0; i < substrings.length; i++) {
            System.out.println(substrings[i]);
        }
        for (int i = 0; i < nodestrings.length; i++) {
            System.out.println(nodestrings[i]);
        }
    }
}
