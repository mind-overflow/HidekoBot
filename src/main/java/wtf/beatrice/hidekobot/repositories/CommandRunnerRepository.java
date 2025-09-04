package wtf.beatrice.hidekobot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wtf.beatrice.hidekobot.entities.CommandRunner;

public interface CommandRunnerRepository extends JpaRepository<CommandRunner, String>
{
}
