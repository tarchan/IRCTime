/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.OptionBox;
import com.mac.tarchan.desktop.event.EventQuery;

/**
 * ChatWindow
 */
@SuppressWarnings("serial")
public class ChatWindow extends JFrame
{
	private static final Log log = LogFactory.getLog(ChatWindow.class);

	private JTabbedPane tabPanel = new JTabbedPane();

	private OptionBox option;

	private IRCTime app;

	public ChatWindow(String tile)
	{
		super(tile);
		log.debug("ウインドウを作成: " + tile);
		tabPanel.setTabPlacement(JTabbedPane.BOTTOM);
		add(tabPanel);
		setJMenuBar(createMenuBar());
		createDialog();
	}

	private void createDialog()
	{
		option = new OptionBox(this);
	}

	private JMenuBar createMenuBar()
	{
		AbstractAction loginAction = new AbstractAction()
		{
			{
				this.putValue(NAME, "サーバへ接続...");
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("meta L"));
			}

			@Override
			public void actionPerformed(ActionEvent evt)
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

		JMenu awayMenu = new JMenu("自分の状況");
		awayMenu.add("チャット可能");
		awayMenu.addSeparator();
		awayMenu.add("不在");
		awayMenu.add("昼食のため外出中");
		awayMenu.add("電話中");
		awayMenu.add("会議中");
		awayMenu.addSeparator();
		awayMenu.add("不在メッセージをカスタマイズ...");

		JMenu chatMenu = new JMenu("チャット");
		chatMenu.add(loginAction);
		chatMenu.add("ニックネームを変更...");
		chatMenu.add(awayMenu);
		chatMenu.addSeparator();
		chatMenu.add("情報を見る");
		chatMenu.addSeparator();
		chatMenu.add("チャットに参加...");
//		chatMenu.add("チャットへの招待...");
//		chatMenu.add("チャットを閉じる...");
		chatMenu.add("メンバーを追加...");
		chatMenu.add("チャットを離脱...");
		chatMenu.addSeparator();
		chatMenu.add("メッセージを送信...");
		chatMenu.add("コマンドを送信...");
		chatMenu.add("CTCP コマンドを送信...");
		chatMenu.add("アクションを送信...");

		JMenu memberMenu = new JMenu("メンバー");
		memberMenu.add("ダイレクトメッセージを送信...");
		memberMenu.add("ファイルを送信...");
		memberMenu.addSeparator();
		memberMenu.add("メンバー情報を見る");
		memberMenu.addSeparator();
		memberMenu.add("なるとを付ける");
		memberMenu.add("なるとを外す");
		memberMenu.add("発言権を付ける");
		memberMenu.add("発言権を外す");
		memberMenu.addSeparator();
		memberMenu.add("キック");

		JMenu windowMenu = new JMenu("ウインドウ");
		windowMenu.add("しまう");
		windowMenu.add("拡大／縮小");
		windowMenu.addSeparator();
		windowMenu.add("すべてを手前に移動");
		windowMenu.addSeparator();
		windowMenu.add("前のチャット");
		windowMenu.add("次のチャット");
		windowMenu.addSeparator();
		windowMenu.add("ファイル転送");

		JMenuBar menuBar = new JMenuBar();
//		menuBar.add(ircMenu);
		menuBar.add(chatMenu);
		menuBar.add(memberMenu);
		menuBar.add(windowMenu);
		return menuBar;
	}

	public ChannelPanel getTab(String name)
	{
		int index = tabPanel.indexOfTab(name);
		if (index < 0)
		{
			ChannelPanel tab = new ChannelPanel();
			tab.setName(name);
//			tab.setTopic(name);
			tabPanel.addTab(name, tab);

			EventQuery.from(tab).find("inputText").click(app, "inputText", "");

			return tab;
		}
		else
		{
			return getTab(index);
		}
	}

	public ChannelPanel getTab(int index)
	{
		ChannelPanel tab = (ChannelPanel)tabPanel.getComponentAt(index);
		return tab;
	}

	public void setApp(IRCTime app)
	{
		this.app = app;
	}

//	public ChatPanel currentTab()
//	{
//		ChatPanel tab = (ChatPanel)tabPanel.getSelectedComponent();
//		return tab;
//	}

	void appendLine(String name, String text)
	{
		ChannelPanel tab = getTab(name);
		tab.appendLine(text);
	}

	List<ChannelPanel> findPanel(String nick)
	{
		List<ChannelPanel> list = new ArrayList<ChannelPanel>();
		int count = tabPanel.getTabCount();
		for (int i = 0; i < count; i++)
		{
			ChannelPanel tab = getTab(i);
			if (tab == null) throw new RuntimeException("タブが見つかりません。: " + i);
			if (tab.containsNick(nick)) list.add(tab);
		}
		if (list.size() == 0) log.warn(String.format("%s を含むタブが見つかりません。(%s)", nick, count));

		return list;
	}

	void appendLineForNick(String nick, String text)
	{
		List<ChannelPanel> list = findPanel(nick);
		if (list.size() > 0)
		{
			for (ChannelPanel tab : list)
			{
				tab.appendLine(text);
			}
		}
		else
		{
			ChannelPanel tab = getTab(0);
			tab.appendLine(text);
		}
	}

	void setTopic(String name, String text)
	{
		ChannelPanel tab = getTab(name);
		tab.setTopic(text);
	}

	void setNames(String channel, String[] names)
	{
		if (channel == null) throw new RuntimeException("チャンネル名が見つかりません。");
		if (names == null) throw new RuntimeException("リストが見つかりません。");
		ChannelPanel tab = getTab(channel);
		if (tab == null) throw new RuntimeException("タブが見つかりません。");
		tab.setNames(names);
	}

	public void updateNick(String oldNick, String newNick)
	{
		List<ChannelPanel> list = findPanel(oldNick);
		if (list.size() > 0)
		{
			for (ChannelPanel tab : list)
			{
				tab.updateNick(oldNick, newNick);
			}
		}
	}

	public void addNick(String channel, String nick)
	{
		ChannelPanel tab = getTab(channel);
		if (tab == null) throw new RuntimeException("タブが見つかりません。");
		tab.addNick(nick);
	}

	public void deleteNick(String channel, String nick)
	{
		ChannelPanel tab = getTab(channel);
		if (tab == null) throw new RuntimeException("タブが見つかりません。");
		tab.deleteNick(nick);
	}

	public void deleteNick(String nick)
	{
		int count = tabPanel.getTabCount();
		for (int i = 0; i < count; i++)
		{
			ChannelPanel tab = getTab(i);
			if (tab == null) throw new RuntimeException("タブが見つかりません。: " + i);
			if (tab.containsNick(nick)) tab.deleteNick(nick);
		}
	}

	public void updateMode(String channel, String mode, String nick)
	{
		ChannelPanel tab = getTab(channel);
		if (tab == null) throw new RuntimeException("タブが見つかりません。");
		tab.updateMode(nick, mode);
	}
}
