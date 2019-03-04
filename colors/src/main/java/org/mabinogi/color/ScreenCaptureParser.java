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

import java.awt.Point;
import java.awt.image.BufferedImage;

public interface ScreenCaptureParser {

    /**
     * Parses the palette origin absolute point.
     * @param image the screen capture.
     * @return the origin point.
     */
    Point parsePaletteOrigin(BufferedImage image);
    
    /**
     * Parses the color pickers' relative points, pickers[0] is (0,0), and others
     * relate to pickers[0].
     * @param image the screen capture.
     * @param origin the origin point.
     * @return the pickers' relative points.
     */
    Point[] parseColorPickers(BufferedImage image, Point origin);
}
