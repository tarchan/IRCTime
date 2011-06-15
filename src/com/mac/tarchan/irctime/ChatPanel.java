/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.event.EventQuery;

/**
 * ChatPanel
 */
@SuppressWarnings("serial")
public class ChatPanel extends JPanel
{
	private static final Log log = LogFactory.getLog(ChatPanel.class);

	private DefaultListModel nameModel = new DefaultListModel();

	private JTextField inputText = new JTextField(30);

	private JButton sendButton = new JButton("Send");

	private JTextPane mainText = new JTextPane();

	private JList nameList = new JList(nameModel);

	public ChatPanel()
	{
		createMain(this);

		EventQuery.ready(this)
			.button().click(this).end()
			.input().click(this).end();
	}

	Component createMain(JPanel main)
	{
		inputText.setName("inputText");
		inputText.setRequestFocusEnabled(true);
		sendButton.setName("sendButton");
//		mainText.setName("mainText");
		mainText.setEditable(false);
		nameList.setFixedCellWidth(150);

		JScrollPane contentPane = new JScrollPane(mainText);
		JScrollPane infoPane = new JScrollPane(nameList);

		Box inputBox = Box.createHorizontalBox();
		inputBox.add(inputText);
		inputBox.add(sendButton);

		main.setLayout(new BorderLayout());
		main.add(infoPane, BorderLayout.EAST);
		main.add(contentPane, BorderLayout.CENTER);
		main.add(inputBox, BorderLayout.SOUTH);
		return main;
	}

	public void inputText()
	{
		log.debug("inputText");
		sendButton.doClick();
	}

	public void sendButton()
	{
		log.debug("sendButton");
		inputMessage();
	}

	void inputMessage()
	{
		String text = inputText.getText();
		inputText.setText(null);
		if (text.trim().length() == 0) return;
		appendLine(text);
		
	}

	public void appendLine(String text)
	{
		mainText.setText(String.format("%s%s%n", mainText.getText(), text));
	}
}
