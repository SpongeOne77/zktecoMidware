package com.zkteco.attpush.utils;

import com.zkteco.attpush.entity.Command;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class CommandManager {
    private final Map<String, Queue<Command>> deviceCommandQueues = new ConcurrentHashMap<>();
    public void addCommand(String deviceSn, Command command) {
        Queue<Command> queue = deviceCommandQueues.computeIfAbsent(deviceSn, k -> new LinkedBlockingDeque<>());
        queue.offer(command);
    }

    public void addBatchCommands(String deviceSn, List<Command> commandList) {
        if (commandList == null || commandList.isEmpty()) {
            return;
        }
        Queue<Command> queue = deviceCommandQueues.computeIfAbsent(deviceSn, k -> new LinkedBlockingDeque<>());
        commandList.forEach(queue::offer);
    }

    public List<Command> fetchCommands(String deviceSn) {
        Queue<Command> queue = deviceCommandQueues.get(deviceSn);
        if (queue == null ||  queue.isEmpty()) {
            return Collections.emptyList();
        }
        List<Command> commands = new ArrayList<>();
        Command command;
        while ((command = queue.poll()) != null) {
            commands.add(command);
        }
        return commands;
    }
}
