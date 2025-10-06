package sigiv.Backend.sigiv.Backend.dto.persona;


import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Persona;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequestDto {

    private Long idpersona;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private LocalDate fechaIngreso;
    private Persona.Estado estado;
    private Long empresaId;
}

 
