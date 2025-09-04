package wtf.beatrice.hidekobot.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "urban_dictionary")
public class UrbanDictionaryEntry
{
    @Id
    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "page", nullable = false)
    private Integer page;

    @Column(name = "meanings", nullable = false, columnDefinition = "TEXT")
    private String meanings;

    @Column(name = "examples", nullable = false, columnDefinition = "TEXT")
    private String examples;

    @Column(name = "contributors", nullable = false, columnDefinition = "TEXT")
    private String contributors;

    @Column(name = "dates", nullable = false, columnDefinition = "TEXT")
    private String dates;

    @Column(name = "term", nullable = false)
    private String term;
}
