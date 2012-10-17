package com.wineshop.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Basic;

@Embeddable
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic
    private String address;    
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String adresse) {
        this.address = adresse;
    }
}
