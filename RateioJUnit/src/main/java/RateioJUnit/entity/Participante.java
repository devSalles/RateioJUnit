package RateioJUnit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Participante {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,unique = true)
    private String email;

    @OneToMany(mappedBy = "usuario")
    private List<Divisao> divisao = new ArrayList<>();

    @OneToMany(mappedBy = "pagador")
    private List<Despesa> despesasPagas = new ArrayList<>();

    @OneToMany(mappedBy = "devedor")
    private List<Saldo> saldoDevedor = new ArrayList<>();

    @OneToMany(mappedBy = "credor")
    private List<Saldo> saldoCredor = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Participante participante = (Participante) o;
        return Objects.equals(id, participante.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
