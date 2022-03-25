package com.task4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public abstract class Task extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(Task.class);
    private Integer executionCount = 0;

    public Integer getExecutionCount() {
        return executionCount;
    }

    private OnTaskCanceledListener onTaskCanceledListener;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOnTaskCanceledListener(OnTaskCanceledListener onTaskCanceledListener) {
        this.onTaskCanceledListener = onTaskCanceledListener;
    }

    @Override
    public boolean cancel() {
        boolean result = super.cancel();
        if (onTaskCanceledListener != null) {
            onTaskCanceledListener.OnTaskCanceled();
        }
        return result;
    }

    @Override
    public void run() {
        try {
            execute();
            executionCount++;
        } catch (Exception e) {
            log.error(ExceptionUtil.simplifyException(e));
        }
    }

    public abstract void execute();
}
