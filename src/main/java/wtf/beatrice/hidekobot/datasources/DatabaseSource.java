package wtf.beatrice.hidekobot.datasources;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.Cache;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSource
{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DatabaseSource.class);
    private static final String JDBC_URL = "jdbc:sqlite:%path%";
    private Connection dbConnection = null;
    private final String dbPath;

    public DatabaseSource(String dbPath)
    {
        this.dbPath = dbPath;
    }

    private void logException(SQLException e)
    {
        LOGGER.error("Database Exception", e);
    }

    public boolean connect()
    {
        String url = JDBC_URL.replace("%path%", dbPath);

        if (!close()) return false;

        try
        {
            dbConnection = DriverManager.getConnection(url);
            LOGGER.info("Database connection established!");
            return true;
        } catch (SQLException e)
        {
            logException(e);
            return false;
        }

    }


    public boolean close()
    {
        if (dbConnection != null)
        {
            try
            {
                if (!dbConnection.isClosed())
                {
                    dbConnection.close();
                }
            } catch (SQLException e)
            {
                logException(e);
                return false;
            }

            dbConnection = null;
        }

        return true;
    }

    /*
     * DB STRUCTURE
     * TABLE 1: pending_disabled_messages
     * ----------------------------------------------------------------------------------
     * | guild_id      | channel_id         | message_id       | expiry_timestamp       |
     * ----------------------------------------------------------------------------------
     * |39402849302   | 39402849302        | 39402849302      | 2022-11-20 22:45:53:300 |
     * ---------------------------------------------------------------------------------
     *
     *
     * TABLE 2: command_runners
     * --------------------------------------------------------------------------------------------
     * | guild_id      | channel_id         | message_id       | user_id        | channel_type    |
     * --------------------------------------------------------------------------------------------
     * | 39402849302   | 39402849302        | 39402849302      | 39402849302    | PRIVATE         |
     * --------------------------------------------------------------------------------------------
     *
     * TABLE 3: urban_dictionary
     * -----------------------------------------------------------------------------------------------------
     * | message_id    | page      | meanings    | examples    | contributors      | dates      | term     |
     * -----------------------------------------------------------------------------------------------------
     * | 39402849302   | 0         | base64      | base64      | base64            | base64     | miku     |
     * -----------------------------------------------------------------------------------------------------
     */

    //todo: javadocs
    public boolean initDb()
    {
        List<String> newTables = new ArrayList<>();

        newTables.add("""
                CREATE TABLE IF NOT EXISTS pending_disabled_messages (
                guild_id TEXT NOT NULL,
                channel_id TEXT NOT NULL,
                message_id TEXT NOT NULL,
                expiry_timestamp TEXT NOT NULL);
                """);

        newTables.add("""
                CREATE TABLE IF NOT EXISTS command_runners (
                guild_id TEXT NOT NULL,
                channel_id TEXT NOT NULL,
                message_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                channel_type TEXT NOT NULL);
                """);

        newTables.add("""
                CREATE TABLE IF NOT EXISTS urban_dictionary (
                message_id TEXT NOT NULL,
                page INTEGER NOT NULL,
                meanings TEXT NOT NULL,
                examples TEXT NOT NULL,
                contributors TEXT NOT NULL,
                dates TEXT NOT NULL,
                term TEXT NOT NULL
                );
                """);

        for (String sql : newTables)
        {
            try (Statement stmt = dbConnection.createStatement())
            {
                // execute the statement
                stmt.execute(sql);
            } catch (SQLException e)
            {
                logException(e);
                return false;
            }
        }

        return true;
    }

    public boolean trackRanCommandReply(Message message, User user)
    {
        String userId = user.getId();
        String guildId;

        ChannelType channelType = message.getChannelType();
        if (!(channelType.isGuild()))
        {
            guildId = userId;
        } else
        {
            guildId = message.getGuild().getId();
        }

        String channelId = message.getChannel().getId();
        String messageId = message.getId();


        String query = """
                INSERT INTO command_runners
                (guild_id, channel_id, message_id, user_id, channel_type) VALUES
                 (?, ?, ?, ?, ?);
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);
            preparedStatement.setString(4, userId);
            preparedStatement.setString(5, channelType.name());

            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e)
        {
            logException(e);
        }

        return false;
    }

    public boolean isUserTrackedFor(String userId, String messageId)
    {
        String trackedUserId = getTrackedReplyUserId(messageId);
        if (trackedUserId == null) return false;
        return userId.equals(trackedUserId);
    }

    public ChannelType getTrackedMessageChannelType(String messageId)
    {
        String query = """
                SELECT channel_type
                FROM command_runners
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                String channelTypeName = resultSet.getString("channel_type");
                return ChannelType.valueOf(channelTypeName);
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;

    }

    public String getTrackedReplyUserId(String messageId)
    {
        String query = """
                SELECT user_id
                FROM command_runners
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("user_id");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public boolean queueDisabling(Message message)
    {
        String messageId = message.getId();
        String channelId = message.getChannel().getId();
        String guildId;

        ChannelType channelType = message.getChannelType();
        if (!(channelType.isGuild()))
        {
            guildId = "PRIVATE";
        } else
        {
            guildId = message.getGuild().getId();
        }

        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(Cache.getExpiryTimeSeconds());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Cache.getExpiryTimestampFormat());
        String expiryTimeFormatted = dateTimeFormatter.format(expiryTime);

        String query = """
                INSERT INTO pending_disabled_messages
                (guild_id, channel_id, message_id, expiry_timestamp) VALUES
                 (?, ?, ?, ?);
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);
            preparedStatement.setString(4, expiryTimeFormatted);

            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e)
        {
            logException(e);
        }

        return false;
    }

    public List<String> getQueuedExpiringMessages()
    {
        List<String> messages = new ArrayList<>();

        String query = """
                SELECT message_id
                FROM pending_disabled_messages;
                """;

        try (Statement statement = dbConnection.createStatement())
        {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.isClosed()) return messages;
            while (resultSet.next())
            {
                messages.add(resultSet.getString("message_id"));
            }
        } catch (SQLException e)
        {
            logException(e);
        }

        return messages;
    }

    public boolean untrackExpiredMessage(String messageId)
    {
        String query = "DELETE FROM pending_disabled_messages WHERE message_id = ?;";

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            preparedStatement.execute();
        } catch (SQLException e)
        {
            logException(e);
            return false;
        }

        query = "DELETE FROM command_runners WHERE message_id = ?;";
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            preparedStatement.execute();
        } catch (SQLException e)
        {
            logException(e);
            return false;
        }

        query = "DELETE FROM urban_dictionary WHERE message_id = ?;";
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            preparedStatement.execute();
        } catch (SQLException e)
        {
            logException(e);
            return false;
        }

        return true;
    }

    public String getQueuedExpiringMessageExpiryDate(String messageId)
    {
        String query = """
                SELECT expiry_timestamp
                FROM pending_disabled_messages
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("expiry_timestamp");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public String getQueuedExpiringMessageChannel(String messageId)
    {
        String query = """
                SELECT channel_id
                FROM pending_disabled_messages
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("channel_id");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public String getQueuedExpiringMessageGuild(String messageId)
    {
        String query = """
                SELECT guild_id
                FROM pending_disabled_messages
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("guild_id");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public boolean trackUrban(String meanings, String examples,
                              String contributors, String dates,
                              Message message, String term)
    {

        String query = """
                INSERT INTO urban_dictionary
                (message_id, page, meanings, examples, contributors, dates, term) VALUES
                 (?, ?, ?, ?, ?, ?, ?);
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, message.getId());
            preparedStatement.setInt(2, 0);
            preparedStatement.setString(3, meanings);
            preparedStatement.setString(4, examples);
            preparedStatement.setString(5, contributors);
            preparedStatement.setString(6, dates);
            preparedStatement.setString(7, term);

            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e)
        {
            logException(e);
        }

        return false;
    }

    public int getUrbanPage(String messageId)
    {
        String query = """
                SELECT page
                FROM urban_dictionary
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return 0;
            while (resultSet.next())
            {
                return resultSet.getInt("page");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return 0;
    }

    public String getUrbanMeanings(String messageId)
    {
        String query = """
                SELECT meanings
                FROM urban_dictionary
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("meanings");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public String getUrbanExamples(String messageId)
    {
        String query = """
                SELECT examples
                FROM urban_dictionary
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("examples");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public String getUrbanContributors(String messageId)
    {
        String query = """
                SELECT contributors
                FROM urban_dictionary
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("contributors");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public String getUrbanDates(String messageId)
    {
        String query = """
                SELECT dates
                FROM urban_dictionary
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("dates");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public String getUrbanTerm(String messageId)
    {
        String query = """
                SELECT term
                FROM urban_dictionary
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed()) return null;
            while (resultSet.next())
            {
                return resultSet.getString("term");
            }

        } catch (SQLException e)
        {
            logException(e);
        }

        return null;
    }

    public boolean setUrbanPage(String messageId, int page)
    {
        String query = """
                UPDATE urban_dictionary
                SET page = ? 
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setInt(1, page);
            preparedStatement.setString(2, messageId);
            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e)
        {
            logException(e);
        }

        return false;
    }

    public boolean resetExpiryTimestamp(String messageId)
    {
        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(Cache.getExpiryTimeSeconds());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Cache.getExpiryTimestampFormat());
        String expiryTimeFormatted = dateTimeFormatter.format(expiryTime);

        String query = """
                UPDATE pending_disabled_messages
                SET expiry_timestamp = ? 
                WHERE message_id = ?;
                """;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, expiryTimeFormatted);
            preparedStatement.setString(2, messageId);
            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e)
        {
            logException(e);
        }

        return false;
    }


}
