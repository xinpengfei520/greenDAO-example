package com.xpf.android.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by x-sir on 2019-07-15 :)
 * Function:
 */
@Entity
public class Student {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    @Property(nameInDb = "message")
    @Index(unique = true)
    private String message;
    @Transient
    private long time;

    public Student(String name, String message, long time) {
        this.name = name;
        this.message = message;
        this.time = time;
    }

    @Generated(hash = 1887985253)
    public Student(Long id, String name, String message) {
        this.id = id;
        this.name = name;
        this.message = message;
    }

    @Generated(hash = 1556870573)
    public Student() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
