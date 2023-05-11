package ru.skypro.sockwarehouse.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime date;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private Condition condition;

    @ManyToOne
    @JoinColumn(name = "socks_id")
    private Socks socks;

    public Registration(LocalDateTime date, Integer quantity, Condition condition) {
        this.date = date;
        this.quantity = quantity;
        this.condition = condition;
    }

    public Registration() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Socks getSocks() {
        return socks;
    }

    public void setSocks(Socks socks) {
        this.socks = socks;
    }
}
