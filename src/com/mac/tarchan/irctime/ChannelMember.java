/*
 * ChannelMember.java
 * IRCTime
 * 
 * Created by tarchan on 2011/07/13.
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.irc.client.IRCPrefix;

/**
 * ChannelMember
 */
public class ChannelMember
{
	private static final Log log = LogFactory.getLog(ChannelMember.class);

	String id;

	String nick;

	String o = "";

	String v = "";

	String displayNick;

	public ChannelMember(String nick)
	{
		setNick(nick);
	}

	public ChannelMember(IRCPrefix prefix)
	{
		this(prefix.getNick());
	}

	public void setNick(String nick)
	{
		if (nick.startsWith("@"))
		{
			this.nick = nick.substring(1);
			o = "@";
		}
		else if (nick.startsWith("+"))
		{
			this.nick = nick.substring(1);
			v = "+";
		}
		else
		{
			this.nick = nick;
		}
		this.id = this.nick.toUpperCase();
		this.displayNick = this.o + this.nick;
	}

	@Override
	public int hashCode()
	{
		log.debug(toString() + "=" + id.hashCode());
		return id.hashCode();
	}

	@Override
	public boolean equals(Object paramObject)
	{
		if (!ChannelMember.class.isInstance(paramObject)) return false;
		ChannelMember a = ChannelMember.class.cast(paramObject);
		log.debug(id + "=" + a.id + "," + id.equals(a.id));
		return id.equals(a.id);
	}

	@Override
	public String toString()
	{
		return displayNick;
	}
}
