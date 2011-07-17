/*
 * TopicBox.java
 * IRCTime
 * 
 * Created by tarchan on 2011/07/17.
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mac.tarchan.desktop.OptionBox;

/**
 * TopicBox
 */
@SuppressWarnings("serial")
public class TopicBox extends OptionBox
{
	/** トピック */
	private JTextField topicBox = new JTextField(20);

	public TopicBox(Window owner)
	{
		super(owner);
		mainBox.add(new JLabel("トピック:"));
		mainBox.add(topicBox);
		okButton.setText("トピックを変更");
	}
}
