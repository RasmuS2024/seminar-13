package seminars.kafka.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query("SELECT o FROM OutboxEvent o WHERE o.status = :status ORDER BY o.createdAt ASC")
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(@Param("status") OutboxEvent.OutboxStatus status, Pageable page);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = :status WHERE o.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") OutboxEvent.OutboxStatus status);

}
