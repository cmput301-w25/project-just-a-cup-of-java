package com.example.justacupofjavapersonal;

import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

//java documentation

/**
 * FirebaseIdlingResource is a custom implementation of the IdlingResource interface
 *
*/
public class FirebaseIdlingResource implements IdlingResource {
    private final String resourceName;
    private final AtomicInteger activeOperations = new AtomicInteger(0);
    private volatile ResourceCallback callback;

    public FirebaseIdlingResource(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getName() {
        return resourceName;
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = activeOperations.get() == 0;
        if (idle && callback != null) {
            callback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public void increment() {
        activeOperations.incrementAndGet();
    }

    public void decrement() {
        int count = activeOperations.decrementAndGet();
        if (count == 0 && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}
