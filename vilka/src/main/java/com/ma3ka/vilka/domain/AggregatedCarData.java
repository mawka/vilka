package com.ma3ka.vilka.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CARFINISHED")
public class AggregatedCarData {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "mark", length = 20, nullable = false, columnDefinition="text")
    private String mark;

    @Column(name = "model", length = 20, nullable = false, columnDefinition="text")
    private String model;

    @Column(name = "number", length = 20, nullable = false, columnDefinition="text")
    private String number;

    @Column(name = "details", length = 500, nullable = false, columnDefinition="text")
    private String details;

    public AggregatedCarData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
