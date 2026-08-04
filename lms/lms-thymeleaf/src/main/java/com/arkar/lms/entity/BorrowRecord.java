package com.arkar.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Book book;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status;

    @Column(nullable = false)
    private LocalDate borrowedDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnedDate;
}
