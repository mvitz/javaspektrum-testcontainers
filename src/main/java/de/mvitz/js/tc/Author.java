package de.mvitz.js.tc;

public class Author {

    private Long id;
    private final String name;

    public Author(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
