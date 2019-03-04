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
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Archive {
    private static final String SAVE_PATH = "Save";
    private static final String JMCH_FILE = ".jmch";
    private static final String  MCH_FILE =  ".mch";
    private static final String KEY_COLOR = "color";
    static {
        File save = new File(SAVE_PATH);
        if (!save.exists()) {
            save.mkdir();
        }
    }
    public final Color color;
    
    public Archive(Color color) {
        this.color = color;
    }

    public static Archive open(Component parent) throws IOException {
        JFileChooser jfc = new JFileChooser(SAVE_PATH);
        jfc.setFileFilter(new FileNameExtensionFilter(JMCH_FILE + " & " + MCH_FILE,
                JMCH_FILE.substring(1), MCH_FILE.substring(1)));
        int state = jfc.showOpenDialog(parent);
        if (state == JFileChooser.APPROVE_OPTION) {
            return load(jfc.getSelectedFile());
        } else {
            return null;
        }
    }
    
    public static Archive load(File file) throws IOException {
        if (file.getName().toLowerCase().endsWith(JMCH_FILE)) {
            Properties properties = new Properties();
            try (InputStream is = new FileInputStream(file)) {
                properties.load(is);
                return new Archive(Color.decode(properties.getProperty(KEY_COLOR)));
            }
        } else if (file.getName().toLowerCase().endsWith(MCH_FILE)) {
            return loadMCH(file);
        } else {
            throw new IOException("");
        }
    }
    
    private static Archive loadMCH(File mch) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(mch))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(KEY_COLOR)) {
                    return new Archive(Color.decode("0x" + line.substring(line.indexOf('#') + 1)));
                }
            }
        }
        return null;
    }
    
    public static void save(Archive archive, Component parent) throws IOException {
        JFileChooser jfc = new JFileChooser(SAVE_PATH);
        jfc.setFileFilter(new FileNameExtensionFilter(JMCH_FILE, JMCH_FILE.substring(1)));
        int state = jfc.showSaveDialog(parent);
        if (state == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            save(file.getName().endsWith(JMCH_FILE) ? file : new File(file + JMCH_FILE), archive);
        }
    }
    
    public static void save(File file, Archive archive) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(KEY_COLOR, "0x" + MabinogiColorHelper.toHexString(archive.color.getRGB()));
        try (OutputStream os = new FileOutputStream(file)) {
            properties.store(os, "JMabinogiColorHelper archive");
        }
    }
}
