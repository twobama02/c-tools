package com.bixuebihui.generated.tablegen.business;
/**
  * T_metacolumn
  *
  * WARNING! Automatically generated file!
  * Do not edit the pojo and dal packages,use bixuebihui-smartable!
  * Code Generator by J.A.Carter
  * Modified by Xing Wanxiang 2008
  * (c) www.goldjetty.com
  */

import com.bixuebihui.generated.tablegen.dal.T_metacolumnList;
import com.bixuebihui.generated.tablegen.pojo.T_metacolumn;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

public class T_metacolumnManager  extends T_metacolumnList
{

	/**
	 * Don't direct use the T_metacolumnListList, use T_metacolumnListManager instead.
	 *
	 * @param ds
	 */
	public T_metacolumnManager(DataSource ds) {
		super(ds);
	}

	Map<String,Collection<T_metacolumn>> getColumnDataExt(Vector<String> tableNames){
		return null;
	}

}
