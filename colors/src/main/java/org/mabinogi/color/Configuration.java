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
import java.awt.Dimension;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_FILE = "config.properties";
    private static Configuration instance;
    private final Properties properties = new Properties();
    
    private Configuration() throws IOException {
        try (InputStream is = new FileInputStream(CONFIG_FILE)) {
            properties.load(is);
        }
    }
    
    public static synchronized Configuration getInstance() {
        if (instance == null) {
            try {
                instance = new Configuration();
            } catch (IOException e) {
                throw new MabinogiException(CONFIG_FILE + " not found", e);
            }
        }
        return instance;
    }
    
    public String getColorDifferenceCalculatorImplClass() {
        return properties.getProperty("ColorDifferenceCalculator");
    }
    
    public String getLanguage() {
        return properties.getProperty("Language");
    }
    
    public Dimension getFrameSize() {
        String property = properties.getProperty("FrameSize");
        String[] split = property.split("\\*");
        return new Dimension(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
    
    public int getDefaultTargetColor() {
        return Color.decode(properties.getProperty("DefaultTargetColor")).getRGB();
    }
    
    public Dimension getPaletteSize() {
        String property = properties.getProperty("PaletteSize");
        String[] split = property.split("\\*");
        return new Dimension(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
    
    public int getFrameColor() {
        return Color.decode(properties.getProperty("FrameColor")).getRGB();
    }
    
    public int getFrameTolerance() {
        return Integer.parseInt(properties.getProperty("FrameTolerance"));
    }
    
    public int getPickerColor() {
        return Color.decode(properties.getProperty("PickerColor")).getRGB();
    }
    
    public int getPickerTolerance() {
        return Integer.parseInt(properties.getProperty("PickerTolerance"));
    }
    
    public Point[] getPickerMatrix() {
        List<Point> matrix = new ArrayList<>();
        String property = properties.getProperty("PickerMatrix");
        for (String split : property.split("\\|")) {
            String[] xy = split.split("\\,");
            matrix.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
        return matrix.toArray(new Point[0]);
    }
}
