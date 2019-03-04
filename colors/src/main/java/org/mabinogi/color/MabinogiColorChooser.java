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

import javax.swing.JColorChooser;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class MabinogiColorChooser extends JColorChooser {
    private static final long serialVersionUID = 1L;

    public MabinogiColorChooser(Color initialColor) {
        super(initialColor);
        for (AbstractColorChooserPanel ccp : getChooserPanels()) {
            if (ccp.getDisplayName().contains("RGB")) {
                AbstractColorChooserPanel[] ccps = new AbstractColorChooserPanel[] {ccp};
                setChooserPanels(ccps);
                break;
            }
        }
    }
}
