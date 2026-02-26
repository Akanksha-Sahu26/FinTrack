package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;

    private BigDecimal monthlyIncome;

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Expense> expenses;

}
