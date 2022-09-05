package com.ma3ka.vilka.domain;

import javax.persistence.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Table(name = "cardetailed")
public class CarDetailed {

    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Full_Detail", length = 500, nullable = false, columnDefinition="text")
    private String fullDetail;

    public CarDetailed(String fullDetail) {
        this.fullDetail = fullDetail;
    }

    public CarDetailed() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullDetail() {
        return fullDetail;
    }

    public void setFullDetail(String fullDetail) {
        this.fullDetail = fullDetail;
    }
}
