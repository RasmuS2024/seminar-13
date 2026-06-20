package seminars.kafka.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {
    boolean existsByEventId(UUID eventId);

}

