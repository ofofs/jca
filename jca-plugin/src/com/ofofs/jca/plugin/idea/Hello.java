package com.ofofs.jca.plugin.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * @author kangyonggan
 * @since 2018/6/30 0030
 */
public class Hello extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Messages.showMessageDialog("这个是我的测试弹窗！", "TestTile", Messages.getWarningIcon());
    }
}
