/*
 * NickBox.java
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
 * NickBox
 */
@SuppressWarnings("serial")
public class NickBox extends OptionBox
{
	/** ニックネーム */
	private JTextField nickBox = new JTextField(20);

	public NickBox(Window owner)
	{
		super(owner);
		mainBox.add(new JLabel("ニックネーム:"));
		mainBox.add(nickBox);
		okButton.setText("ニックネームを変更");
//		pack();
	}
}
