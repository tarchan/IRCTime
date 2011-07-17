/*
 * LoginBox.java
 * IRCTime
 * 
 * Created by tarchan on 2011/07/17.
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mac.tarchan.desktop.OptionBox;

/**
 * LoginBox
 */
@SuppressWarnings("serial")
public class LoginBox extends OptionBox
{
	/** ホスト名 */
	private JTextField hostBox = new JTextField(16);

	/** ポート番号 */
	private JTextField portBox = new JTextField(4);

	/** ユーザ名 */
	private JTextField userBox = new JTextField(20);

	/** パスワード */
	private JTextField passBox = new JPasswordField(20);

	public LoginBox(Window owner)
	{
		super(owner);
		mainBox.add(new JLabel("ホスト名:"));
		mainBox.add(hostBox);
		mainBox.add(new JLabel("ポート番号:"));
		mainBox.add(portBox);
		mainBox.add(new JLabel("ユーザ名:"));
		mainBox.add(userBox);
		mainBox.add(new JLabel("パスワード:"));
		mainBox.add(passBox);
		okButton.setText("ログイン");
	}
}
