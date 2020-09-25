package me.bc56.discord.gateway.dispatch;

import me.bc56.discord.gateway.dispatch.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DispatchHandler {
    public static Logger log = LoggerFactory.getLogger(DispatchHandler.class);

    @SuppressWarnings("unchecked") //We guarantee the superclass is DispatchData in constructMap()
    Map<Class<? extends DispatchData>, Method> classToMethodMap = (Map<Class<? extends DispatchData>, Method>) constructMap();

    public DispatchHandler() {
    }

    public <T extends DispatchData> void handleDispatch(T dispatchData) {
        try {
            classToMethodMap.get(dispatchData.getClass()).invoke(this, dispatchData);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Problem while handling dispatch", e);
        }
    }

    Map<Class<?>, Method> constructMap() {
        var methods = this.getClass().getDeclaredMethods();

        //We only want methods of the pattern: handle(Class var)
        return Arrays.stream(methods)
                .filter(method -> method.getName().equals("handle")
                        && method.getParameterCount() == 1
                        && method.getParameterTypes()[0].getSuperclass().equals(DispatchData.class))
                .collect(Collectors.toUnmodifiableMap(
                        method -> method.getParameterTypes()[0],
                        method -> method
                ));
    }

    void handle(Ready ready) {

    }

    void handle(Resumed resumed) {

    }

    void handle(ChannelCreate channelCreate) {
    }

    void handle(ChannelUpdate channelUpdate) {
    }

    void handle(ChannelDelete channelDelete) {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle() {
    }

    void handle(ChannelPinsUpdate channelPinsUpdate) {
    }

}
