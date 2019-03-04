package net.jmecn.mabi.skill.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.jmecn.mabi.skill.Skillinfo;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton btn;
	private JLabel label;
	
	private Skillinfo info;
	public InfoPanel(Skillinfo info) {
		this.info = info;
		btn = new JButton("GET");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDetail();
			}});
		label = new JLabel(info.getSkillLocalName());

		this.setBackground(Color.lightGray);
		this.setPreferredSize(new Dimension(360, 40));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(btn);
		this.add(label);
	}
	
	private void showDetail() {
		JOptionPane.showMessageDialog(this, info.getClosedDesc());
	}
}
