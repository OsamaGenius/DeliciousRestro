/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.restro.models.admins;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author u_s_e
 */
public class UserModel extends RecursiveTreeObject<UserModel> {
    
    public final SimpleStringProperty id, username, job, email, b_email, phone, created_at;
    
    // Initialize and fetching database data
    public UserModel(String id, String username, String job, String email, String b_email, String phone, String created_at) {
        this.id = new SimpleStringProperty(id);
        this.username = new SimpleStringProperty(username);
        this.job = new SimpleStringProperty(job);
        this.email = new SimpleStringProperty(email);
        this.b_email = new SimpleStringProperty(b_email);
        this.phone = new SimpleStringProperty(phone);
        this.created_at = new SimpleStringProperty(created_at);
    }
    
    // Return database user id
    public String getID() {
        return id.get();
    }
    
    // Return database user username
    public String getUsername() {
        return username.get();
    }
    
    // Return database user job
    public String getJob() {
        return job.get();
    }
    
    // Return database user email
    public String getEmail() {
        return email.get();
    }
    
    // Return database user back up email
    public String getBupEmail() {
        return b_email.get();
    }
    
    // Return database user phone
    public String getPhone() {
        return phone.get();
    }
    
    // Return database user membership time
    public String getMemeber() {
        return created_at.get();
    }
    
}
