package de.eldritch.discord.turtlecrawler.util;

public final class Status {
    public static final int PRE_INIT = 0;
    public static final int INIT = 1;
    public static final int RUNNING = 2;
    public static final int STOPPING = 3;
    public static final int STOPPED = 4;

    private int status = PRE_INIT;

    public void set(int status) {
        if (status < PRE_INIT || status > STOPPED)
            throw new IllegalArgumentException("Unknown status (out of bounds)");
        this.status = Math.max(status, this.status);
    }

    public int get() {
        return status;
    }
}
