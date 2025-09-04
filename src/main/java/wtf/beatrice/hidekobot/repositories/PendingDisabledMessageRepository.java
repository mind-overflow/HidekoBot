package wtf.beatrice.hidekobot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wtf.beatrice.hidekobot.entities.PendingDisabledMessage;

public interface PendingDisabledMessageRepository extends JpaRepository<PendingDisabledMessage, String>
{
}
