package com.amf.connectsong.model;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roulette")
public class Roulette implements Serializable{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @Basic(optional = false)
    @OneToMany(mappedBy = "roulette", cascade = CascadeType.ALL)
    private Set<Album> albums;

    @Basic(optional = true)
    @OneToMany(mappedBy = "historic", cascade = CascadeType.ALL)
    private Set<Album> historic;

    public Roulette(User user, Set<Album> albums) {
        this.user = user;
        this.albums = albums;
    }
}
