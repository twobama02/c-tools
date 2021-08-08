package com.bixuebihui.tablegen.dbinfo;
/**
 * @author xwx
 */
public class IntegerIdentifierFactory
{
	private int next;

	public IntegerIdentifierFactory()
	{
		this(0);
	}

	public IntegerIdentifierFactory(int initialValue)
	{
		super();
		next = initialValue;
	}

	public synchronized IntegerIdentifier createIdentifier()
	{
		return new IntegerIdentifier(next++);
	}
}
