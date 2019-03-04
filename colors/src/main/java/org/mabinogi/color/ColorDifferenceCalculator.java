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

public interface ColorDifferenceCalculator {
    
    /**
     * Calculates color difference. You'd better return the value at
     * [0, MabinogiColorHelper.MAX_TOLERANCE] mostly.
     * @param benchmark the benchmark color.
     * @param contrast the constrast color.
     * @return color difference.
     */
    double calculateDifference(Color benchmark, Color contrast);
}
