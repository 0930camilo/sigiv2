package sigiv.Backend.sigiv.Backend.entity;
import java.io.Serializable;
import java.util.Objects;

import lombok.Data;


@Data
public class PersonaNominaId implements Serializable {
    private Long idpersona;
    private Long idnomina;

    // equals() y hashCode() son obligatorios para llaves compuestas

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonaNominaId)) return false;
        PersonaNominaId that = (PersonaNominaId) o;
        return Objects.equals(idpersona, that.idpersona) &&
               Objects.equals(idnomina, that.idnomina);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpersona, idnomina);
    }
}
