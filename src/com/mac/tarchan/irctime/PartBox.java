/*
 * PartBox.java
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
 * PartBox
 */
@SuppressWarnings("serial")
public class PartBox extends OptionBox
{
	/** 離脱コメント */
	private JTextField partBox = new JTextField(20);

	public PartBox(Window owner)
	{
		super(owner);
		mainBox.add(new JLabel("離脱コメント:"));
		mainBox.add(partBox);
		okButton.setText("チャットを離脱");
	}
}
