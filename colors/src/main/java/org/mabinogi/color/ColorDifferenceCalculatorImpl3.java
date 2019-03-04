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

public class ColorDifferenceCalculatorImpl3 implements ColorDifferenceCalculator {
    private final int AMPLIFY = 1000;

    @Override
    public double calculateDifference(Color benchmark, Color contrast) {
        /*
         * Vector angular distance measure for indexing and retrieval of color, 1999,
         * by Androutsos D; Plataniotis K N; Venetsanopoulos A N
         */
        double br = benchmark.getRed();
        double bg = benchmark.getGreen();
        double bb = benchmark.getBlue();
        double cr = contrast.getRed();
        double cg = contrast.getGreen();
        double cb = contrast.getBlue();
        double product = (br * cr + bg * cg + bb * cb);
        double acos;
        if (product == 0) {
            acos = 0;
        } else {
            acos = Math.acos(product / (Math.sqrt(br * br + bg * bg + bb * bb) * Math.sqrt(cr * cr + cg * cg + cb * cb)));
        }
        double d1 = 1 - 2 / Math.PI * acos;
        double difference = Math.sqrt(Math.pow(br - cr, 2) + Math.pow(bg - cg, 2) + Math.pow(bb - cb, 2));
        double d2 = (1 - difference / Math.sqrt(3 * 255 * 255));
        return AMPLIFY * (1 - d1 * d2);
    }
}
