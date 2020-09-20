package me.bc56.discord.model;

import com.google.gson.annotations.JsonAdapter;
import me.bc56.discord.adapter.SnowflakeAdapter;
import me.bc56.discord.util.Constants;

import java.math.BigInteger;
import java.time.Instant;

// https://discord.com/developers/docs/reference#snowflakes
@JsonAdapter(SnowflakeAdapter.class)
public class Snowflake {
    public final Long id;

    private Instant creationDate;
    private final Object creationDateLock = new Object();

    public Snowflake(String rawId) {
        id = Long.parseUnsignedLong(rawId);
    }

    public Snowflake(long rawId) {
        id = rawId;
    }

    public Instant getCreationDate() {
        if (creationDate == null) {
            synchronized (creationDateLock) {
                if (creationDate == null) {
                    long timestamp = (id >> 22) + Constants.DISCORD_EPOCH.toEpochMilli();

                    return Instant.ofEpochMilli(timestamp);
                }
            }
        }

        return creationDate;
    }

    public byte getInternalWorkerId() {
        return (byte)((id & 0x3E0000) >> 17);
    }

    public byte getInternalProcessId() {
        return (byte)((id & 0x1F000) >> 12);
    }

    public short getIncrement() {
        return (short)(id & 0xFFF);
    }

    //This does not generate a real Snowflake, only a fake one.
    //TODO: This needs thorough testing
    public static Snowflake fromDate(Instant date) {
        return new Snowflake((date.toEpochMilli() - Constants.DISCORD_EPOCH.toEpochMilli()) << 22);
    }

    public boolean equals(Snowflake other) {
        return id.equals(other.id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
