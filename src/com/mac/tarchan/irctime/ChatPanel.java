/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.DesktopSupport;
import com.mac.tarchan.desktop.event.EventQuery;

/**
 * ChatPanel
 */
@SuppressWarnings("serial")
public class ChatPanel extends JPanel
{
	private static final Log log = LogFactory.getLog(ChatPanel.class);

	private DefaultListModel nameModel = new DefaultListModel();

	private String topicText;

	private JTextField inputText = new JTextField(30);

	private JButton sendButton = new JButton("送信");

	private JTextPane mainText = new JTextPane();

	private JList nameList = new JList(nameModel);

	private JLabel topicLabel = new JLabel();

	public ChatPanel()
	{
		createMain(this);

		EventQuery.from(this)
			.button().click(this).end()
			.input().click(this).end();
	}

	private Component createMain(JPanel main)
	{
		inputText.setName("inputText");
		inputText.setRequestFocusEnabled(true);
		sendButton.setName("sendButton");
//		mainText.setName("mainText");
		mainText.setEditable(false);
		nameList.setFixedCellWidth(100);

		JScrollPane contentPane = new JScrollPane(mainText);
		JScrollPane infoPane = new JScrollPane(nameList);
		infoPane.setVisible(false);

		Box inputBox = Box.createHorizontalBox();
		inputBox.add(inputText);
		inputBox.add(sendButton);

		main.setLayout(new BorderLayout());
		main.add(topicLabel, BorderLayout.NORTH);
		main.add(contentPane, BorderLayout.CENTER);
		main.add(inputBox, BorderLayout.SOUTH);
		main.add(infoPane, BorderLayout.EAST);
		return main;
	}

	/**
	 * @see #sendButton()
	 */
	public void inputText()
	{
		log.debug("inputText");
		sendButton.doClick();
	}

	/**
	 * @see #inputMessage()
	 */
	public void sendButton()
	{
		log.debug("sendButton");
		inputMessage();
	}

	private void inputMessage()
	{
		log.debug("clearText");
		String text = inputText.getText();
		inputText.setText(null);
		if (text.trim().length() == 0) return;
//		appendLine(text);
	}

	public void goInput()
	{
		inputText.requestFocus();
	}

	private void updateTopic()
	{
//		topicLabel.setText(String.format("%s (%,d)", topicText, nameModel.size()));
	}

	public void appendLine(String text)
	{
		mainText.setText(String.format("%s%s%n", mainText.getText(), text));
	}

	public void setNames(String[] names)
	{
		nameModel.clear();
		for (String nick : names)
		{
			nameModel.addElement(new ChannelMember(nick));
		}
		JScrollPane view = DesktopSupport.componentOwner(nameList, JScrollPane.class);
//		log.debug(view);
		view.setVisible(true);
		updateTopic();
	}

	public void setTopic(String text)
	{
		topicText = text;
		updateTopic();
	}

	public String getTopic()
	{
		return topicText;
	}

	public int getNickCount()
	{
		return nameModel.size();
	}

	public boolean containsNick(String nick)
	{
		if (nick == null) return false;
		return nameModel.contains(new ChannelMember(nick));
	}

	public void addNick(String nick)
	{
		if (nick == null) return;
		nameModel.addElement(new ChannelMember(nick));
		updateTopic();
	}

	public void deleteNick(String nick)
	{
		if (nick == null) return;
		boolean removed = nameModel.removeElement(new ChannelMember(nick));
		updateTopic();
		if (!removed) throw new RuntimeException("削除するメンバーが見つかりません。: " + nick);
	}

	public void updateNick(String oldNick, String newNick)
	{
		if (oldNick == null || newNick == null) return;
		int index = nameModel.indexOf(new ChannelMember(oldNick));
		if (index >= 0)
		{
			ChannelMember member = ChannelMember.class.cast(nameModel.elementAt(index));
			member.setNick(newNick);
			nameModel.set(index, member);
		}
		else
		{
			throw new RuntimeException("更新するメンバーが見つかりません。: " + oldNick);
		}
	}

	public void updateMode(String nick, String mode)
	{
		int index = nameModel.indexOf(new ChannelMember(nick));
		if (index >= 0)
		{
			ChannelMember member = ChannelMember.class.cast(nameModel.elementAt(index));
			member.setMode(mode);
			nameModel.set(index, member);
		}
		else
		{
			throw new RuntimeException("更新するメンバーが見つかりません。: " + nick);
		}
	}

	public ChannelMember[] getSelectedMembers()
	{
		Object[] list = nameList.getSelectedValues();
		return Arrays.asList(list).toArray(new ChannelMember[]{});
	}
}
