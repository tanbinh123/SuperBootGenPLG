package com.ssx;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CreateNewSpringBootModule extends AnAction {
    public CreateNewSpringBootModule() {
        super("CreateNewSpringBootModule");
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile virtualFile = e.getData(LangDataKeys.VIRTUAL_FILE);

        if (virtualFile == null){
            Messages.showErrorDialog("Please do not contact ssx", "Error");
            return;
        }

        File pom = new File(virtualFile.getPath() + File.separator + "pom.xml");
        if (!pom.exists()) {
            Messages.showErrorDialog("Current dir doesn't contain pom.xml", "Invalid Parent Module");
            return;
        }





        Document document;
        String i_groupId, i_artifactId, i_version;

        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(pom);
            document.setXMLEncoding("UTF-8");
            Element root = document.getRootElement();

            Element groupId_i = root.element("groupId");
            if (groupId_i != null){
                i_groupId = groupId_i.getText();
            }else {
                Element parent = root.element("parent");
                Element groupId_p = parent.element("groupId");
                i_groupId = groupId_p.getText();
            }

            Element artifactId_i = root.element("artifactId");
            i_artifactId = artifactId_i.getText();

            Element version_i = root.element("version");
            if (version_i != null) {
                i_version = version_i.getText();
            }else {
                Element parent = root.element("parent");
                Element version_p = parent.element("version");
                i_version = version_p.getText();
            }
        } catch (Exception exception) {
            Messages.showErrorDialog("Please check pom.xml", "Error");
            return;
        }

        try{
            (new CreateNewDialog(null, i_groupId, i_artifactId, i_version, document, virtualFile)).show();
        } catch (Exception exception) {
            Messages.showErrorDialog(exception.getMessage(), "Error");
        }
    }

}