package com.forgottenspace.loaders;

import org.apache.commons.lang3.StringUtils;

import com.forgottenspace.parsers.entitytemplate.TemplateLoader;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

public class SceneLoader {

    private final AssetManager assetManager;
    private Node scene;

    public SceneLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
        TemplateLoader.setClassLoader(this.getClass().getClassLoader());
        assetManager.registerLoader(TemplateLoader.class, "etpl", "ETPL");
    }

    public Node loadScene(String sceneFileName) {
    	if (StringUtils.isEmpty(sceneFileName)) {
    		throw new LoaderException("Scene filename is either null or emtpy.");
    	}
        scene = (Node) assetManager.loadModel(sceneFileName);
        parseScene();
        return scene;
    }

    private void parseScene() {
        scene.depthFirstTraversal(new EntitySceneConverter(assetManager));
    }

    
}
