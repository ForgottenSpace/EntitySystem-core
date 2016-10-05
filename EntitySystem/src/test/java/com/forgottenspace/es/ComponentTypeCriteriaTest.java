package com.forgottenspace.es;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.es.EntityException;

public class ComponentTypeCriteriaTest {

    public ComponentTypeCriteriaTest() {
    }

    @Test(expected=EntityException.class)
	public void createNoComponentTypes() {
        new ComponentTypeCriteria();
    }

    @Test(expected=EntityException.class)
	public void createEmptyComponentTypes() {
        new ComponentTypeCriteria(MockEntityComponent.class, null);
    }

    @Test
	public void create() {
        new ComponentTypeCriteria(MockEntityComponent.class);
    }

    @Test
	public void matchesFully() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(MockEntityComponent.class);
        Set<Class<? extends EntityComponent>> compTypeList = new HashSet<Class<? extends EntityComponent>>();
        compTypeList.add(MockEntityComponent.class);
        boolean result = criteria.matches(compTypeList);
        Assert.assertTrue(result);
    }

    @Test
	public void matchesPartly() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(MockEntityComponent.class);
        Set<Class<? extends EntityComponent>> compTypeList = new HashSet<Class<? extends EntityComponent>>();
        compTypeList.add(MockEntityComponent.class);
        compTypeList.add(MatchEntityComponent.class);
        boolean result = criteria.matches(compTypeList);
        Assert.assertTrue(result);
    }

    @Test
	public void matchesNoMatch() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(MockEntityComponent.class);
        Set<Class<? extends EntityComponent>> compTypeList = new HashSet<Class<? extends EntityComponent>>();
        compTypeList.add(MatchEntityComponent.class);
        boolean result = criteria.matches(compTypeList);
        Assert.assertFalse(result);
    }

    class MatchEntityComponent implements EntityComponent{

    }
}
