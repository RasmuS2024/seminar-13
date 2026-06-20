package seminars.kafka.inbox;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InboxService {
    private final InboxEventRepository inboxEventRepository;

    public void saveToInbox(InboxEvent event) {
        inboxEventRepository.save(event);
    }

    public boolean existsByEventId(UUID eventId) {
        return inboxEventRepository.existsByEventId(eventId);
    }
}
