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

	protected String id;

	protected String nick;

	protected boolean op;

	protected boolean voice;

	protected String displayNick;

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
			op = true;
		}
		else if (nick.startsWith("+"))
		{
			this.nick = nick.substring(1);
			voice = true;
		}
		else
		{
			this.nick = nick;
		}
		this.id = this.nick.toUpperCase();
		updateDisplayNick();
	}

	public void setMode(String mode)
	{
		if (mode.equals("+o")) op = true;
		else if (mode.equals("-o")) op = false;
		else if (mode.equals("+v")) voice = true;
		else if (mode.equals("-v")) voice = false;
		updateDisplayNick();
	}

	void updateDisplayNick()
	{
		if (op) displayNick = "@" + nick;
		else if (voice) displayNick = "+" + nick;
		else displayNick = nick;
	}

	@Override
	public int hashCode()
	{
		if (log.isTraceEnabled()) log.trace(toString() + "=" + id.hashCode());
		return id.hashCode();
	}

	@Override
	public boolean equals(Object paramObject)
	{
		if (!ChannelMember.class.isInstance(paramObject)) return false;
		ChannelMember a = ChannelMember.class.cast(paramObject);
		if (log.isTraceEnabled()) log.trace(id + "=" + a.id + "," + id.equals(a.id));
		return id.equals(a.id);
	}

	@Override
	public String toString()
	{
		return displayNick;
	}
}
