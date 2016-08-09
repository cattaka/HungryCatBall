package net.cattaka.hungrycatball.core;

import org.jbox2d.common.Vec2;

public class UserInput {
    public TouchState currentMenuState = TouchState.RELEASED;
    public TouchState currentBackState = TouchState.RELEASED;
    public TouchState currentTouchState = TouchState.RELEASED;
    public boolean currentMenuStateConsumed = true;
    public boolean currentBackStateConsumed = true;
    public boolean currentTouchStateConsumed = true;
    public Vec2 currentPosition = new Vec2();

    public void set(UserInput src) {
        currentMenuState = src.currentMenuState;
        currentBackState = src.currentBackState;
        currentTouchState = src.currentTouchState;
        currentMenuStateConsumed = src.currentMenuStateConsumed;
        currentBackStateConsumed = src.currentBackStateConsumed;
        currentTouchStateConsumed = src.currentTouchStateConsumed;
        currentPosition.set(src.currentPosition);
    }

    public void step() {
        switch (currentTouchState) {
            case RELEASE:
                currentTouchState = TouchState.RELEASED;
                break;
            case PRESSE:
                currentTouchState = TouchState.PRESSED;
                break;
            case RELEASED:
            case PRESSED:
                break;
        }

        switch (currentMenuState) {
            case RELEASE:
                currentMenuState = TouchState.RELEASED;
                break;
            case PRESSE:
                currentMenuState = TouchState.PRESSED;
                break;
            case RELEASED:
            case PRESSED:
                break;
        }

        switch (currentBackState) {
            case RELEASE:
                currentBackState = TouchState.RELEASED;
                break;
            case PRESSE:
                currentBackState = TouchState.PRESSED;
                break;
            case RELEASED:
            case PRESSED:
                break;
        }
    }

    public enum TouchState {
        RELEASED,
        PRESSE,
        PRESSED,
        RELEASE,
    }

    public enum PhysicalButton {
        NONE,
        MENU,
        BACK
    }
}
