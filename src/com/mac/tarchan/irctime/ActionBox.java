/*
 * ActionBox.java
 * IRCTime
 * 
 * Created by tarchan on 2011/07/26.
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mac.tarchan.desktop.OptionBox;

/**
 * ActionBox
 */
@SuppressWarnings("serial")
public class ActionBox extends OptionBox
{
	/** アクション */
	private JTextField actionBox = new JTextField(20);

	public ActionBox(Window owner)
	{
		super(owner);
		mainBox.add(new JLabel("アクション:"));
		mainBox.add(actionBox);
		okButton.setText("アクションを送信");
	}
}
