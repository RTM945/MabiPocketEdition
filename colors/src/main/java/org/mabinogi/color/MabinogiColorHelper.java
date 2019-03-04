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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MabinogiColorHelper extends JFrame implements ActionListener, SwingConstants, LanguageKey {
    public static final String VERSION = "v1.2 Beta";
    public static final int UNKNOWN_COLOR = 0xF0000000; /* the stupid black frame cover the palette by one pixel per edge */
    public static final int MAX_TOLERANCE = 1024 - 1;
    private static final long serialVersionUID = 1L;
    
    private final Dimension fSize;
    private final Dimension pSize;
    private final ColorDifferenceCalculator cdc;
    private final ScreenHook screen;
    private final KeyboardHook keyboard;
    private final MouseHook mouse;
    private final ScreenCaptureParser parser;
    private final Language language;
    private final Dimension screen_size;
    private final JMenuBar menuBar;
    private final JMenu fileMenu, helpMenu;
    private final JMenuItem openMenu, saveMenu, exitMenu, aboutMenu;
    private final Container contentPane;
    private final SpringLayout layout;
    private final JPanel canvas;
    private final JLabel tcLabel, toLabel;
    private final JSpinner spinner;
    private final JPanel pPanel;
    private final TitledBorder pBorder;
    private final MabinogiColorPicker tPicker, picker1, picker2, picker3, picker4, picker5;
    private final JTextArea message;
    
    private int target_color;
    private int tolerance;
    private BufferedImage palette;
    private Point origin;
    private Point[] pickers;
    private List<Target> targets;
    private List<Plan> plans;
    private int index;
    private Plan current;
    
    private class Target extends Point implements Comparable<Target> {
        private static final long serialVersionUID = 1L;
        private final int cd;
        
        private Target(int x, int y, int cd) {
            super(x, y);
            this.cd = cd;
        }
        
        @Override
        public int compareTo(Target o) {
            if (cd != o.cd) {
                return cd - o.cd;
            } else {
                return x - o.x == 0 ? y - o.y : x - o.x ;
            }
        }
        
        @Override
        public String toString() {
            return toHexString(palette.getRGB(x, y)) + " " + cd;
        }
    }
    
    private class Plan extends Point implements Comparable<Plan> {
        private static final long serialVersionUID = 1L;
        private final int[] colors = new int[pickers.length];
        private int cd = MAX_TOLERANCE;
        private int count;

        private Plan(Point target, Point picker) {
            x = adjustX(target.x - picker.x);
            y = adjustY(target.y - picker.y);
            init();
        }
        
        private void init() {
            for (int index = 0; index < pickers.length; ++index) {
                int px = adjustX(x + pickers[index].x);
                int py = adjustY(y + pickers[index].y);
                if (px == 0 || px == pSize.width - 1 || py == 0 || py == pSize.height - 1) {
                    colors[index] = UNKNOWN_COLOR;
                    continue;
                } else {
                    colors[index] = palette.getRGB(px, py);
                    int ti = targets.indexOf(new Point(px, py));
                    if (ti != -1) {
                        cd = Math.min(cd, targets.get(ti).cd);
                        ++count;
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            return "" + cd;
        }

        @Override
        public int compareTo(Plan o) {
            if (cd != o.cd) {
                return cd - o.cd;
            } else if (count != o.count) {
                return o.count - count;
            } else {
                return x - o.x == 0 ? y - o.y : x - o.x ;
            }
        }
    }
    
    public MabinogiColorHelper() throws AWTException {
        super(MabinogiFactory.getLanguage().getString(TITLE) + VERSION);
        screen   = MabinogiFactory.getScreenHook();
        cdc      = MabinogiFactory.getColorDifferenceCalculator();
        keyboard = MabinogiFactory.getKeyboardHook();
        mouse    = MabinogiFactory.getMouseHook();
        parser   = MabinogiFactory.getScreenCaptureParser();
        language = MabinogiFactory.getLanguage();
        Configuration  config = MabinogiFactory.getConfiguration();
        fSize        = config.getFrameSize(); 
        target_color = config.getDefaultTargetColor();
        pSize        = config.getPaletteSize();
        screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        menuBar   = new JMenuBar();
        fileMenu  = new JMenu(language.getString(FILE) + "(F)");
        helpMenu  = new JMenu(language.getString(HELP) + "(H)");
        openMenu  = new JMenuItem(language.getString(OPEN) + "(O)", KeyEvent.VK_O);
        saveMenu  = new JMenuItem(language.getString(SAVE) + "(S)", KeyEvent.VK_S);
        exitMenu  = new JMenuItem(language.getString(EXIT) + "(X)", KeyEvent.VK_X);
        aboutMenu = new JMenuItem(language.getString(ABOUT) + "(A)", KeyEvent.VK_A);
        contentPane = getContentPane();
        layout = new SpringLayout();
        canvas = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                if (palette != null) {
                    g.drawImage(palette, 0, 0, null);
                    if (current != null) {
                        /* draw crosses */
                        g.setColor(Color.BLACK);
                        for (Point p : pickers) {
                            int px = adjustX(current.x + p.x);
                            int py = adjustY(current.y + p.y);
                            g.drawLine(px, py - 5, px, py + 5);
                            g.drawLine(px - 5, py, px + 5, py);
                        }
                    }
                }
            }
        };
        tcLabel = new JLabel(language.getString(COLOR), RIGHT);
        toLabel = new JLabel(language.getString(TOLERANCE), RIGHT);
        spinner = new JSpinner();
        message = new JTextArea();
        pPanel  = new JPanel(new GridLayout(1, 5, 4, 0));
        pBorder = BorderFactory.createTitledBorder("0 / 0");
        tPicker = new MabinogiColorPicker(new Color(target_color));
        picker1 = new MabinogiColorPicker(null);
        picker2 = new MabinogiColorPicker(null);
        picker3 = new MabinogiColorPicker(null);
        picker4 = new MabinogiColorPicker(null);
        picker5 = new MabinogiColorPicker(null);
        init();
    }
    
    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(layout);
        setSize(fSize);
        setResizable(false);
        setLocationRelativeTo(null);
        setJMenuBar(menuBar);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.setDisplayedMnemonicIndex(fileMenu.getText().length() - 2);
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.setDisplayedMnemonicIndex(helpMenu.getText().length() - 2);
        openMenu.addActionListener(this);
        openMenu.setDisplayedMnemonicIndex(openMenu.getText().length() - 2);
        saveMenu.addActionListener(this);
        saveMenu.setDisplayedMnemonicIndex(saveMenu.getText().length() - 2);
        exitMenu.addActionListener(this);
        exitMenu.setDisplayedMnemonicIndex(exitMenu.getText().length() - 2);
        aboutMenu.addActionListener(this);
        aboutMenu.setDisplayedMnemonicIndex(aboutMenu.getText().length() - 2);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        fileMenu.add(openMenu);
        fileMenu.add(saveMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);
        helpMenu.add(aboutMenu);
        keyboard.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_HOME: {
                        BufferedImage capture = screen.createScreenCapture(new Rectangle(screen_size));
                        origin = parser.parsePaletteOrigin(capture);
                        if (origin == null) {
                            throw new MabinogiException(language.getString(HOME_ERROR));
                        }
                        palette = capture.getSubimage(origin.x, origin.y, pSize.width, pSize.height);
                        pickers = new Point[] {new Point(0, 0)};
                        targets = null;
                        plans = null;
                        message(language.getString(END), JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    case KeyEvent.VK_END: {
                        if (palette == null) {
                            throw new MabinogiException(language.getString(HOME));
                        }
                        BufferedImage capture = screen.createScreenCapture(new Rectangle(screen_size));
                        pickers = parser.parseColorPickers(capture, origin);
                        findTargets();
                        findPlans();
                        if (plans.isEmpty()) {
                            message(language.getString(NO_PLAN)
                                    + language.getString(DRAG)
                                    + language.getString(INSERT),
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            message(String.format(language.getString(PLAN), plans.size())
                                    + language.getString(PAGE)
                                    + language.getString(DRAG)
                                    + language.getString(INSERT),
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        index = 0;
                        /* falls */
                    }
                    case KeyEvent.VK_PAGE_UP: {
                        if (plans != null && index > 0) {
                            --index;
                        }
                        pageUpDown();
                        break;
                    }
                    case KeyEvent.VK_PAGE_DOWN: {
                        if (plans != null && index < plans.size() - 1) {
                            ++index;
                        }
                        pageUpDown();
                        break;
                    }
                    case KeyEvent.VK_INSERT: {
                        insert();
                        break;
                    }
                    default:
                        return;
                    }
                } catch (MabinogiException me) {
                    message(me.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        canvas.setPreferredSize(pSize);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (palette != null) {
                    current = new Plan(e.getPoint(), pickers[0]);
                    displayPlan();
                }
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                x = x >= pSize.width ? pSize.width - 1 : x < 0 ? 0 : x ;
                int y = e.getY();
                y = y >= pSize.height ? pSize.height - 1 : y < 0 ? 0 : y ;
                if (palette != null) {
                    current = new Plan(new Point(x, y), pickers[0]);
                    displayPlan();
                }
            }
        });
        tPicker.addActionListener(new ActionListener() {
            private boolean ok;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == tPicker) {
                    ok = false;
                    JColorChooser chooser = new MabinogiColorChooser(tPicker.getBackground());
                    JColorChooser.createDialog(MabinogiColorHelper.this, language.getString(CHOOSER), true, chooser, this, null)
                        .setVisible(true);
                    if (ok) {
                        target_color = chooser.getColor().getRGB();
                        tPicker.setBackground(target_color);
                    }
                } else {
                    ok = true;
                }
            }
        });
        spinner.setModel(new SpinnerNumberModel(0, 0, MAX_TOLERANCE, 1));
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tolerance = Integer.parseInt(spinner.getValue().toString());
            }
        });
        message.setEditable(false);
        message.setBackground(tcLabel.getBackground());
        message.setLineWrap(true);
        message.setText(language.getString(HOME));
        picker1.addActionListener(this);
        picker2.addActionListener(this);
        picker3.addActionListener(this);
        picker4.addActionListener(this);
        picker5.addActionListener(this);
        
        pPanel.add(picker1);
        pPanel.add(picker2);
        pPanel.add(picker3);
        pPanel.add(picker4);
        pPanel.add(picker5);
        pPanel.setBorder(pBorder);
        contentPane.add(canvas);
        contentPane.add(tcLabel);
        contentPane.add(tPicker);
        contentPane.add(toLabel);
        contentPane.add(spinner);
        contentPane.add(message);
        contentPane.add(pPanel);
        doSpringLayout();
    }
    
    private void doSpringLayout() {
        layout.putConstraint(SpringLayout.WEST, canvas, 10, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, canvas, 10, SpringLayout.NORTH, contentPane);
        
        layout.putConstraint(SpringLayout.WEST, tcLabel, 5, SpringLayout.EAST, canvas);
        layout.putConstraint(SpringLayout.NORTH, tcLabel, 10, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, tcLabel, 0, SpringLayout.SOUTH, tPicker);
        
        layout.putConstraint(SpringLayout.WEST, tPicker, 5, SpringLayout.EAST, tcLabel);
        layout.putConstraint(SpringLayout.NORTH, tPicker, 10, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, tPicker, -10, SpringLayout.EAST, contentPane);
        
        layout.putConstraint(SpringLayout.WEST, toLabel, 0, SpringLayout.WEST, tcLabel);
        layout.putConstraint(SpringLayout.NORTH, toLabel, 10, SpringLayout.SOUTH, tcLabel);
        layout.putConstraint(SpringLayout.EAST, toLabel, 0, SpringLayout.EAST, tcLabel);
        layout.putConstraint(SpringLayout.SOUTH, toLabel, 0, SpringLayout.SOUTH, spinner);
        
        layout.putConstraint(SpringLayout.WEST, spinner, 0, SpringLayout.WEST, tPicker);
        layout.putConstraint(SpringLayout.NORTH, spinner, 0, SpringLayout.NORTH, toLabel);
        layout.putConstraint(SpringLayout.EAST, spinner, 0, SpringLayout.EAST, tPicker);
        layout.putConstraint(SpringLayout.SOUTH, spinner, 24, SpringLayout.NORTH, spinner);
        
        layout.putConstraint(SpringLayout.WEST, message, 0, SpringLayout.WEST, tcLabel);
        layout.putConstraint(SpringLayout.NORTH, message, 20, SpringLayout.SOUTH, toLabel);
        layout.putConstraint(SpringLayout.EAST, message, 0, SpringLayout.EAST, spinner);
        layout.putConstraint(SpringLayout.SOUTH, message, 0, SpringLayout.SOUTH, canvas);
        
        layout.putConstraint(SpringLayout.WEST, pPanel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, pPanel, -50, SpringLayout.SOUTH, pPanel);
        layout.putConstraint(SpringLayout.EAST, pPanel, -5, SpringLayout.EAST, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, pPanel, -5, SpringLayout.SOUTH, contentPane);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == openMenu) {
            try {
                Archive archive = Archive.open(this);
                if (archive != null) {
                    target_color = archive.color.getRGB();
                    tPicker.setBackground(archive.color);
                }
            } catch (IOException ioe) {
                message(language.getString(IO_ERROR), JOptionPane.ERROR_MESSAGE);
            }
        } else if (obj == saveMenu) {
            try {
                Archive.save(new Archive(new Color(target_color)), this);
            } catch (IOException ioe) {
                message(language.getString(IO_ERROR), JOptionPane.ERROR_MESSAGE);
            }
        } else if (obj == exitMenu) {
            System.exit(0);
        } else if (obj == aboutMenu) {
            new MabinogiAboutDialog(this).setVisible(true);
        } else if (obj instanceof MabinogiColorPicker) {
            MabinogiColorPicker mcp = (MabinogiColorPicker) obj;
            if (!mcp.isUnknown()) {
                Color color = mcp.getBackground();
                tPicker.setBackground(color);
                target_color = color.getRGB();
            }
        }
    }
    
    private void findTargets() {
        targets = new ArrayList<>();
        /* the stupid black frame cover the palette by one pixel per edge, so that [1, size - 2] */
        int w = pSize.width - 1;
        int h = pSize.height - 1;
        for (int x = 1; x < w; ++x) {
            for (int y = 1; y < h; ++y) {
                int color = palette.getRGB(x, y);
                int cd = calculateCD(color);
                if (cd <= tolerance) {
                    targets.add(new Target(x, y, cd));
                }
            }
        }
        Collections.sort(targets);
    }
    
    private int calculateCD(int rgb) {
        if (tolerance == 0) {
            return rgb == target_color ? 0 : MAX_TOLERANCE;
        }
        return (int) cdc.calculateDifference(new Color(target_color), new Color(rgb));
    }
    
    private void findPlans() {
        Set<Plan> set = new TreeSet<>();
        for (Point target : targets) {
            for (Point picker : pickers) {
                set.add(new Plan(target, picker));
            }
        }
        plans = new ArrayList<>(set);
    }
    
    private void displayPlan() {
        switch (current.colors.length) {
        case 5:
            picker5.setBackground(current.colors[4]);
        case 4:
            picker4.setBackground(current.colors[3]);
        case 3:
            picker3.setBackground(current.colors[2]);
        case 2:
            picker2.setBackground(current.colors[1]);
        case 1:
            picker1.setBackground(current.colors[0]);
        default:
            canvas.repaint();
        }
    }
    
    private void pageUpDown() {
        if (origin == null) {
            throw new MabinogiException(language.getString(HOME));
        }
        if (plans == null) {
            return;
        }
        if (plans.isEmpty()) {
            pBorder.setTitle("0 / 0");
        } else {
            current = plans.get(index);
            pBorder.setTitle(String.format(language.getString(HIT),
                    current.count, index + 1, plans.size()));
            displayPlan();
        }
        pPanel.repaint();
    }
    
    private void insert() {
        if (origin == null || current == null) {
            return;
        }
        mouse.mouseMove(origin.x + current.x, origin.y + current.y);
    }
    
    private int adjustX(int x) {
        if (x < 0) {
            return x += pSize.width;
        } else if (x >= pSize.width) {
            return x -= pSize.width;
        }
        return x;
    }
    
    private int adjustY(int y) {
        if (y < 0) {
            return y += pSize.height;
        } else if (y >= pSize.height) {
            return y -= pSize.height;
        }
        return y;
    }
    
    public void message(String msg, int messageType) {
        message.setText(msg);
        repaint();
    }
    
    public static void error(Component parentComponent, Object message, int messageType) {
        JOptionPane.showMessageDialog(parentComponent, message, "Error", messageType);
    }
    
    public static String toHexString(int color) {
        if (color == UNKNOWN_COLOR) {
            return MabinogiFactory.getLanguage().getString(UNKNOWN);
        } else {
            String hex = Integer.toHexString(color | 0xFF000000).toUpperCase();
            return hex.substring(2);
        }
    }
    
    public static void main(String[] args) {
        try {
            new MabinogiColorHelper().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            error(null, e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
