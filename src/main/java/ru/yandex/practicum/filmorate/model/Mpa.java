package ru.yandex.practicum.filmorate.model;

public class Mpa {
    private int id;
    private String name;

    public Mpa() {
        id = 1;
    }

    public Mpa(int id) {
        this.id = id;
    }

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}


