package com.ractoc.fs.parsers.entitytemplate;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;
import java.io.InputStream;

public class TemplateLoader implements AssetLoader {

    private static ClassLoader classLoader;

    public static void setClassLoader(ClassLoader loader) {
        TemplateLoader.classLoader = loader;
    }

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        TemplateParser parser = new TemplateParser();
        if (classLoader != null) {
            parser.setClassLoader(classLoader);
        }
        InputStream is = assetInfo.openStream();
        EntityTemplate tpl = parser.parse(is);
        is.close();
        return tpl;
    }
}
