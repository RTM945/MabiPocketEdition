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

public class ColorDifferenceCalculatorImpl2 implements ColorDifferenceCalculator {
    private static final int R = 3, G = 4, B = 2;

    @Override
    public double calculateDifference(Color benchmark, Color contrast) {
        return Math.sqrt(
                R * Math.pow(contrast.getRed() - benchmark.getRed(), 2) +
                G * Math.pow(contrast.getGreen() - benchmark.getGreen(), 2) +
                B * Math.pow(contrast.getBlue() - benchmark.getBlue(), 2));
    }
}
