/*
 * JoinBox.java
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
 * JoinBox
 */
@SuppressWarnings("serial")
public class JoinBox extends OptionBox
{
	/** チャンネル名 */
	private JTextField joinBox = new JTextField(20);

	public JoinBox(Window owner)
	{
		super(owner);
		mainBox.add(new JLabel("チャンネル:"));
		mainBox.add(joinBox);
		okButton.setText("チャットに参加");
	}
}
