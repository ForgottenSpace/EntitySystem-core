package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;

public class OriginComponent implements EntityComponent {

    private final Long originEntityId;

    public OriginComponent(Long originEntityId) {
        this.originEntityId = originEntityId;
    }

    public Long getOriginEntityId() {
        return originEntityId;
    }
}
