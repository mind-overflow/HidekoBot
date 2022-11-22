package wtf.beatrice.hidekobot.datasources;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSource
{

    private final static String sqliteURL = "jdbc:sqlite:%path%";
    private Connection dbConnection = null;
    private final String dbPath;
    private final Logger logger;

    public DatabaseSource(String dbPath)
    {
        this.dbPath = dbPath;
        this.logger = new Logger(getClass());
    }

    public boolean connect()
    {
        String url = sqliteURL.replace("%path%", dbPath);

        if(!close()) return false;

        try {
            dbConnection = DriverManager.getConnection(url);
            logger.log("Database connection established!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


    public boolean close()
    {
        if (dbConnection != null)
        {
            try {
                if(!dbConnection.isClosed())
                {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
     */

    //todo: javadocs

    public boolean initDb()
    {
        List<String> newTables = new ArrayList<>();

        newTables.add("CREATE TABLE IF NOT EXISTS pending_disabled_messages (" +
                "guild_id TEXT NOT NULL, " +
                "channel_id TEXT NOT NULL," +
                "message_id TEXT NOT NULL," +
                "expiry_timestamp TEXT NOT NULL " +
                ");");

        newTables.add("CREATE TABLE IF NOT EXISTS command_runners (" +
                "guild_id TEXT NOT NULL, " +
                "channel_id TEXT NOT NULL," + // channel the command was run in
                "message_id TEXT NOT NULL," + // message id of the bot's response
                "user_id TEXT NOT NULL, " + // user who ran the command
                "channel_type TEXT NOT NULL" + // channel type (PRIVATE, FORUM, ...)
                ");");

        for(String sql : newTables)
        {
            try (Statement stmt = dbConnection.createStatement()) {
                // execute the statement
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
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
        if(!(channelType.isGuild()))
        {
            guildId = userId;
        } else {
            guildId = message.getGuild().getId();
        }

        String channelId = message.getChannel().getId();
        String messageId = message.getId();


        String query = "INSERT INTO command_runners " +
                "(guild_id, channel_id, message_id, user_id, channel_type) VALUES " +
                " (?, ?, ?, ?, ?);";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
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
            e.printStackTrace();
        }

        return false;
    }

    public boolean isUserTrackedFor(String userId, String messageId)
    {
        String trackedUserId = getTrackedReplyUserId(messageId);
        if(trackedUserId == null) return false;
        return userId.equals(trackedUserId);
    }

    public ChannelType getTrackedMessageChannelType(String messageId)
    {
        String query = "SELECT channel_type " +
                "FROM command_runners " +
                "WHERE message_id = ?;";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.isClosed()) return null;
            while(resultSet.next())
            {
                String channelTypeName = resultSet.getString("channel_type");
                return ChannelType.valueOf(channelTypeName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getTrackedReplyUserId(String messageId)
    {
        String query = "SELECT user_id " +
                "FROM command_runners " +
                "WHERE message_id = ?;";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.isClosed()) return null;
            while(resultSet.next())
            {
                return resultSet.getString("user_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean queueDisabling(Message message)
    {
        String messageId = message.getId();
        String channelId = message.getChannel().getId();
        String guildId;

        ChannelType channelType = message.getChannelType();
        if(!(channelType.isGuild()))
        {
            guildId = "PRIVATE";
        } else {
            guildId = message.getGuild().getId();
        }

        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(Cache.getExpiryTimeSeconds());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Cache.getExpiryTimestampFormat());
        String expiryTimeFormatted = dateTimeFormatter.format(expiryTime);

        String query = "INSERT INTO pending_disabled_messages " +
                "(guild_id, channel_id, message_id, expiry_timestamp) VALUES " +
                " (?, ?, ?, ?);";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);
            preparedStatement.setString(4, expiryTimeFormatted);

            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getQueuedExpiringMessages()
    {
        List<String> messages = new ArrayList<>();

        String query = "SELECT message_id " +
                "FROM pending_disabled_messages ";

        try (Statement statement = dbConnection.createStatement())
        {
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.isClosed()) return messages;
            while(resultSet.next())
            {
                messages.add(resultSet.getString("message_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public boolean untrackExpiredMessage(String messageId)
    {
        String query = "DELETE FROM pending_disabled_messages WHERE message_id = ?;";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            preparedStatement.execute();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        query = "DELETE FROM command_runners WHERE message_id = ?;";
        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            preparedStatement.execute();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String getQueuedExpiringMessageExpiryDate(String messageId)
    {
        String query = "SELECT expiry_timestamp " +
                "FROM pending_disabled_messages " +
                "WHERE message_id = ?;";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.isClosed()) return null;
            while(resultSet.next())
            {
                return resultSet.getString("expiry_timestamp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getQueuedExpiringMessageChannel(String messageId)
    {
        String query = "SELECT channel_id " +
                "FROM pending_disabled_messages " +
                "WHERE message_id = ?;";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.isClosed()) return null;
            while(resultSet.next())
            {
                return resultSet.getString("channel_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getQueuedExpiringMessageGuild(String messageId)
    {
        String query = "SELECT guild_id " +
                "FROM pending_disabled_messages " +
                "WHERE message_id = ?;";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(query))
        {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.isClosed()) return null;
            while(resultSet.next())
            {
                return resultSet.getString("guild_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
