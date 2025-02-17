package com.bixuebihui.generated.tablegen.business;

/**
 * T_metatable
 *
 * WARNING! Automatically generated file!
 * Do not edit the pojo and dal packages,use bixuebihui-smartable!
 * Code Generator by J.A.Carter
 * Modified by Xing Wanxiang 2008
 * (c) www.goldjetty.com
 */

import com.bixuebihui.generated.tablegen.dal.T_metatableList;
import com.bixuebihui.generated.tablegen.pojo.T_metatable;
import com.bixuebihui.jdbc.IDbHelper;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author xwx
 */
public class T_metatableManager extends T_metatableList {
	/**
	 * Don't direct use the T_metatableListList, use T_metatableListManager instead.
	 *
	 * @param ds
	 */
	public T_metatableManager(DataSource ds) {
		super(ds);
	}

	@SuppressWarnings("unchecked")
	public Collection<T_metatable> getTableDataExt(List<String> tableNames)
			throws SQLException {
		String select = "select * from " + this.getTableName();
		String whereClause = " where tname in (" + v2str(tableNames) + ")";
		String orderBy = " order by tid ";
		return this.select(select, whereClause,
				orderBy, null, 0, 10000, T_metatable.class);
	}

	private String v2str(List<String> tableNames) {
		StringBuilder sb = new StringBuilder();
		for (String s : tableNames) {
            sb.append("'").append(s).append("'").append(",");
        }

		return StringUtils.stripEnd(sb.toString(), ",");
	}

	public void setDbHelper(IDbHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
}
