package org.example;

import client.component.AdminLoginFrame;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        UIManager.put("Component.arc", 300); // 컴포넌트 모서리 둥글기 (기본 5)
        UIManager.put("Button.arc", 100);
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        AdminLoginFrame.start();
    }
}