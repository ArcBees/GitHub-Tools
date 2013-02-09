package com.arcbees.maven;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MavenProperties {
    private final String settingsPath;
    
    private Document document;

    @SuppressWarnings("unused")
    private MavenProperties() {
        this.settingsPath = null;
    }

    public MavenProperties(String settingsPath) {
        this.settingsPath = settingsPath;
    }

    public Document fetchProperties() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        document = docBuilder.parse(new File(settingsPath));
        return document;
    }

    public MavenGithub getGithubCredentials(String githubServerId) {
        String username = getServerValue(githubServerId, "username");
        String password = getServerValue(githubServerId, "password");
        return new MavenGithub(username, password);
    }

    public String getServerValue(String name, String node) {
        NodeList serversNodes = document.getElementsByTagName("servers");
        NodeList serverNodes = serversNodes.item(0).getChildNodes();
        
        String value = null;
        for (int i = 0; i < serverNodes.getLength(); i++) {
            Node serverNode = serverNodes.item(i);
            if (serverNodes.item(i).hasChildNodes() && hasId(serverNode, name)) {
                value = getValue(serverNode, node);
            }
        }
        return value;
    }

    public boolean hasId(Node serverNode, String name) {
        NodeList elements = serverNode.getChildNodes();
        boolean isNode = false;
        for (int i = 1; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            if (element.getNodeName().equals("id") && element.getTextContent().equals(name)) {
                isNode = true;
                break;
            }
        }
        return isNode;
    }

    public String getValue(Node serverNode, String name) {
        NodeList elements = serverNode.getChildNodes();
        String value = null;
        for (int i = 1; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            //System.out.println("serverElement=" + element.getTextContent());
            if (element.getNodeName().equals(name)) {
                value = element.getTextContent();
                break;
            }
        }
        return value;
    }
}
