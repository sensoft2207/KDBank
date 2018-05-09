package com.example.kidbank.kidbanknew.Model;

/**
 * Created by vishal on 8/3/18.
 */

public class TaskListData {

    String task_id;
    String task_name;
    String salary_amount;
    String last_date;
    String TaskImage;

    public String getKidname() {
        return kidname;
    }

    public void setKidname(String kidname) {
        this.kidname = kidname;
    }

    String kidname;

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    String task_status;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getSalary_amount() {
        return salary_amount;
    }

    public void setSalary_amount(String salary_amount) {
        this.salary_amount = salary_amount;
    }

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }

    public String getTaskImage() {
        return TaskImage;
    }

    public void setTaskImage(String taskImage) {
        TaskImage = taskImage;
    }

}
