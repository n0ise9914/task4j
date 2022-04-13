package com.task4j;

import java.util.TimerTask;

public abstract class Task extends TimerTask {

    protected boolean canceled = false;
    private Integer executionCount = 0;
    private OnTaskCanceledListener onTaskCanceledListener;
    private String id;

    public Integer getExecutionCount() {
        return executionCount;
    }

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
        if (canceled) return true;
        canceled = true;
        boolean result = super.cancel();
        if (onTaskCanceledListener != null) {
            onTaskCanceledListener.OnTaskCanceled();
        }
        return result;
    }

    @Override
    public void run() {
        try {
            if (canceled) return;
            execute();
            executionCount++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void execute();
}
