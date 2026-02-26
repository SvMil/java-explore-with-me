package ru.practicum.ewm.user.model;

import jakarta.validation.Valid;
import lombok.*;
import jakarta.persistence.*;

@Table(name = "users")
@Entity
@Setter
@Getter
@Valid
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 250)
    private String name;

    @Column(name = "email", nullable = false, length = 254, unique = true)
    private String email;
}
