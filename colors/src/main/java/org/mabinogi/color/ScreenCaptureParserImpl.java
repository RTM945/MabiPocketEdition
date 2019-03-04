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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ScreenCaptureParserImpl implements ScreenCaptureParser, LanguageKey {
    private static final int CENTER_PICKER_AREA = 64;
    private static ScreenCaptureParserImpl instance;
    private final ColorDifferenceCalculator cdc;
    private final Color frame_color;
    private final int frame_tolerance;
    private final Color picker_color;
    private final int picker_tolerance;
    private final Point[] picker_matrix;
    private final Dimension size;
    
    private ScreenCaptureParserImpl() {
        cdc = MabinogiFactory.getColorDifferenceCalculator();
        Configuration config = MabinogiFactory.getConfiguration();
        frame_color      = new Color(config.getFrameColor());
        frame_tolerance  = config.getFrameTolerance();
        picker_color     = new Color(config.getPickerColor());
        picker_tolerance = config.getPickerTolerance();
        picker_matrix    = config.getPickerMatrix();
        size             = config.getPaletteSize();
    }
    
    public static synchronized ScreenCaptureParserImpl getInstance() {
        if (instance == null) {
            instance = new ScreenCaptureParserImpl();
        }
        return instance;
    }
    
    @Override
    public Point parsePaletteOrigin(BufferedImage capture) {
        int width  = capture.getWidth();
        int height = capture.getHeight();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (isOrigin(capture, x, y)) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }
    
    private boolean isOrigin(BufferedImage capture, int x, int y) {
        if (x + size.width - 1 >= capture.getWidth() || y + size.height - 1 >= capture.getHeight()) {
            return false;
        }
        double cd;
        for (int offset = 0; offset < size.width; ++offset) {
            cd = cdc.calculateDifference(frame_color, new Color(capture.getRGB(x + offset, y)));
            if (cd > frame_tolerance) {
                return false;
            }
            cd = cdc.calculateDifference(frame_color, new Color(capture.getRGB(x + offset, y + size.height - 1)));
            if (cd > frame_tolerance) {
                return false;
            }
        }
        for (int offset = 0; offset < size.height; ++offset) {
            cd = cdc.calculateDifference(frame_color, new Color(capture.getRGB(x, y + offset)));
            if (cd > frame_tolerance) {
                return false;
            }
            cd = cdc.calculateDifference(frame_color, new Color(capture.getRGB(x + size.width - 1, y + offset)));
            if (cd > frame_tolerance) {
                return false;
            }
        }
        /* fix a bug 1.1 -> 1.2 */
        cd = cdc.calculateDifference(frame_color, new Color(capture.getRGB(x + size.width / 2, y + size.height / 2)));
        return cd > frame_tolerance;
    }

    @Override
    public Point[] parseColorPickers(BufferedImage capture, Point origin) {
        List<Point> pickers = new ArrayList<>();
        /* the stupid devCat moves the pickers to width + 1 & height + 1 */
        int bx = origin.x + size.width + 1;
        int by = origin.y + size.height + 1;
        for (int x = origin.x; x < bx; ++x) {
            for (int y = origin.y; y < by; ++y) {
                if (isPicker(capture, x, y)) {
                    pickers.add(new Point(x - origin.x, y - origin.y));
                }
            }
        }
        if (pickers.size() != 5) {
            throw new MabinogiException(MabinogiFactory.getLanguage().getString(END_ERROR));
        }
        return adjustPickers(pickers);
    }
    
    private boolean isPicker(BufferedImage capture, int x, int y) {
        for (Point point : picker_matrix) {
            double cd = cdc.calculateDifference(picker_color, new Color(capture.getRGB(x + point.x, y + point.y)));
            if (cd > picker_tolerance) {
                return false;
            }
        }
        return true;
    }
    
    private Point[] adjustPickers(List<Point> pickers) {
        /* suppose that only 5 pickers */
        for (int index = 0; index < 5; ++index) {
            List<Point> copy = new ArrayList<>(pickers);
            Point[] ps = new Point[5];
            /* suppose it is the center picker */
            ps[0] = copy.remove(index);
            /* picker number found */
            boolean p2f = false;
            boolean p3f = false;
            boolean p4f = false;
            boolean p5f = false;
            for (int ci = 0; ci < 4; ++ci) {
                Point p;
                if (in3rdQuadrant(ps[0], p = new Point(copy.get(ci)))) {
                    ps[1] = p;
                    p2f = true;
                } else if (in4thQuadrant(ps[0], p = new Point(copy.get(ci)))) {
                    ps[2] = p;
                    p3f = true;
                } else if (in2ndQuadrant(ps[0], p = new Point(copy.get(ci)))) {
                    ps[3] = p;
                    p4f = true;
                } else if (in1stQuadrant(ps[0], p = new Point(copy.get(ci)))) {
                    ps[4] = p;
                    p5f = true;
                } else {
                    break;
                }
            }
            if (p2f && p3f && p4f && p5f) {
                ps[0].x = 0;
                ps[0].y = 0;
                return ps;
            }
        }
        return null;
    }
    
    
    /*
     *     |
     *   3 | 4
     * ----o----> x
     *   2 | 1
     *     |
     *     y
     */
    private boolean inQuadrant(Point o, Point p, int qx, int qy) {
        p.x -= o.x;
        p.y -= o.y;
        if (p.x * qx > 0 && p.x * qx < CENTER_PICKER_AREA) {
            /* do nothing */
        } else if ((p.x + size.width) * qx > 0
                && (p.x + size.width) * qx < CENTER_PICKER_AREA) {
            p.x += size.width;
        } else if ((p.x - size.width) * qx > 0
                && (p.x - size.width) * qx < CENTER_PICKER_AREA) {
            p.x -= size.width;
        } else {
            return false;
        }
        if (p.y * qy > 0 && p.y * qy < CENTER_PICKER_AREA) {
            /* do nothing */
        } else if ((p.y + size.height) * qy > 0
                && (p.y + size.height) * qy < CENTER_PICKER_AREA) {
            p.y += size.height;
        } else if ((p.y - size.height) * qy > 0
                && (p.y - size.height) * qy < CENTER_PICKER_AREA) {
            p.y -= size.height;
        } else {
            return false;
        }
        return true;
    }
    
    private boolean in1stQuadrant(Point o, Point p) {
        return inQuadrant(o, p, 1, 1);
    }
    
    private boolean in2ndQuadrant(Point o, Point p) {
        return inQuadrant(o, p, -1, 1);
    }
    
    private boolean in3rdQuadrant(Point o, Point p) {
        return inQuadrant(o, p, -1, -1);
    }
    
    private boolean in4thQuadrant(Point o, Point p) {
        return inQuadrant(o, p, 1, -1);
    }
}
