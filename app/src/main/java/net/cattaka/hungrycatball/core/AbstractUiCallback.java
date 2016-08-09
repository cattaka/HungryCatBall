package net.cattaka.hungrycatball.core;

import java.util.List;

public class AbstractUiCallback {
    private String title;

    ;
    private UiType uiType;
    private List<Object> selectItems;
    private String inputDefault;
    public AbstractUiCallback(UiType uiType) {
        this.uiType = uiType;
    }

    public AbstractUiCallback(String title, String inputDefault) {
        this.title = title;
        this.uiType = UiType.UI_INPUT;
        this.inputDefault = inputDefault;
    }

    public AbstractUiCallback(String title, List<Object> selectItems) {
        this.title = title;
        this.uiType = UiType.UI_SELECT;
        this.selectItems = selectItems;
    }

    public UiType getUiType() {
        return uiType;
    }

    public List<Object> getSelectItems() {
        return selectItems;
    }

    public String getInputDefault() {
        return inputDefault;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void onFinished(Object value) {
    }

    public void onCancel() {
    }

    public enum UiType {
        UI_SELECT,
        UI_INPUT,
        UI_QUIT
    }
}
