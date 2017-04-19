package com.vaadin.random.files;

import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class LabelModes extends com.vaadin.ui.VerticalLayout {
    
    @PropertyId("mine")
    public Label myLabel;

    protected void initializeComponents() {

        Label l;
        l = new Label(
                "This is an undefined wide label with default content mode");
        l.setWidth(null);
        addComponent(l);

        l = new Label(
                "This label                       contains\nnewlines and spaces\nbut is in\ndefault content mode");
        l.setWidth(null);
        addComponent(l);

        l = new Label(
                "This label                       contains\nnewlines and spaces\nand is in\npreformatted mode");
        l.setContentMode(ContentMode.PREFORMATTED);
        l.setWidth(null);
        addComponent(l);

        l = new Label(
                "This label                       contains\nnewlines and spaces\nand is in\nhtml mode");
        l.setContentMode(ContentMode.HTML);
        l.setWidth(null);
        addComponent(l);

    }

}
