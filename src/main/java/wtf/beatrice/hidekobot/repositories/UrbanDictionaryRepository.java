package wtf.beatrice.hidekobot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wtf.beatrice.hidekobot.entities.UrbanDictionaryEntry;

public interface UrbanDictionaryRepository extends JpaRepository<UrbanDictionaryEntry, String>
{
}
