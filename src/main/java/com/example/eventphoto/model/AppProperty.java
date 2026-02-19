package com.example.eventphoto.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppProperty {

    @Id
    @Column(name = "property_key", length = 100)
    private String propertyKey;

    @Column(name = "property_value", length = 500)
    private String propertyValue;

    @Column(length = 255)
    private String description;
}
