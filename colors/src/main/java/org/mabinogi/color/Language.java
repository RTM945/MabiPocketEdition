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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Language implements LanguageKey {
    private static final String LANG_PATH = "Lang";
    private static final String LANG_FILE = ".properties";
    private static Language instance;
    private final Properties properties;
    
    public Language() throws IOException {
        properties = new Properties();
        String lang = MabinogiFactory.getConfiguration().getLanguage();
        try (InputStream is = new FileInputStream(new File(LANG_PATH, lang + LANG_FILE))) {
            properties.load(is);
        }
    }
    
    public static synchronized Language getInstance() throws IOException {
        if (instance == null) {
            instance = new Language();
        }
        return instance;
    }
    
    public String getString(String key) {
        return properties.getProperty(key);
    }
}
