package com.forgottenspace.appstates;

import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.es.EntityResultSet;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;

public abstract class AbstractEntityControl extends AbstractAppState {
    
    private SimpleApplication application;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        application = (SimpleApplication) app;
    }

    @SuppressWarnings("unchecked")
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
