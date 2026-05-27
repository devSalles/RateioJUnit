package RateioJUnit.entity;

import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "despesa")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDivisao tipoDivisao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusDespesa statusDespesa;

    @Column(nullable = false)
    LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "pagador_id",nullable = false)
    private Participante pagador;

    @OneToMany(mappedBy = "despesa",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Divisao> divisoes = new ArrayList<>();

    @OneToMany(mappedBy = "despesa",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Saldo> saldo = new ArrayList<>();
}
