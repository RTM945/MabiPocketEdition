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
import java.io.IOException;

public class MabinogiFactory {
    private static ColorDifferenceCalculator cdc;
    
    private MabinogiFactory() {
        /* hide */
    }
    
    public static synchronized ColorDifferenceCalculator getColorDifferenceCalculator() {
        if (cdc == null) {
            try {
                cdc = (ColorDifferenceCalculator) Class.forName(
                        getConfiguration().getColorDifferenceCalculatorImplClass()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return cdc;
    }
    
    public static ScreenHook getScreenHook() {
        try {
            return MabinogiRobot.getInstance();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyboardHook getKeyboardHook() {
        try {
            return MabinogiRobot.getInstance();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static MouseHook getMouseHook() {
        try {
            return MabinogiRobot.getInstance();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static ScreenCaptureParser getScreenCaptureParser() {
        return ScreenCaptureParserImpl.getInstance();
    }
    
    public static Configuration getConfiguration() {
        return Configuration.getInstance();
    }
    
    public static Language getLanguage() {
        try {
            return Language.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
