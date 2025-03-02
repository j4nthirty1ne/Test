package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.example.views.DisplayUI;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DisplayUI displayUI = new DisplayUI();
        List<Map<String, Object>> menuItems = new ArrayList<>();
        displayUI.displayUI(menuItems);
    }
}