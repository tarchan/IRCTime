/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.Window;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.irc.IRCClient;
import com.mac.tarchan.irc.IRCEvent;
import com.mac.tarchan.irc.IRCHandler;
import com.mac.tarchan.irc.IRCMessage;
import com.mac.tarchan.irc.IRCName;

/**
 * IRCTime
 */
public class IRCTime implements IRCHandler
{
	private static final Log log = LogFactory.getLog(IRCTime.class);

	private JTabbedPane tabPanel = new JTabbedPane();

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		log.info("IRCTimeを起動します。");
		new IRCTime().createWindow().setVisible(true);
	}

	Window createWindow()
	{
		tabPanel.setTabPlacement(JTabbedPane.BOTTOM);

		JFrame window = new JFrame("IRCTime");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(tabPanel);
		window.setSize(500, 400);

		login();

		return window;
	}

	void addTab(String name)
	{
		ChatPanel tab = new ChatPanel();
		tab.setName(name);
		tabPanel.addTab(name, tab);
	}

	void selectTab(String name)
	{
		int index = tabPanel.indexOfTab(name);
		tabPanel.setSelectedIndex(index);
	}

	ChatPanel currentTab()
	{
		ChatPanel tab = (ChatPanel)tabPanel.getSelectedComponent();
		return tab;
	}

	public void login()
	{
		try
		{
			// irc.livedoor.ne.jp、irc6.livedoor.ne.jp、125.6.255.10
//			String host = "irc.livedoor.ne.jp";
//			int port = 6667;
//			String nick = "mybot";
//			String pass = null;
			String host = "cafebabe.ddo.jp";
			addTab(host);
			int port = 6667;
			String nick = "tarchan";
			String pass = "mahotai!";
			String[] channles = {"#javabreak"};
			login(host, port, nick, pass, channles);
		}
		catch (IOException x)
		{
			throw new RuntimeException("サーバーに接続できません。", x);
		}
	}

	public void login(String host, int port, String nick, String pass, String[] channels) throws IOException
	{
//		this.channels = channels;
		IRCClient irc = IRCClient.createClient(host, port, nick, pass)
			.on(this)
//			.on("001", this)
//			.on("privmsg", this)
//			.on("notice", this)
//			.on("ping", this)
			.connect();
		System.out.println("接続: " + irc);
	}

	public void onMessage(IRCEvent event)
	{
		IRCMessage message = event.getMessage();
//		System.out.println("メッセージ: " + message);
//		message.getServer().send("me, too.");
		IRCClient irc = event.getClient();
//		client.postMessage("privmsg", "me, too.");

		String command = message.getCommand();
		if (command.equals("PRIVMSG"))
		{
			// privmsg
			String nick = message.getPrefix();
			String chan = message.getParam(0);
			String msg = message.getTrailing();
			nick = IRCName.getSimpleName(nick);
			String text = String.format("%s:%s> %s", chan, nick, msg);
			ChatPanel panel = currentTab();
			panel.appendLine(text);
//			if (!chan.equals(irc.getNick()))
//			{
//				irc.privmsg(chan, msg);
//			}
//			else
//			{
//				irc.privmsg(nick, msg);
//			}
		}
		else if (command.equals("JOIN"))
		{
			String chan = message.getTrailing();
			addTab(chan);
		}
		else if (command.equals("PING"))
		{
			// ping
			String payload = message.getTrailing();
			irc.pong(payload);
		}
		else if (command.equals("ERROR"))
		{
			// error
		}
		else if (command.equals("001"))
		{
			// welcome
//			join(irc);
		}
		else
		{
			String text = message.toString();
			ChatPanel panel = currentTab();
			panel.appendLine(text);
		}
	}
}
