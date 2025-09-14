package sigiv.Backend.sigiv.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<sigiv.Backend.sigiv.Backend.entity.Admin, Long> {
    
    // Additional query methods can be defined here if needed
    
}
