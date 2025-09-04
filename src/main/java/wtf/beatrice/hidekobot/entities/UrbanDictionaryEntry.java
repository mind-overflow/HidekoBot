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

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public Integer getPage()
    {
        return page;
    }

    public void setPage(Integer page)
    {
        this.page = page;
    }

    public String getMeanings()
    {
        return meanings;
    }

    public void setMeanings(String meanings)
    {
        this.meanings = meanings;
    }

    public String getExamples()
    {
        return examples;
    }

    public void setExamples(String examples)
    {
        this.examples = examples;
    }

    public String getContributors()
    {
        return contributors;
    }

    public void setContributors(String contributors)
    {
        this.contributors = contributors;
    }

    public String getDates()
    {
        return dates;
    }

    public void setDates(String dates)
    {
        this.dates = dates;
    }

    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }
}
