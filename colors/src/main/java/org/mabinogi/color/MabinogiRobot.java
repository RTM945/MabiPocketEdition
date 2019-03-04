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

import java.awt.AWTException;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MabinogiRobot extends Robot implements ScreenHook, KeyboardHook, MouseHook,
KeyEventPostProcessor {
    private static MabinogiRobot instance;
    private final int auto_delay = 500;
    private KeyListener keyListener;

    private MabinogiRobot() throws AWTException {
        super();
        setAutoDelay(auto_delay);
    }
    
    public static synchronized MabinogiRobot getInstance() throws AWTException {
        if (instance == null) {
            instance = new MabinogiRobot();
        }
        return instance;
    }
    
    @Override
    public void addKeyListener(KeyListener kl) {
        if (keyListener == null) {
            keyListener = kl;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
        }
    }

    @Override
    public boolean postProcessKeyEvent(KeyEvent e) {
        switch (e.getID()) {
        case KeyEvent.KEY_TYPED:
            keyListener.keyTyped(e);
            break;
        case KeyEvent.KEY_PRESSED:
            keyListener.keyPressed(e);
            break;
        case KeyEvent.KEY_RELEASED:
            keyListener.keyReleased(e);
            break;
        }
        return true;
    }
}
