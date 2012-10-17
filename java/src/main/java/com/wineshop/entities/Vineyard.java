package com.wineshop.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

@Entity
public class Vineyard extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    @Basic
    private String name;
    
    @Embedded
    private Address address = new Address(); 
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="vineyard", orphanRemoval=true)
    private Set<Wine> wines;

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Wine> getWines() {
        return wines;
    }

    public void setWines(Set<Wine> wines) {
        this.wines = wines;
    }
}