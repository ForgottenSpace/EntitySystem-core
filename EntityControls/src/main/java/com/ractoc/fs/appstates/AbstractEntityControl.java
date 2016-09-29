package com.ractoc.fs.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.es.EntityResultSet;

public abstract class AbstractEntityControl extends AbstractAppState {
    
    private SimpleApplication application;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        application = (SimpleApplication) app;
    }

    protected EntityResultSet queryEntityResultSet(Class<? extends EntityComponent>... componentTypes) {
        return Entities.getInstance().queryEntities(new ComponentTypeCriteria(componentTypes));
    }

    protected SimpleApplication getApplication() {
        return application;
    }
    
    protected AssetManager getAssetManager() {
        return getApplication().getAssetManager();
    }
    
}
