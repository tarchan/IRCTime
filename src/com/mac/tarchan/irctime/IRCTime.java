/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.DesktopSupport;
import com.mac.tarchan.desktop.SexyControl;
import com.mac.tarchan.irc.IRCClient;
import com.mac.tarchan.irc.IRCEvent;
import com.mac.tarchan.irc.IRCHandler;
import com.mac.tarchan.irc.IRCMessage;
import com.mac.tarchan.irc.IRCPrefix;
import com.mac.tarchan.irc.util.IRCBotAdapter;

/**
 * IRCTime
 */
public class IRCTime extends IRCBotAdapter
{
	private static final Log log = LogFactory.getLog(IRCTime.class);

	private String[] channels;

	private ChatWindow window;

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		SexyControl.useScreenMenuBar();
		SexyControl.setAppleMenuAboutName("IRCTime");
		DesktopSupport.useSystemLookAndFeel();
		DesktopSupport.execute(new Runnable()
		{
			public void run()
			{
				try
				{
					log.info("IRCTimeを起動します。");
					IRCTime app = new IRCTime();
					app.login(args);
				}
				catch (Throwable x)
				{
					log.info("IRCTimeを中止します。", x);
				}
			}
		});
	}

	public IRCTime()
	{
		window = createWindow();
		window.setVisible(true);
	}

	private ChatWindow createWindow()
	{
//		tabPanel.setTabPlacement(JTabbedPane.BOTTOM);

//		JFrame window = new JFrame("IRCTime");
		ChatWindow window = new ChatWindow("IRCTime");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		window.setJMenuBar(createMenuBar());
//		window.add(tabPanel);
		window.setSize(500, 400);

//		option = new OptionBox(window);
//		option.setVisible(true);
//		login();

		return window;
	}

	public void login(String[] args)
	{
		try
		{
			if (args.length < 4)
			{
				System.out.println("Usage: IRCTime <ホスト名> <ポート番号> <ニックネーム> <パスワード> <チャンネル名>");
				throw new IllegalArgumentException("引数が不足しています。: " + args.length);
			}
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			String nick = args[2];
			String pass = args[3];
			String[] channels = Arrays.asList(args).subList(4, args.length).toArray(new String[]{});
			this.channels = channels;
			log.debug("channels=" + Arrays.toString(channels));
			login(host, port, nick, pass);
//			login(host, port, nick, pass, channles);
		}
		catch (IOException x)
		{
			throw new RuntimeException("サーバーに接続できません。", x);
		}
	}

//	public void login(String host, int port, String nick, String pass, String[] channels) throws IOException
//	{
////		this.channels = channels;
//		IRCClient irc = IRCClient.createClient(host, port, nick, pass)
//			.on(this)
//			.on(new NamesHandler(window))
////			.on("001", this)
////			.on("privmsg", this)
////			.on("notice", this)
////			.on("ping", this)
//			.start();
//		System.out.println("接続: " + irc);
//	}

	@Override
	public void onStart()
	{
		log.info("接続しました。");
		for (String channel : channels)
		{
			irc.join(channel);
		}
	}

	@Override
	public void onNick(String oldNick, String newNick)
	{
		String nowNick = irc.getUserNick();
		String text = String.format("%s -> %s (%s)", oldNick, newNick, nowNick);
		log.info(text);
//		window.appendLine(text);
	}

	@Override
	public void onJoin(String channel, IRCPrefix prefix)
	{
		String nick = prefix.getNick();
		String text = String.format("join %s", nick);
		window.appendLine(channel, text);
	}

	@Override
	public void onNames(String channel, String[] names)
	{
		window.setNames(channel, names);
	}

	@Override
	public void onTopic(String channel, String topic)
	{
		window.setTopic(channel, topic);
	}

	@Override
	public void onPart(String channel, IRCPrefix prefix)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onPart(channel, prefix);
	}

	@Override
	public void onQuit(IRCPrefix prefix, String text)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onQuit(prefix, text);
	}

	@Override
	public void onChannelMode(String channel, String mode)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onChannelMode(channel, mode);
	}

	@Override
	public void onMessage(IRCMessage message)
	{
		long when = message.getWhen();
		String nick = message.getPrefix().getNick();
		String chan = message.getParam(0);
		String msg = message.getTrailing();
		String text = String.format("%tH:%<tM %s: %s", when, nick, msg);
		window.appendLine(chan, text);
	}

	@Override
	public void onDirectMessage(IRCMessage message)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onDirectMessage(message);
	}

	@Override
	public void onNotice(IRCMessage message)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onNotice(message);
	}

	@Override
	public void onError(String text)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onError(text);
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
			long when = message.getWhen();
			String nick = message.getPrefix().getNick();
			String chan = message.getParam(0);
			String msg = message.getTrailing();
//			String text = String.format("%s:%s> %s", chan, nick, msg);
//			ChatPanel panel = currentTab();
//			panel.appendLine(text);
			String text = String.format("%tH:%<tM %s: %s", when, nick, msg);
			window.appendLine(chan, text);
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
			String nowNick = irc.getUserNick();
			String oldNick = message.getPrefix().getNick();
			String newNick = message.getTrailing();
			log.debug(String.format("%s -> %s (%s)", oldNick, newNick, nowNick));
//			String text = String.format("%s -> %s (%s)", oldNick, newNick, nowNick);
		}
		else if (command.equals("JOIN"))
		{
			String nick = message.getPrefix().getNick();
			String chan = message.getTrailing();
			String text = String.format("join %s", nick);
			window.appendLine(chan, text);
		}
		else if (command.equals("332"))
		{
			String chan = message.getParam(1);
			String text = message.getTrailing();
			window.setTopic(chan, text);
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
			String host = irc.getHost();
			String text = message.toString();
			window.appendLine(host, text);
//			ChatPanel panel = window.currentTab();
//			panel.appendLine(text);
		}
	}
}

class NamesHandler implements IRCHandler
{
	ChatWindow app;

	ArrayList<String> list = new ArrayList<String>();

	NamesHandler(ChatWindow app)
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
