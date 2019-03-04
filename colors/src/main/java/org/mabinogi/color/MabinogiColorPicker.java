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

import java.awt.Color;

import javax.swing.JButton;

public class MabinogiColorPicker extends JButton {
    public static final Color UNKNOWN_COLOR = Color.BLACK;
    private static final long serialVersionUID = 1L;
    private boolean unknown = true;

    public MabinogiColorPicker(Color color) {
        setBackground(color);
        setFocusable(false);
    }
    
    public void setBackground(int bg) {
        setBackground(bg == MabinogiColorHelper.UNKNOWN_COLOR ? null : new Color(bg));
    }
    
    @Override
    public void setBackground(Color bg) {
        if (bg == null) {
            setText(MabinogiColorHelper.toHexString(MabinogiColorHelper.UNKNOWN_COLOR));
            bg = UNKNOWN_COLOR;
            unknown = true;
        } else {
            setText(MabinogiColorHelper.toHexString(bg.getRGB()));
            unknown = false;
        }
        super.setBackground(bg);
        /* set foreground the reverse color */
        setForeground(new Color(~bg.getRGB()));
    }

    public boolean isUnknown() {
        return unknown;
    }
}
