package com.c1se22.publiclaundsmartsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class MaintenanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer id;
    @Column(name = "maintenance_type", nullable = false)
    private String maintenanceType;
    @Column(name = "description", nullable = false)
    private String maintenanceDescription;
    @Column(name = "maintenance_cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal maintenanceCost;
    @Column(name = "maintenance_date", nullable = false)
    private LocalDate maintenanceDate;
    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;
    @Column(name = "technician_name", nullable = false)
    private String technicianName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private Machine machine;
}
