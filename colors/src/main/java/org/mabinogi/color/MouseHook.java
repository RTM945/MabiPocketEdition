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

import java.awt.Toolkit;
import java.awt.event.InputEvent;

public interface MouseHook {
    
    /**
     * Moves mouse pointer to given screen coordinates.
     * @param x X position.
     * @param y Y position.
     */
    void mouseMove(int x, int y);
    
    /**
     * Presses one or more mouse buttons.  The mouse buttons should
     * be released using the {@link #mouseRelease(int)} method.
     *
     * @param buttons the Button mask; a combination of one or more
     * mouse button masks.
     * <p>
     * It is allowed to use only a combination of valid values as a {@code buttons} parameter.
     * A valid combination consists of {@code InputEvent.BUTTON1_DOWN_MASK},
     * {@code InputEvent.BUTTON2_DOWN_MASK}, {@code InputEvent.BUTTON3_DOWN_MASK}
     * and values returned by the
     * {@link InputEvent#getMaskForButton(int) InputEvent.getMaskForButton(button)} method.
     *
     * The valid combination also depends on a
     * {@link Toolkit#areExtraMouseButtonsEnabled() Toolkit.areExtraMouseButtonsEnabled()} value as follows:
     * <ul>
     * <li> If support for extended mouse buttons is
     * {@link Toolkit#areExtraMouseButtonsEnabled() disabled} by Java
     * then it is allowed to use only the following standard button masks:
     * {@code InputEvent.BUTTON1_DOWN_MASK}, {@code InputEvent.BUTTON2_DOWN_MASK},
     * {@code InputEvent.BUTTON3_DOWN_MASK}.
     * <li> If support for extended mouse buttons is
     * {@link Toolkit#areExtraMouseButtonsEnabled() enabled} by Java
     * then it is allowed to use the standard button masks
     * and masks for existing extended mouse buttons, if the mouse has more then three buttons.
     * In that way, it is allowed to use the button masks corresponding to the buttons
     * in the range from 1 to {@link java.awt.MouseInfo#getNumberOfButtons() MouseInfo.getNumberOfButtons()}.
     * <br>
     * It is recommended to use the {@link InputEvent#getMaskForButton(int) InputEvent.getMaskForButton(button)}
     * method to obtain the mask for any mouse button by its number.
     * </ul>
     * <p>
     * The following standard button masks are also accepted:
     * <ul>
     * <li>{@code InputEvent.BUTTON1_MASK}
     * <li>{@code InputEvent.BUTTON2_MASK}
     * <li>{@code InputEvent.BUTTON3_MASK}
     * </ul>
     * However, it is recommended to use {@code InputEvent.BUTTON1_DOWN_MASK},
     * {@code InputEvent.BUTTON2_DOWN_MASK},  {@code InputEvent.BUTTON3_DOWN_MASK} instead.
     * Either extended {@code _DOWN_MASK} or old {@code _MASK} values
     * should be used, but both those models should not be mixed.
     */
    void mousePress(int buttons);
    
    /**
     * Releases one or more mouse buttons.
     *
     * @param buttons the Button mask; a combination of one or more
     * mouse button masks.
     * <p>
     * It is allowed to use only a combination of valid values as a {@code buttons} parameter.
     * A valid combination consists of {@code InputEvent.BUTTON1_DOWN_MASK},
     * {@code InputEvent.BUTTON2_DOWN_MASK}, {@code InputEvent.BUTTON3_DOWN_MASK}
     * and values returned by the
     * {@link InputEvent#getMaskForButton(int) InputEvent.getMaskForButton(button)} method.
     *
     * The valid combination also depends on a
     * {@link Toolkit#areExtraMouseButtonsEnabled() Toolkit.areExtraMouseButtonsEnabled()} value as follows:
     * <ul>
     * <li> If the support for extended mouse buttons is
     * {@link Toolkit#areExtraMouseButtonsEnabled() disabled} by Java
     * then it is allowed to use only the following standard button masks:
     * {@code InputEvent.BUTTON1_DOWN_MASK}, {@code InputEvent.BUTTON2_DOWN_MASK},
     * {@code InputEvent.BUTTON3_DOWN_MASK}.
     * <li> If the support for extended mouse buttons is
     * {@link Toolkit#areExtraMouseButtonsEnabled() enabled} by Java
     * then it is allowed to use the standard button masks
     * and masks for existing extended mouse buttons, if the mouse has more then three buttons.
     * In that way, it is allowed to use the button masks corresponding to the buttons
     * in the range from 1 to {@link java.awt.MouseInfo#getNumberOfButtons() MouseInfo.getNumberOfButtons()}.
     * <br>
     * It is recommended to use the {@link InputEvent#getMaskForButton(int) InputEvent.getMaskForButton(button)}
     * method to obtain the mask for any mouse button by its number.
     * </ul>
     * <p>
     * The following standard button masks are also accepted:
     * <ul>
     * <li>{@code InputEvent.BUTTON1_MASK}
     * <li>{@code InputEvent.BUTTON2_MASK}
     * <li>{@code InputEvent.BUTTON3_MASK}
     * </ul>
     * However, it is recommended to use {@code InputEvent.BUTTON1_DOWN_MASK},
     * {@code InputEvent.BUTTON2_DOWN_MASK},  {@code InputEvent.BUTTON3_DOWN_MASK} instead.
     * Either extended {@code _DOWN_MASK} or old {@code _MASK} values
     * should be used, but both those models should not be mixed.
     */
    void mouseRelease(int buttons);
}
