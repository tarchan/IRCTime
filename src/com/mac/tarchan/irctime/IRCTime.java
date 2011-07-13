/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.DesktopSupport;
import com.mac.tarchan.desktop.SexyControl;
import com.mac.tarchan.irc.client.IRCMessage;
import com.mac.tarchan.irc.client.IRCPrefix;
import com.mac.tarchan.irc.client.util.BotAdapter;
import com.mac.tarchan.irc.client.util.DccSendFile;

/**
 * IRCTime
 */
public class IRCTime extends BotAdapter
{
	private static final Log log = LogFactory.getLog(IRCTime.class);

	private String[] channels;

	private ChatWindow window;

	/**
	 * IRCTime
	 * 
	 * @param args なし
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
							try
							{
								app.irc.quit("Quit IRCTime");
								app.irc.close();
							}
							catch (IOException ignore)
							{
							}
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
		window.setSize(600, 400);
		window.setApp(this);

//		option = new OptionBox(window);
//		option.setVisible(true);
//		login();

		return window;
	}

	private void login(String[] args)
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

	private boolean isHost(String name)
	{
		return name != null && !name.startsWith("#") && name.contains(".");
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
//			ChatPanel panel = (ChatPanel)input.getParent().getParent();
			ChannelPanel panel = DesktopSupport.componentOwner(input, ChannelPanel.class);
			String channel = panel.getName();
			String nick = irc.getUserNick();
//			log.info(input.getParent());
//			log.info(input.getParent().getParent());
//			log.info(evt.getActionCommand());
			String msg = String.format("%s %s: %s", getTimeString(when), nick, text);
//			log.info(msg);
			window.appendLine(channel, msg);
			if (text.startsWith("/"))
			{
				irc.postMessage(text.substring(1));
			}
			else if (isHost(channel))
			{
				irc.postMessage(text);
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
		String line = String.format("%s %s names is set (%s)", getTimeString(when), channel, names.length);
		window.appendLine(channel, line);
	}

	@Override
	public void onNick(IRCPrefix prefix, String newNick)
	{
		long when = prefix.getWhen();
		String oldNick = prefix.getNick();
		String nowNick = irc.getUserNick();
		log.debug("oldNick=" + oldNick);
		log.debug("newNick=" + newNick);
		log.debug("nowNick=" + nowNick);
		String line = String.format("%s %s -> %s (%s)", getTimeString(when), oldNick, newNick, prefix);
		log.info(line);
		window.appendLineForNick(oldNick, line);
		window.updateNick(oldNick, newNick);
	}

	@Override
	public void onJoin(IRCPrefix prefix, String channel)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String line = String.format("%s %s has joined (%s)", getTimeString(when), nick, prefix);
		window.appendLine(channel, line);
		window.addNick(channel, nick);
	}

	@Override
	public void onPart(IRCPrefix prefix, String channel, String text)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String line = String.format("%s %s has left %s (%s)", getTimeString(when), nick, channel, prefix);
		window.appendLine(channel, line);
		window.deleteNick(channel, nick);
	}

	@Override
	public void onQuit(IRCPrefix prefix, String text)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String line = String.format("%s %s has left IRC (%s)", getTimeString(when), nick, text);
		window.appendLineForNick(nick, line);
		window.deleteNick(nick);
	}

	@Override
	public void onMode(IRCPrefix prefix, String channel, String mode, String nick)
	{
		String line = String.format("%s has changed mode %s", channel, mode);
		window.appendLine(channel, line);
		window.updateMode(channel, mode, nick);
	}

	@Override
	public void onMessage(IRCPrefix prefix, String channel, String text)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String line = String.format("%s %s: %s", getTimeString(when), nick, text);
		window.appendLine(channel, line);
	}

	@Override
	public void onNotice(IRCPrefix prefix, String channel, String text)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String line;
		if (nick != null)
		{
			line = String.format("%s %s: %s", getTimeString(when), nick, text);
		}
		else
		{
			line = String.format("%s %s", getTimeString(when), text);
		}
		window.appendLineForNick(nick, line);
//		window.appendLine(channel, line);
	}

	@Override
	public void onDirectMessage(IRCPrefix prefix, String target, String text)
	{
		long when = prefix.getWhen();
		String nick = prefix.getNick();
		String line = String.format("%s %s: %s", getTimeString(when), nick, text);
		window.appendLine(nick, line);
	}

	@Override
	public void onDccSend(String trail, IRCPrefix prefix)
	{
		try
		{
			// TODO DCC SEND
			DccSendFile dccfile = new DccSendFile(trail);
			File savefile = new File("dcc/" + prefix.getNick(), dccfile.getName());
			dccfile.save(savefile);
		}
		catch (IOException x)
		{
			log.error(x);
		}
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
		log.error(text);
	}
}
