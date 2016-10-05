package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;

public class OriginComponent implements EntityComponent {

    private final Long originEntityId;

    public OriginComponent(Long originEntityId) {
        this.originEntityId = originEntityId;
    }

    public Long getOriginEntityId() {
        return originEntityId;
    }
}
