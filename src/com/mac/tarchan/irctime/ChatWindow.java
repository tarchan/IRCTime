/*
 * Copyright (c) 2011 tarchan. All rights reserved.
 */
package com.mac.tarchan.irctime;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.OptionBox;

/**
 * ChatWindow
 */
@SuppressWarnings("serial")
public class ChatWindow extends JFrame
{
	private static final Log log = LogFactory.getLog(ChatWindow.class);

	private JTabbedPane tabPanel = new JTabbedPane();

	private OptionBox option;

	public ChatWindow(String tile)
	{
		super(tile);
		log.debug("�E�C���h�E���쐬: " + tile);
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
				this.putValue(NAME, "�T�[�o�֐ڑ�...");
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("meta L"));
			}

			@Override
			public void actionPerformed(ActionEvent arg0)
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

		JMenu awayMenu = new JMenu("�����̏�");
		awayMenu.add("�`���b�g�\");
		awayMenu.addSeparator();
		awayMenu.add("�s��");
		awayMenu.add("���H�̂��ߊO�o��");
		awayMenu.add("�d�b��");
		awayMenu.add("��c��");
		awayMenu.addSeparator();
		awayMenu.add("�s�݃��b�Z�[�W���J�X�^�}�C�Y...");

		JMenu chatMenu = new JMenu("�`���b�g");
		chatMenu.add(loginAction);
		chatMenu.add("�j�b�N�l�[����ύX...");
		chatMenu.add(awayMenu);
		chatMenu.addSeparator();
		chatMenu.add("��������");
		chatMenu.addSeparator();
		chatMenu.add("�`���b�g�ɎQ��...");
//		chatMenu.add("�`���b�g�ւ̏���...");
//		chatMenu.add("�`���b�g�����...");
		chatMenu.add("�����o�[��ǉ�...");
		chatMenu.add("�`���b�g�𗣒E...");
		chatMenu.addSeparator();
		chatMenu.add("���b�Z�[�W�𑗐M...");
		chatMenu.add("�R�}���h�𑗐M...");
		chatMenu.add("CTCP �R�}���h�𑗐M...");
		chatMenu.add("�A�N�V�����𑗐M...");

		JMenu memberMenu = new JMenu("�����o�[");
		memberMenu.add("�_�C���N�g���b�Z�[�W�𑗐M...");
		memberMenu.add("�t�@�C���𑗐M...");
		memberMenu.addSeparator();
		memberMenu.add("�����o�[��������");
		memberMenu.addSeparator();
		memberMenu.add("�Ȃ�Ƃ�t����");
		memberMenu.add("�Ȃ�Ƃ��O��");
		memberMenu.add("��������t����");
		memberMenu.add("���������O��");
		memberMenu.addSeparator();
		memberMenu.add("�L�b�N");

		JMenu windowMenu = new JMenu("�E�C���h�E");
		windowMenu.add("���܂�");
		windowMenu.add("�g��^�k��");
		windowMenu.addSeparator();
		windowMenu.add("���ׂĂ���O�Ɉړ�");
		windowMenu.addSeparator();
		windowMenu.add("�O�̃`���b�g");
		windowMenu.add("���̃`���b�g");
		windowMenu.addSeparator();
		windowMenu.add("�t�@�C���]��");

		JMenuBar menuBar = new JMenuBar();
//		menuBar.add(ircMenu);
		menuBar.add(chatMenu);
		menuBar.add(memberMenu);
		menuBar.add(windowMenu);
		return menuBar;
	}

	public ChatPanel getTab(String name)
	{
		int index = tabPanel.indexOfTab(name);
		if (index < 0)
		{
			ChatPanel tab = new ChatPanel();
			tab.setName(name);
//			tab.setTopic(name);
			tabPanel.addTab(name, tab);
			return tab;
		}
		else
		{
			ChatPanel tab = (ChatPanel)tabPanel.getComponentAt(index);
			return tab;
		}
	}

//	public ChatPanel currentTab()
//	{
//		ChatPanel tab = (ChatPanel)tabPanel.getSelectedComponent();
//		return tab;
//	}

	void appendLine(String name, String text)
	{
		ChatPanel tab = getTab(name);
		tab.appendLine(text);
	}

	void setNames(String name, String[] names)
	{
		if (name == null) throw new RuntimeException("�`�����l������������܂���B");
		if (names == null) throw new RuntimeException("���X�g��������܂���B");
		ChatPanel tab = getTab(name);
		if (tab == null) throw new RuntimeException("�^�u��������܂���B");
		tab.setNames(names);
	}

	void setTopic(String name, String text)
	{
		ChatPanel tab = getTab(name);
		tab.setTopic(text);
	}
}
