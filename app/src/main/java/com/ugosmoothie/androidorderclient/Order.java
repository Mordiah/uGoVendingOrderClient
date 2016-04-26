package com.ugosmoothie.androidorderclient;

/**
 * Created by Michelle on 3/19/2016.
 */
public class Order {
    private Long orderId;
    private String smoothieName;
    private String liquidName;
    private String supplementName;
    private boolean completed;

    public Order(Long orderId, String smoothieName, String liquidName, String supplementName) {
        this.orderId = orderId;
        this.smoothieName = smoothieName;
        this.liquidName = liquidName;
        this.supplementName = supplementName;
    }

    public void Complete() {
        this.completed = true;
    }

    public Long getId() { return this.orderId; }

    @Override
    public String toString() {
        return this.smoothieName;
    }

}
