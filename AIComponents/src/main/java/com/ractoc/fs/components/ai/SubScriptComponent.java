package com.ractoc.fs.components.ai;

import static com.ractoc.fs.components.ai.AiConstants.ASSET_MANAGER_PROPERTY;
import static com.ractoc.fs.components.ai.AiConstants.SCRIPTS_PROPERTY;
import static com.ractoc.fs.components.ai.AiConstants.SUB_SCRIPTS_EXIT;

import java.util.ArrayList;
import java.util.List;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.ai.AiScript;
import com.forgottenspace.parsers.ai.AiComponentExit;
import com.forgottenspace.parsers.ai.AiComponentProperty;
import com.jme3.asset.AssetManager;

public class SubScriptComponent extends AiComponent {

    private List<AiScript> scripts = new ArrayList<AiScript>();
    @AiComponentProperty(name = SCRIPTS_PROPERTY, displayName = "Sub Scripts", type = String.class, shortDescription = "Comma seperated list of Fully Qualified names for the sub script files.")
    private String scriptNames;
    @AiComponentExit(name = SUB_SCRIPTS_EXIT, displayName = "Exit", type = String.class, shortDescription = "Called when all the seperate sub scripts have finished.")
    private String exit;

    public SubScriptComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{SCRIPTS_PROPERTY};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{SUB_SCRIPTS_EXIT};
    }

    @Override
    public void initialiseProperties() {
        scriptNames = (String) getProp(SCRIPTS_PROPERTY);
        exit = (String) exits.get(SUB_SCRIPTS_EXIT);
        AssetManager assetManager = (AssetManager) getProp(ASSET_MANAGER_PROPERTY);

        if (assetManager != null) {
            String[] scriptNameList = scriptNames.split(",");
            for (String scriptName : scriptNameList) {
                AiScript script = (AiScript) assetManager.loadAsset(scriptName.trim());
                script.initialise(entity);
                script.setSubScript(true);
                script.getGlobalProps().putAll(aiScript.getGlobalProps());
                scripts.add(script);
            }
        }
    }

    @Override
    public void updateProperties() {
        props.put(SCRIPTS_PROPERTY, scriptNames);
    }

    @Override
    public void update(float tpf) {
        boolean stillRunning = true;
        for (AiScript script : scripts) {
            script.update(tpf);
            stillRunning = !script.isFinished();
        }
        if (!stillRunning) {
            aiScript.setCurrentComponent(exit);
        }
    }
}
