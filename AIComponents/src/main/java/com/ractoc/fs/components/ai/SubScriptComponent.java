package com.ractoc.fs.components.ai;

import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.parsers.ai.AiComponentExit;
import com.ractoc.fs.parsers.ai.AiComponentProperty;
import java.util.ArrayList;
import java.util.List;

public class SubScriptComponent extends AiComponent {

    private List<AiScript> scripts = new ArrayList<AiScript>();
    @AiComponentProperty(name = "scripts", displayName = "Su Scripts", type = String.class, shortDescription = "Comma seperated list of Fully Qualified names for the sub script files.")
    private String scriptNames;
    @AiComponentExit(name = "exit", displayName = "Exit", type = String.class, shortDescription = "Called when all the seperate sub scripts have finished.")
    private String exit;

    public SubScriptComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{"scripts"};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"exit"};
    }

    @Override
    public void initialiseProperties() {
        scriptNames = (String) getProp("scripts");
        exit = (String) exits.get("exit");

        if (assetManager != null) {
            String[] scriptNameList = scriptNames.split(",");
            for (String scriptName : scriptNameList) {
                AiScript script = (AiScript) assetManager.loadAsset(scriptName.trim());
                script.initialise(entity, assetManager);
                script.setSubScript(true);
                script.getGlobalProps().putAll(aiScript.getGlobalProps());
                scripts.add(script);
            }
        }
    }

    @Override
    public void updateProperties() {
        props.put("scripts", scriptNames);
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
