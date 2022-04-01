package com.task4j;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private final ScheduledThreadPoolExecutor executor;
    private final Map<String, Timer> timers;
    private final Map<String, Task> tasks;
    private final Set<String> names;
    private final Map<Class<?>, IdProvider<?>> idProviders;

    public TaskManager() {
        timers = new Hashtable<>();
        tasks = new Hashtable<>();
        names = new HashSet<>();
        idProviders = new Hashtable<>();
        executor = new ScheduledThreadPoolExecutor(1);
        executor.setMaximumPoolSize(200);
        registerIdProvider(String.class, Object::toString);
        registerIdProvider(Integer.class, Object::toString);
        registerIdProvider(Double.class, Object::toString);
        registerIdProvider(Float.class, Object::toString);
        registerIdProvider(Long.class, Object::toString);
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        executor.setMaximumPoolSize(maximumPoolSize);
    }

    public void registerIdProvider(Class<?> clazz, IdProvider<?> provider) {
        idProviders.put(clazz, provider);
    }

    private String generateId(String name, Object... idProviders) {
        StringBuilder id = new StringBuilder(name);
        for (Object o : idProviders) {
            if (this.idProviders.containsKey(o.getClass())) {
                id.append("-").append(this.idProviders.get(o.getClass()).getId(o));
            } else {
                throw new RuntimeException("unknown id");
            }
        }
        return id.toString();
    }

    public void execute(String name, Task task, Long delay, Object... idProviders) {
        schedule(false, name, task, delay, -1L, idProviders);
    }

    public void schedule(String name, Task task, Long delay, Long period, Object... idProviders) {
        schedule(false, name, task, delay, period, idProviders);
    }

    public void executeHeavy(String name, Task task, Long delay, Object... idProviders) {
        schedule(true, name, task, delay, -1L, idProviders);
    }

    public void scheduleHeavy(String name, Task task, Long delay, Long period, Object... idProviders) {
        schedule(true, name, task, delay, period, idProviders);
    }

    public void kill(String name, Object... ids) {
        kill(generateId(name, ids));
    }

    public void killAll(String name) {
        Set<String> ids = new HashSet<>();
        tasks.forEach((id, task) -> {
            if (id.equals(name) || id.startsWith(name + "-")) {
                ids.add(id);
            }
        });
        ids.forEach(this::kill);
    }

    private void kill(String id) {
        try {
            Optional.ofNullable(tasks.get(id))
                    .ifPresent(task -> {
                        task.cancel();
                        tasks.remove(id);
                        executor.remove(task);
                    });
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public Set<String> getTaskIds() {
        return this.tasks.keySet();
    }

    private void schedule(Boolean heavy, String name, Task task, Long delay, Long period, Object... ids) {
        names.add(name);
        task.setOnTaskCanceledListener(() -> kill(task.getId()));
        String id = generateId(name, ids);
        task.setId(id);
        kill(id, ids);
        tasks.put(id, task);
        if (heavy) {
            if (period == -1) {
                executor.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
            } else {
                executor.scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
            }
        } else {
            Timer timer = getTimer(name);
            if (period == -1) {
                timer.schedule(task, delay);
            } else {
                timer.schedule(task, delay, period);
            }
        }
    }

    private Timer getTimer(String name) {
        if (!timers.containsKey(name)) {
            timers.put(name, new Timer("task4j." + name));
        }
        return timers.get(name);
    }
}
