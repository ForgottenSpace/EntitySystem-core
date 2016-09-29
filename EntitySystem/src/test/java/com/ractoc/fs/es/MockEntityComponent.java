package com.ractoc.fs.es;

public class MockEntityComponent implements EntityComponent {

    @Override
    public int hashCode() {
        return 5;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

}
