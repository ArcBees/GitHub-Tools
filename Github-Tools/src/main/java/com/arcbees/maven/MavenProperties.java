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
        this.settingsPath = parseSettingsPath(settingsPath);
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

    public MavenTeamCity getTeamCityCredentials(String teamcityServerId) {
        String username = getServerValue(teamcityServerId, "username");
        String password = getServerValue(teamcityServerId, "password");
        String url = getServerConfigurationValue(teamcityServerId, "url");
        return new MavenTeamCity(username, password, url);
    }

    private String getServerConfigurationValue(String serverId, String node) {
        NodeList serversNodes = document.getElementsByTagName("servers");
        NodeList serverNodes = serversNodes.item(0).getChildNodes();

        String value = null;
        for (int i = 0; i < serverNodes.getLength(); i++) {
            Node serverNode = serverNodes.item(i);
            if (serverNodes.item(i).hasChildNodes() && hasIdNodeName(serverNode, serverId)) {
                value = getConfurationValue(serverNode, node);
                break;
            }
        }
        return value;
    }

    private String getConfurationValue(Node serverElements, String nodeName) {
        Node confNode = getNode(serverElements, "configuration");
        return getValueFromNodeList(confNode, nodeName);
    }
    
    public Node getNode(Node node, String nodeName) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node serverNode = list.item(i);
            if (list.item(i).hasChildNodes() && list.item(i).getNodeName().equals(nodeName)) {
                return serverNode;
            }
        }
        return null;
    }

    public String getServerValue(String serverId, String nodeName) {
        NodeList serversNodes = document.getElementsByTagName("servers");
        NodeList serverNodes = serversNodes.item(0).getChildNodes();

        String value = null;
        for (int i = 0; i < serverNodes.getLength(); i++) {
            Node serverNode = serverNodes.item(i);
            if (serverNodes.item(i).hasChildNodes() && hasIdNodeName(serverNode, serverId)) {
                value = getValueFromNodeList(serverNode, nodeName);
            }
        }
        return value;
    }

    public boolean hasIdNodeName(Node serverNode, String name) {
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

    public String getValueFromNodeList(Node node, String name) {
        NodeList elements = node.getChildNodes();
        String value = null;
        for (int i = 1; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            // System.out.println("serverElement=" + element.getTextContent());
            if (element.getNodeName().equals(name)) {
                value = element.getTextContent();
                break;
            }
        }
        return value;
    }

    private String parseSettingsPath(String settingsPath) {
        if (settingsPath.matches("^~.*")) {
            settingsPath = settingsPath.replace("~", "");
            String home = System.getProperty("user.home");
            settingsPath = home + settingsPath;
        }
        return settingsPath;
    }
}
