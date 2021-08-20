package com.ssx.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collection;

/**
 * @Author ssx
 * @Date 2021/7/29 15:13
 * @Version 1.0
 */
public class PomXmlPage {
    public static Document creatDocument() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document xml = documentBuilderFactory.newDocumentBuilder().newDocument();
        xml.setXmlStandalone(true);
        return xml;
    }
    public static Element creatProject(Document xml) {
        Element project = xml.createElement("project");
        project.setAttribute("xmlns", "http://maven.apache.org/POM/4.0.0");
        project.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        project.setAttribute("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
        return project;
    }

    public static void appendModelVersion(Document xml, Element project) {
        Element modelVersion = xml.createElement("modelVersion");
        modelVersion.setTextContent("4.0.0");
        project.appendChild(modelVersion);
    }
    public static void appendArtifactId(Document document, Element element, String aid) {
        Element artifactId = document.createElement("artifactId");
        artifactId.setTextContent(aid);
        element.appendChild(artifactId);
    }
    public static void appendGroupId(Document document, Element element, String gid) {
        Element groupId = document.createElement("groupId");
        groupId.setTextContent(gid);
        element.appendChild(groupId);
    }
    public static void appendVersion(Document document, Element element, String vid) {
        Element version = document.createElement("version");
        version.setTextContent(vid);
        element.appendChild(version);
    }
    public static void appendBootDependencyList(Document xml, Element dependencies, Collection<String> collection){
        for (String dep : collection) {
            Element dependency = xml.createElement("dependency");

            Element groupId = xml.createElement("groupId");groupId.setTextContent("org.springframework.boot");
            dependency.appendChild(groupId);
            Element artifactId = xml.createElement("artifactId");artifactId.setTextContent(dep);
            dependency.appendChild(artifactId);

            dependencies.appendChild(dependency);

        }
    }

    public static void appendMavenBuildPlugin(Document document, Element element) {
        Element build = document.createElement("build");
        Element plugins = document.createElement("plugins");
        Element plugin = document.createElement("plugin");
        Element groupId = document.createElement("groupId");
        groupId.setTextContent("org.springframework.boot");
        Element artifactId = document.createElement("artifactId");
        artifactId.setTextContent("spring-boot-maven-plugin");
        plugin.appendChild(groupId);
        plugin.appendChild(artifactId);
        plugins.appendChild(plugin);
        build.appendChild(plugins);
        element.appendChild(build);
    }
    public static void appendMybatisPlusGen(Document xml, Element dependencies) {
        Element d0 = xml.createElement("dependency");
        Element g0 = xml.createElement("groupId");g0.setTextContent("com.baomidou");
        Element a0 = xml.createElement("artifactId");a0.setTextContent("mybatis-plus-boot-starter");
        Element v0 = xml.createElement("version");v0.setTextContent("3.4.2");
        Element d1 = xml.createElement("dependency");
        Element g1 = xml.createElement("groupId");g1.setTextContent("com.baomidou");
        Element a1 = xml.createElement("artifactId");a1.setTextContent("mybatis-plus-generator");
        Element v1 = xml.createElement("version");v1.setTextContent("3.4.1");
        Element d2 = xml.createElement("dependency");
        Element g2 = xml.createElement("groupId");g2.setTextContent("org.apache.velocity");
        Element a2 = xml.createElement("artifactId");a2.setTextContent("velocity-engine-core");
        Element v2 = xml.createElement("version");v2.setTextContent("2.3");
        d0.appendChild(g0);d0.appendChild(a0);d0.appendChild(v0);
        d1.appendChild(g1);d1.appendChild(a1);d1.appendChild(v1);
        d2.appendChild(g2);d2.appendChild(a2);d2.appendChild(v2);
        dependencies.appendChild(d0);
        dependencies.appendChild(d1);
        dependencies.appendChild(d2);
    }

    public static void toPomXml(Document xml, Element project, File distFile) throws TransformerException {
        xml.appendChild(project);
        File pom = new File(distFile + File.separator + "pom.xml");

        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.VERSION, "1.0");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.transform(new DOMSource(xml), new StreamResult(pom));
    }
}
