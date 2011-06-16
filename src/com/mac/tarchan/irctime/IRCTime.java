/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.DesktopSupport;
import com.mac.tarchan.desktop.OptionBox;
import com.mac.tarchan.desktop.SexyControl;
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

	private OptionBox option;

	/**
	 * @param args
	 */
	public static void main(String[] args)
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
					log.info("IRCTime���N�����܂��B");
					System.setProperty("java.net.useSystemProxies", "true");
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					IRCTime app = new IRCTime();
					app.createWindow().setVisible(true);
					app.login();
				}
				catch (Throwable x)
				{
					log.info("IRCTime�𒆎~���܂��B", x);
				}
			}
		});
	}

	private Window createWindow()
	{
		tabPanel.setTabPlacement(JTabbedPane.BOTTOM);

		JFrame window = new JFrame("IRCTime");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setJMenuBar(createMenuBar());
		window.add(tabPanel);
		window.setSize(500, 400);

		option = new OptionBox(window);
//		option.setVisible(true);
//		login();

		return window;
	}

	@SuppressWarnings("serial")
	private JMenuBar createMenuBar()
	{
		AbstractAction loginAction = new AbstractAction()
		{
			{
				this.putValue(NAME, "�T�[�o�֐ڑ�...");
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("meta L"));
			}

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				option.setVisible(true);
			}
		};

//		JMenu ircMenu = new JMenu("IRC");
//		ircMenu.add(loginAction);
//		ircMenu.add(nickItem);
//		ircMenu.addSeparator();
//		ircMenu.add(joinItem);
//		ircMenu.add(partItem);
//		ircMenu.add(modeItem);
//		ircMenu.add(topicItem);
//		ircMenu.addSeparator();
//		ircMenu.add(quitItem);

		JMenu awayMenu = new JMenu("�����̏�");
		awayMenu.add("�`���b�g�\");
		awayMenu.addSeparator();
		awayMenu.add("�s��");
		awayMenu.add("���H�̂��ߊO�o��");
		awayMenu.add("�d�b��");
		awayMenu.add("��c��");
		awayMenu.addSeparator();
		awayMenu.add("�s�݃��b�Z�[�W���J�X�^�}�C�Y...");

		JMenu chatMenu = new JMenu("�`���b�g");
		chatMenu.add(loginAction);
		chatMenu.add("�j�b�N�l�[����ύX...");
		chatMenu.add(awayMenu);
		chatMenu.addSeparator();
		chatMenu.add("��������");
		chatMenu.addSeparator();
		chatMenu.add("�`���b�g�ɎQ��...");
//		chatMenu.add("�`���b�g�ւ̏���...");
//		chatMenu.add("�`���b�g�����...");
		chatMenu.add("�����o�[��ǉ�...");
		chatMenu.add("�`���b�g�𗣒E...");
		chatMenu.addSeparator();
		chatMenu.add("���b�Z�[�W�𑗐M...");
		chatMenu.add("�R�}���h�𑗐M...");
		chatMenu.add("CTCP �R�}���h�𑗐M...");
		chatMenu.add("�A�N�V�����𑗐M...");

		JMenu memberMenu = new JMenu("�����o�[");
		memberMenu.add("�_�C���N�g���b�Z�[�W�𑗐M...");
		memberMenu.add("�t�@�C���𑗐M...");
		memberMenu.addSeparator();
		memberMenu.add("�����o�[��������");
		memberMenu.addSeparator();
		memberMenu.add("�Ȃ�Ƃ�t����");
		memberMenu.add("�Ȃ�Ƃ��O��");
		memberMenu.add("��������t����");
		memberMenu.add("���������O��");
		memberMenu.addSeparator();
		memberMenu.add("�L�b�N");

		JMenu windowMenu = new JMenu("�E�C���h�E");
		windowMenu.add("���܂�");
		windowMenu.add("�g��^�k��");
		windowMenu.addSeparator();
		windowMenu.add("���ׂĂ���O�Ɉړ�");
		windowMenu.addSeparator();
		windowMenu.add("�O�̃`���b�g");
		windowMenu.add("���̃`���b�g");
		windowMenu.addSeparator();
		windowMenu.add("�t�@�C���]��");

		JMenuBar menuBar = new JMenuBar();
//		menuBar.add(ircMenu);
		menuBar.add(chatMenu);
		menuBar.add(memberMenu);
		menuBar.add(windowMenu);
		return menuBar;
	}

	private ChatPanel getTab(String name)
	{
		int index = tabPanel.indexOfTab(name);
		if (index < 0)
		{
			ChatPanel tab = new ChatPanel();
			tab.setName(name);
//			tab.setTopic(name);
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
			// irc.livedoor.ne.jp�Airc6.livedoor.ne.jp�A125.6.255.10
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
			throw new RuntimeException("�T�[�o�[�ɐڑ��ł��܂���B", x);
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
		System.out.println("�ڑ�: " + irc);
	}

	public void onMessage(IRCEvent event)
	{
		IRCMessage message = event.getMessage();
//		System.out.println("���b�Z�[�W: " + message);
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
		else if (command.equals("332"))
		{
			String chan = message.getParam(1);
			String text = message.getTrailing();
			setTopic(chan, text);
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
		if (name == null) throw new RuntimeException("�`�����l������������܂���B");
		if (names == null) throw new RuntimeException("���X�g��������܂���B");
		ChatPanel tab = getTab(name);
		if (tab == null) throw new RuntimeException("�^�u��������܂���B");
		tab.setNames(names);
	}

	void setTopic(String name, String text)
	{
		ChatPanel tab = getTab(name);
		tab.setTopic(text);
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
