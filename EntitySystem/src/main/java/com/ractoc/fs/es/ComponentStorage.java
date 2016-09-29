package com.ractoc.fs.es;

public interface ComponentStorage {
    void storeComponentForEntity(Long entityId, EntityComponent entityComponent);
    EntityComponent loadComponentForEntity(Long entityId);
    void changeComponentForEntity(Long entityId, EntityComponent entityComponent);
    void removeComponentForEntity(Long entityId);
}
