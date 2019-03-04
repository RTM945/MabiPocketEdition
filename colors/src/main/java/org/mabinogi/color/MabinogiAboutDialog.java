/*
 * Copyright 2013 Valkyria.Lucy@qq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mabinogi.color;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

public class MabinogiAboutDialog extends JDialog implements ActionListener, LanguageKey {
    private static final long serialVersionUID = 1L;
    private static final String ID = "切露西";
    private static final String MAIL_ADDRESS = "Valkyria.Lucy@qq.com";
    
    private final Container contentPane = getContentPane();
    private final SpringLayout layout;
    private final JTextArea area;
    private final JButton ok;

    public MabinogiAboutDialog(Frame mch) {
        super(mch, MabinogiFactory.getLanguage().getString(ABOUT), true);
        area = new JTextArea(String.format(MabinogiFactory.getLanguage().getString(COPYRIGHT),
                MabinogiColorHelper.VERSION, 2013, ID, MAIL_ADDRESS));
        ok = new JButton(MabinogiFactory.getLanguage().getString(OK));
        layout = new SpringLayout();
        setLayout(layout);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(256, 192);
        setResizable(false);
        setLocationRelativeTo(mch);
        area.setEditable(false);
        area.setBackground(mch.getBackground());
        ok.addActionListener(this);
        contentPane.add(area);
        contentPane.add(ok);
        layout.putConstraint(SpringLayout.WEST, area, 16, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, area, 16, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, ok, -96, SpringLayout.EAST, ok);
        layout.putConstraint(SpringLayout.EAST, ok, -16, SpringLayout.EAST, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, ok, -16, SpringLayout.SOUTH, contentPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
}
