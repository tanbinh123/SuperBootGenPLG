package com.ssx;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperBootGenPLG extends ActionGroup {

    public SuperBootGenPLG() {
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        AnAction[] SuperBootGenPLGActionGroup = new AnAction[]{
                new CreateNewSpringBootModule(),
        };
        return SuperBootGenPLGActionGroup;
    }
}

