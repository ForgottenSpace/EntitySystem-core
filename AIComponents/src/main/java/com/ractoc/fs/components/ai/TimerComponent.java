package com.ractoc.fs.components.ai;

import static com.ractoc.fs.components.ai.AiConstants.INTERVALPROPERTY;
import static com.ractoc.fs.components.ai.AiConstants.TIME_EXIT;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.parsers.ai.AiComponentExit;
import com.forgottenspace.parsers.ai.AiComponentProperty;

public class TimerComponent extends AiComponent {

    @AiComponentProperty(name = INTERVALPROPERTY, displayName = "Interval", type = Float.class, shortDescription = "Comma seperated list of Fully Qualified names for the sub script files.")
    private Float interval;
    @AiComponentExit(name = TIME_EXIT, displayName = "Time", type = String.class, shortDescription = "The time interval has expired.")
    private String time;
    private Float expiredTime = 0.0F;

    public TimerComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{INTERVALPROPERTY};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{TIME_EXIT};
    }

    @Override
    public void initialiseProperties() {
        interval = Float.valueOf((String) getProp(INTERVALPROPERTY));
        time = (String) exits.get(TIME_EXIT);
    }

    @Override
    public void updateProperties() {
        props.put(INTERVALPROPERTY, interval);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        expiredTime += tpf;
        if (expiredTime >= interval) {
            expiredTime = 0.0F;
            aiScript.setCurrentComponent(time);
        }
    }
}
