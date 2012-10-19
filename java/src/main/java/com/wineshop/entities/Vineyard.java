package com.wineshop.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Vineyard extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    @Basic
    @NotNull
    @Size(min=5, max=100, message="The name must contain between {min} and {max} characters")
    private String name;
    
    @Embedded
    @Valid
    private Address address = new Address(); 
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="vineyard", orphanRemoval=true)
    @Valid
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