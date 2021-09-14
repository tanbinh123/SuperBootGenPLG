package com.ssx;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.ssx.utils.PomXmlPage;
import com.ssx.utils.SuperUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ssx
 * @Date 2021/7/28 23:15
 * @Version 1.0
 */
public class CreateNewDialog extends DialogWrapper {
    private final String p_groupId;
    private final String p_artifactId;
    private final String p_version;
    private final Document p_document;
    private final String p_path;
    private final VirtualFile virtualFile;
    private final JPanel center = new JPanel();
    private final JPanel north = new JPanel();
    private final JPanel south = new JPanel();

    protected CreateNewDialog(@Nullable Project project,
                              String p_groupId,
                              String p_artifactId,
                              String p_version,
                              Document p_document,
                              VirtualFile virtualFile) {

        super(project);
        this.p_groupId = p_groupId;
        this.p_artifactId = p_artifactId;
        this.p_version = p_version;
        this.p_document = p_document;
        this.p_path = virtualFile.getPath();
        this.virtualFile = virtualFile;
        this.setResizable(false);
        this.setTitle("Guide");
        this.init();
    }
    @Override
    protected JComponent createNorthPanel() {
        JLabel title = new JLabel("Warning: Don't use illegal charz like[*/+] or same module name used be4 in this pro");
        // TODO: get ancestor first
        // TODO: get all modules name list (depend on it's pom.xml)
        title.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        north.add(title);
        return title;
    }
    @Override
    protected JComponent createCenterPanel() {
        center.setLayout(new GridLayout(5,2));
        JLabel al = new JLabel("artifactId:");
        JTextField artifactId = new JTextField();
        JLabel gl = new JLabel("groupId:");
        JTextField groupId = new JTextField(p_groupId);
        JLabel vl = new JLabel("version:");
        JTextField version = new JTextField(p_version);
        JCheckBox mvnBuildPlugin = new JCheckBox("MvnBuildPlugin",true);
        JCheckBox mybatisPlusGen = new JCheckBox("MybatisPlusGen",false);
        JLabel dl = new JLabel("dataSourcePrefix:");
        JTextField dataSourcePrefix = new JTextField("spring.datasource");
        {
            // dataSourcePrefix 默认禁用
            dataSourcePrefix.setEnabled(false);
            mybatisPlusGen.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    dataSourcePrefix.setEnabled(!dataSourcePrefix.isEnabled());
                }
            });
        }

        center.add(al);
        center.add(artifactId);
        center.add(gl);
        center.add(groupId);
        center.add(vl);
        center.add(version);
        center.add(mvnBuildPlugin);
        center.add(mybatisPlusGen);
        center.add(dl);
        center.add(dataSourcePrefix);

        center.setVisible(true);
        return center;
    }

    @Override
    protected JComponent createSouthPanel() {
        south.setLayout(new FlowLayout());
        JButton finish = new JButton("Finish");
        finish.addActionListener(new MyFinishActionListener());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener((e) -> this.close(-1));
        south.add(finish);
        south.add(cancel);
        return south;
    }

    class MyFinishActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField atf = (JTextField) center.getComponent(1);
            String artifactId = atf.getText().trim();
            JTextField gtf = (JTextField) center.getComponent(3);
            String groupId = gtf.getText().trim();
            JTextField vtf = (JTextField) center.getComponent(5);
            String version = vtf.getText().trim();
            JCheckBox mvn = (JCheckBox) center.getComponent(6);
            boolean mavenBuildPluginSupport = mvn.isSelected();
            JCheckBox mpg = (JCheckBox) center.getComponent(7);
            boolean mybatisPlusGenSupport = mpg.isSelected();

            JTextField dtf = (JTextField) center.getComponent(9);
            String dataSourcePrefix = dtf.getText().trim();
            if (!dataSourcePrefix.endsWith("."))dataSourcePrefix+=".";

            if ("".equals(artifactId) || "".equals(groupId) || "".equals(version)) {
                Messages.showErrorDialog("GAV can't be empty", "Error");
                return;
            }

            File subDir = new File(p_path + File.separator + artifactId);
            if (subDir.exists()){
                // TODO: module name validate
                // TODO: get all modules name list (depend on it's pom.xml)
                Messages.showErrorDialog("Module name already exist", "Error");
                return;
            }

            // main logic
            try {
                subDir.mkdir();
                // TODO: error handle
                File appDir = new File(subDir + "/src/main/java/" + groupId.replace(".","/"));
                File testDir = new File(subDir + "/src/test/java/" + groupId.replace(".","/"));
                File appResourcesDir = new File(subDir + "/src/main/resources");
                appDir.mkdirs();
                testDir.mkdirs();
                appResourcesDir.mkdirs();
                File publicDir = new File(appResourcesDir + File.separator + "public");
                File staticDir = new File(appResourcesDir + File.separator + "static");
                File templatesDir = new File(appResourcesDir + File.separator + "templates");
                publicDir.mkdir();
                staticDir.mkdir();
                templatesDir.mkdir();

                String appName = SuperUtils.fromArtifactIdGetName(artifactId);
                String testName = SuperUtils.fromArtifactIdGetName(artifactId);
                String appResourcesName = "application.yml";

                Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
                configuration.setDefaultEncoding("UTF-8");
                configuration.setClassForTemplateLoading(CreateNewSpringBootModule.class, "/ftl");

                Template appTemplate = configuration.getTemplate("application.ftl");
                Map<String, String> appDataModel = new HashMap<>();
                appDataModel.put("groupId", groupId);
                appDataModel.put("ApplicationClassName", appName);
                OutputStreamWriter appWriter = new OutputStreamWriter(new FileOutputStream(appDir + File.separator + appName + ".java"), StandardCharsets.UTF_8);
                appTemplate.process(appDataModel, appWriter);
                appWriter.flush();
                appWriter.close();

                Template testTemplate = configuration.getTemplate("applicationTest.ftl");
                Map<String, String> testDataModel = new HashMap<>();
                testDataModel.put("groupId", groupId);
                testDataModel.put("TextClassName", testName + "Tests");
                OutputStreamWriter testWriter = new OutputStreamWriter(new FileOutputStream(testDir + File.separator + testName + "Tests.java"), StandardCharsets.UTF_8);
                testTemplate.process(testDataModel, testWriter);
                testWriter.flush();
                testWriter.close();

                new File(appResourcesDir + File.separator + appResourcesName).createNewFile();

                if (mybatisPlusGenSupport) {
                    Template mpgTemplate = configuration.getTemplate("mybatisPlus.ftl");
                    Map<String, String> mpgDataModel = new HashMap<>();
                    mpgDataModel.put("groupId", groupId);
                    mpgDataModel.put("dataSourcePrefix", dataSourcePrefix);
                    mpgDataModel.put("outputDir", appDir.getPath().replace("\\","/"));
                    OutputStreamWriter mpgWriter = new OutputStreamWriter(new FileOutputStream(appDir + File.separator + "SuperBootMpGen.java"), StandardCharsets.UTF_8);
                    mpgTemplate.process(mpgDataModel, mpgWriter);
                    mpgWriter.flush();
                    mpgWriter.close();

                    Template rTemplate = configuration.getTemplate("mybatisPlusProperties.ftl");
                    Map<String, String> rDataModel = new HashMap<>();
                    rDataModel.put("dataSourcePrefix", dataSourcePrefix);
                    OutputStreamWriter rWriter = new OutputStreamWriter(new FileOutputStream(appResourcesDir + File.separator +"sbg-mpg.properties"), StandardCharsets.UTF_8);
                    rTemplate.process(rDataModel, rWriter);
                    rWriter.flush();
                    rWriter.close();
                }

                // The POM-KING
                {
                    org.w3c.dom.Document xml = PomXmlPage.creatDocument();
                    Element project = PomXmlPage.creatProject(xml);
                    PomXmlPage.appendModelVersion(xml,project);
                    {
                        Element parent = xml.createElement("parent");
                        PomXmlPage.appendArtifactId(xml, parent, p_artifactId);
                        PomXmlPage.appendGroupId(xml, parent, p_groupId);
                        PomXmlPage.appendVersion(xml, parent, p_version);
                        project.appendChild(parent);
                    }
                    PomXmlPage.appendArtifactId(xml, project, artifactId);
                    if (!groupId.equals(p_groupId)) PomXmlPage.appendGroupId(xml, project, groupId);
                    if (!version.equals(p_version)) PomXmlPage.appendVersion(xml, project, version);
                    {
                        Element dependencies = xml.createElement("dependencies");
                        PomXmlPage.appendBootDependencyList(xml, dependencies, Arrays.asList("spring-boot-starter", "spring-boot-starter-test"));
                        if (mybatisPlusGenSupport) PomXmlPage.appendMybatisPlusGen(xml, dependencies);
                        project.appendChild(dependencies);
                    }
                    if (mavenBuildPluginSupport) PomXmlPage.appendMavenBuildPlugin(xml, project);
                    PomXmlPage.toPomXml(xml, project, subDir);
                }

                // the last step modify the p_document
                // TODO：convert dependencies to dependencyManagement
                org.dom4j.Element project = p_document.getRootElement();
                org.dom4j.Element packaging = project.element("packaging");
                if (packaging != null) packaging.setText("pom");
                else {
                    org.dom4j.Element packaging_new = project.addElement("packaging");
                    packaging_new.setText("pom");
                }
                org.dom4j.Element modules = project.element("modules");
                if (packaging != null){
                    org.dom4j.Element module_new = modules.addElement("module");
                    module_new.setText(artifactId);
                } else {
                    org.dom4j.Element modules_new = project.addElement("modules");
                    org.dom4j.Element module_new = modules_new.addElement("module");
                    module_new.setText(artifactId);
                }
                OutputFormat outputFormat= OutputFormat.createPrettyPrint();
                outputFormat.setEncoding("UTF-8");
                XMLWriter xmlWriter = new XMLWriter(new FileWriter(virtualFile.getPath() + File.separator + "pom.xml"),outputFormat);
                xmlWriter.write(p_document);
                xmlWriter.flush();
                xmlWriter.close();

                virtualFile.refresh(true, true);

                Messages.showInfoMessage("OK","Title");
            } catch (Exception exception) {
                Messages.showErrorDialog("Please do not contact ssx", "Error");
            }
            close(-1);
        }
    }


}
