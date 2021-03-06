package com.wixpress.ci.teamcity.dependenciesTab;

import com.wixpress.ci.teamcity.domain.LogMessage;
import com.wixpress.ci.teamcity.domain.LogMessageType;
import com.wixpress.ci.teamcity.maven.listeners.ListenerLogger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.SBuildType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author yoav
 * @since 2/16/12
 */
public class CollectingMessagesListenerLogger implements ListenerLogger {

    private final List<LogMessage> messages = newArrayList();
    private String buildTypeId = "";

    public void info(String message) {
        synchronized (messages) {
            Loggers.SERVER.info(String.format("%s: %s", buildTypeId, message));
            messages.add(new LogMessage(message, LogMessageType.info));
        }
    }

    public void progress(String message) {
        synchronized (messages) {
            Loggers.SERVER.info(String.format("%s: %s", buildTypeId, message));
            messages.add(new LogMessage(message, LogMessageType.progress));
        }
    }

    public void error(String message) {
        synchronized (messages) {
            Loggers.SERVER.error(String.format("%s: %s", buildTypeId, message));
            messages.add(new LogMessage(message, LogMessageType.error));
        }
    }

    public void error(String message, Exception e) {
        synchronized (messages) {
            Loggers.SERVER.error(String.format("%s: %s", buildTypeId, message), e);
            messages.add(new LogMessage(message, LogMessageType.error, e));
        }
    }

    public List<LogMessage> getMessages() {
        synchronized (messages) {
            return new ArrayList<LogMessage>(messages);
        }
    }

    public List<LogMessage> getMessages(Integer position) {
        synchronized (messages) {
            if (position < messages.size())
                return new ArrayList<LogMessage>(messages.subList(position, messages.size()));
            else
                return newArrayList();
        }
    }

    public void completedCollectingDependencies(SBuildType buildType) {
        synchronized (messages) {
            String message = String.format("completed collecting dependencies for %s:%s", buildType.getProject().getName(), buildType.getName());
            System.out.println(message);
            messages.add(new LogMessage(message, LogMessageType.info));
        }
    }

    public void failedCollectingDependencies(SBuildType buildType, Exception e) {
        synchronized (messages) {
            String message = String.format("failed collecting dependencies for %s:%s", buildType.getProject().getName(), buildType.getName());
            System.out.println(message);
            e.printStackTrace();
            messages.add(new LogMessage(message, LogMessageType.error, e));
        }
    }

    public void setBuildTypeId(String buildTypeId) {
        this.buildTypeId = buildTypeId;
    }
}
