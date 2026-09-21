package com.example.polychat_be.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/** Keep the completed startup line; suppress preliminary startup information. */
public class StartupConsoleFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLevel() == Level.INFO
                && "com.example.polychat_be.PolychatBeApplication".equals(event.getLoggerName())
                && !event.getFormattedMessage().startsWith("Started PolychatBeApplication ")) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
