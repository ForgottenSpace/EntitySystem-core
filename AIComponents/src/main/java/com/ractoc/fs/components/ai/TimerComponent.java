package com.ractoc.fs.components.ai;

import com.jme3.asset.AssetManager;
import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.parsers.ai.AiComponentExit;
import com.ractoc.fs.parsers.ai.AiComponentProperty;

public class TimerComponent extends AiComponent {

    @AiComponentProperty(name = "interval", displayName = "Interval", type = Float.class, shortDescription = "Comma seperated list of Fully Qualified names for the sub script files.")
    private Float interval;
    @AiComponentExit(name = "time", displayName = "Time", type = String.class, shortDescription = "The time interval has expired.")
    private String time;
    private Float expiredTime = new Float(0);

    public TimerComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{"interval"};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"time"};
    }

    @Override
    public void initialiseProperties() {
        interval = Float.valueOf((String) getProp("interval"));
        time = (String) exits.get("time");
    }

    @Override
    public void updateProperties() {
        props.put("interval", interval);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        expiredTime += tpf;
        if (expiredTime >= interval) {
            expiredTime = new Float(0);
            aiScript.setCurrentComponent(time);
        }
    }
}
