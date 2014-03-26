
package org.mule.module.google.calendar.config;

import javax.annotation.Generated;
import org.mule.module.google.calendar.oauth.GoogleCalendarConnectorOAuthManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

@Generated(value = "Mule DevKit Version 3.4.3", date = "2014-03-26T12:32:33-05:00", comments = "Build 3.4.3.1620.30ea288")
public class GoogleCalendarConnectorConfigDefinitionParser
    extends AbstractDefinitionParser
{


    public BeanDefinition parse(Element element, ParserContext parserContext) {
        parseConfigName(element);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GoogleCalendarConnectorOAuthManager.class.getName());
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        setInitMethodIfNeeded(builder, GoogleCalendarConnectorOAuthManager.class);
        setDestroyMethodIfNeeded(builder, GoogleCalendarConnectorOAuthManager.class);
        parseProperty(builder, element, "consumerKey", "consumerKey");
        parseProperty(builder, element, "consumerSecret", "consumerSecret");
        parseProperty(builder, element, "applicationName", "applicationName");
        parseProperty(builder, element, "scope", "scope");
        parseProperty(builder, element, "identifierPolicy", "identifierPolicy");
        if (hasAttribute(element, "clientFactory-ref")) {
            if (element.getAttribute("clientFactory-ref").startsWith("#")) {
                builder.addPropertyValue("clientFactory", element.getAttribute("clientFactory-ref"));
            } else {
                builder.addPropertyValue("clientFactory", new RuntimeBeanReference(element.getAttribute("clientFactory-ref")));
            }
        }
        Element httpCallbackConfigElement = DomUtils.getChildElementByTagName(element, "oauth-callback-config");
        if (httpCallbackConfigElement!= null) {
            parseProperty(builder, httpCallbackConfigElement, "domain");
            parseProperty(builder, httpCallbackConfigElement, "localPort");
            parseProperty(builder, httpCallbackConfigElement, "remotePort");
            parseProperty(builder, httpCallbackConfigElement, "async");
            parseProperty(builder, httpCallbackConfigElement, "path");
            if (hasAttribute(httpCallbackConfigElement, "connector-ref")) {
                builder.addPropertyValue("connector", new RuntimeBeanReference(httpCallbackConfigElement.getAttribute("connector-ref")));
            }
        }
        Element oauthStoreConfigElement = DomUtils.getChildElementByTagName(element, "oauth-store-config");
        if (oauthStoreConfigElement!= null) {
            parsePropertyRef(builder, oauthStoreConfigElement, "objectStore-ref", "accessTokenObjectStore");
        }
        parseProperty(builder, element, "authorizationUrl");
        parseProperty(builder, element, "accessTokenUrl");
        BeanDefinition definition = builder.getBeanDefinition();
        setNoRecurseOnDefinition(definition);
        return definition;
    }

}
