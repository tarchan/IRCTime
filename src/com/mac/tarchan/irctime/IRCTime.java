/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					log.info("IRCTimeを起動します。");
					System.setProperty("java.net.useSystemProxies", "true");
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					IRCTime app = new IRCTime();
					app.createWindow().setVisible(true);
					app.login();
				}
				catch (Throwable x)
				{
					log.info("IRCTimeを中止します。", x);
				}
			}
		});
	}

	Window createWindow()
	{
		tabPanel.setTabPlacement(JTabbedPane.BOTTOM);

		JFrame window = new JFrame("IRCTime");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(tabPanel);
		window.setSize(500, 400);

//		login();

		return window;
	}

	private ChatPanel getTab(String name)
	{
		int index = tabPanel.indexOfTab(name);
		if (index < 0)
		{
			ChatPanel tab = new ChatPanel();
			tab.setName(name);
			tab.setTopic(name);
			tabPanel.addTab(name, tab);
			return tab;
		}
		else
		{
			ChatPanel tab = (ChatPanel)tabPanel.getComponentAt(index);
			return tab;
		}
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
			getTab(host);
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
			.on(new NamesHandler(this))
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
//			String text = String.format("%s:%s> %s", chan, nick, msg);
//			ChatPanel panel = currentTab();
//			panel.appendLine(text);
			String text = String.format("%s: %s", nick, msg);
			appendLine(chan, text);
//			if (!chan.equals(irc.getNick()))
//			{
//				irc.privmsg(chan, msg);
//			}
//			else
//			{
//				irc.privmsg(nick, msg);
//			}
		}
		else if (command.equals("NICK"))
		{
			String nowNick = irc.getNick();
			String oldNick = IRCName.getSimpleName(message.getPrefix());
			String newNick = message.getTrailing();
			log.debug(String.format("%s -> %s (%s)", oldNick, newNick, nowNick));
//			String text = String.format("%s -> %s (%s)", oldNick, newNick, nowNick);
		}
		else if (command.equals("JOIN"))
		{
			String nick = message.getPrefix();
			String chan = message.getTrailing();
			String text = String.format("join %s", nick);
			appendLine(chan, text);
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

	void appendLine(String name, String text)
	{
		ChatPanel tab = getTab(name);
		tab.appendLine(text);
	}

	void setNames(String name, String[] names)
	{
		if (name == null) throw new RuntimeException("チャンネル名が見つかりません。");
		if (names == null) throw new RuntimeException("リストが見つかりません。");
		ChatPanel tab = getTab(name);
		if (tab == null) throw new RuntimeException("タブが見つかりません。");
		tab.setNames(names);
	}
}

class NamesHandler implements IRCHandler
{
	IRCTime app;


	ArrayList<String> list = new ArrayList<String>();

	NamesHandler(IRCTime app)
	{
		this.app = app;
	}

	@Override
	public void onMessage(IRCEvent event)
	{
		IRCMessage message = event.getMessage();
		String command = message.getCommand();
		if (command.equals("353"))
		{
			addNames(message);
		}
		else if (command.equals("366"))
		{
			fireNames(message);
		}
	}

	void addNames(IRCMessage message)
	{
		String channel = message.getParam(2);
		String[] names = message.getTrailing().split(" ");
		for (String name : names)
		{
			list.add(name);
		}
		app.appendLine(channel, "add list: " + names.length);
	}

	void fireNames(IRCMessage message)
	{
		String channel = message.getParam(1);
		String[] names = list.toArray(new String[]{});
		app.setNames(channel, names);
		list.clear();
	}
}
