package com.c1se22.publiclaundsmartsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Set;

@Entity
@Table(name = "locations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer id;
    @Column(name = "location_name")
    private String name;
    @Column(name = "city")
    private String city;
    @Column(name = "district")
    private String district;
    @Column(name = "ward")
    private String ward;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "lng")
    private Double lng;
    @Column(name = "lat")
    private Double lat;
    @OneToMany(mappedBy = "location")
    private Set<Machine> machines;
}
