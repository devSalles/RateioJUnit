package RateioJUnit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "saldo")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Saldo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "devedor_id",nullable = false)
    private Participante devedor;

    @ManyToOne
    @JoinColumn(name = "credor_id",nullable = false)
    private Participante credor;

    @ManyToOne
    @JoinColumn(name = "despesa_id")
    private Despesa despesa;
}
