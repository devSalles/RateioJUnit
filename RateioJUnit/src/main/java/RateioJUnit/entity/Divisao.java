package RateioJUnit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "divisoes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Divisao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "usuario_id",nullable = false)
    private Participante participante;

    @ManyToOne
    @JoinColumn(name = "despesa_id",nullable = false)
    private Despesa despesa;
}
