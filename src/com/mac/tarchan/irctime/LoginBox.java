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
import javax.swing.SpringLayout;

import com.mac.tarchan.desktop.OptionBox;
import com.mac.tarchan.desktop.SpringUtilities;

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

		JLabel hostLabel = new JLabel("ホスト名:", JLabel.TRAILING);
		hostLabel.setLabelFor(hostBox);
		JLabel portLabel = new JLabel("ポート番号:", JLabel.TRAILING);
		portLabel.setLabelFor(portBox);
		JLabel userLabel = new JLabel("ユーザ名:", JLabel.TRAILING);
		userLabel.setLabelFor(userBox);
		JLabel passLabel = new JLabel("パスワード:", JLabel.TRAILING);
		passLabel.setLabelFor(passBox);

		mainBox.add(hostLabel);
		mainBox.add(hostBox);
		mainBox.add(portLabel);
		mainBox.add(portBox);
		mainBox.add(userLabel);
		mainBox.add(userBox);
		mainBox.add(passLabel);
		mainBox.add(passBox);
		okButton.setText("ログイン");

		mainBox.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(mainBox, 4, 2, 8, 8, 8, 8);
	}
}
