package com.bixuebihui.generated.tablegen;
/**
 * BaseList
 * <p>
 * WARNING! Automatically generated file!
 * Do not edit!
 * Code Generator by J.A.Carter
 * Modified by Xing Wanxiang 2008
 * (c) www.goldjetty.com
 */

import com.bixuebihui.jdbc.BaseDao;

import javax.sql.DataSource;

/**
 * @author xwx
 */
public abstract class BaseList<T, V> extends BaseDao<T, V> {
    public BaseList(DataSource ds) {
       super(ds);
    }
}
