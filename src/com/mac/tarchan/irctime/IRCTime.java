/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.DesktopSupport;
import com.mac.tarchan.desktop.SexyControl;
import com.mac.tarchan.irc.client.IRCClient;
import com.mac.tarchan.irc.client.IRCEvent;
import com.mac.tarchan.irc.client.IRCMessage;
import com.mac.tarchan.irc.client.IRCPrefix;
import com.mac.tarchan.irc.client.util.BotAdapter;

/**
 * IRCTime
 */
public class IRCTime extends BotAdapter
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
					final IRCTime app = new IRCTime();
					DesktopSupport.shutdown(new Runnable()
					{
						@Override
						public void run()
						{
							app.irc.quit("Quit IRCTime");
						}
					});
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
		window.setApp(this);

//		option = new OptionBox(window);
//		option.setVisible(true);
//		login();

		return window;
	}

	public void inputText(ActionEvent evt)
	{
		try
		{
			log.debug(evt.getSource());
			String text = evt.getActionCommand();
			if (text.trim().length() == 0) return;

			long when = evt.getWhen();
			JTextComponent input = (JTextComponent)evt.getSource();
			ChatPanel panel = (ChatPanel)input.getParent().getParent();
			String channel = panel.getName();
			String nick = irc.getUserNick();
//			log.info(input.getParent());
//			log.info(input.getParent().getParent());
//			log.info(evt.getActionCommand());
			String msg = String.format("%s %s %s", getTimeString(when), nick, text);
//			log.info(msg);
			window.appendLine(channel, msg);
			if (text.startsWith("/"))
			{
				irc.postMessage(text.substring(1));
			}
			else
			{
				irc.privmsg(channel, text);
			}
		}
		catch (RuntimeException x)
		{
			log.error("テキスト送信を中止しました。", x);
		}
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

	private static String getTimeString(long when)
	{
		return String.format("%tH:%<tM", when);
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
	public void onTopic(String channel, String topic, long when)
	{
		window.setTopic(channel, topic);
		String text = String.format("%s %s topic is set (%s)", getTimeString(when), channel, topic);
		window.appendLine(channel, text);
	}

	@Override
	public void onNames(String channel, String[] names, long when)
	{
		window.setNames(channel, names);
		String text = String.format("%s %s names is set (%s)", getTimeString(when), channel, names.length);
		window.appendLine(channel, text);
	}

	@Override
	public void onNick(String oldNick, String newNick, long when)
	{
		String nowNick = irc.getUserNick();
		String text = String.format("%s %s -> %s (%s)", getTimeString(when), oldNick, newNick, nowNick);
		log.info(text);
		window.appendLineForNick(oldNick, text);
		// TODO ニックネームの変更に追従
	}

	@Override
	public void onJoin(String channel, IRCPrefix prefix)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String text = String.format("%s %s has joined (%s)", getTimeString(when), nick, prefix);
		window.appendLine(channel, text);
	}

	@Override
	public void onPart(String channel, IRCPrefix prefix)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String text = String.format("%s %s has left %s (%s)", getTimeString(when), nick, channel, prefix);
		window.appendLine(channel, text);
	}

	@Override
	public void onQuit(String trail, IRCPrefix prefix)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String text = String.format("%s %s has left IRC (%s)", getTimeString(when), nick, trail);
		window.appendLineForNick(nick, text);
		// TODO ニックネームの削除に追従
	}

	@Override
	public void onChannelMode(String channel, String mode)
	{
		String text = String.format("%s has changed mode %s", channel, mode);
		window.appendLine(channel, text);
	}

	@Override
	public void onMessage(IRCMessage message)
	{
		long when = message.getWhen();
		String nick = message.getPrefix().getNick();
		String chan = message.getParam0();
		String msg = message.getTrail();
		String text = String.format("%s %s: %s", getTimeString(when), nick, msg);
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
	public void onNumericReply(IRCMessage message)
	{
		String numeric = message.getCommand();
		long when = message.getWhen();
		String host = message.getPrefix().getNick();
		String msg = message.getTrail();
		String text = String.format("%tH:%<tM (%s) %s", when, numeric, msg);
		window.appendLine(host, text);
	}

	@Override
	public void onError(String text)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onError(text);
	}

	public void _onMessage(IRCEvent event)
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
			String msg = message.getTrail();
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
			String newNick = message.getTrail();
			log.debug(String.format("%s -> %s (%s)", oldNick, newNick, nowNick));
//			String text = String.format("%s -> %s (%s)", oldNick, newNick, nowNick);
		}
		else if (command.equals("JOIN"))
		{
			String nick = message.getPrefix().getNick();
			String chan = message.getTrail();
			String text = String.format("join %s", nick);
			window.appendLine(chan, text);
		}
		else if (command.equals("332"))
		{
			String chan = message.getParam(1);
			String text = message.getTrail();
			window.setTopic(chan, text);
		}
		else if (command.equals("PING"))
		{
			// ping
			String payload = message.getTrail();
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
